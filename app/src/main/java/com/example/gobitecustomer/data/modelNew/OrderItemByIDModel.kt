package com.example.gobitecustomer.data.modelNew

data class OrderItemByIDModel(
    val `data`: OData,
    val message: String
)

data class OData(
    val order: OrderX,
)
