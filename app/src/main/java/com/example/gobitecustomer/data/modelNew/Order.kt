package com.example.gobitecustomer.data.modelNew

data class Order(
    val created_at: String,
    val customer_id: Int,
    val deleted: Boolean,
    val id: String,
    val items: List<ItemX>,
    val meta: MetaX,
    val order_placed_time: Any,
    val order_status: String,
    val payment_status: String,
    val price: Double,
    val rating: Int,
    val shop_id: Int,
    val tax: Int,
    val transactions: List<Any>,
    val updated_at: String
)