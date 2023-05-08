package com.example.gobitecustomer.utils

import com.example.gobitecustomer.utils.AppConstants.ORDER_STATUS_ACCEPTED
import com.example.gobitecustomer.utils.AppConstants.ORDER_STATUS_BEING_PREPARED
import com.example.gobitecustomer.utils.AppConstants.ORDER_STATUS_CANCELLED
import com.example.gobitecustomer.utils.AppConstants.ORDER_STATUS_CREATED
import com.example.gobitecustomer.utils.AppConstants.ORDER_STATUS_PREPARED

object StatusHelper{

    fun getMessageDetail(status: String?): String{
        return when(status){
            "Pending" -> getStatusDetailedMessage(ORDER_STATUS_CREATED)
            "Transaction failed"  -> getStatusDetailedMessage(ORDER_STATUS_CANCELLED)
            "Placed"  -> getStatusDetailedMessage(ORDER_STATUS_ACCEPTED)
            "Cancelled by you"  -> getStatusDetailedMessage(ORDER_STATUS_CANCELLED)
            "Accepted"  -> getStatusDetailedMessage(ORDER_STATUS_ACCEPTED)
            "Cancelled by shop"  -> getStatusDetailedMessage(ORDER_STATUS_CANCELLED)
            "Ready"  -> getStatusDetailedMessage(ORDER_STATUS_CANCELLED)
//            "Out for delivery"  -> getStatusDetailedMessage(ORDER_STATUS_OUT_FOR_DELIVERY)
//            "Completed"  -> getStatusDetailedMessage(ORDER_STATUS_COMPLETED)
//            "Delivered"  -> getStatusDetailedMessage(ORDER_STATUS_DELIVERED)
//            "Refund initiated"  -> getStatusDetailedMessage(ORDER_STATUS_REFUND_INITIATED)
//            "Refunded"  -> getStatusDetailedMessage(ORDER_STATUS_REFUND_COMPLETED)
            else -> status.toString()
        }
    }
    fun getStatusMessage(status: String?): String{
        return when(status){
            ORDER_STATUS_CREATED -> "Pending"
            ORDER_STATUS_ACCEPTED -> "Placed"
            ORDER_STATUS_CANCELLED -> "Cancelled by you"
            ORDER_STATUS_ACCEPTED -> "Accepted"
            ORDER_STATUS_CANCELLED -> "Cancelled by shop"
            ORDER_STATUS_PREPARED -> "Ready"
            else -> status.toString()
        }
    }
    fun getStatusDetailedMessage(status: String): String{
        return when(status){
            ORDER_STATUS_CREATED -> "Transaction pending. Bank is still processing your transaction"
            ORDER_STATUS_BEING_PREPARED -> "Order Accepted. Restaurant is preparing your food"
            ORDER_STATUS_ACCEPTED -> "Order has been successfully placed. Waiting for restaurant response"
            ORDER_STATUS_CANCELLED -> "Order Cancelled"
            ORDER_STATUS_PREPARED -> "Your order is ready."
            else -> status
        }
    }
}