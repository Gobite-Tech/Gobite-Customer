package com.example.gobitecustomer.data.retrofit

import retrofit2.Retrofit

class ItemRepository(retrofit: Retrofit) {
    private val services = retrofit.create(CustomApi::class.java)
    suspend fun getMenu(shopId : String) = services.getMenu(shopId)
}