package com.example.gobitecustomer.di

import com.example.gobitecustomer.ui.cart.CartViewModel
import com.example.gobitecustomer.ui.home.HomeViewModel
import com.example.gobitecustomer.ui.login.LoginViewModel
import com.example.gobitecustomer.ui.payment.PaymentViewModel
import com.example.gobitecustomer.ui.placeorder.PlaceOrderViewModel
import com.example.gobitecustomer.ui.profile.ProfileViewModel
import com.example.gobitecustomer.ui.restaurant.RestaurantViewModel
import com.example.gobitecustomer.ui.signup.SignUpViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { LoginViewModel(get()) }
//    viewModel { OtpViewModel(get()) }
    viewModel { RestaurantViewModel(get()) }
    viewModel { SignUpViewModel(get()
//        , get()
    ) }
    viewModel { ProfileViewModel(get())
        //, get()
        //,get())
    }
//    viewModel { SearchViewModel(get(), get()) }
//    viewModel { OrderViewModel(get()) }
    viewModel { CartViewModel(get()) }
    viewModel { PlaceOrderViewModel(get()) }
    viewModel { PaymentViewModel(get()) }
//    viewModel { ContributorViewModel() }
}