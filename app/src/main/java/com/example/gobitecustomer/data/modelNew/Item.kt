package com.example.gobitecustomer.data.modelNew

import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("category")
    val category: String,
    @SerializedName("cover_photos")
    val cover_photos: List<String>,
    @SerializedName("created_at")
    val created_at: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("filterable_fields")
    val filterable_fields: List<Any>,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("item_type")
    val item_type: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("updated_at")
    val updated_at: String,
    @SerializedName("variants")
    val variants: ArrayList<variantsItem>,

    var shop_id : Int,
    var quantity: Int = 0
)