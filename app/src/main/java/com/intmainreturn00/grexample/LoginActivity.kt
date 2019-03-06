package com.intmainreturn00.grexample

import android.os.Bundle
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
            grapi.loginEnd(intent) {ok ->
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
            //val ownedBooks = grapi.getUserOwnedBooks(1, userId.id)
            val booksFromShelf = grapi.getBooksFromShelf(userId.id, "read", 1, 2)

            println(userId)
            println(shelves)
            //println(ownedBooks)
            println(booksFromShelf)
        }
    }






}
