package com.example.gobitecustomer.data.retrofit

import com.example.gobitecustomer.data.model.NotificationTokenUpdate
import com.example.gobitecustomer.data.model.UpdateUserRequest
import com.example.gobitecustomer.data.modelNew.LoginRequestNew
import com.example.gobitecustomer.data.modelNew.OTPRequest
import com.example.gobitecustomer.data.modelNew.SignUpRequestNew
import retrofit2.Retrofit

class UserRepository(retrofit: Retrofit) {
    private val services = retrofit.create(CustomApi::class.java)
    suspend fun getOTP(OTPRequest: OTPRequest) = services.getOTP(OTPRequest)
    suspend fun LoginUser(loginRequestNew: LoginRequestNew) = services.LoginUser(loginRequestNew)
    suspend fun registerUser(signUpRequestNew: SignUpRequestNew) = services.registerUser(signUpRequestNew)
    suspend fun updateUser(updateUserRequest: UpdateUserRequest) = services.updateUser(updateUserRequest)
    suspend fun updateFcmToken(notificationTokenUpdate: NotificationTokenUpdate) = services.updateFcmToken(notificationTokenUpdate)
}