package com.liqo.retail_expertz.Fragment.home

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.liqo.kktext.Model.StateBean
import com.liqo.retail_expertz.Activity.*
import com.liqo.retail_expertz.Adapter.DashboardAdapter
import com.liqo.retail_expertz.Adapter.TelecallerAdapter
import com.liqo.retail_expertz.ApiHelper.ApiController
import com.liqo.retail_expertz.ApiHelper.ApiResponseListner
import com.liqo.retail_expertz.Model.*
import com.liqo.retail_expertz.R
import com.liqo.retail_expertz.Utills.*
import com.liqo.retail_expertz.databinding.FragmentHomeBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.stpl.antimatter.Utils.ApiContants


class HomeFragment : Fragment(), ApiResponseListner {
    private lateinit var apiClient: ApiController
    private var _binding: FragmentHomeBinding? = null

    var pendingComplaint = ""
    var completedComplaint = ""
    var rejectedComplaint = ""
    var processingComplaint = ""

    private val binding get() = _binding!!

    lateinit var barData: BarData
    var barDataSet: BarDataSet? = null
    var barEntriesList: ArrayList<BarEntry>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding.refreshLayout.setOnRefreshListener {
            //  apiGetDashboard()
            apiAllGet()
            binding.refreshLayout.isRefreshing = false
        }

        apiAllGet()

        if (PrefManager.getString(ApiContants.Role, "").equals("telecaller")) {
            binding.rcTelecaer.visibility = View.VISIBLE
            binding.cardTottalLead.visibility = View.VISIBLE
            binding.fbAddArchitect.visibility = View.GONE
        } else {
            binding.rcTelecaer.visibility = View.GONE
            binding.cardTottalLead.visibility = View.GONE
            binding.fbAddArchitect.visibility = View.VISIBLE
        }

       /* binding.fbAddArchitect.setOnClickListener {
            //  callPGURL("https://atulautomotive.online/architect-signup")

        }*/

        binding.fbAddArchitect.setOnClickListener {
            startActivity(
                Intent(
                    requireActivity(),
                    AddCustomerActivity::class.java
                ).putExtra("way", "AddCustomer")
            )
        }

        //    val textView: TextView = binding.textHome
        /*   homeViewModel.text.observe(viewLifecycleOwner) {
               textView.text = it
           }*/

        return root
    }

    fun barChart() {
        barDataSet = BarDataSet(barEntriesList, "Weekly Data")
        // on below line we are initializing our bar data
        barData = BarData(barDataSet)
        // on below line we are setting data to our bar chart
        binding.idBarChart.data = barData
        // on below line we are setting colors for our bar chart text
        barDataSet?.valueTextColor = Color.BLACK
        // on below line we are setting color for our bar data set
        barDataSet?.setColor(resources.getColor(R.color.purple_200))
        // on below line we are setting text size
        barDataSet?.valueTextSize = 16f
        // on below line we are enabling description as false
        binding.idBarChart.description.isEnabled = false
    }

    private fun getBarEntries(dashboardBean: List<TelecalerDashboardBean.Data.WeeklyData>) {
        // creating a new array list
        barEntriesList = ArrayList()
        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        //  for (dashboardBean.totalCustomer  DashboardBean.Data.WeeklyData)
        //barEntriesList?.add(BarEntry(4f, dashboardBean.get(0).totalCustomer.toFloat()))
        barEntriesList?.add(BarEntry(4f,  dashboardBean.get(0).totalCustomer.toFloat()))
        barEntriesList?.add(BarEntry(1f, dashboardBean.get(0).totalVisitor.toFloat()))
        barChart()
    }

    fun apiAllGet() {
        SalesApp.isAddAccessToken = true
        apiClient = ApiController(activity, this)
        val params = Utility.getParmMap()
        apiClient.progressView.showLoader()
        apiClient.getApiPostCall(ApiContants.getSource, params)
        apiClient.getApiPostCall(ApiContants.getState, params)

        if (PrefManager.getString(ApiContants.Role, "").equals("telecaller")) {
            apiClient.getApiPostCall(ApiContants.getDashboardTelecaller, params)
        } else {
            apiClient.getApiPostCall(ApiContants.getDashboardSalesman, params)
        }

        apiClient.getApiPostCall(ApiContants.getCustomerMobList, params)
    }

    override fun success(tag: String?, jsonElement: JsonElement) {
        try {
            apiClient.progressView.hideLoader()

            if (tag == ApiContants.logout) {
                val baseResponseBean = apiClient.getConvertIntoModel<BaseResponseBean>(
                    jsonElement.toString(),
                    BaseResponseBean::class.java
                )
                Toast.makeText(activity, baseResponseBean.msg, Toast.LENGTH_SHORT).show()
                PrefManager.clear()
                GeneralUtilities.launchActivity(
                    requireContext() as AppCompatActivity?,
                    LoginActivity::class.java
                )
                requireActivity().finishAffinity()
            }

            if (tag == ApiContants.getCustomerMobList) {
                val custMobList = apiClient.getConvertIntoModel<GetCustomerMobListBean>(
                    jsonElement.toString(),
                    GetCustomerMobListBean::class.java
                )

                if (custMobList.error == false) {
                    SalesApp.custMobList.clear()
                    SalesApp.custMobList.addAll(custMobList.data)
                }

            }

            if (tag == ApiContants.getState) {
                val stateBean = apiClient.getConvertIntoModel<StateBean>(
                    jsonElement.toString(),
                    StateBean::class.java
                )
                if (stateBean.error == false) {
                    SalesApp.stateList.clear()
                    SalesApp.stateList.addAll(stateBean.data)
                }
            }

            if (tag == ApiContants.getSource) {
                val sourceBean = apiClient.getConvertIntoModel<SourceBean>(
                    jsonElement.toString(),
                    SourceBean::class.java
                )
                if (sourceBean.error == false) {
                    SalesApp.sourceList.clear()
                    SalesApp.sourceList.addAll(sourceBean.data)
                }
            }

            if (tag == ApiContants.getDashboardSalesman) {
                val salesmanDashboardBean = apiClient.getConvertIntoModel<TelecalerDashboardBean>(
                    jsonElement.toString(),
                    TelecalerDashboardBean::class.java
                )

                if (salesmanDashboardBean.error == false) {
                    Log.d("DashboardRes>>",Gson().toJson(salesmanDashboardBean.data))
                    handleRcDashboard(salesmanDashboardBean.data.customerData)
                   if (salesmanDashboardBean.data.weeklyData.size>0){
                       binding.idBarChart.visibility=View.VISIBLE
                       getBarEntries(salesmanDashboardBean.data.weeklyData)
                   }else{
                       binding.idBarChart.visibility=View.GONE
                   }
                }else{
                    Toast.makeText(activity, salesmanDashboardBean.msg, Toast.LENGTH_SHORT).show()
                }
            }

            if (tag == ApiContants.getDashboardTelecaller) {
                val telecalerDashBean = apiClient.getConvertIntoModel<TelecalerDashboardBean>(
                    jsonElement.toString(),
                    TelecalerDashboardBean::class.java
                )

                if (telecalerDashBean.error == false) {
                    Log.d("DashboardRes<<",Gson().toJson(telecalerDashBean.data))
/*
                        if (telecalerDashBean.data.customerData.get(0).totalCustomer != null) {
                        totalCustomer = telecalerDashBean.data.customerData.get(0).totalCustomer
                    } else if (telecalerDashBean.data.customerData.get(0).totalVisitor != null) {
                        totalVisitor = telecalerDashBean.data.customerData.get(0).totalVisitor
                    }*/

                    binding.tvTitleTotatl.setText(telecalerDashBean.data.totalLeads.status.toString())
                    binding.tvTitleTotatlVal.setText(telecalerDashBean.data.totalLeads.value.toString())
                    handleRcDashboard(telecalerDashBean.data.customerData)
                    handleRcTelecaler(telecalerDashBean.data.dashboard)
                    if (telecalerDashBean.data.weeklyData.size>0){
                        binding.idBarChart.visibility=View.VISIBLE
                        getBarEntries(telecalerDashBean.data.weeklyData)
                    }else{
                        binding.idBarChart.visibility=View.GONE
                    }
                }
                else{
                //    ApiContants.callPGURL(requireActivity(),"")
                    Toast.makeText(activity, telecalerDashBean.msg, Toast.LENGTH_SHORT).show()
                }
            }

        } catch (e: Exception) {
            Log.d("error>>", e.localizedMessage)
        }
    }

    override fun failure(tag: String?, errorMessage: String) {
        apiClient.progressView.hideLoader()
        Utility.showSnackBar(requireActivity(), errorMessage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun handleRcDashboard(customerData: List<TelecalerDashboardBean.Data.CustomerData>) {
        binding.rcDashboard.layoutManager = GridLayoutManager(requireContext(), 2)
        var mAdapter = DashboardAdapter(requireActivity(), getMenus(customerData), object :
            RvStatusClickListner {
            override fun clickPos(status: String, pos: Int) {
                if (pos == 0) {
                    startActivity(
                        Intent(
                            requireActivity(),
                            AllCustomerTypeActivity::class.java
                        ).putExtra("customerType", "customer")
                    )
                } else if (pos == 1) {
                    startActivity(
                        Intent(
                            requireActivity(),
                            AllCustomerTypeActivity::class.java
                        ).putExtra("customerType", "visitor")
                    )
                }
            }
        })
        binding.rcDashboard.adapter = mAdapter
        // rvMyAcFiled.isNestedScrollingEnabled = false

    }

    private fun getMenus(customerData: List<TelecalerDashboardBean.Data.CustomerData>): ArrayList<MenuModelBean> {
        var menuList = ArrayList<MenuModelBean>()

        menuList.add(
            MenuModelBean(
                0,
                "Total Customer",
                customerData.get(0).totalCustomer.toString(),
                R.drawable.ic_dashbord
            )
        )
        menuList.add(
            MenuModelBean(
                1,
                "Total Visitor",
                customerData.get(0).totalVisitor.toString(),
                R.drawable.ic_dashbord
            )
        )


        return menuList
    }

    fun handleRcTelecaler(customerData: List<TelecalerDashboardBean.Data.Dashboard>) {
        binding.rcTelecaer.layoutManager = GridLayoutManager(requireContext(), 2)
        var mAdapter = TelecallerAdapter(requireActivity(), customerData, object :
            RvStatusClickListner {
            override fun clickPos(status: String, pos: Int) {
                startActivity(
                    Intent(
                        requireActivity(),
                        CustTelecallerActivity::class.java
                    ).putExtra("status_id", pos.toString()).putExtra("status_name", status)
                )
            }
        })
        binding.rcTelecaer.adapter = mAdapter
        // rvMyAcFiled.isNestedScrollingEnabled = false

    }

    fun handleRcComplaint() {
        binding.rcDashComplaint.layoutManager = GridLayoutManager(requireContext(), 2)
        var mAdapter = DashboardAdapter(requireActivity(), getMenusComplaint(), object :
            RvStatusClickListner {
            override fun clickPos(status: String, pos: Int) {
                startActivity(
                    Intent(
                        context,
                        AllComplaintsActivity::class.java
                    ).putExtra("Status", status)
                )

            }
        })
        binding.rcDashComplaint.adapter = mAdapter
        // rvMyAcFiled.isNestedScrollingEnabled = false

    }

    private fun getMenusComplaint(): ArrayList<MenuModelBean> {
        var menuList = ArrayList<MenuModelBean>()

        menuList.add(MenuModelBean(1, "Pending", pendingComplaint, R.drawable.ic_dashbord))
        menuList.add(MenuModelBean(2, "Processing", processingComplaint, R.drawable.ic_dashbord))
        menuList.add(
            MenuModelBean(
                3,
                "Completed",
                completedComplaint,
                R.drawable.ic_dashbord
            )
        )
        menuList.add(
            MenuModelBean(
                3,
                "Rejected",
                rejectedComplaint,
                R.drawable.ic_dashbord
            )
        )


        return menuList
    }

    fun callPGURL(url: String) {
        Log.d("weburl", url)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage("com.android.chrome")
        try {
            startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            // Chrome browser presumably not installed so allow user to choose instead
            intent.setPackage(null)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //requireActivity().startService(Intent(requireActivity(), LocationService::class.java))
    }

}