package com.liqo.retail_expertz.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liqo.retail_expertz.Adapter.AllStatusAdapter
import com.liqo.retail_expertz.Adapter.CommonFieldDrawerAdapter
import com.liqo.retail_expertz.ApiHelper.ApiController
import com.liqo.retail_expertz.ApiHelper.ApiResponseListner
import com.liqo.retail_expertz.Model.*
import com.liqo.retail_expertz.R
import com.liqo.retail_expertz.Utills.*
import com.liqo.retail_expertz.databinding.ActivityMainBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.stpl.antimatter.Utils.ApiContants
import java.util.Locale

class DashboardActivity : AppCompatActivity(), ApiResponseListner , GoogleApiClient.OnConnectionFailedListener,
ConnectivityListener.ConnectivityReceiverListener {
    private lateinit var apiClient: ApiController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    lateinit var rcStatus: RecyclerView
    lateinit var rcMaster: RecyclerView
    lateinit var llMaster: LinearLayout
    lateinit var ivDownArrowMaster: ImageView
    var myReceiver: ConnectivityListener? = null
    private var currentLoc: String? = null
    private val permissionId = 2
    var list: List<Address>? = null
    var isActive = true
    var dayStatus = 5

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        //  val navView: NavigationView = binding.navView
        val navBottomView: BottomNavigationView = binding.appBarMain.bottomNavView
        myReceiver = ConnectivityListener()
        val headerView: View = binding.navView.getHeaderView(0)
        rcStatus = headerView.findViewById<RecyclerView>(R.id.rcStatus)
        rcMaster = headerView.findViewById<RecyclerView>(R.id.rcMaster)
        llMaster = headerView.findViewById<LinearLayout>(R.id.llMaster)
        ivDownArrowMaster = headerView.findViewById<ImageView>(R.id.ivDownArrowMaster)

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        binding.appBarMain.appbarLayout.ivMenu.setOnClickListener {
            drawerLayout.open()
        }

        handleRcMaster()
        getLocation()
     //   getLocation()
        if (PrefManager.getString(ApiContants.Role,"").equals("telecaller")){
        apiGetStatus()
        }

        if (SalesApp.isEnableScreenshort==true){
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
         startService(Intent(this, LocationService::class.java))
        //   binding.appBarMain.appbarLayout.switchDayStart="Day Start"
        binding.appBarMain.appbarLayout.ivLogout.setOnClickListener {
            apiCallLogout()
        }

        Log.d("token>>>>>", PrefManager.getString(ApiContants.AccessToken, ""))

        if (PrefManager.getString(ApiContants.dayStatus, "").equals("start") || dayStatus == 1) {
            binding.appBarMain.appbarLayout.switchDayStart.isChecked = true
            //    Toast.makeText(this@DashboardActivity, "rr", Toast.LENGTH_SHORT).show()
            binding.appBarMain.appbarLayout.switchDayStart.text = "Day Start"
        }
        else {
            //     Toast.makeText(this@DashboardActivity, "werwe", Toast.LENGTH_SHORT).show()
            binding.appBarMain.appbarLayout.switchDayStart.isChecked = false
            binding.appBarMain.appbarLayout.switchDayStart.text = "Day End"
        }

        binding.appBarMain.appbarLayout.switchDayStart.setOnCheckedChangeListener({ _, isChecked ->
            if (isChecked) {
                binding.appBarMain.appbarLayout.switchDayStart.text = "Day Start"
                getLocation()
                apiCallDayStatus(ApiContants.startDay)
                PrefManager.putString(ApiContants.dayStatus, "start")
            } else {
                binding.appBarMain.appbarLayout.switchDayStart.text = "Day End"
                getLocation()

                apiCallDayStatus(ApiContants.endDay)
                PrefManager.putString(ApiContants.dayStatus, "end")
            }
            /*  Toast.makeText(this@DashboardActivity, message.toString(),
                  Toast.LENGTH_SHORT).show()*/
        })


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        /* appBarConfiguration = AppBarConfiguration(
             setOf(
                 R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
             ), drawerLayout
         )*/


        //  setupActionBarWithNavController(navController, appBarConfiguration)
        navBottomView.setupWithNavController(navController)

        llMaster.setOnClickListener(View.OnClickListener {
            if (isActive) {
                isActive = false
                ivDownArrowMaster.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                rcMaster.setVisibility(View.VISIBLE)

            } else {
                isActive = true
                ivDownArrowMaster.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                rcMaster.setVisibility(View.GONE)

            }
        })

    }

    fun apiCallDayStatus(dayStatus: String) {
        SalesApp.isAddAccessToken = true
        apiClient = ApiController(this, this)
        val params = Utility.getParmMap()
        params["last_location"] = "${list?.get(0)?.latitude},${list?.get(0)?.longitude}"
        apiClient.progressView.showLoader()
        apiClient.getApiPostCall(dayStatus, params)

    }
    fun apiGetStatus() {
        SalesApp.isAddAccessToken = true
        apiClient = ApiController(this, this)
        val params = Utility.getParmMap()
        apiClient.progressView.showLoader()
        apiClient.getApiPostCall(ApiContants.GetStatus, params)

    }

    fun apiCallLogout() {
        SalesApp.isAddAccessToken = true
        apiClient = ApiController(this, this)
        val params = Utility.getParmMap()
        params["last_location"] = "${list?.get(0)?.latitude},${list?.get(0)?.longitude}"
        apiClient.progressView.showLoader()
        apiClient.getApiPostCall(ApiContants.logout, params)

    }

    override fun success(tag: String?, jsonElement: JsonElement) {
        try {
            apiClient.progressView.hideLoader()
            if (tag == ApiContants.logout) {
                val baseResponseBean = apiClient.getConvertIntoModel<BaseResponseBean>(
                    jsonElement.toString(),
                    BaseResponseBean::class.java
                )
                Toast.makeText(this, baseResponseBean.msg, Toast.LENGTH_SHORT).show()
                PrefManager.clear()
                GeneralUtilities.launchActivity(this, LoginActivity::class.java)
                finishAffinity()
            }
            if (tag == ApiContants.startDay) {
                val dayStatusBean = apiClient.getConvertIntoModel<StartDayBean>(
                    jsonElement.toString(),
                    StartDayBean::class.java
                )
                if (dayStatusBean.error == false) {
                    Utility.showSnackBar(this, dayStatusBean.msg)
                }
            }

            if (tag == ApiContants.GetStatus) {
                val allStatusBean = apiClient.getConvertIntoModel<GetAllStatusBean>(
                    jsonElement.toString(),
                    GetAllStatusBean::class.java
                )
          /*      if (allStatusBean.error == false) {
                    SalesApp.allStatusList.clear()
                    SalesApp.allStatusList.addAll(allStatusBean.data)
                }*/
                if (allStatusBean.error==false) {
                //    dayStatus = allStatusBean.dayStatus
                   handleRcStatus(allStatusBean.data)
                }
            }

            if (tag == ApiContants.endDay) {
                val dayStatusBean = apiClient.getConvertIntoModel<EndDayBean>(
                    jsonElement.toString(),
                    EndDayBean::class.java
                )
                if (dayStatusBean.error == false) {
                    Utility.showSnackBar(this, dayStatusBean.msg)
                }
            }


        } catch (e: Exception) {
            Log.d("error>>", e.localizedMessage)
        }

    }

    override fun failure(tag: String?, errorMessage: String) {
        apiClient.progressView.hideLoader()
        // Toast.makeText(this, "4", Toast.LENGTH_SHORT).show()
        Utility.showSnackBar(this, errorMessage)
    }

    fun handleRcStatus(data: List<GetAllStatusBean.Data>) {
        rcStatus.layoutManager = LinearLayoutManager(this)
        var mAdapter = AllStatusAdapter(this, data, object :
            RvStatusClickListner {
            override fun clickPos(status: String, id: Int) {
                startActivity(
                    Intent(
                        this@DashboardActivity,
                        CustTelecallerActivity::class.java
                    ).putExtra("status_id", id.toString()).putExtra("status_name", status)
                )

                binding.drawerLayout.closeDrawers()
            }
        })
        rcStatus.adapter = mAdapter
        // rvMyAcFiled.isNestedScrollingEnabled = false

    }
    fun handleRcMaster() {
        rcMaster.layoutManager = LinearLayoutManager(this)
        var mAdapter = CommonFieldDrawerAdapter(this, getMaster(), object :
            RvClickListner {
            override fun clickPos(pos: Int) {
                if (pos == 0) {
                    binding.drawerLayout.closeDrawers()
                }else if (pos == 1) {
                    startActivity(
                        Intent(
                            this@DashboardActivity,
                            AddCustomerActivity::class.java
                        ).putExtra("way","AddCustomer")
                    )
                } else if (pos == 2) {
                    startActivity(
                        Intent(
                            this@DashboardActivity,
                            AllCustomerTypeActivity::class.java
                        ).putExtra("customerType","customer")
                    )
                } else if (pos == 3) {
                    startActivity(
                        Intent(
                            this@DashboardActivity,
                            AllCustomerTypeActivity::class.java
                        ).putExtra("customerType","visitor")
                    )

                }  else if (pos == 4) {
                    startActivity(
                        Intent(
                            this@DashboardActivity,
                            ContactListActivity::class.java
                        )
                    )
                } else if (pos == 5) {
                    startActivity(
                        Intent(
                            this@DashboardActivity,
                            CustTelecallerActivity::class.java
                        )/*.putExtra("status_id",)*/
                    )

                } else if (pos == 6) {
                    apiCallLogout()
                }
                binding.drawerLayout.closeDrawers()
            }

        })
        rcMaster.adapter = mAdapter
        // rvMyAcFiled.isNestedScrollingEnabled = false

    }

    private fun getMaster(): ArrayList<MenuModelBean> {
        var menuList = ArrayList<MenuModelBean>()
        if (PrefManager.getString(ApiContants.Role,"").equals("salesman")){
            menuList.add(MenuModelBean(0, "Dashboard", "", R.drawable.ic_dashbord))
            menuList.add(MenuModelBean(1, "Add Customer/Visitor", "", R.drawable.ic_dashbord))
            menuList.add(MenuModelBean(2, "Customer", "", R.drawable.ic_dashbord))
            menuList.add(MenuModelBean(3, "Visitor", "", R.drawable.ic_dashbord))
            menuList.add(MenuModelBean(4, "Team Contact", "", R.drawable.ic_dashbord))
        }else{
      //      menuList.add(MenuModelBean(5, "Customer Telecaller", "", R.drawable.ic_dashbord))
            menuList.add(MenuModelBean(4, "Team Contact", "", R.drawable.ic_dashbord))
        }
        menuList.add(MenuModelBean(6, "Logout", "", R.drawable.ic_dashbord))


        return menuList
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        list =

                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        Log.d("zxxzv", "Lat" + Gson().toJson(list?.get(0)?.latitude))
                        Log.d("zxxzv", "Long" + Gson().toJson(list?.get(0)?.longitude))
                        Log.d("zxxzv", Gson().toJson(list?.get(0)?.countryName))
                        Log.d("zxxzv", Gson().toJson(list?.get(0)?.locality))
                        Log.d("zxxzv", Gson().toJson(list?.get(0)?.getAddressLine(0)))

                        currentLoc = list?.get(0)?.getAddressLine(0)
                        /*    mainBinding.apply {
                                tvLatitude.text = "Latitude\n${list[0].latitude}"
                                tvLongitude.text = "Longitude\n${list[0].longitude}"
                                tvCountryName.text = "Country Name\n${list[0].countryName}"
                                tvLocality.text = "Locality\n${list[0].locality}"
                                tvAddress.text = "Address\n${list[0].getAddressLine(0)}"
                            }*/
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        } else {
            //  checkPermissions()
        }
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            return true
        }
        return false
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onPause() {
        super.onPause()
        GeneralUtilities.unregisterBroadCastReceiver(this, myReceiver)
    }

    override fun onResume() {
        GeneralUtilities.registerBroadCastReceiver(this, myReceiver)
        SalesApp.setConnectivityListener(this)
        super.onResume()
    }

    override fun onNetworkConnectionChange(isconnected: Boolean) {
        ApiContants.isconnectedtonetwork = isconnected
        GeneralUtilities.internetConnectivityAction(this, isconnected)
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {}

    override fun onDestroy() {
        super.onDestroy()
        // Start the LocationService when the app is closed
        //  startService(Intent(this, LocationService::class.java))
    }
}