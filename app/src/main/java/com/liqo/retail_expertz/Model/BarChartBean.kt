package com.liqo.retail_expertz.Model


import com.google.gson.annotations.SerializedName

data class BarChartBean(

    @SerializedName("total_customer")
    val totalCustomer: Int, // 0
    @SerializedName("total_visitor")
    val totalVisitor: Int // 1
) {

}