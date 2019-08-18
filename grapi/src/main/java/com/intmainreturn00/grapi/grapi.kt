package com.intmainreturn00.grapi

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.*
import com.github.scribejava.core.oauth.OAuth10aService
import kotlinx.coroutines.*
import kotlin.math.ceil

object grapi {

    private const val TAG = "grapi:"
    private const val PREF_FILE = "token_storage"
    private const val PREF_TOKEN = "token"
    private const val PREF_SECRET = "token_secret"
    private const val MAX_PER_PAGE = 200
    private const val PER_PAGE = 50
    private lateinit var sharedPref: SharedPreferences
    private lateinit var requestToken: OAuth1RequestToken
    private lateinit var accessToken: OAuth1AccessToken
    private lateinit var oauth: OAuth10aService
    private var initialize = false
    private var requestTokenObtained = false
    private var loggedIn = false

    fun init(context: Context, devKey: String, devSecret: String, callback: String) {
        oauth = ServiceBuilder(devKey).apiSecret(devSecret)
            .callback(callback).build(GoodreadsOauthApi.instance())
        initialize = true
        sharedPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        // check for token
        val token = sharedPref.getString(PREF_TOKEN, "")
        val tokenSecret = sharedPref.getString(PREF_SECRET, "")
        if (token != null && !token.isEmpty() && tokenSecret != null && !tokenSecret.isEmpty()) {
            accessToken = OAuth1AccessToken(token, tokenSecret)
            loggedIn = true
        }
    }


    fun isLoggedIn(): Boolean {
        return loggedIn
    }

    /**
     * Initiate OAuth flow.
     *
     * Oauth flow:
     *  1) [loginStart]
     *  2) Redirect to url from [getAuthorizationUrl]
     *  3) After user returns back, [loginEnd] with given intent
     */
    suspend fun loginStart() {
        if (!initialize) {
            throw Exception("$TAG init method wasn't called")
        }
        if (isLoggedIn()) {
            return
        }
        withContext(Dispatchers.Default) {
            requestToken = oauth.requestToken()!!
        }
        requestTokenObtained = true
    }


    /**
     * Returns the URL where you should redirect your users to authenticate your application.
     */
    fun getAuthorizationUrl(): String {
        if (!initialize || !requestTokenObtained) {
            throw Exception("$TAG should call init and loginStart first ")
        }
        return oauth.getAuthorizationUrl(requestToken)
    }


    /**
     * Complete OAuth flow.
     */
    suspend fun loginEnd(intent: Intent, result: (ok: Boolean) -> Unit) {
        if (!initialize) {
            throw Exception("$TAG init method wasn't called")
        }
        if (isLoggedIn()) {
            return
        }
        if (intent.data == null) {
            return // this was not auth redirect
        }
        val uri: Uri = intent.data!!
        val clientOauthToken: String? = uri.getQueryParameter("oauth_token")
        val authorize: String? = uri.getQueryParameter("authorize")

        if (clientOauthToken == null || authorize == null) {
            return // this was not auth redirect
        } else if (authorize == "0") {
            withContext(Dispatchers.Main) {
                result(false) // user cancel auth
            }
        } else {
            // user accept authorization -> request accessToken
            withContext(Dispatchers.IO) {
                accessToken = oauth.accessToken(requestToken, clientOauthToken)!!
                with(sharedPref.edit()) {
                    putString(PREF_TOKEN, accessToken.token)
                    putString(PREF_SECRET, accessToken.tokenSecret)
                    commit()
                }
                loggedIn = true
            }
            withContext(Dispatchers.Main) {
                result(true)
            }
        }
    }


    suspend fun getUserId(): UserId = withContext(Dispatchers.IO) {
        val xml = oauth.executeSignedRequest("https://www.goodreads.com/api/auth_user", accessToken).body
        parse<UserId>(xml)
    }


    suspend fun getUserShelves(page: Int, userId: String): UserShelves = withContext(Dispatchers.IO) {
        val params = mapOf("page" to page.toString(), "user_id" to userId)
        val xml = oauth.executeSignedRequest("https://www.goodreads.com/shelf/list.xml", accessToken, params).body
        parse<UserShelves>(xml)
    }


    suspend fun getReviewList(
        userId: String,
        shelf: String = "",
        page: Int = 1,
        perPage: Int = MAX_PER_PAGE,
        search: String = "",
        sort: Sort = Sort.EMPTY,
        order: Order = Order.DESCENDING
    ): ReviewList =
        withContext(Dispatchers.IO) {
            val params =
                mapOf(
                    "v" to "2",
                    "id" to userId,
                    "shelf" to shelf,
                    "page" to page.toString(),
                    "per_page" to perPage.toString(),
                    "search" to search,
                    "key" to oauth.apiKey,
                    "sort" to (if (sort == Sort.EMPTY) "" else sort.toString().toLowerCase()),
                    "order" to (if (order == Order.DESCENDING) "d" else "a")
                )
            val xml = oauth.executeSignedRequest(
                "https://www.goodreads.com/review/list/${userId}.xml",
                accessToken, params
            ).body
            parse<ReviewList>(xml)
        }


    suspend fun getBookByISBN(isbn: String): Book = withContext(Dispatchers.IO) {
        val params = mapOf("key" to oauth.apiKey)
        val xml = oauth.executeSignedRequest(
            "https://www.goodreads.com/book/isbn/${isbn}?format=xml",
            accessToken, params
        ).body
        parse<Book>(xml)
    }


    suspend fun getBookByGRID(id: String): Book = withContext(Dispatchers.IO) {
        val params = mapOf("key" to oauth.apiKey)
        val xml = oauth.executeSignedRequest(
            "https://www.goodreads.com/book/show/${id}?format=xml",
            accessToken, params
        ).body
        parse<Book>(xml)
    }


    suspend fun getSearchResults(query: String, page: Int = 1): SearchResults = withContext(Dispatchers.IO) {
        val params = mapOf("key" to oauth.apiKey, "page" to page.toString(), "q" to query)
        val xml = oauth.executeSignedRequest(
            "https://www.goodreads.com/search/index.xml",
            accessToken, params
        ).body
        parse<SearchResults>(xml)
    }


    suspend fun getUser(userId: String) = withContext(Dispatchers.IO) {
        val params = mapOf("key" to oauth.apiKey, "id" to userId)
        val xml = oauth.executeSignedRequest(
            "https://www.goodreads.com/user/show/${userId}.xml",
            accessToken, params
        ).body
        parse<User>(xml)
    }


    suspend fun getAllReviews(
        userId: String,
        shelf: String = "",
        sort: Sort = Sort.EMPTY,
        order: Order = Order.DESCENDING
    ): List<Review> = withContext(Dispatchers.IO) {
        val reviews: MutableList<Review> = mutableListOf()
        var page = 1
        val startPiece =
            getReviewList(userId, page = page++, perPage = MAX_PER_PAGE, sort = sort, order = order, shelf = shelf)
        reviews.addAll(startPiece.reviews)
        while (reviews.size < startPiece.total) {
            val piece =
                getReviewList(userId, page = page++, perPage = MAX_PER_PAGE, sort = sort, order = order, shelf = shelf)
            reviews.addAll(piece.reviews)
        }
        reviews
    }

    /**
     * Concurrent (and faster) version of [getAllReviews].
     *
     * Semantics of this call is the following:
     *  1) Send first request to find out a total amount of reviews
     *  2) Send a bunch of concurrent requests with respect to perPage arg.
     *  3) Aggregate results
     *
     *  Exception propagation used - return only if all requests succeed.
     *  Use with caution, since it may unintentionally violate GoodReads API rate limits.
     */
    suspend fun getAllReviewsConcurrent(
        userId: String,
        shelf: String = "",
        perPage: Int = PER_PAGE,
        sort: Sort = Sort.EMPTY,
        order: Order = Order.DESCENDING
    ): List<Review> = withContext(Dispatchers.IO) {
        val reviews: MutableList<Review> = mutableListOf()
        val startPiece = getReviewList(userId, page = 1, perPage = perPage, sort = sort, order = order, shelf = shelf)

        val pages = ceil(startPiece.total / perPage.toFloat()).toInt()
        val requests = mutableListOf<Deferred<ReviewList>>()

        coroutineScope{
            repeat(pages - 1) {
                val deferred = async {
                    getReviewList(userId, page = it + 2, perPage = perPage, sort = sort, order = order, shelf = shelf)
                }
                requests.add(it, deferred)
            }
        }
        reviews.addAll(startPiece.reviews)
        requests.forEach { reviews.addAll(it.await().reviews) }

        reviews
    }


    suspend fun getAllShelves(userId: String): List<Shelf> = withContext(Dispatchers.IO) {
        val shelves: MutableList<Shelf> = mutableListOf()
        var page = 1
        val startPiece = getUserShelves(page = page++, userId = userId)
        shelves.addAll(startPiece.shelves)
        while (shelves.size < startPiece.total) {
            val piece = getUserShelves(page = page++, userId = userId)
            shelves.addAll(piece.shelves)
        }
        shelves
    }

}