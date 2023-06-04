package com.example.gobitecustomer.data.retrofit

import com.example.gobitecustomer.utils.AppConstants
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class BasicInterceptor() : Interceptor {
    private val credentials: String = Credentials.basic(AppConstants.APP_ID, AppConstants.APP_KEY)

    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()

        val endpath = listOf(
            AppConstants.MESSAGE_URL
        )

        val authenticatedRequest = if (endpath.contains(req.url().encodedPath())) {
            req.newBuilder()
                .addHeader("Authorization", credentials)
                .build()
        } else {
            req.newBuilder().build()
        }

        return chain.proceed(authenticatedRequest)
    }

}
