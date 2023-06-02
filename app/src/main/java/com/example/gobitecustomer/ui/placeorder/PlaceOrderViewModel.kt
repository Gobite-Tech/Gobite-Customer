package com.example.gobitecustomer.ui.placeorder

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gobitecustomer.data.local.Resource
import com.example.gobitecustomer.data.modelNew.UpdateStatusModel
import com.example.gobitecustomer.data.modelNew.VerifyOrderResponse
import com.example.gobitecustomer.data.modelNew.sendOtpModel
import com.example.gobitecustomer.data.retrofit.AuthInterceptor
import com.example.gobitecustomer.data.retrofit.OrderRepository
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class PlaceOrderViewModel(private val orderRepository: OrderRepository, private var authInterceptor: AuthInterceptor) : ViewModel() {

    //place order
    private val placeOrder = MutableLiveData<Resource<VerifyOrderResponse>>()
    val placeOrderStatus: LiveData<Resource<VerifyOrderResponse>>
        get() = placeOrder

    fun placeOrder(orderId: String , updatestatus : UpdateStatusModel) {
        viewModelScope.launch {
            try {
                placeOrder.value = Resource.loading()
                val response = orderRepository.placeOrder(orderId, updatestatus)
                if(response!=null){
                    if(response.isSuccessful){
                        placeOrder.value = Resource.success(response.body()!!)
                    }else{
                        placeOrder.value = Resource.error(null,response.message())
                    }
                }
            } catch (e: Exception) {
                Log.e("place order failed"," ${e.message}")
                if (e is UnknownHostException) {
                    placeOrder.value = Resource.offlineError()
                } else {
                    placeOrder.value = Resource.error(e)
                }
            }
        }
    }

    fun sendOTP(sendOtpModel: sendOtpModel) {
        viewModelScope.launch {
            try{
                val response = orderRepository.sendOTP(sendOtpModel)
                if(response.isSuccessful){
                    Log.e("Seller Notified"," ${response.body()}")
                }else{
                    Log.e("Notification Failed"," ${response.message()}")
                }
            }catch (e: Exception){
                Log.e("Notification Failed"," ${e.message}")
            }

            change(0)
        }
    }

    fun change(num : Int){
        authInterceptor.headerchange(num)
    }

}