package com.example.gobitecustomer.utils

import com.example.gobitecustomer.utils.AppConstants.ORDER_STATUS_PLACED
import com.example.gobitecustomer.utils.AppConstants.ORDER_STATUS_BEING_PREPARED
import com.example.gobitecustomer.utils.AppConstants.ORDER_STATUS_CANCELLED
import com.example.gobitecustomer.utils.AppConstants.ORDER_STATUS_COMPLETED
import com.example.gobitecustomer.utils.AppConstants.ORDER_STATUS_CREATED
import com.example.gobitecustomer.utils.AppConstants.ORDER_STATUS_PREPARED

object StatusHelper{

    fun getMessageDetail(status: String?): String{
        return when(status){
            "Pending" -> getStatusDetailedMessage(ORDER_STATUS_CREATED)
            "Cancelled"  -> getStatusDetailedMessage(ORDER_STATUS_CANCELLED)
            "Placed"  -> getStatusDetailedMessage(ORDER_STATUS_PLACED)
            "Prepared"  -> getStatusDetailedMessage(ORDER_STATUS_PREPARED)
            "Completed"  -> getStatusDetailedMessage(ORDER_STATUS_COMPLETED)
            "Being Prepared"  -> getStatusDetailedMessage(ORDER_STATUS_BEING_PREPARED)
            else -> status.toString()
        }
    }
    fun getStatusMessage(status: String?): String{
        return when(status){
            ORDER_STATUS_CREATED -> "Pending"
            ORDER_STATUS_PLACED -> "Placed"
            ORDER_STATUS_CANCELLED -> "Cancelled"
            ORDER_STATUS_PREPARED -> "Prepared"
            ORDER_STATUS_COMPLETED -> "Completed"
            ORDER_STATUS_BEING_PREPARED -> "Being Prepared"
            else -> status.toString()
        }
    }
    fun getStatusDetailedMessage(status: String): String{
        return when(status){
            ORDER_STATUS_CREATED -> "Transaction pending. Bank is still processing your transaction"
            ORDER_STATUS_BEING_PREPARED -> "Order Accepted. Restaurant is preparing your food"
            ORDER_STATUS_PLACED -> "Order has been successfully placed. Waiting for restaurant response"
            ORDER_STATUS_CANCELLED -> "Order Cancelled"
            ORDER_STATUS_PREPARED -> "Your order is ready. Collect your food and scratch card."
            ORDER_STATUS_COMPLETED -> "Order completed"
            else -> status
        }
    }
}