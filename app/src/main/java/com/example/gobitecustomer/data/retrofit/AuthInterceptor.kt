package com.example.gobitecustomer.data.retrofit

import android.content.Context
import com.example.gobitecustomer.data.local.PreferencesHelper
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(val context: Context, val preferences: PreferencesHelper) :
    Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val whiteListedEndpoints = listOf(
            "/v2/auth/otp","/v2/auth/signup_with_mobile","/v2/auth/login"
        )
        val request = if (!whiteListedEndpoints.contains(req.url().encodedPath())) {
            println("oauth_id testing 1"+preferences.oauthId)
            req.newBuilder()
                .addHeader("Authorization", "Bearer ${preferences.jwtToken}")
//                    .addHeader("id", preferences.userId.toString())
//                    .addHeader("role", preferences.role)
                .build()
        } else {
            println("oauth_id testing 2"+preferences.oauthId)
            req.newBuilder().build()
        }
        val response = chain.proceed(request)
        return response
    }
}