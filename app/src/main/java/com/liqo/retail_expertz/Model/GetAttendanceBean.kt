package com.liqo.retail_expertz.Model


import com.google.gson.annotations.SerializedName

data class GetAttendanceBean(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("error")
    val error: Boolean, // true
    @SerializedName("msg")
    val msg: String
) {
    data class Data(
        @SerializedName("day_status")
        val dayStatus: Boolean // false
    )
}