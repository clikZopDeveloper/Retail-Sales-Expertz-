package com.liqo.retail_expertz.Model


import com.google.gson.annotations.SerializedName

data class ProfileNameBean(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("error")
    val error: Boolean, // false
    @SerializedName("msg")
    val msg: String
) {
    data class Data(
        @SerializedName("created_at")
        val createdAt: String, // 2024-01-25 16:59:10
        @SerializedName("id")
        val id: Int, // 1
        @SerializedName("name")
        val name: String // profile color
    )
}