package com.example.gobitecustomer.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

object EventBus {
    @ExperimentalCoroutinesApi
    val bus: BroadcastChannel<Any> = BroadcastChannel(1)

    @ExperimentalCoroutinesApi
    fun send(o: Any) {
        CoroutineScope(Dispatchers.IO).launch {
            bus.send(o)
        }
    }

//    @ExperimentalCoroutinesApi
//    inline fun <reified T> asChannel(): ReceiveChannel<T> {
//        return bus.openSubscription().filter { it is T }.map { it as T }
//    }
}