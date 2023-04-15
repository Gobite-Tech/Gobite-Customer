package com.example.gobitecustomer.data.modelNew

import com.google.gson.annotations.SerializedName

data class OTPRequest(
    //NEW
    @SerializedName("purpose")
    val purpose: String,
    @SerializedName("mobile")
    val mobile : String
)
