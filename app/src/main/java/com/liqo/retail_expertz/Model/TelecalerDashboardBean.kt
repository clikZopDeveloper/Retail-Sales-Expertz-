package com.liqo.retail_expertz.Model


import com.google.gson.annotations.SerializedName

data class TelecalerDashboardBean(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("error")
    val error: Boolean, // false
    @SerializedName("msg")
    val msg: String
) {
    data class Data(
        @SerializedName("customer_data")
        val customerData: List<CustomerData>,
        @SerializedName("dashboard")
        val dashboard: List<Dashboard>,
        @SerializedName("total_leads")
        val totalLeads: TotalLeads,
        @SerializedName("weekly_data")
        val weeklyData: List<WeeklyData>
    ) {
        data class CustomerData(
            @SerializedName("total_customer")
            val totalCustomer: Int, // 0
            @SerializedName("total_visitor")
            val totalVisitor: Int // 3
        )

        data class Dashboard(
            @SerializedName("status")
            val status: String, // New Lead
            @SerializedName("status_id")
            val statusId: Int, // 1
            @SerializedName("value")
            val value: String // 1
        )

        data class TotalLeads(
            @SerializedName("status")
            val status: String, // Total Leads
            @SerializedName("status_id")
            val statusId: Int, // 0
            @SerializedName("value")
            val value: Int // 3
        )

        data class WeeklyData(
            @SerializedName("date")
            val date: String, // 2024-02-21
            @SerializedName("total_customer")
            val totalCustomer: Int, // 0
            @SerializedName("total_visitor")
            val totalVisitor: Int // 3
        )
    }
}