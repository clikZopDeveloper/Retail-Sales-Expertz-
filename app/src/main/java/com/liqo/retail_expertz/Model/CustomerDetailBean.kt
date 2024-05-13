package com.liqo.retail_expertz.Model


import com.google.gson.annotations.SerializedName

data class CustomerDetailBean(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("error")
    val error: Boolean, // false
    @SerializedName("msg")
    val msg: String
) {
    data class Data(
        @SerializedName("customer_interested_category")
        val customerInterestedCategory: List<CustomerInterestedCategory>,
        @SerializedName("customer_purchased_category")
        val customerPurchasedCategory: List<CustomerPurchasedCategory>,
        @SerializedName("customers")
        val customers: Customers,
        @SerializedName("lead_comments")
        val leadComments: List<LeadComment>,
        @SerializedName("lead_history")
        val leadHistory: List<LeadHistory>,
    ) {
        data class CustomerInterestedCategory(
            @SerializedName("id")
            val id: Int, // 1
            @SerializedName("interested_status")
            val interestedStatus: Int, // 0
            @SerializedName("name")
            val name: String // AC - 1 Ton
        )

        data class CustomerPurchasedCategory(
            @SerializedName("id")
            val id: Int, // 1
            @SerializedName("name")
            val name: String, // AC - 1 Ton
            @SerializedName("purchased_status")
            val purchasedStatus: Int, // 0

        )

        data class Customers(
            @SerializedName("address")
            val address: String, // fsdfsd
            @SerializedName("allocated_date")
            val allocatedDate: Any, // null
            @SerializedName("city")
            val city: String, // East Kameng
            @SerializedName("converted")
            val converted: Int, // 0
            @SerializedName("converted_date")
            val convertedDate: String, // 0000-00-00
            @SerializedName("created_at")
            val createdAt: String, // 2024-03-20 13:47:16
            @SerializedName("customer_type")
            val customerType: String, // visitor
            @SerializedName("doa")
            val doa: String, // 2024-03-30
            @SerializedName("dob")
            val dob: String, // 2024-03-28
            @SerializedName("email")
            val email: String, // test@gmail.com
            @SerializedName("id")
            val id: Int, // 274
            @SerializedName("is_allocated")
            val isAllocated: Int, // 0
            @SerializedName("name")
            val name: String, // test
            @SerializedName("phone")
            val phone: String, // 9876543222
            @SerializedName("remarks")
            val remarks: String, // null
            @SerializedName("remind_date")
            val remindDate: Any, // null
            @SerializedName("remind_time")
            val remindTime: Any, // null
            @SerializedName("source")
            val source: String, // Youtube
            @SerializedName("staff_id")
            val staffId: Int, // 3
            @SerializedName("state")
            val state: String, // Arunachal Pradesh
            @SerializedName("status")
            val status: Int, // 1
            @SerializedName("updated_at")
            val updatedAt: Any, // null
            @SerializedName("user_id")
            val userId: Int, // 3
            @SerializedName("whatsapp")
            val whatsapp: String // 7878754654
        )

        data class LeadComment(
            @SerializedName("created_at")
            val createdAt: String, // 2024-03-20 13:47:16
            @SerializedName("id")
            val id: Int, // 265
            @SerializedName("lead_id")
            val leadId: Int, // 274
            @SerializedName("remarks")
            val remarks: String, // New Lead
            @SerializedName("remind_date")
            val remindDate: String, // 0000-00-00
            @SerializedName("remind_time")
            val remindTime: String, // 00:00:00
            @SerializedName("status")
            val status: String, // New Lead
            @SerializedName("updated_at")
            val updatedAt: Any, // null
            @SerializedName("user_id")
            val userId: Int // 3
        )

        data class LeadHistory(
            @SerializedName("interested_category")
            val interestedCategory: String,
            @SerializedName("store_name")
            val storeName: String // Panchkula
        )
    }
}