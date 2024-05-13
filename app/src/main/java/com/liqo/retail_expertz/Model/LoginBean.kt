package com.liqo.retail_expertz.Model


import com.google.gson.annotations.SerializedName

data class LoginBean(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("error")
    val error: Boolean, // false
    @SerializedName("msg")
    val msg: String // User logged in successfully.
) {
    data class Data(
        @SerializedName("address")
        val address: Any, // null
        @SerializedName("area_id")
        val areaId: Any, // null
        @SerializedName("city")
        val city: Any, // null
        @SerializedName("created_at")
        val createdAt: String, // 2024-02-09 12:55:55
        @SerializedName("email")
        val email: String, // vaibhav@clikzop.com
        @SerializedName("fcm_token")
        val fcmToken: Any, // null
        @SerializedName("firebase_token")
        val firebaseToken: Any, // null
        @SerializedName("id")
        val id: Int, // 3
        @SerializedName("is_active")
        val isActive: Int, // 1
        @SerializedName("joining_date")
        val joiningDate: Any, // null
        @SerializedName("last_ip")
        val lastIp: Any, // null
        @SerializedName("last_location")
        val lastLocation: Any, // null
        @SerializedName("last_login")
        val lastLogin: String, // 2024-02-16 15:21:07
        @SerializedName("location_time")
        val locationTime: Any, // null
        @SerializedName("name")
        val name: String, // vaibhav
        @SerializedName("parent_id")
        val parentId: Any, // null
        @SerializedName("password")
        val password: String, // 123456
        @SerializedName("phone")
        val phone: String, // 8888888888
        @SerializedName("role")
        val role: String, // salesman
        @SerializedName("salary")
        val salary: Any, // null
        @SerializedName("sm_id")
        val smId: Any, // null
        @SerializedName("state")
        val state: Any, // null
        @SerializedName("store_id")
        val storeId: Int, // 1
        @SerializedName("token")
        val token: String, // 8dvRPKZ0GpXz
        @SerializedName("updated_at")
        val updatedAt: String, // 2024-02-16 15:21:07
        @SerializedName("whatsapp")
        val whatsapp: String // 65455564654
    )
}