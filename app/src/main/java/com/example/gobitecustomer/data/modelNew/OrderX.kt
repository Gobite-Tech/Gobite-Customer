package com.example.gobitecustomer.data.modelNew

data class OrderX(
    val created_at: String,
    val customer_id: Int,
    val deleted: Boolean,
    val id: String,
    val items: List<ItemXX>,
    val meta: MetaXX,
    val order_placed_time: String,
    var order_status: String,
    val payment_status: String,
    val price: Double,
    val rating: Int,
    val shop_id: Int,
    val tax: Int,
    val transactions: List<Any>,
    val updated_at: String,
    var shop_name : String
)