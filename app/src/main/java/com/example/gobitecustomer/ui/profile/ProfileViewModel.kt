package com.example.gobitecustomer.ui.profile

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gobitecustomer.data.local.PreferencesHelper
import com.example.gobitecustomer.data.local.Resource
import com.example.gobitecustomer.data.modelNew.UpdateUserRequest
import com.example.gobitecustomer.data.modelNew.UserUpdateResponse
import com.example.gobitecustomer.data.retrofit.PlaceRepository
import com.example.gobitecustomer.data.retrofit.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class ProfileViewModel(private val userRepository: UserRepository
//, private val placeRepository: PlaceRepository,
//                       private val preferencesHelper: PreferencesHelper
) : ViewModel() {

    //Fetch places list
//    private val performFetchPlacesList = MutableLiveData<Resource<List<PlaceModel>>>()
//    val performFetchPlacesStatus: LiveData<Resource<Responsesd<List<PlaceModel>>>>
//        get() = performFetchPlacesList

//    private var placesList: ArrayList<PlaceModel> = ArrayList()
//    fun getPlaces() {
//        viewModelScope.launch {
//            try {
//                performFetchPlacesList.value = Resource.loading()
//                val response = placeRepository.getPlaces()
//                if(response.code==1) {
//                    if (!response.data.isNullOrEmpty()) {
//                        placesList.clear()
//                        placesList.addAll(response.data)
//                        performFetchPlacesList.value = Resource.success(response)
//                    } else {
//                        if (response.data != null) {
//                            if (response.data.isEmpty()) {
//                                performFetchPlacesList.value = Resource.empty()
//                            }
//                        } else {
//                            performFetchPlacesList.value = Resource.error(null, message = "Something went wrong!")
//                        }
//                    }
//                }else{
//                    performFetchPlacesList.value = Resource.error(null, message = response.message)
//                }
//            } catch (e: Exception) {
//                println("fetch places list failed ${e.message}")
//                if (e is UnknownHostException) {
//                    performFetchPlacesList.value = Resource.offlineError()
//                } else {
//                    performFetchPlacesList.value = Resource.error(e)
//                }
//            }
//        }
//    }
//
//    fun searchPlace(query: String?) {
//        if(!query.isNullOrEmpty()) {
//            val queryPlaceList = placesList.filter {
//                it.name.toLowerCase().contains(query?.toLowerCase().toString())
//            }
//            performFetchPlacesList.value = Resource.success(Responsesd(1, queryPlaceList, ""))
//        }else{
//            performFetchPlacesList.value = Resource.success(Responsesd(1, placesList, ""))
//        }
//    }
//
//
//    //Update User Details
    private val performUpdate = MutableLiveData<Resource<UserUpdateResponse>>()
    val performUpdateStatus: MutableLiveData<Resource<UserUpdateResponse>>
        get() = performUpdate

    fun updateUserDetails(updateUserRequest: UpdateUserRequest) {
        viewModelScope.launch {
            try {
                performUpdate.value = Resource.loading()
                val response = userRepository.updateUser(updateUserRequest)
                if(response.isSuccessful) {
                    if (response.body()!=null) {
                        performUpdate.value = Resource.success(response.body()!!)
                    } else {
                        performUpdate.value = Resource.error(null, message = "Something went wrong")
                    }
                }else{
                    performUpdate.value = Resource.error(null, message = response.body()?.message)
                }
            } catch (e: Exception) {
                println("update user details failed ${e.message}")
                if (e is UnknownHostException) {
                    performUpdate.value = Resource.offlineError()
                } else {
                    performUpdate.value = Resource.error(e)
                }
            }
        }
    }
//
//    //Update User FCM token
//    private val performNotificationTokenUpdate = MutableLiveData<Resource<Responsesd<String>>>()
//    val performNotificationTokenUpdateStatus: LiveData<Resource<Responsesd<String>>>
//        get() = performNotificationTokenUpdate
//
//    fun updateFcmToken(notificationTokenUpdate: NotificationTokenUpdate) {
//        viewModelScope.launch {
//            try {
//                performNotificationTokenUpdate.value = Resource.loading()
//                val response = userRepository.updateFcmToken(notificationTokenUpdate)
//                if(response.code==1) {
//                    if (response.data!=null) {
//                        performNotificationTokenUpdate.value = Resource.success(response)
//                    } else {
//                        performNotificationTokenUpdate.value = Resource.error(null, message = "Something went wrong")
//                    }
//                }else{
//                    performNotificationTokenUpdate.value = Resource.error(null, message = response.message)
//                }
//            } catch (e: Exception) {
//                println("update fcm token failed ${e.message}")
//                if (e is UnknownHostException) {
//                    performNotificationTokenUpdate.value = Resource.offlineError()
//                } else {
//                    performNotificationTokenUpdate.value = Resource.error(e)
//                }
//            }
//        }
//    }



}