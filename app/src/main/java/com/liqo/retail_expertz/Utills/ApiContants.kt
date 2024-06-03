package com.stpl.antimatter.Utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.fragment.app.FragmentActivity

public class ApiContants {
    companion object {
        var isconnectedtonetwork = false


        const val BaseUrl="https://retail.salesexpertz.in/api/"//Live URL
     //   const val BaseUrl="https://liqo.ecommercekart.in/api/"//Test URL
        const val ImgBaseUrl="https://liqo.ecommercekart.in/api/"//Image Base URL

        const val EmailAddress = "****************"
        const val REQ_CODE_VERSION_UPDATE = 530
        const val PlaceLocation = "location"
        const val mobileNumber = "mobileNumber"
        const val password = "password"
        const val PlaceRegion = "locationCountry"
        const val PlaceLatLang = "locLatLang"
        const val PlaceLat = "locLat"
        const val PlaceLang = "PlaceLang"
        val WhatsAppNumber = "**********"
        const val PREF_IS_METRIC = "unit"
        const val UserDetails = "userDetails"
        const val UserAvailableAmt = "useravailablebal"
        const val DeviceToken = "321"
        const val AccessToken = "accessToken"
        const val Role = "role"
        const val Type = "android"
        const val currency = "₹"
        const val dayStatus = "dayStatus"



        const val success = "success"
        const val failure = "failure"
        const val NoInternetConnection = "Please check your internet connection"

        //        api Tags
        const val login = "login-salesman"
        const val logout = "logout"
        const val startDay = "start-day"
        const val endDay = "end-day"
        const val AddCustomer = "add-customer"
        const val getComplaints = "get-complaints"
        const val getProfile = "get-profile"
        const val getCity = "get-city"
        const val getState = "get-state"
        const val getSource = "get-source"
        const val GetStatus = "get-status"
        const val GetAttendance = "get-attendance"
        const val getCategory = "get-category"
        const val getSubCategory = "get-sub-category"
        const val getPasswordChange = "password-change"
        const val getCustomer = "get-customers"
        const val getUpdateAllocateRequest = "update-allocation-request"
        const val getUpdateComplaint = "update-complaint"
        const val getLocationUpdate = "update-location"
        const val getCustomerData = "get-customer-data"
        const val getUpdateCustomer = "update-customer"
        const val getCustTelecaller = "get-customers-telecaller"
        const val getTeamContact = "get-team-contacts"
        const val getDashboardSalesman = "dashboard-salesman"
        const val getDashboardTelecaller = "dashboard-telecaller"
        const val getCustomerMobList = "get-customers-list"
        const val getUpdateStatus = "update-status"
        const val getConvertInterestedToPurchased = "convert-interested-to-purchased"
        const val getUpdateCustomerCategory = "update-customer-category"



        fun callPGURL(context: Context, url: String) {
            Log.d("weburl", url)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setPackage("com.android.chrome")
            try {
                context.startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                // Chrome browser presumably not installed so allow user to choose instead
                intent.setPackage(null)
                context.startActivity(intent)
            }
        }
    }
}