package com.liqo.retail_expertz.Model


import com.google.gson.annotations.SerializedName

data class ContactListBean(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("error")
    val error: Boolean, // false
    @SerializedName("msg")
    val msg: String
) {
    data class Data(
        @SerializedName("careated_at")
        val careatedAt: String, // 2024-02-19 17:45:40
        @SerializedName("designation")
        val designation: String, // desi
        @SerializedName("id")
        val id: Int, // 1
        @SerializedName("name")
        val name: String, // test
        @SerializedName("number")
        val number: String // 2020202020
    )
}