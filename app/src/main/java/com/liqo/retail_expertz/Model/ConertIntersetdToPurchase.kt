package com.liqo.retail_expertz.Model


import com.google.gson.annotations.SerializedName

data class ConertIntersetdToPurchase(
    @SerializedName("data")
    val `data`: Any, // null
    @SerializedName("error")
    val error: Boolean, // false
    @SerializedName("msg")
    val msg: String // Save Successfully
)