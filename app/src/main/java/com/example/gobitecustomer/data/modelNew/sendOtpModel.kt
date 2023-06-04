package com.example.gobitecustomer.data.modelNew

data class sendOtpModel(
    val campaign_id: String,
    val data_coding: String,
    val from: String,
    val template_id: String,
    val to: List<String>,
    val type: String,
    val validity: String
)