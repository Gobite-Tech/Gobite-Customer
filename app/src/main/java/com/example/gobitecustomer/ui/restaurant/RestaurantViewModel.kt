package com.example.gobitecustomer.ui.restaurant

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gobitecustomer.data.local.Resource
import com.example.gobitecustomer.data.modelNew.Data
import com.example.gobitecustomer.data.modelNew.Item
import com.example.gobitecustomer.data.modelNew.MenuItem
import com.example.gobitecustomer.data.retrofit.ItemRepository
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RestaurantViewModel(
    private val itemRepository: ItemRepository
    ) : ViewModel() {

    //Fetch menu items
    private val performFetchMenu = MutableLiveData<Resource<Data>>()

    val performFetchMenuStatus: MutableLiveData<Resource<Data>>
        get() = performFetchMenu

    var menuList:ArrayList<Item> = ArrayList()
    var menuVegList:ArrayList<Item> = ArrayList()
    fun getMenu(shopId: String) {
        viewModelScope.launch {
            try {
                performFetchMenu.value = Resource.loading()
                val response = itemRepository.getMenu(shopId)
                if(response!=null){
                    if(response.isSuccessful){
                        menuList.clear()
                        menuVegList.clear()
                        response.body()?.data!!.items.forEach {
                            it.shop_id = shopId.toInt()
                            menuList.add(it)
                        }
                        menuList.sortByDescending {
                            it.category
                        }
//                        menuList.forEach{
//                            if(it.isVeg==1){
//                                menuVegList.add(it)
//                            }
//                        }
                        performFetchMenu.value = Resource.success(response.body()?.data!!)
                    }else{
                        performFetchMenu.value = Resource.empty()
                    }
                }
            } catch (e: Exception) {
                println("fetch menu failed ${e.message}")
                if (e is UnknownHostException) {
                    performFetchMenu.value = Resource.offlineError()
                } else {
                    performFetchMenu.value = Resource.error(e)
                }
            }
        }
    }


//    fun switchMenu(isVeg: Boolean){
//        println("switch menu testing veg "+menuVegList.size)
//        println("switch menu testing non veg "+menuList.size)
//        if(isVeg){
//            performFetchMenu.value = Resource.success(menuVegList)
//        }else{
//            performFetchMenu.value = Resource.success(menuList)
//        }
//
//    }
//
//    fun getTime(time: String?): Date {
//        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
//        val timeCalendar = Calendar.getInstance()
//        val closingTime = sdf.parse(time)
//        val cal1 = Calendar.getInstance()
//        cal1.time = closingTime
//        timeCalendar[Calendar.HOUR_OF_DAY] = cal1.get(Calendar.HOUR_OF_DAY)
//        timeCalendar[Calendar.MINUTE] = cal1.get(Calendar.MINUTE)
//        timeCalendar[Calendar.SECOND] = cal1.get(Calendar.SECOND)
//        return timeCalendar.time
//    }
}