package com.example.gobitecustomer.di

import com.example.gobitecustomer.data.retrofit.AuthInterceptor
import com.example.gobitecustomer.data.retrofit.BasicInterceptor
import com.example.gobitecustomer.data.retrofit.ItemRepository
import com.example.gobitecustomer.data.retrofit.OrderRepository
import com.example.gobitecustomer.data.retrofit.PlaceRepository
import com.example.gobitecustomer.data.retrofit.ShopRepository
import com.example.gobitecustomer.data.retrofit.UserRepository
import com.example.gobitecustomer.utils.AppConstants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single { AuthInterceptor(get(),get()) }
    single { BasicInterceptor()}
    single { provideRetrofit(get(),get()) }
    single { UserRepository(get()) }
    single { ShopRepository(get()) }
    single { PlaceRepository(get()) }
    single { OrderRepository(get()) }
    single { ItemRepository(get()) }
}

fun provideRetrofit(
    authInterceptor: AuthInterceptor,
    BasicInterceptor: BasicInterceptor
): Retrofit {
    return Retrofit.Builder()
        .baseUrl(AppConstants.CUSTOM_BASE_URL)
        .client(provideOkHttpClient(authInterceptor,BasicInterceptor))
        .addConverterFactory(GsonConverterFactory.create()).build()
}

fun provideOkHttpClient(
    authInterceptor: AuthInterceptor,
    BasicInterceptor: BasicInterceptor
): OkHttpClient {
    val builder = OkHttpClient()
        .newBuilder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(authInterceptor)
        .addInterceptor(BasicInterceptor)

        val requestInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    return builder.build()
}
