package com.intmainreturn00.grapi

import android.content.Intent
import android.net.Uri
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.OAuth1AccessToken
import com.github.scribejava.core.model.OAuth1RequestToken
import com.github.scribejava.core.model.OAuthAsyncRequestCallback
import com.github.scribejava.core.oauth.OAuth10aService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


object grapi {

    private val TAG = "grapi:"
    private lateinit var requestToken: OAuth1RequestToken
    private lateinit var accessToken: OAuth1AccessToken
    private lateinit var oauth: OAuth10aService
    private var initialize = false
    private var loggedIn = false
    private var requestTokenObtained = false

    fun init(devKey: String, devSecret: String, callback: String) {
        oauth = ServiceBuilder(devKey).apiSecret(devSecret)
            .callback(callback).build(GoodreadsOauthApi.instance())
        initialize = true
    }


    fun isLoggedIn(): Boolean {
        return loggedIn
    }


    private suspend fun requestToken(): OAuth1RequestToken? =
        suspendCoroutine { cont ->
            val callback = object : OAuthAsyncRequestCallback<OAuth1RequestToken> {
                override fun onThrowable(t: Throwable?) {
                    cont.resumeWithException(Exception(t))
                }

                override fun onCompleted(response: OAuth1RequestToken?) {
                    cont.resume(response)
                }
            }
            oauth.getRequestTokenAsync(callback)
        }


    private suspend fun accessToken(requestToken: OAuth1RequestToken, oauthVerifier: String): OAuth1AccessToken? =
        suspendCoroutine { cont ->
            val callback = object : OAuthAsyncRequestCallback<OAuth1AccessToken> {
                override fun onThrowable(t: Throwable?) {
                    cont.resumeWithException(Exception(t))
                }

                override fun onCompleted(response: OAuth1AccessToken?) {
                    cont.resume(response)
                }
            }
            oauth.getAccessTokenAsync(requestToken, oauthVerifier, callback)
        }


    fun getAuthorizationUrl(): String {
        if (!initialize) {
            throw Exception("$TAG init method wasn't called")
        }
        return oauth.getAuthorizationUrl(requestToken)
    }


    suspend fun loginStart() {
        if (!initialize) {
            throw Exception("$TAG init method wasn't called")
        }
        if (isLoggedIn()) {
            return
        }
        withContext(Dispatchers.Default) {
            requestToken = requestToken()!!
        }
        requestTokenObtained = true
    }


    suspend fun loginEnd(intent: Intent, result: (ok: Boolean)->Unit) {
        if (!initialize) {
            throw Exception("$TAG init method wasn't called")
        }
        if (isLoggedIn()) {
            return
        }
        if (intent.data == null) {
            return // this was not auth redirect
        }
        val uri:Uri = intent.data!!
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
            withContext(Dispatchers.Default) {
                accessToken = accessToken(requestToken, clientOauthToken)!!
            }
            withContext(Dispatchers.Main) {
                result(true)
            }
        }
    }

}