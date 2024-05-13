package com.liqo.retail_expertz.Activity

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liqo.retail_expertz.Adapter.*
import com.liqo.retail_expertz.ApiHelper.ApiController
import com.liqo.retail_expertz.ApiHelper.ApiResponseListner
import com.liqo.retail_expertz.Model.CategoryBean
import com.liqo.retail_expertz.Model.ConertIntersetdToPurchase
import com.liqo.retail_expertz.Model.CustomerDetailBean
import com.liqo.retail_expertz.Model.SubCatBean
import com.liqo.retail_expertz.R
import com.liqo.retail_expertz.Utills.*

import com.liqo.retail_expertz.databinding.ActivityCustDetailBinding

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.stpl.antimatter.Utils.ApiContants

class CustomerDetailActivity : AppCompatActivity(), ApiResponseListner,
    GoogleApiClient.OnConnectionFailedListener,
    ConnectivityListener.ConnectivityReceiverListener {
    private lateinit var binding: ActivityCustDetailBinding
    private lateinit var apiClient: ApiController
    private lateinit var rcPurchase: RecyclerView
    var myReceiver: ConnectivityListener? = null
    private var catPurchaseListID: MutableList<Any?>? = null
    private var catIntrsetedListID: MutableList<Any?>? = null
    var activity: Activity = this
    private var catList:List<CategoryBean.Data>?=null
    var custID = ""
    var catType = "interested"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cust_detail)
        if (SalesApp.isEnableScreenshort == true) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            );
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        myReceiver = ConnectivityListener()

        binding.igToolbar.tvTitle.text = "Customer Detail"
        binding.igToolbar.ivMenu.setImageDrawable(resources.getDrawable(R.drawable.ic_back_black))
        binding.igToolbar.ivMenu.setOnClickListener { finish() }
        binding.igToolbar.ivLogout.visibility = View.GONE
        binding.igToolbar.switchDayStart.visibility = View.GONE
        apiCategory()
        intent.getStringExtra("cust_ID")?.let { apiCustomerDetail(it) }
        custID = intent.getStringExtra("cust_ID").toString()


        binding.tvAddCat.setOnClickListener {
            dialogSelectCat()
        }
    }

    fun apiCategory() {
        SalesApp.isAddAccessToken = true
        apiClient = ApiController(activity, this)
        val params = Utility.getParmMap()
    //   apiClient.progressView.showLoader()
        apiClient.getApiPostCall(ApiContants.getCategory, params)
    }

    fun apiCustomerDetail(cust_id: String) {
        SalesApp.isAddAccessToken = true
        apiClient = ApiController(this, this)
        val params = Utility.getParmMap()
        params["customer_id"] = cust_id
     //   apiClient.progressView.showLoader()
        apiClient.getApiPostCall(ApiContants.getCustomerData, params)

    }

    fun apiSubCategory(catID: String) {
        SalesApp.isAddAccessToken = true
        apiClient = ApiController(activity, this)
        val params = Utility.getParmMap()
        params["category_id"] = catID
        apiClient.progressView.showLoader()
        apiClient.getApiPostCall(ApiContants.getSubCategory, params)
    }

    fun apiConvertInterestedToPurchased(cust_id: String, cat_id: String) {
        SalesApp.isAddAccessToken = true
        apiClient = ApiController(this, this)
        val params = Utility.getParmMap()
        params["customer_id"] = cust_id
        params["category_id"] = cat_id
        apiClient.progressView.showLoader()
        apiClient.getApiPostCall(ApiContants.getConvertInterestedToPurchased, params)
    }

    fun apiUpdateCategory(cust_id: String, cat_id: String) {
        SalesApp.isAddAccessToken = true
        apiClient = ApiController(this, this)
        val params = Utility.getParmMap()
        params["customer_id"] = cust_id
        params["category_ids"] = cat_id
        params["category_type"] = catType
        apiClient.progressView.showLoader()
        apiClient.getApiPostCall(ApiContants.getUpdateCustomerCategory, params)
    }

    fun dialogSelectCat() {
        val builder = AlertDialog.Builder(this,R.style.CustomAlertDialog)
            .create()
        val dialog = layoutInflater.inflate(R.layout.dialog_addcategory,null)

        builder.setView(dialog)

        builder.setCanceledOnTouchOutside(false)
        builder.show()
        val ivClose = dialog.findViewById<ImageView>(R.id.ivClose)
        val radioGroup = dialog.findViewById<RadioGroup>(R.id.radioGroup) as RadioGroup
        val btnSubmit = dialog.findViewById<TextView>(R.id.btnSubmit) as TextView
         rcPurchase = dialog.findViewById<RecyclerView>(R.id.rcPurchase) as RecyclerView
        val statCat = dialog.findViewById<AutoCompleteTextView>(R.id.statCat) as AutoCompleteTextView

        ivClose.setOnClickListener {  builder.dismiss() }
        btnSubmit.setOnClickListener {
            builder.dismiss()
            apiUpdateCategory(custID,catPurchaseListID.toString())
        }
        setCategory(catList!!,statCat)

        typeCatMode(radioGroup)

    }

    fun typeCatMode(radioGroup: RadioGroup) {
        radioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                if (checkedId == R.id.rbInterested) {
                    catType = "interested"
                } else if (checkedId == R.id.rbPurchase) {
                    catType = "purchased"

                }
            }
        })
    }

    fun CatIntersetList(data: List<SubCatBean.Data>) {
        binding.rcInterseted.layoutManager = GridLayoutManager(this,3)
        var mAdapter = CategoryAdapter(this, data, object :
            RvListClickListner {
            override fun clickPos(status: MutableList<Any?>?, pos: Int) {
                catIntrsetedListID = status
            }
        })
        binding.rcInterseted.adapter = mAdapter
        // rvMyAcFiled.isNestedScrollingEnabled = false

    }

    fun CatPurchaseList(data: List<SubCatBean.Data>) {
        rcPurchase.layoutManager = GridLayoutManager(this,3)
        var mAdapter = CategoryAdapter(this, data, object :
            RvListClickListner {
            override fun clickPos(status: MutableList<Any?>?, pos: Int) {
                catPurchaseListID = status
            }
        })
        rcPurchase.adapter = mAdapter
        // rvMyAcFiled.isNestedScrollingEnabled = false

    }

    fun handleCustInterestedCat(data: List<CustomerDetailBean.Data.CustomerInterestedCategory>) {
        /*if (data.size > 0) {
            binding.rcInterseted.visibility = View.VISIBLE
            binding.tvInterseted.visibility = View.VISIBLE
        } else {
            binding.rcInterseted.visibility = View.GONE
            binding.tvInterseted.visibility = View.GONE
        }*/
        binding.rcInterseted.layoutManager = GridLayoutManager(this, 3)
        var mAdapter = CustomerInterstedAdapter(this, "Detail", data, object :
            RvListClickListner {
            override fun clickPos(status: MutableList<Any?>?, catID: Int) {
                //   catListID=status
                val builder = AlertDialog.Builder(this@CustomerDetailActivity)
                builder.setMessage("Are you sure you want to covert interested to purchased product?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        apiConvertInterestedToPurchased(custID, catID.toString())
                    }
                    .setNegativeButton("No") { dialog, id ->
                        // Dismiss the dialog
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()


            }
        })
        binding.rcInterseted.adapter = mAdapter
        // rvMyAcFiled.isNestedScrollingEnabled = false
    }

    fun handleCustPurchaseCat(data: List<CustomerDetailBean.Data.CustomerPurchasedCategory>) {
     /*   if (data.size > 0) {
            binding.rcPurchase.visibility = View.VISIBLE
            binding.tvPurchase.visibility = View.VISIBLE
        } else {
            binding.rcPurchase.visibility = View.GONE
            binding.tvPurchase.visibility = View.GONE
        }*/
        binding.rcPurchase.layoutManager = GridLayoutManager(this, 3)
        var mAdapter = CustomerPurchaseAdapter(this, "Detail", data, object :
            RvListClickListner {
            override fun clickPos(status: MutableList<Any?>?, pos: Int) {
                //  catListID=status
            }
        })
        binding.rcPurchase.adapter = mAdapter
        // rvMyAcFiled.isNestedScrollingEnabled = false

    }

    fun handleLeadCommentList(
        leadProduct: List<CustomerDetailBean.Data.LeadComment>
    ) {
        binding.rcCommentList.layoutManager = LinearLayoutManager(this)
        var mAdapter = LeadCommentListAdapter(this, leadProduct, object :
            RvStatusClickListner {
            override fun clickPos(status: String, pos: Int) {

            }
        })
        binding.rcCommentList.adapter = mAdapter
        // rvMyAcFiled.isNestedScrollingEnabled = false

    }

    fun handleLeadHisList(leadHistory: List<CustomerDetailBean.Data.LeadHistory>) {
        binding.rcHisList.layoutManager = LinearLayoutManager(this)
        var mAdapter = HisListAdapter(this, leadHistory, object :
            RvStatusClickListner {
            override fun clickPos(status: String, pos: Int) {
            }
        })
        binding.rcHisList.adapter = mAdapter
        // rvMyAcFiled.isNestedScrollingEnabled = false

    }

    fun setCategory(data: List<CategoryBean.Data>, statCat: AutoCompleteTextView) {
        val state = arrayOfNulls<String>(data.size)
        for (i in data.indices) {
            state[i] = data.get(i).name
        }
        val adapte1: ArrayAdapter<String?>
        adapte1 = ArrayAdapter(
            this@CustomerDetailActivity,
            android.R.layout.simple_list_item_1,
            state
        )
        statCat.setAdapter(adapte1)
        statCat.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            Log.d("xcvxcvc", Gson().toJson(data.get(position).name))
            for (i in data.indices) {
                if (data.get(i).name.equals(parent.getItemAtPosition(position))) {
                    Log.d("StateID", data.get(i).id.toString())
                    setCategory(data, statCat)
                    apiSubCategory( data.get(i).id.toString())
                }
            }
        })
        adapte1.notifyDataSetChanged()
    }

    override fun success(tag: String?, jsonElement: JsonElement?) {
        try {
            apiClient.progressView.hideLoader()

            if (tag == ApiContants.getCustomerData) {
                val customrDetailBean = apiClient.getConvertIntoModel<CustomerDetailBean>(
                    jsonElement.toString(),
                    CustomerDetailBean::class.java
                )
                //   Toast.makeText(this, allStatusBean.msg, Toast.LENGTH_SHORT).show()
                if (customrDetailBean.error == false) {
                    binding.tvName.setText(customrDetailBean.data.customers.name)
                    binding.tvEmail.setText(customrDetailBean.data.customers.email)
                    binding.tvMobileNo.setText(customrDetailBean.data.customers.phone)
                    binding.tvWhatsappNo.setText(customrDetailBean.data.customers.whatsapp)
                    binding.tvDOB.setText(customrDetailBean.data.customers.dob)
                    binding.tvDOA.setText(customrDetailBean.data.customers.doa)
                    binding.tvState.setText(customrDetailBean.data.customers.state)
                    binding.tvCity.setText(customrDetailBean.data.customers.city)
                    binding.tvAddress.setText(customrDetailBean.data.customers.address)
                    binding.tvCustType.setText(customrDetailBean.data.customers.customerType)
                    binding.tvSource.setText(customrDetailBean.data.customers.source)
                    binding.ivCallMob.setOnClickListener {
                        GeneralUtilities.makeCall(this, customrDetailBean.data.customers.phone)
                    }
                    binding.ivCallWhatsApp.setOnClickListener {
                        GeneralUtilities.makeCall(this, customrDetailBean.data.customers.whatsapp)
                    }
                    handleCustPurchaseCat(customrDetailBean.data.customerPurchasedCategory)
                    handleCustInterestedCat(customrDetailBean.data.customerInterestedCategory)
                    handleLeadCommentList(customrDetailBean.data.leadComments)
                    handleLeadHisList(customrDetailBean.data.leadHistory)
                }
            }

            if (tag == ApiContants.getConvertInterestedToPurchased) {
                val customrDetailBean = apiClient.getConvertIntoModel<ConertIntersetdToPurchase>(
                    jsonElement.toString(),
                    ConertIntersetdToPurchase::class.java
                )
                //   Toast.makeText(this, allStatusBean.msg, Toast.LENGTH_SHORT).show()
                if (customrDetailBean.error == false) {
                    Toast.makeText(this, customrDetailBean.msg, Toast.LENGTH_SHORT).show()
                    apiCustomerDetail(custID)
                }
            }

            if (tag == ApiContants.getUpdateCustomerCategory) {
                val customrDetailBean = apiClient.getConvertIntoModel<ConertIntersetdToPurchase>(
                    jsonElement.toString(),
                    ConertIntersetdToPurchase::class.java
                )
                //   Toast.makeText(this, allStatusBean.msg, Toast.LENGTH_SHORT).show()
                if (customrDetailBean.error == false) {
                    Toast.makeText(this, customrDetailBean.msg, Toast.LENGTH_SHORT).show()
                    apiCustomerDetail(custID)
                }
            }

            if (tag == ApiContants.getCategory) {
                val categoryBean = apiClient.getConvertIntoModel<CategoryBean>(
                    jsonElement.toString(),
                    CategoryBean::class.java
                )
                if (categoryBean.error == false) {
                    catList = categoryBean.data
                 //   CatIntersetList(categoryBean.data)

                }
            }
            if (tag == ApiContants.getSubCategory) {
                val subCatBean = apiClient.getConvertIntoModel<SubCatBean>(
                    jsonElement.toString(),
                    SubCatBean::class.java
                )
                if (subCatBean.error == false) {
                    CatPurchaseList(subCatBean.data)
                }

            }
        } catch (e: Exception) {
            Log.d("error>>", e.localizedMessage)
        }
    }

    override fun failure(tag: String?, errorMessage: String) {
        apiClient.progressView.hideLoader()
        Utility.showSnackBar(this, errorMessage)
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
        //    startService(Intent(this, LocationService::class.java))
    }
}
