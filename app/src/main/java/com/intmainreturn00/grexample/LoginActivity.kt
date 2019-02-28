package com.intmainreturn00.grexample

import android.os.Bundle
import com.intmainreturn00.grapi.grapi
import kotlinx.android.synthetic.main.login_main.*
import kotlinx.coroutines.*
import org.jetbrains.anko.browse
import org.jetbrains.anko.toast

class LoginActivity : ScopedAppActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.login_main)

        login.setOnClickListener {
            if (!grapi.isLoggedIn()) {
                launch {
                    try {
                        grapi.loginStart()
                        browse(grapi.getAuthorizationUrl())
                    } catch (e: Exception) {
                        println(e)
                    }
                }
            }
        }

        launch {
            try {
                grapi.loginEnd(intent) {ok ->
                    toast("auth $ok")
                }
            } catch (e: Exception) {
                println(e)
            }
        }
    }
}
