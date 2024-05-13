package com.liqo.retail_expertz.Model


import com.google.gson.annotations.SerializedName

data class SubCatBean(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("error")
    val error: Boolean, // false
    @SerializedName("msg")
    val msg: String
) {
    data class Data(
        @SerializedName("careated_at")
        val careatedAt: String, // 2024-02-24 12:57:38
        @SerializedName("category_id")
        val categoryId: Int, // 1
        @SerializedName("id")
        val id: Int, // 1
        @SerializedName("name")
        val name: String // 1 Ton
    )
}