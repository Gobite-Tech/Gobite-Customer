package com.example.gobitecustomer.data.modelNew

import com.google.gson.annotations.SerializedName

data class SignUpRequestNew(
    @SerializedName("purpose")
    val purpose : String,
    @SerializedName("auth_token")
    val auth_token : String,
    @SerializedName("otp")
    val otp : String,
    @SerializedName("mobile")
    val mobile : String,
    @SerializedName("password")
    val password : String,
    @SerializedName("password_confirmation")
    val password_confirmation : String
)
