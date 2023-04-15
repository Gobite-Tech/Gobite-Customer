package com.example.gobitecustomer.data.modelNew

import java.io.Serializable

data class OTP(
    val data: token,
    val message: String,
    val success: Boolean
) : Serializable