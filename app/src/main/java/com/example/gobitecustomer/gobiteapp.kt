package com.example.gobitecustomer

import android.app.Application
import com.example.gobitecustomer.di.appModule
import com.example.gobitecustomer.di.networkModule
import com.example.gobitecustomer.di.viewModelModule
import com.example.gobitecustomer.utils.PicassoImageLoadingService
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class gobiteapp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@gobiteapp)
            modules(listOf(appModule, networkModule, viewModelModule))
        }
//        Slider.init(PicassoImageLoadingService(this))
    }

}