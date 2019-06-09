package com.intmainreturn00.grexample

import android.os.Bundle
import com.intmainreturn00.grapi.Order
import com.intmainreturn00.grapi.Sort
import com.intmainreturn00.grapi.grapi
import kotlinx.android.synthetic.main.login_main.*
import kotlinx.coroutines.*
import org.jetbrains.anko.browse


class LoginActivity : ScopedAppActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.login_main)

        login.setOnClickListener {
            if (!grapi.isLoggedIn()) {
                launch {
                    grapi.loginStart()
                    browse(grapi.getAuthorizationUrl())
                }
            } else {
                tryUseApi()
            }
        }

        launch {
            grapi.loginEnd(intent) { ok ->
                if (ok) {
                    // here we can start using api!
                    tryUseApi()
                }
            }
        }

    }

    fun tryUseApi() {
        launch {
            val userId = grapi.getUserId()
            val shelves = grapi.getUserShelves(1, userId.id)
            val reviews = grapi.getReviewList(
                userId.id,
                "read",
                1, 2,
                sort = Sort.NUM_PAGES,
                order = Order.DESCENDING
            )
            val book = grapi.getBookByISBN("837054150X")
            val book2 = grapi.getBookByGRID("13588846")
            val res = grapi.getSearchResults("Wiedźmin")
            val user = grapi.getUser(userId.id)
            val allReviews = grapi.getAllReviews(userId.id, shelf = "favorites")
            val allShelves = grapi.getAllShelves(userId.id)

            println(userId)
            println(allShelves)
            println(allReviews)
            println(reviews.reviews[1])
            println(user)



        }
    }


}
