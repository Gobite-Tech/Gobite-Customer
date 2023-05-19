package com.example.gobitecustomer.utils

import com.google.firebase.messaging.FirebaseMessaging

object FcmUtils {
    fun subscribeToTopic(topic: String){
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
    }

    fun unsubscribeFromTopic(topic: String){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
    }
}