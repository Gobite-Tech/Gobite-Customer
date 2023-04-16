package com.example.gobitecustomer.di

import com.example.gobitecustomer.data.local.PreferencesHelper
import com.google.gson.Gson
import org.koin.dsl.module

val appModule = module {
    single {
        Gson()
    }

    single {
        PreferencesHelper(get())
    }
}