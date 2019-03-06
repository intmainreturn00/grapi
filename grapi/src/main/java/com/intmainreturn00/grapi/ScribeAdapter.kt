package com.intmainreturn00.grapi

import com.github.scribejava.core.model.*
import com.github.scribejava.core.oauth.OAuth10aService
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


suspend fun OAuth10aService.requestToken(): OAuth1RequestToken? = suspendCoroutine { cont ->
    val callback = object : OAuthAsyncRequestCallback<OAuth1RequestToken> {
        override fun onThrowable(t: Throwable?) {
            cont.resumeWithException(Exception(t))
        }

        override fun onCompleted(response: OAuth1RequestToken?) {
            cont.resume(response)
        }
    }
    getRequestTokenAsync(callback)
}


suspend fun OAuth10aService.accessToken(requestToken: OAuth1RequestToken, oauthVerifier: String): OAuth1AccessToken? =
    suspendCoroutine { cont ->
        val callback = object : OAuthAsyncRequestCallback<OAuth1AccessToken> {
            override fun onThrowable(t: Throwable?) {
                cont.resumeWithException(Exception(t))
            }

            override fun onCompleted(response: OAuth1AccessToken?) {
                cont.resume(response)
            }
        }
        getAccessTokenAsync(requestToken, oauthVerifier, callback)
    }


suspend fun OAuth10aService.executeSignedRequest(
    url: String,
    accessToken: OAuth1AccessToken,
    params: Map<String, String>? = null
): Response =
    suspendCoroutine { cont ->
        val callback = object : OAuthAsyncRequestCallback<Response> {
            override fun onThrowable(t: Throwable?) {
                cont.resumeWithException(Exception(t))
            }

            override fun onCompleted(response: Response?) {
                cont.resume(response!!)
            }

        }
        val request = OAuthRequest(Verb.GET, url)
        if (params != null) {
            for ((name, value) in params) {
                request.addParameter(name, value)
            }
        }
        signRequest(accessToken, request)
        execute(request, callback)
    }