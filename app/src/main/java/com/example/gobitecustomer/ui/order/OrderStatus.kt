package com.example.gobitecustomer.ui.order

data class OrderStatus(
    var isDone: Boolean = false,
    var isCurrent: Boolean = false,
    var name: String,
    var orderStatusList: List<OrderStatusModel> = listOf()
)