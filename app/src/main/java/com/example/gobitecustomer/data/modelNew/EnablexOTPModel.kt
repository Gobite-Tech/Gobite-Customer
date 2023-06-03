package com.example.gobitecustomer.data.modelNew

data class EnablexOTPModel(
    val campaign_id: String,
    val `data`: DataXXXX,
    val data_coding: String,
    val from: String,
    val template_id: String,
    val to: List<String>,
    val type: String,
    val validity : String
)