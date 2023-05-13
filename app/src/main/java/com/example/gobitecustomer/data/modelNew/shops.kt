package com.example.gobitecustomer.data.modelNew

data class shops(
    val id : Int,
    val name : String,
    val icon : String,
    val tags : ArrayList<String>,
    val area : String,
    val mobile : String,
    val telephone : String,
    val opening_time : String,
    val closing_time : String,
    val open_now : Boolean,
    val avg_serve_time : Int,
    val coverurl :String = ""
)
