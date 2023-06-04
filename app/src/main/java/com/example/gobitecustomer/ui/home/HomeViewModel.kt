package com.example.gobitecustomer.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.gobitecustomer.data.local.Resource
import com.example.gobitecustomer.data.modelNew.shopsList
import com.example.gobitecustomer.data.retrofit.ShopRepository
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import androidx.lifecycle.*
import com.example.gobitecustomer.data.retrofit.AuthInterceptor

class HomeViewModel(private val shopRepository: ShopRepository, private val authInterceptor: AuthInterceptor):ViewModel() {

    private val performFetchShops = MutableLiveData<Resource<shopsList>>()
    val performFetchShopsStatus: LiveData<Resource<shopsList>>
        get() = performFetchShops


    fun getShops() {
        viewModelScope.launch {
            try {
                performFetchShops.value = Resource.loading()
                val response = shopRepository.getShops()
                if (response.isSuccessful) {
                    performFetchShops.value = Resource.success(response.body()!!)
                } else {
                    performFetchShops.value = Resource.empty()
                }
            } catch (e: Exception) {
                Log.e("Home viewmodel" , " ${e.message}")
                if (e is UnknownHostException) {
                    performFetchShops.value = Resource.offlineError()
                } else {
                    performFetchShops.value = Resource.error(e)
                }
            }
        }
    }

    fun change(num : Int){
        authInterceptor.headerchange(num)
    }
}