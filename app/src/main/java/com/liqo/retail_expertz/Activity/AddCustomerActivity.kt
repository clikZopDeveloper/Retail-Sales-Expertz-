package com.liqo.retail_expertz.Activity

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.liqo.kktext.Model.CityBean

import com.liqo.retail_expertz.ApiHelper.ApiController
import com.liqo.retail_expertz.ApiHelper.ApiResponseListner
import com.liqo.retail_expertz.Model.*

import com.liqo.retail_expertz.R
import com.liqo.retail_expertz.Utills.*
import com.liqo.retail_expertz.databinding.ActivityAddCustomerBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.liqo.retail_expertz.Adapter.*
import com.stpl.antimatter.Utils.ApiContants
import java.io.File
import java.util.*


class AddCustomerActivity : AppCompatActivity(), ApiResponseListner,
    GoogleApiClient.OnConnectionFailedListener,
    ConnectivityListener.ConnectivityReceiverListener {
    private var mAdapterPurchaseCat: CategoryAdapter?=null
    private var mAdapterPurchase: CustomProdListAdapter?=null
    private var mAdapterInter: CustomProdInterListAdapter?=null
    private var catName= ""
    val purchaseNameList = mutableListOf<AddProductBean>()
    val interNameList = mutableListOf<AddProductBean>()
    private var mAdapterInterCat: CategoryAdapter?=null
    private lateinit var binding: ActivityAddCustomerBinding
    private var catPurchaseListID: MutableList<Any?>? = null
    private var catIntrsetedListID: MutableList<Any?>? = null
    val imgList: MutableList<File> = ArrayList()
    private lateinit var apiClient: ApiController
    private var calendar: Calendar? = null
    var myReceiver: ConnectivityListener? = null
    var way = ""
    var projectType = "Customer"
    var activity: Activity = this
    val PERMISSION_CODE = 12345
    val CAMERA_PERMISSION_CODE1 = 123
    var SELECT_PICTURES1 = 1
    var custID = ""
    var file2: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_customer)
        if (SalesApp.isEnableScreenshort==true){
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        myReceiver = ConnectivityListener()

        binding.igToolbar.ivMenu.setImageDrawable(resources.getDrawable(R.drawable.ic_back_black))
        binding.igToolbar.ivMenu.setOnClickListener { finish() }

        binding.igToolbar.ivLogout.visibility = View.GONE
        binding.igToolbar.switchDayStart.visibility = View.GONE

        way = intent.getStringExtra("way")!!
        //      requestPermission()

        calendar = Calendar.getInstance();
        val hour: Int = calendar!!.get(Calendar.HOUR_OF_DAY)
        val min: Int = calendar!!.get(Calendar.MINUTE)

        //   callCityListAdapter()
        if (way.equals("AddCustomer")) {
            binding.igToolbar.tvTitle.text = "Add Customer/Visitor"
            apiCategory()

        } else {
            //      binding.igToolbar.tvTitle.text = "Update Customer"
            intent.getStringExtra("cust_ID")?.let { apiCustomerDetail(it) }
            custID = intent.getStringExtra("cust_ID")!!

            //   val custResponse:CustomerListBean.Data = (intent.getSerializableExtra("custResponse") as CustomerListBean.Data?)!!
            //   Log.d("sdfdf",Gson().toJson(custResponse))

        }

        setState()
        setSourceData()
        typeMode()

        binding.btnAddCustProduct.setOnClickListener {

            binding.tvPurchase.visibility=View.VISIBLE
            binding.tvInetset.visibility=View.VISIBLE

            purchaseNameList.addAll(mAdapterPurchaseCat?.purchaseCatNameList!!)
            interNameList.addAll(mAdapterInterCat?.purchaseCatNameList!!)

            handleCustomProdList(purchaseNameList)
            handleInstertCatList(interNameList)

           mAdapterPurchaseCat?.purchaseCatNameList!!.clear()
           mAdapterInterCat?.purchaseCatNameList!!.clear()

           // Log.d("erwer",catName+"\n"+Gson().toJson(mAdapterInterCat?.purchaseCatNameList)+"\n"+mAdapterPurchaseCat?.purchaseCatNameList)
            Log.d("erwer",catName+"\n"+Gson().toJson(purchaseNameList)+"\n"+Gson().toJson(interNameList))

        }

        if (PrefManager.getString(ApiContants.Role,"").equals("telecaller")){

        }else{
            setSearchNum()
        }

        binding.apply {

            btnUplaodImages.setOnClickListener {
                openCameraDialog(SELECT_PICTURES1, CAMERA_PERMISSION_CODE1)
            }

            btnSubmit.setOnClickListener {
                apiAddCustomer()
            }

            editDOB.setOnClickListener(View.OnClickListener {
                val c = Calendar.getInstance()
                val year = c[Calendar.YEAR]
                val month = c[Calendar.MONTH]
                val day = c[Calendar.DAY_OF_MONTH]
                val datePickerDialog = DatePickerDialog(
                    this@AddCustomerActivity,
                    { view, year, monthOfYear, dayOfMonth ->
                        //  dob.setText(dateofnews);

                        //    val dateofnews = "${dayOfMonth.toString() + "/" + (monthOfYear + 1).toString() + "/" + year}"
                        val dateofnews =
                            "${year.toString() + "-" + (monthOfYear + 1).toString() + "-" + dayOfMonth.toString()}"

                        //   val dateofnews = (monthOfYear + 1).toString() + "/" + dayOfMonth + "/" + year

                        editDOB.setText(dateofnews)
                    },
                    year, month, day
                )
                datePickerDialog.show()
            })
            editDOA.setOnClickListener(View.OnClickListener {
                val c = Calendar.getInstance()
                val year = c[Calendar.YEAR]
                val month = c[Calendar.MONTH]
                val day = c[Calendar.DAY_OF_MONTH]
                val datePickerDialog = DatePickerDialog(
                    this@AddCustomerActivity,
                    { view, year, monthOfYear, dayOfMonth ->
                        //  dob.setText(dateofnews);

                        //    val dateofnews = "${dayOfMonth.toString() + "/" + (monthOfYear + 1).toString() + "/" + year}"
                        val dateofnews =
                            "${year.toString() + "-" + (monthOfYear + 1).toString() + "-" + dayOfMonth.toString()}"

                        //   val dateofnews = (monthOfYear + 1).toString() + "/" + dayOfMonth + "/" + year

                        editDOA.setText(dateofnews)
                    },
                    year, month, day
                )
                datePickerDialog.show()
            })

        }
    }

    fun handleCustomProdList(purchaseCatNameList: MutableList<AddProductBean>?) {
        binding.rcAllPurchaseCat.layoutManager = LinearLayoutManager(this)
         mAdapterPurchase = CustomProdListAdapter(this, purchaseCatNameList,catName)
        binding.rcAllPurchaseCat.adapter = mAdapterPurchase
        mAdapterPurchase!!.notifyDataSetChanged()
        // rvMyAcFiled.isNestedScrollingEnabled = false
    }

    fun handleInstertCatList(
        data: MutableList<AddProductBean>?,
    ) {
        binding.rcAllInstertCat.layoutManager = LinearLayoutManager(this)
        mAdapterInter = CustomProdInterListAdapter(this, data,catName)
        binding.rcAllInstertCat.adapter = mAdapterInter
        mAdapterInter!!.notifyDataSetChanged()
        // rvMyAcFiled.isNestedScrollingEnabled = false
    }

     fun removeItemByNamePurchase(id: Int) {
        val position = purchaseNameList.indexOfFirst { it.ID == id }
        if (position != -1) {
            mAdapterPurchase!!.removeItem(position)
            mAdapterPurchase!!.notifyDataSetChanged()
        }
    }

    fun removeItemByNameInter(id: Int) {
        val position = interNameList.indexOfFirst { it.ID == id }
        if (position != -1) {
            mAdapterInter!!.removeItem(position)
            mAdapterInter!!.notifyDataSetChanged()
        }
    }

    fun apiCategory() {
        SalesApp.isAddAccessToken = true
        apiClient = ApiController(activity, this)
        val params = Utility.getParmMap()
        apiClient.progressView.showLoader()
        apiClient.getApiPostCall(ApiContants.getCategory, params)

    }

    fun apiCustomerDetail(cust_id: String) {
        SalesApp.isAddAccessToken = true
        apiClient = ApiController(this, this)
        val params = Utility.getParmMap()
        params["customer_id"] = cust_id
        apiClient.progressView.showLoader()
        apiClient.getApiPostCall(ApiContants.getCustomerData, params)

    }

    fun typeMode() {
        binding.radioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                if (checkedId == R.id.rbCust) {
                    projectType = "Customer"
                    binding.tvPurchaseCat.visibility = View.VISIBLE
                    binding.tvPurchase.visibility = View.VISIBLE
                    binding.rcPurchase.visibility = View.VISIBLE
                    binding.rcAllPurchaseCat.visibility = View.VISIBLE
                } else if (checkedId == R.id.rbVisitor) {
                    projectType = "Visitor"
                    binding.tvPurchaseCat.visibility = View.GONE
                    binding.tvPurchase.visibility = View.GONE
                    binding.rcPurchase.visibility = View.GONE
                    binding.rcAllPurchaseCat.visibility = View.GONE

                }
            }
        })
    }

    fun apiCity(stateName: String) {
        SalesApp.isAddAccessToken = true
        apiClient = ApiController(activity, this)
        val params = Utility.getParmMap()
        params["state"] = stateName
        apiClient.progressView.showLoader()
        apiClient.getApiPostCall(ApiContants.getCity, params)
    }

    fun apiSubCategory(catID: String) {
        SalesApp.isAddAccessToken = true
        apiClient = ApiController(activity, this)
        val params = Utility.getParmMap()
        params["category_id"] = catID
        apiClient.progressView.showLoader()
        apiClient.getApiPostCall(ApiContants.getSubCategory, params)
    }

    fun setSourceData() {
        //  binding.stateSource.setThreshold(1);//will start working from first character
        val state = arrayOfNulls<String>(SalesApp.sourceList.size)
        for (i in SalesApp.sourceList.indices) {
            state[i] = SalesApp.sourceList.get(i).name
        }

        binding.stateSource.setAdapter(
            ArrayAdapter(
                this@AddCustomerActivity,
                android.R.layout.simple_list_item_1, state
            )
        )

        binding.stateSource.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            //  var sourceName = SalesApp.sourceList.get(position).name

            binding.stateSource.setText(parent.getItemAtPosition(position).toString())
            Log.d("StateID", "" + parent.getItemAtPosition(position).toString())
            Toast.makeText(
                applicationContext,
                binding.stateSource.getText().toString(),
                Toast.LENGTH_SHORT
            ).show()
            setSourceData()

        })
    }

    override fun success(tag: String?, jsonElement: JsonElement) {
        try {
            apiClient.progressView.hideLoader()
            if (tag == ApiContants.AddCustomer) {
                val updateLeadBean = apiClient.getConvertIntoModel<TelecalerDashboardBean>(
                    jsonElement.toString(),
                    TelecalerDashboardBean::class.java
                )

                Toast.makeText(this, updateLeadBean.msg, Toast.LENGTH_SHORT).show()
                finish()
            }

            if (tag == ApiContants.getCustomerData) {
                val customrDetailBean = apiClient.getConvertIntoModel<CustomerDetailBean>(
                    jsonElement.toString(),
                    CustomerDetailBean::class.java
                )
                //   Toast.makeText(this, allStatusBean.msg, Toast.LENGTH_SHORT).show()
                if (customrDetailBean.error == false) {

                    binding.igToolbar.tvTitle.text =
                        "Update " + customrDetailBean.data.customers.customerType
                    binding.editName.setText(customrDetailBean.data.customers.name)
                    binding.editMobileNo.setText(customrDetailBean.data.customers.phone)
                    binding.editWatsMobileNo.setText(customrDetailBean.data.customers.whatsapp)
                    binding.editEmailID.setText(customrDetailBean.data.customers.email)
                    binding.editDOB.setText(customrDetailBean.data.customers.dob)
                    binding.editDOA.setText(customrDetailBean.data.customers.doa)
                    binding.editAddress.setText(customrDetailBean.data.customers.address)
                    binding.stateselector.setText(customrDetailBean.data.customers.state)
                    binding.cityselector.setText(customrDetailBean.data.customers.city)
                    binding.stateSource.setText(customrDetailBean.data.customers.source)
                    if (customrDetailBean.data.customers.remarks != null) {
                        binding.editRemark.setText(customrDetailBean.data.customers.remarks)
                    } else {

                    }

                    if (customrDetailBean.data.customers.customerType.equals("visitor")) {
                        binding.rbVisitor.isChecked = true
                        binding.rbCust.isChecked = false
                        binding.tvPurchaseCat.visibility = View.GONE
                        binding.rcPurchase.visibility = View.GONE
                    } else {
                        binding.rbCust.isChecked = true
                        binding.rbVisitor.isChecked = false
                        binding.tvPurchaseCat.visibility = View.VISIBLE
                        binding.rcPurchase.visibility = View.VISIBLE
                    }

                    if (PrefManager.getString(ApiContants.Role,"").equals("telecaller")){
                        binding.tvPurchaseCat.visibility = View.GONE
                        binding.rcPurchase.visibility = View.GONE
                        binding.tvIntersetedCat.visibility = View.GONE
                        binding.rcInterseted.visibility = View.GONE
                        binding.layoutCategory.visibility = View.GONE
                    }
                    /*     for (custCat in customrDetailBean.data.customerPurchasedCategory) {
                             for (i in 0 until customrDetailBean.data.customerPurchasedCategory.size) {
                                 if (custCat.categoryId.equals(catList?.get(i)?.id)){
                                     custCat.customerId
                                 }
                             }
                         }*/

                    handleCustPurchaseCat(customrDetailBean.data.customerPurchasedCategory)
                    handleCustInterestedCat(customrDetailBean.data.customerInterestedCategory)
                }
            }

            if (tag == ApiContants.getUpdateCustomer) {
                val updateLeadBean = apiClient.getConvertIntoModel<TelecalerDashboardBean>(
                    jsonElement.toString(),
                    TelecalerDashboardBean::class.java
                )

                Toast.makeText(this, updateLeadBean.msg, Toast.LENGTH_SHORT).show()
                finish()
            }

            if (tag == ApiContants.getCity) {
                val cityBean = apiClient.getConvertIntoModel<CityBean>(
                    jsonElement.toString(),
                    CityBean::class.java
                )
                if (cityBean.error == false) {
                    val state = arrayOfNulls<String>(cityBean.data.size)
                    for (i in cityBean.data.indices) {
                        //Storing names to string array
                        state[i] = cityBean.data.get(i).city
                    }
                    val adapte1: ArrayAdapter<String?>
                    adapte1 = ArrayAdapter(
                        this@AddCustomerActivity,
                        android.R.layout.simple_list_item_1,
                        state
                    )
                    binding.cityselector.setAdapter(adapte1)
                    binding.cityselector.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
                        binding.cityselector.setText(parent.getItemAtPosition(position).toString())
                        Log.d("StateID", "" + parent.getItemAtPosition(position).toString())
                        Toast.makeText(
                            applicationContext,
                            binding.cityselector.getText().toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        apiCity(binding.stateselector.text.toString())
                    })
                }

            }

            if (tag == ApiContants.getCategory) {
                val categoryBean = apiClient.getConvertIntoModel<CategoryBean>(
                    jsonElement.toString(),
                    CategoryBean::class.java
                )
                if (categoryBean.error == false) {
                  //  catList = categoryBean.data
                    Log.d("asdasd",Gson().toJson(categoryBean.data))
                    setCategory(categoryBean.data)

                }
            }
            if (tag == ApiContants.getSubCategory) {
                val subCatBean = apiClient.getConvertIntoModel<SubCatBean>(
                    jsonElement.toString(),
                    SubCatBean::class.java
                )
                if (subCatBean.error == false) {
                        CatIntersetList(subCatBean.data)
                        CatPurchaseList(subCatBean.data)
                }

            }
        } catch (e: Exception) {
            Log.d("error>>", e.localizedMessage)
        }

    }

    override fun failure(tag: String?, errorMessage: String) {
        apiClient.progressView.hideLoader()
        Utility.showSnackBar(activity, errorMessage)
        Log.d("error", errorMessage)

    }

    private fun setSearchNum() {
        val state = arrayOfNulls<String>(SalesApp.custMobList.size)
        for (i in SalesApp.custMobList.indices) {
            state[i] = SalesApp.custMobList.get(i).phone+"/"+ SalesApp.custMobList.get(i).name
        }
        val adapte1: ArrayAdapter<String?>
        adapte1 = ArrayAdapter(
            this@AddCustomerActivity,
            android.R.layout.simple_list_item_1,
            state
        )
        binding.editMobileNo.setAdapter(adapte1)
        binding.editMobileNo.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            Log.d("xcvxcvc", Gson().toJson(SalesApp.custMobList.get(position).name))
            for (i in SalesApp.custMobList.indices) {
                val name=SalesApp.custMobList.get(i).phone+"/"+ SalesApp.custMobList.get(i).name
                if (name.equals(parent.getItemAtPosition(position))) {
                    Log.d("StateID", SalesApp.custMobList.get(i).id.toString())
                  //  apiCustomerDetail(SalesApp.custMobList.get(i).id.toString())
                    startActivity(Intent(this,CustomerDetailActivity::class.java).putExtra("cust_ID",SalesApp.custMobList.get(i).id.toString()))
                finish()
                //cust_ID
                }
            }
        })
        adapte1.notifyDataSetChanged()
    }

    fun handleCustInterestedCat(data: List<CustomerDetailBean.Data.CustomerInterestedCategory>) {
        binding.rcInterseted.layoutManager = GridLayoutManager(this,3)
        var mAdapter = CustomerInterstedAdapter(this, "Update", data, object :
            RvListClickListner {
            override fun clickPos(status: MutableList<Any?>?, pos: Int) {
                catIntrsetedListID = status
            }
        })
        binding.rcInterseted.adapter = mAdapter
        // rvMyAcFiled.isNestedScrollingEnabled = false

    }

    fun handleCustPurchaseCat(data: List<CustomerDetailBean.Data.CustomerPurchasedCategory>) {
        binding.rcPurchase.layoutManager = GridLayoutManager(this,3)
       val mAdapter = CustomerPurchaseAdapter(this, "Update", data, object :
            RvListClickListner {
            override fun clickPos(status: MutableList<Any?>?, pos: Int) {
                catPurchaseListID = status
            }
        })
        binding.rcPurchase.adapter = mAdapter
        // rvMyAcFiled.isNestedScrollingEnabled = false
    }

    fun CatIntersetList(data: List<SubCatBean.Data>) {
        binding.rcInterseted.layoutManager = GridLayoutManager(this,3)
         mAdapterInterCat = CategoryAdapter(this, data, object :
            RvListClickListner {
            override fun clickPos(status: MutableList<Any?>?, pos: Int) {
                catIntrsetedListID = status
            }
        })
        binding.rcInterseted.adapter = mAdapterInterCat
        // rvMyAcFiled.isNestedScrollingEnabled = false

    }

    fun CatPurchaseList(data: List<SubCatBean.Data>) {
        binding.rcPurchase.layoutManager = GridLayoutManager(this,3)
         mAdapterPurchaseCat = CategoryAdapter(this, data, object :
            RvListClickListner {
            override fun clickPos(status: MutableList<Any?>?, pos: Int) {
                catPurchaseListID = status
            }
        })
        binding.rcPurchase.adapter = mAdapterPurchaseCat
        // rvMyAcFiled.isNestedScrollingEnabled = false

    }

    fun setState() {
        val state = arrayOfNulls<String>(SalesApp.stateList.size)
        for (i in SalesApp.stateList.indices) {
            state[i] = SalesApp.stateList.get(i).state
        }

        binding.stateselector.setAdapter(
            ArrayAdapter(
                this@AddCustomerActivity,
                android.R.layout.simple_list_item_1, state
            )
        )

        binding.stateselector.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            binding.stateselector.setText(parent.getItemAtPosition(position).toString())
            Log.d("StateID", "" + parent.getItemAtPosition(position).toString())
            Toast.makeText(
                applicationContext,
                binding.stateselector.getText().toString(),
                Toast.LENGTH_SHORT
            ).show()

            setState()
            apiCity(binding.stateselector.text.toString())
        })
    }

    fun setCategory(data: List<CategoryBean.Data>) {
        val state = arrayOfNulls<String>(data.size)
        for (i in data.indices) {
            state[i] = data.get(i).name
        }

        val adapte1: ArrayAdapter<String?>
        adapte1 = ArrayAdapter(
            this@AddCustomerActivity,
            android.R.layout.simple_list_item_1,
            state
        )
        binding.statCat.setAdapter(adapte1)
        binding.statCat.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
        Log.d("xcvxcvc", Gson().toJson(data.get(position).name))
            catName=data.get(position).name.toString()
        for (i in data.indices) {
            if (data.get(i).name.equals(parent.getItemAtPosition(position))) {
                Log.d("StateID", data.get(i).id.toString())
                setCategory(data)
                apiSubCategory( data.get(i).id.toString())
            }
        }
    })
    adapte1.notifyDataSetChanged()

    }

    fun ClickPicCamera(CAMERA_PERMISSION_CODE: Int) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_PERMISSION_CODE)
    }

    fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_MEDIA_IMAGES
            ),
            PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission is Granted", Toast.LENGTH_SHORT).show()

            } else {
                requestPermission()
            }
        }
    }

    private fun uploadImage(SELECT_PICTURES: Int) {

        if (Build.VERSION.SDK_INT < 19) {
            var intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Choose Pictures"), SELECT_PICTURES
            )
        } else { // For latest versions API LEVEL 19+
            var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, SELECT_PICTURES);
        }
    }

    fun openCameraDialog(SELECT_PICTURES: Int, CAMERA_PERMISSION_CODE: Int) {
        val dialog: Dialog = GeneralUtilities.openBootmSheetDailog(
            R.layout.dialog_camera, R.style.AppBottomSheetDialogTheme,
            this
        )
        val ivClose = dialog.findViewById<ImageView>(R.id.ivClose)
        val llInternalPhoto = dialog.findViewById<View>(R.id.llInternalPhoto) as LinearLayout
        val llClickPhoto = dialog.findViewById<View>(R.id.llClickPhoto) as LinearLayout

        llInternalPhoto.setOnClickListener {
            dialog.dismiss()
            requestPermission()
            uploadImage(SELECT_PICTURES)
        }

        llClickPhoto.setOnClickListener {
            dialog.dismiss()
            requestPermission()
            ClickPicCamera(CAMERA_PERMISSION_CODE)

        }
        ivClose.setOnClickListener { dialog.dismiss() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == SELECT_PICTURES1) {
            if (data?.getClipData() != null) { // if multiple images are selected
                var count = data.clipData?.itemCount
                binding.tvImageCount.visibility = View.VISIBLE
                binding.tvImageCount.text = "$count Images"
                Log.d("wewwe", "$count")
                for (i in 0..count!! - 1) {
                    var imageUri: Uri = data.clipData?.getItemAt(i)!!.uri
                    val picturePath: String = GeneralUtilities.getPath(
                        applicationContext, imageUri
                    )
                    file2 = File(picturePath)
                    //val custImg = CustProdImgBean(file2)
                    imgList.add(file2!!)
                    //   Log.d("MultiPicturePath", picturePath)

                    //     iv_image.setImageURI(imageUri) Here you can assign your Image URI to the ImageViews
                }

            } else if (data?.getData() != null) {   // if single image is selected
                binding.tvImageCount.visibility = View.GONE
                var imageUri: Uri = data.data!!
                val picturePath: String = GeneralUtilities.getPath(
                    applicationContext, imageUri
                )
                file2 = File(picturePath)
                val myBitmap = BitmapFactory.decodeFile(file2!!.absolutePath)
                binding.btnAadharFront.setImageBitmap(myBitmap)
                imgList.add(file2!!)

                Log.d("SinglePicturePath", picturePath)
                //   iv_image.setImageURI(imageUri) Here you can assign the picked image uri to your imageview
            }
        }

        if (requestCode == CAMERA_PERMISSION_CODE1) {
            try {
                Toast.makeText(this@AddCustomerActivity, "sdfsd", Toast.LENGTH_SHORT).show()

                val imageBitmap = data?.extras?.get("data") as Bitmap
                binding.btnAadharFront.setImageBitmap(imageBitmap)
                val tempUri = GeneralUtilities.getImageUri(applicationContext, imageBitmap)
                file2 = File(GeneralUtilities.getRealPathFromURII(this, tempUri))
                imgList.add(file2!!)
                Log.e("Path", file2.toString())

                //Toast.makeText(getContext(), ""+picturePath, Toast.LENGTH_SHORT).show();
            } catch (e: java.lang.Exception) {
                Log.e("Path Error", e.toString())
                Toast.makeText(applicationContext, "" + e, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun apiAddCustomer() {
        SalesApp.isAddAccessToken = true
        apiClient = ApiController(activity, this)
        //  val builder = MultipartBody.Builder()
        //  builder.setType(MultipartBody.FORM)
        val params = Utility.getParmMap()

        params["name"] = binding.editName.text.toString()
        params["mobile"] = binding.editMobileNo.text.toString()
        params["whatsapp"] = binding.editWatsMobileNo.text.toString()
        params["email"] = binding.editEmailID.text.toString()
        params["dob"] = binding.editDOB.text.toString()
        params["doa"] = binding.editDOA.text.toString()
        params["address"] = binding.editAddress.text.toString()
        params["state"] = binding.stateselector.text.toString()
        params["city"] = binding.cityselector.text.toString()
        params["customer_type"] = projectType
        params["source"] = binding.stateSource.text.toString()
        params["remarks"] = binding.editRemark.text.toString()
     /*   params["interested_category"] = catIntrsetedListID.toString()
        params["purchased_category"] = catPurchaseListID.toString()*/

        params["interested_category"] =Gson().toJson(interNameList)
        params["purchased_category"] =  Gson().toJson(purchaseNameList)

        /*for (i in 0 until imgList.size) {
         builder.addFormDataPart("files[]", imgList.get(i).name,
             RequestBody.create("multipart/form-data".toMediaTypeOrNull(), imgList.get(i)))
     }*/

        Log.d("requestParms", Gson().toJson(params))
        Log.d("requestParms", Gson().toJson(catIntrsetedListID))
        Log.d("requestParms", Gson().toJson(catPurchaseListID))
        apiClient.progressView.showLoader()
        //    apiClient.makeCallMultipart(ApiContants.AddCustomer, builder.build())
        if (way.equals("AddCustomer")) {
            apiClient.getApiPostCall(ApiContants.AddCustomer, params)
        }
        else {
            params["customer_id"] = custID
            params["status_id"] = ""
            apiClient.getApiPostCall(ApiContants.getUpdateCustomer, params)
        }
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
