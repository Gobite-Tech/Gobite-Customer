package com.example.gobitecustomer.di

import androidx.databinding.library.BuildConfig
import com.example.gobitecustomer.data.retrofit.AuthInterceptor
import com.example.gobitecustomer.data.retrofit.ShopRepository
import com.example.gobitecustomer.data.retrofit.UserRepository
import com.example.gobitecustomer.utils.AppConstants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.scope.get
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
//    single { AuthInterceptor(get(),get()) }
    single { provideRetrofit(get()) }
    single { UserRepository(get()) }
    single { ShopRepository(get()) }
//    single { PlaceRepository(get()) }
//    single { OrderRepository(get()) }
//    single { ItemRepository(get()) }
}

fun provideRetrofit(
    authInterceptor: AuthInterceptor
): Retrofit {
    return Retrofit.Builder()
        .baseUrl(AppConstants.BASE_URL)
//        .client(provideOkHttpClient(authInterceptor))
        .addConverterFactory(GsonConverterFactory.create()).build()
}

fun provideOkHttpClient(
//    authInterceptor: AuthInterceptor
): OkHttpClient {
    val builder = OkHttpClient()
        .newBuilder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
//        .addInterceptor(authInterceptor)

    if (BuildConfig.DEBUG) {
        val requestInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addNetworkInterceptor(requestInterceptor)
    }
    return builder.build()
}