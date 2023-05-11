package com.example.gobitecustomer.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

object EventBus {
    val flow = MutableSharedFlow<Any>(replay = 1)

    fun send(event: Any) {
        GlobalScope.launch(Dispatchers.IO) {
            flow.emit(event)
        }
    }

    inline fun <reified T> asFlow(): Flow<T> {
        return flow.filterIsInstance<T>()
    }
}