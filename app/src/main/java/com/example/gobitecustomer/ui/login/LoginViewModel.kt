package com.example.gobitecustomer.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gobitecustomer.data.local.Resource
import com.example.gobitecustomer.data.modelNew.LoginRequestNew
import com.example.gobitecustomer.data.modelNew.LoginResponse
import com.example.gobitecustomer.data.retrofit.UserRepository
import com.example.gobitecustomer.data.modelNew.OTP
import com.example.gobitecustomer.data.modelNew.OTPRequest
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    //GET OTP
    private val performGetOTP = MutableLiveData<Resource<OTP>>()
    val performGetOTPStatus: LiveData<Resource<OTP>>
        get() = performGetOTP

    fun getOTP(OTPRequest: OTPRequest) {
        viewModelScope.launch {
            try {
                performGetOTP.value = Resource.loading()
                var response = userRepository.getOTP(OTPRequest)
                performGetOTP.value = Resource.success(response.body()!!)

            } catch (e: Exception) {
                println("login failed ${e.message}")
                if (e is UnknownHostException) {
                    performGetOTP.value = Resource.offlineError()
                } else {
                    performGetOTP.value = Resource.error(e)
                }
            }
        }
    }

    //SIGNUP
    private val performSignUpIn = MutableLiveData<Resource<OTP>>()
    val performSignUpInStatus: LiveData<Resource<OTP>>
        get() = performSignUpIn

    fun SignUpIn(OTPRequest: OTPRequest) {
        viewModelScope.launch {
            try {
                performSignUpIn.value = Resource.loading()
                val response = userRepository.getOTP(OTPRequest)
                performSignUpIn.value = Resource.success(response.body()!!)
            } catch (e: Exception) {
                println("login failed ${e.message}")
                if (e is UnknownHostException) {
                    performSignUpIn.value = Resource.offlineError()
                } else {
                    performSignUpIn.value = Resource.error(e)
                }
            }
        }
    }

    //LOGIN
    private val performLogin = MutableLiveData<Resource<LoginResponse>>()
    val performLoginStatus: LiveData<Resource<LoginResponse>>
        get() = performLogin

    fun Login(loginRequestNew: LoginRequestNew) {
        viewModelScope.launch {
            try {
                performLogin.value = Resource.loading()
                val response = userRepository.LoginUser(loginRequestNew)
                performLogin.value = Resource.success(response.body()!!)
            } catch (e: Exception) {
                println("login failed ${e.message}")
                if (e is UnknownHostException) {
                    performLogin.value = Resource.offlineError()
                } else {
                    performLogin.value = Resource.error(e)
                }
            }
        }
    }


}