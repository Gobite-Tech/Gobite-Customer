package com.example.gobitecustomer.data.modelNew

import java.io.Serializable

data class LoginRequestNew(
    val purpose : String,
    val auth_token : String,
    val otp : String
) : Serializable
