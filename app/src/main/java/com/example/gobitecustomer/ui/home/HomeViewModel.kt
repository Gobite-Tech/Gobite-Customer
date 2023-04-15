package com.food.ordering.zinger.ui.home

import androidx.lifecycle.*
import com.example.gobitecustomer.data.local.Resource
import com.example.gobitecustomer.data.modelNew.shopsList
import com.example.gobitecustomer.data.retrofit.ShopRepository
import kotlinx.coroutines.launch

import java.net.UnknownHostException


class HomeViewModel(private val shopRepository: ShopRepository) : ViewModel() {

    //Fetch shops
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
                println("fetch shops failed ${e.message}")
                if (e is UnknownHostException) {
                    performFetchShops.value = Resource.offlineError()
                } else {
                    performFetchShops.value = Resource.error(e)
                }
            }
        }
    }

}