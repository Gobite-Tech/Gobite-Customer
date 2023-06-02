package com.example.gobitecustomer.data.retrofit

import com.example.gobitecustomer.data.modelNew.PlaceOrderRequest
import com.example.gobitecustomer.data.modelNew.UpdateStatusModel
import com.example.gobitecustomer.data.modelNew.sendOtpModel
import com.example.gobitecustomer.utils.AppConstants
import retrofit2.Retrofit

class OrderRepository(retrofit: Retrofit) {
    private val services = retrofit.create(CustomApi::class.java)
    suspend fun insertOrder(placeOrderRequest: PlaceOrderRequest) = services.insertOrder(placeOrderRequest)
    suspend fun placeOrder(orderId: String , updateStatusModel: UpdateStatusModel) = services.placeOrder(orderId , updateStatusModel)
    suspend fun getOrder() = services.getOrder()

    suspend fun getOrderById(id: String) = services.getOrderById(id)

    suspend fun cancelOrder(orderId: String) = services.cancelOrder(orderId)

    suspend fun sendOTP(sendOtpModel: sendOtpModel) = services.sendOTP(AppConstants.MESSAGE_URL, sendOtpModel)
}