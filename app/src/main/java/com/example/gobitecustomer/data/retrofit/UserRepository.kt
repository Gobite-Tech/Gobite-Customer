package com.example.gobitecustomer.data.retrofit

import com.example.gobitecustomer.data.modelNew.LoginRequestNew
import com.example.gobitecustomer.data.modelNew.OTPRequest
import com.example.gobitecustomer.data.modelNew.SignUpRequestNew
import com.example.gobitecustomer.data.modelNew.UpdateUserRequest
import retrofit2.Retrofit

class UserRepository(retrofit: Retrofit) {
    private val services = retrofit.create(CustomApi::class.java)
    suspend fun getOTP(OTPRequest: OTPRequest) = services.getOTP(OTPRequest)
    suspend fun LoginUser(loginRequestNew: LoginRequestNew) = services.LoginUser(loginRequestNew)
    suspend fun registerUser(signUpRequestNew: SignUpRequestNew) = services.registerUser(signUpRequestNew)

    suspend fun updateUser(userRequest: UpdateUserRequest) = services.updateUser(userRequest)
}