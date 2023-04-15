package com.example.gobitecustomer.data.modelNew

import com.example.gobitecustomer.data.modelNew.jwtToken
import java.io.Serializable

data class LoginResponse(
    val success  :String,
    val message : String,
    val data : jwtToken
) : Serializable
