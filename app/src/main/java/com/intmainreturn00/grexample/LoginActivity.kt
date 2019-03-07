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

            println(userId)
            println(shelves)
            println(reviews)

        }
    }


}
