package com.example.gobitecustomer.data.modelNew

data class sendOtpModel(
    val campaign_id: String,
    val data_coding: String,
    val flash_message: Boolean,
    val from: String,
//    val recipient: List<Recipient>,
    val template_id: String,
    val to: List<String>,
    val type: String,
    val type_details: String
)