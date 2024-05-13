package com.liqo.retail_expertz.Model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CustomerListBean(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("error")
    val error: Boolean, // false
    @SerializedName("msg")
    val msg: String
): Serializable {
    data class Data(
        @SerializedName("address")
        val address: String, // address
        @SerializedName("allocated_date")
        val allocatedDate: String, // 2024-02-10
        @SerializedName("city")
        val city: String, // Patiala
        @SerializedName("converted")
        val converted: Int, // 0
        @SerializedName("converted_date")
        val convertedDate: String, // 0000-00-00
        @SerializedName("created_at")
        val createdAt: String, // 2024-02-10 16:09:01
        @SerializedName("customer_type")
        val customerType: String, // visitor
        @SerializedName("doa")
        val doa: String, // 0000-00-00
        @SerializedName("dob")
        val dob: String, // 0000-00-00
        @SerializedName("email")
        val email: String, // vaibhav@gmail.com
        @SerializedName("id")
        val id: Int, // 7
        @SerializedName("is_allocated")
        val isAllocated: Int, // 1
        @SerializedName("name")
        val name: String, // vaibhav
        @SerializedName("phone")
        val phone: String, // 9675559235
        @SerializedName("remarks")
        val remarks: String, // null
        @SerializedName("remind_date")
        val remindDate: Any, // null
        @SerializedName("remind_time")
        val remindTime: Any, // null
        @SerializedName("source")
        val source: String, // Facebook
        @SerializedName("staff_id")
        val staffId: Int, // 3
        @SerializedName("state")
        val state: String, // Punjab
        @SerializedName("status")
        val status: Int, // 1
        @SerializedName("updated_at")
        val updatedAt: String, // 2024-02-10 17:33:48
        @SerializedName("user_id")
        val userId: Int, // 2
        @SerializedName("whatsapp")
        val whatsapp: String // 9675539898
    ): Serializable
}