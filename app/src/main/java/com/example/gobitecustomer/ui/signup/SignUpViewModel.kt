package com.example.gobitecustomer.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gobitecustomer.data.local.Resource
import com.example.gobitecustomer.data.modelNew.SignUpRequestNew
import com.example.gobitecustomer.data.modelNew.SignupResult
import com.example.gobitecustomer.data.retrofit.UserRepository
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class SignUpViewModel(private val userRepository: UserRepository
//, private val placeRepository: PlaceRepository
                      ) : ViewModel() {

    //Fetch places list
    /* private val performFetchPlacesList = MutableLiveData<Resource<Responsesd<List<PlaceModel>>>>()
     val performFetchPlacesStatus: LiveData<Resource<Responsesd<List<PlaceModel>>>>
         get() = performFetchPlacesList

     private var placesList: ArrayList<PlaceModel> = ArrayList()
     fun getPlaces() {
         viewModelScope.launch {
             try {
                 performFetchPlacesList.value = Resource.loading()
                 val response = placeRepository.getPlaces()
                 if (response.code == 1) {
                     if (!response.data.isNullOrEmpty()) {
                         placesList.clear()
                         placesList.addAll(response.data)
                         performFetchPlacesList.value = Resource.success(response)
                     } else {
                         if (response.data != null) {
                             if (response.data.isEmpty()) {
                                 performFetchPlacesList.value = Resource.empty()
                             }
                         } else {
                             performFetchPlacesList.value = Resource.error(null, message = "Something went wrong!")
                         }
                     }
                 } else {
                     performFetchPlacesList.value = Resource.error(null, message = response.message)
                 }
             } catch (e: Exception) {
                 println("fetch places list failed ${e.message}")
                 if (e is UnknownHostException) {
                     performFetchPlacesList.value = Resource.offlineError()
                 } else {
                     performFetchPlacesList.value = Resource.error(e)
                 }
             }
         }
     }

     fun searchPlace(query: String?) {
         if(!query.isNullOrEmpty()) {
             val queryPlaceList = placesList.filter {
                 it.name.toLowerCase().contains(query?.toLowerCase().toString())
             }
             performFetchPlacesList.value = Resource.success(Responsesd(1, queryPlaceList, ""))
         }else{
             performFetchPlacesList.value = Resource.success(Responsesd(1, placesList, ""))
         }
     }*/

    /*private val performSignUp = MutableLiveData<Resource<Responsesd<String>>>()
    val performSignUpStatus: LiveData<Resource<Responsesd<String>>>
        get() = performSignUp

    fun signUp(updateUserRequest: UpdateUserRequest) {
        viewModelScope.launch {
            try {
                performSignUp.value = Resource.loading()
                val response = userRepository.updateUser(updateUserRequest)
                if (response.code == 1) {
                    if (response.data != null) {
                        performSignUp.value = Resource.success(response)
                    } else {
                        performSignUp.value = Resource.error(null, message = "Something went wrong")
                    }
                } else {
                    performSignUp.value = Resource.error(null, message = response.message)
                }
            } catch (e: Exception) {
                println("Sign Up failed ${e.message}")
                if (e is UnknownHostException) {
                    performSignUp.value = Resource.offlineError()
                } else {
                    performSignUp.value = Resource.error(e)
                }
            }
        }
    }*/

    //SIGN UP
    private val performSignUp = MutableLiveData<Resource<SignupResult>>()
    val performSignUpStatus: LiveData<Resource<SignupResult>>
        get() = performSignUp

    fun registerUser(signUpRequestNew: SignUpRequestNew) {
        viewModelScope.launch {
            try {
                performSignUp.value = Resource.loading()


                var response = userRepository.registerUser(signUpRequestNew)

                performSignUp.value = Resource.success(response.data!!)

            } catch (e: Exception) {
                println("login failed ${e.message}")
                if (e is UnknownHostException) {
                    performSignUp.value = Resource.offlineError()
                } else {
                    performSignUp.value = Resource.error(e)
                }
            }
        }
    }

}