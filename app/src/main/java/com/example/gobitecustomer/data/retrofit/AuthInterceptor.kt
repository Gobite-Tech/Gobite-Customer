package com.example.gobitecustomer.data.retrofit

import android.content.Context
import android.util.Log
import com.example.gobitecustomer.data.local.PreferencesHelper
import com.example.gobitecustomer.utils.AppConstants
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class   AuthInterceptor(val context: Context, val preferences: PreferencesHelper) :
    Interceptor {

    private val credentials: String = Credentials.basic(AppConstants.APP_ID, AppConstants.APP_KEY)
    var value  = credentials

    fun headerchange(num : Int){
        if(num == 0){
            value = "Bearer ${preferences.jwtToken}"
        }else{
            value = credentials
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val whiteListedEndpoints = listOf(
            "/v2/auth/otp","/v2/auth/signup_with_mobile","/v2/auth/login"
        )
        val request = if (!whiteListedEndpoints.contains(req.url().encodedPath()) ) {
            println("oauth_id testing 1"+preferences.oauthId)
            req.newBuilder()
                .addHeader("Authorization", value)
                .build()
        } else {
            req.newBuilder().build()
        }
        val response = chain.proceed(request)
        return response
    }
}