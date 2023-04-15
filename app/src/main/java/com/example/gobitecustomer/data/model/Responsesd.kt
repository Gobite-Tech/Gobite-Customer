package com.example.gobitecustomer.data.model

import com.google.gson.annotations.SerializedName

data class Responsesd<out T>(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: T?,
    @SerializedName("message")
    val message: String
)