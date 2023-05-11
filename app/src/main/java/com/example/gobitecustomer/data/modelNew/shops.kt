package com.example.gobitecustomer.data.modelNew

data class shops(
    val id : Int,
    val name : String,
    val icon : String,
    val tags : ArrayList<String>,
    val area : String,
    val open_at : String,
    val close_at : String,
    val open_now : Boolean,
)
