package com.cxwl.shawn.guangxi.ganzhe

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.Intent.ACTION_OPEN_DOCUMENT
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.View.OnClickListener
import android.view.animation.*
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.cxwl.shawn.guangxi.ganzhe.adapter.ShawnDisasterUploadAdapter
import com.cxwl.shawn.guangxi.ganzhe.common.CONST
import com.cxwl.shawn.guangxi.ganzhe.common.MyApplication
import com.cxwl.shawn.guangxi.ganzhe.dto.DisasterDto
import com.cxwl.shawn.guangxi.ganzhe.util.AuthorityUtil
import com.cxwl.shawn.guangxi.ganzhe.util.CommonUtil
import com.cxwl.shawn.guangxi.ganzhe.util.FileUtils
import com.cxwl.shawn.guangxi.ganzhe.util.OkHttpUtil
import kotlinx.android.synthetic.main.activity_disaster_upload.*
import kotlinx.android.synthetic.main.layout_date.*
import kotlinx.android.synthetic.main.shawn_dialog_camera.view.*
import kotlinx.android.synthetic.main.shawn_layout_title.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request.Builder
import okio.ArrayIndexOutOfBoundsException
import org.json.JSONException
import org.json.JSONObject
import uk.co.senab.photoview.PhotoView
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener
import wheelview.NumericWheelAdapter
import wheelview.OnWheelScrollListener
import wheelview.WheelView
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

/**
 * 灾情反馈
 */
class DisasterUploadActivity : ShawnBaseActivity(), OnClickListener, AMapLocationListener {

    private var mAdapter: ShawnDisasterUploadAdapter? = null
    private val dataList: MutableList<DisasterDto> = ArrayList()
    private val sdf1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    private var lat = 0.0
    private var lng = 0.0
    private var gzType: String? = ""
    private var gzTime: String? = ""
    private var disasterType: String? = "气象灾害"
    private var position: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disaster_upload)
        initWidget()
        initWheelView()
    }

    private fun initWidget() {
        llBack.setOnClickListener(this)
        tvSubmit.setOnClickListener(this)
        etContent.addTextChangedListener(contentWatcher)
        tvDisaster.setOnClickListener(this)
        tvNegtive.setOnClickListener(this)
        tvPositive.setOnClickListener(this)
        val itemWidth = (CommonUtil.widthPixels(this) - CommonUtil.dip2px(this, 24f).toInt()) / 3
        val param = ivWord.layoutParams
        param.width = itemWidth
        param.height = itemWidth
        ivWord.layoutParams = param
        ivWord.setOnClickListener(this)
        tvWordName.setOnClickListener(this)
        tvWordName.tag = ""
        ivWordDelete.setOnClickListener(this)
        val title = intent.getStringExtra(CONST.ACTIVITY_NAME)
        if (!TextUtils.isEmpty(title)) {
            tvTitle.text = title
            if (title.contains("延时") || title.contains("修改")) {//延时
                tvTime.setBackgroundResource(R.drawable.bg_gz_time)
                tvTime.setOnClickListener(this)
                tvLatLng.setBackgroundResource(R.drawable.bg_gz_time)
                tvLatLng.setOnClickListener(this)
            }
        }

        if (intent.hasExtra("data")) {//编辑
            val data: DisasterDto = intent.getParcelableExtra("data")
            etTitle.setText(data.title)
            gzType = data.gzType
            gzTime = data.gzTime

            val regex = "[0-9]+(\\.[0-9]+)?"
            val pattern = Pattern.compile(regex)
            val matcher = pattern.matcher(data.miao)
            var result = ""
            while (matcher.find()) {
                result += matcher.group()+","
            }
            Log.e("resultresult", result)
            if (result.contains(",")) {
                val results = result.split(",")
                try {
                    etMiao1.setText(results[0])
                    etMiao2.setText(results[2])
                    etMiao3.setText(results[4])
                    etMiao4.setText(results[5])
                    etMiao5.setText(results[6])
                    etMiao6.setText(results[7])
                    etMiao7.setText(results[8])
                } catch (e: ArrayIndexOutOfBoundsException) {
                    e.printStackTrace()
                }
            }

            disasterType = data.disasterType
            if (disasterType!!.contains("气象灾害")) {
                tvDisaster1.setTextColor(Color.WHITE)
                tvDisaster1.setBackgroundResource(R.drawable.shawn_bg_corner_left_blue)
                tvDisaster2.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                tvDisaster2.setBackgroundResource(R.drawable.shawn_bg_corner_right_white)
                llDisasterItem.visibility = View.VISIBLE
                tvBingchou1.visibility = View.GONE
                etBingchou.visibility = View.GONE
                tvBingchou2.visibility = View.GONE
                addDisWeather()
            } else {
                tvDisaster1.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                tvDisaster1.setBackgroundResource(R.drawable.shawn_bg_corner_left_white)
                tvDisaster2.setTextColor(Color.WHITE)
                tvDisaster2.setBackgroundResource(R.drawable.shawn_bg_corner_right_blue)
                llDisasterItem.visibility = View.GONE
                addDisaster()
            }

            tvTime.text = data.time
            if (!TextUtils.isEmpty(data.latlon) && data.latlon.contains(",")) {
                val latLngs = data.latlon.split(",")
                lat = latLngs[0].toDouble()
                lng = latLngs[1].toDouble()
                tvLatLng.text = "${lat}N,${lng}E"
            }
            position = data.addr
            etContent.setText(data.content)
            divider8.visibility = View.GONE
            tvPic.visibility = View.GONE
            gridView.visibility = View.GONE
            divider9.visibility = View.GONE
            tvWord.visibility = View.GONE
            tvWordStr.visibility = View.GONE
            csWord.visibility = View.GONE
        } else {//上传
            tvTime.text = sdf1.format(Date())
            startLocation()
            addDisWeather()
        }

        //甘蔗品种
        llGzType.removeAllViews()
        val gzTypes = resources.getStringArray(R.array.gz_type)
        for (i in gzTypes.indices) {
            val tv = TextView(this)
            tv.text = gzTypes[i]
            if (TextUtils.equals(gzType, tv.text)) {//选中
                tv.setTextColor(Color.WHITE)
                tv.setBackgroundResource(R.drawable.bg_gz_type_press)
            } else if (!TextUtils.isEmpty(gzType) && TextUtils.equals("其它", tv.text)) {//编辑时传过来的，其它类型输入的
                etOtherType.setText(gzType)
                tv.setTextColor(Color.WHITE)
                tv.setBackgroundResource(R.drawable.bg_gz_type_press)
            } else {
                tv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                tv.setBackgroundResource(R.drawable.bg_gz_type)
            }
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f)
            tv.setPadding(CommonUtil.dip2px(this, 5f).toInt(),0,CommonUtil.dip2px(this, 5f).toInt(),0)
            tv.gravity = Gravity.CENTER
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.marginStart = CommonUtil.dip2px(this, 5f).toInt()
            tv.layoutParams = params
            llGzType.addView(tv)

            tv.setOnClickListener {
                for (j in 0 until llGzType.childCount) {
                    val item = llGzType.getChildAt(j) as TextView
                    if (TextUtils.equals(item.text, tv.text)) {
                        gzType = tv.text.toString()
                        item.setTextColor(Color.WHITE)
                        item.setBackgroundResource(R.drawable.bg_gz_type_press)
                    } else {
                        item.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        item.setBackgroundResource(R.drawable.bg_gz_type)
                    }
                }

                if (TextUtils.equals(tv.text, "其它")) {
                    llGzType.visibility = View.GONE
                    etOtherType.visibility = View.VISIBLE
                    tvTypeCancel.visibility = View.VISIBLE
                    tvTypeSure.visibility = View.VISIBLE
                } else {
                    etOtherType.visibility = View.GONE
                    tvTypeCancel.visibility = View.GONE
                    tvTypeSure.visibility = View.GONE
                }
            }
        }
        tvTypeCancel.setOnClickListener {
            llGzType.visibility = View.VISIBLE
            etOtherType.visibility = View.GONE
            tvTypeCancel.visibility = View.GONE
            tvTypeSure.visibility = View.GONE
            CommonUtil.hideInputSoft(etOtherType, this)
        }
        tvTypeSure.setOnClickListener {
            gzType = etOtherType.text.toString()
            llGzType.visibility = View.VISIBLE
            etOtherType.visibility = View.GONE
            tvTypeCancel.visibility = View.GONE
            tvTypeSure.visibility = View.GONE
            CommonUtil.hideInputSoft(etOtherType, this)
        }

        //甘蔗种植时间
        llGzTime.removeAllViews()
        val gzTimes = resources.getStringArray(R.array.gz_time)
        for (i in gzTimes.indices) {
            val tv = TextView(this)
            tv.text = gzTimes[i]
            if (TextUtils.equals(gzTime, tv.text)) {//选中
                tv.setTextColor(Color.WHITE)
                tv.setBackgroundResource(R.drawable.bg_gz_type_press)
            } else {
                tv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                tv.setBackgroundResource(R.drawable.bg_gz_type)
            }
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f)
            tv.setPadding(CommonUtil.dip2px(this, 5f).toInt(),0,CommonUtil.dip2px(this, 5f).toInt(),0)
            tv.gravity = Gravity.CENTER
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.marginStart = CommonUtil.dip2px(this, 5f).toInt()
            tv.layoutParams = params
            llGzTime.addView(tv)

            tv.setOnClickListener {
                for (j in 0 until llGzTime.childCount) {
                    val item = llGzTime.getChildAt(j) as TextView
                    if (TextUtils.equals(item.text, tv.text)) {
                        gzTime = tv.text.toString()
                        item.setTextColor(Color.WHITE)
                        item.setBackgroundResource(R.drawable.bg_gz_type_press)
                    } else {
                        item.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        item.setBackgroundResource(R.drawable.bg_gz_type)
                    }
                }
            }
        }

        tvDisaster1.setOnClickListener {
            tvDisaster1.setTextColor(Color.WHITE)
            tvDisaster1.setBackgroundResource(R.drawable.shawn_bg_corner_left_blue)
            tvDisaster2.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            tvDisaster2.setBackgroundResource(R.drawable.shawn_bg_corner_right_white)
            llDisasterItem.visibility = View.VISIBLE
            tvBingchou1.visibility = View.GONE
            etBingchou.visibility = View.GONE
            tvBingchou2.visibility = View.GONE
            disasterType = "气象灾害"
            addDisWeather()
        }
        tvDisaster2.setOnClickListener {
            tvDisaster1.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            tvDisaster1.setBackgroundResource(R.drawable.shawn_bg_corner_left_white)
            tvDisaster2.setTextColor(Color.WHITE)
            tvDisaster2.setBackgroundResource(R.drawable.shawn_bg_corner_right_blue)
            llDisasterItem.visibility = View.GONE
            disasterType = "病虫害"
            addDisaster()
        }

        tvDisasterCancel.setOnClickListener {
            llDisasterItem.visibility = View.GONE
            etDisaster.visibility = View.GONE
            tvDisasterCancel.visibility = View.GONE
            tvDisasterSure.visibility = View.GONE
        }
        tvDisasterSure.setOnClickListener {
            llDisasterItem.visibility = View.GONE
            etDisaster.visibility = View.GONE
            tvDisasterCancel.visibility = View.GONE
            tvDisasterSure.visibility = View.GONE
            disasterType = disasterType+", "+etDisaster.text.toString()
        }

        initGridView()
    }

    //气象灾害
    private fun addDisWeather() {
        llDisaster.removeAllViews()
        llDisasterItem.visibility = View.GONE
        etDisaster.visibility = View.GONE
        tvDisasterCancel.visibility = View.GONE
        tvDisasterSure.visibility = View.GONE
        val disWeathers = resources.getStringArray(R.array.disaster_weather)
        for (i in disWeathers.indices) {
            val tv = TextView(this)
            tv.text = disWeathers[i]
            if (disasterType!!.contains(tv.text)) {
                tv.setTextColor(Color.WHITE)
                tv.setBackgroundResource(R.drawable.bg_gz_type_press)
            } else {
                tv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                tv.setBackgroundResource(R.drawable.bg_gz_type)
            }
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f)
            tv.setPadding(CommonUtil.dip2px(this, 5f).toInt(),0,CommonUtil.dip2px(this, 5f).toInt(),0)
            tv.gravity = Gravity.CENTER
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.marginStart = CommonUtil.dip2px(this, 5f).toInt()
            tv.layoutParams = params
            llDisaster.addView(tv)

            tv.setOnClickListener {
                disasterType = "气象灾害"
                for (j in 0 until llDisaster.childCount) {
                    val item = llDisaster.getChildAt(j) as TextView
                    if (TextUtils.equals(item.text, tv.text)) {
                        disasterType = disasterType+", "+tv.text.toString()
                        item.setTextColor(Color.WHITE)
                        item.setBackgroundResource(R.drawable.bg_gz_type_press)
                    } else {
                        item.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        item.setBackgroundResource(R.drawable.bg_gz_type)
                    }
                }

                if (TextUtils.equals(tv.text, "其它")) {
                    etDisaster.hint = "请输入灾害名称"
                    etDisaster.visibility = View.VISIBLE
                    tvDisasterCancel.visibility = View.VISIBLE
                    tvDisasterSure.visibility = View.VISIBLE
                } else {
                    etDisaster.visibility = View.GONE
                    tvDisasterCancel.visibility = View.GONE
                    tvDisasterSure.visibility = View.GONE
                }

                var arrayName: Array<String>? = null
                when(tv.text) {
                    disWeathers[0] -> {
                        arrayName = resources.getStringArray(R.array.disaster_weather1)
                    }
                    disWeathers[1] -> {
                        arrayName = resources.getStringArray(R.array.disaster_weather2)
                    }
                    disWeathers[2] -> {
                        arrayName = resources.getStringArray(R.array.disaster_weather3)
                    }
                    disWeathers[3] -> {
                        arrayName = resources.getStringArray(R.array.disaster_weather4)
                    }
                    disWeathers[4] -> {
                        arrayName = resources.getStringArray(R.array.disaster_weather5)
                    }
                    disWeathers[5] -> {
                        arrayName = resources.getStringArray(R.array.disaster_weather6)
                    }
                }
                if (arrayName != null) {
                    addDisWeatherItem(arrayName)
                }
            }
        }
    }

    //气象灾害item
    private fun addDisWeatherItem(arrayName: Array<String>) {
        llDisasterItem.visibility = View.VISIBLE
        llDisasterItem.removeAllViews()
        for (i in arrayName.indices) {
            val tv = CheckBox(this)
            tv.text = arrayName[i]
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f)
            tv.setTextColor(ContextCompat.getColor(this, R.color.text_color4))
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.marginStart = CommonUtil.dip2px(this, 10f).toInt()
            tv.layoutParams = params
            llDisasterItem.addView(tv)

            tv.setOnClickListener {
                disasterType = disasterType+", "+tv.text
                for (j in 0 until llDisasterItem.childCount) {
                    val item = llDisasterItem.getChildAt(j) as CheckBox
                    item.isChecked = TextUtils.equals(tv.text, item.text)
                }
            }
        }
    }

    //病虫害
    private fun addDisaster() {
        llDisaster.removeAllViews()
        llDisasterItem.visibility = View.GONE
        etDisaster.visibility = View.GONE
        tvDisasterCancel.visibility = View.GONE
        tvDisasterSure.visibility = View.GONE
        val disWeathers = resources.getStringArray(R.array.disaster_type)
        for (i in disWeathers.indices) {
            val tv = TextView(this)
            tv.text = disWeathers[i]
            if (disasterType!!.contains(tv.text)) {
                tv.setTextColor(Color.WHITE)
                tv.setBackgroundResource(R.drawable.bg_gz_type_press)
            } else {
                tv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                tv.setBackgroundResource(R.drawable.bg_gz_type)
            }
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f)
            tv.setPadding(CommonUtil.dip2px(this, 5f).toInt(),0,CommonUtil.dip2px(this, 5f).toInt(),0)
            tv.gravity = Gravity.CENTER
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.marginStart = CommonUtil.dip2px(this, 5f).toInt()
            tv.layoutParams = params
            llDisaster.addView(tv)

            tv.setOnClickListener {
                disasterType = "病虫害"
                tvBingchou1.visibility = View.VISIBLE
                etBingchou.visibility = View.VISIBLE
                tvBingchou2.visibility = View.VISIBLE
                for (j in 0 until llDisaster.childCount) {
                    val item = llDisaster.getChildAt(j) as TextView
                    if (TextUtils.equals(item.text, tv.text)) {
                        item.setTextColor(Color.WHITE)
                        item.setBackgroundResource(R.drawable.bg_gz_type_press)
                    } else {
                        item.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        item.setBackgroundResource(R.drawable.bg_gz_type)
                    }
                }

                if (TextUtils.equals(tv.text, "其它")) {
                    etDisaster.hint = "请输入病害或虫害名称"
                    etDisaster.visibility = View.VISIBLE
                    tvDisasterCancel.visibility = View.VISIBLE
                    tvDisasterSure.visibility = View.VISIBLE
                } else {
                    etDisaster.visibility = View.GONE
                    tvDisasterCancel.visibility = View.GONE
                    tvDisasterSure.visibility = View.GONE
                }
            }
        }
    }

    /**
     * 开始定位
     */
    private fun startLocation() {
        val mLocationOption = AMapLocationClientOption() //初始化定位参数
        val mLocationClient = AMapLocationClient(this) //初始化定位
        mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.isNeedAddress = true //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.isOnceLocation = true //设置是否只定位一次,默认为false
        mLocationOption.isMockEnable = false //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.interval = 2000 //设置定位间隔,单位毫秒,默认为2000ms
        mLocationClient.setLocationOption(mLocationOption) //给定位客户端对象设置定位参数
        mLocationClient.setLocationListener(this)
        mLocationClient.startLocation() //启动定位
    }

    override fun onLocationChanged(amapLocation: AMapLocation?) {
        if (amapLocation != null && amapLocation.errorCode == AMapLocation.LOCATION_SUCCESS) {
            lat = amapLocation.latitude
            lng = amapLocation.longitude
            tvLatLng.text = "${lat}N,${lng}E"
            position = if (amapLocation.province.contains(amapLocation.city)) {
                amapLocation.city + amapLocation.district + amapLocation.street + amapLocation.aoiName
            } else {
                amapLocation.province + amapLocation.city + amapLocation.district + amapLocation.street + amapLocation.aoiName
            }
        }
    }

    /**
     * 输入内容监听器
     */
    private val contentWatcher: TextWatcher = object : TextWatcher {
        override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
        override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
        override fun afterTextChanged(arg0: Editable) {
            if (etContent.text.isEmpty()) {
                tvTextCount!!.text = "(100字以内)"
            } else {
                val count: Int = 100 - etContent.text.length
                tvTextCount!!.text = "(还可输入" + count + "字)"
            }
        }
    }

    private fun addLastElement() {
        val dto = DisasterDto()
        dto.isLastItem = true
        dataList.add(dto)
    }

    private fun initGridView() {
        addLastElement()
        mAdapter = ShawnDisasterUploadAdapter(this, dataList)
        gridView.adapter = mAdapter
        gridView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val data = dataList[position]
            if (data.isLastItem) { //点击添加按钮
                selectCamera()
            } else {
                val imgList: ArrayList<String> = ArrayList()
                for (i in dataList.indices) {
                    val d = dataList[i]
                    if (!d.isLastItem) {
                        imgList.add(d.imgUrl)
                    }
                }
                initViewPager(position, imgList)
                if (clViewPager!!.visibility == View.GONE) {
                    scaleExpandAnimation(clViewPager)
                    clViewPager!!.visibility = View.VISIBLE
                    tvCount.text = (position + 1).toString() + "/" + imgList.size
                }
            }
        }
    }

    /**
     * 灾情反馈
     */
    private fun okHttpPost() {
        loadingView!!.visibility = View.VISIBLE
        val url = "http://decision-admin.tianqi.cn/home/work2019/decisionZqfk"
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart("uid", MyApplication.UID)
        builder.addFormDataPart("appid", CONST.APPID)
        builder.addFormDataPart("latlon", "$lat,$lng")
        if (!TextUtils.isEmpty(etTitle!!.text.toString())) {
            builder.addFormDataPart("title", etTitle!!.text.toString())
        }
        builder.addFormDataPart("other_param2", gzType!!)//甘蔗品种
        builder.addFormDataPart("other_param3", gzTime!!)//甘蔗品种
        val miao1 = tvMiao1.text.toString()+etMiao1.text.toString()+tvMiao2.text.toString()+etMiao2.text.toString()+tvMiao3.text.toString()
        val miao2 = tvMiao4.text.toString()+etMiao3.text.toString()+tvMiao5.text.toString()+etMiao4.text.toString()+tvMiao6.text.toString()
        val miao3 = tvMiao7.text.toString()+etMiao5.text.toString()+tvMiao8.text.toString()+etMiao6.text.toString()+tvMiao9.text.toString()
        val miao4 = tvMiao10.text.toString()+etMiao7.text.toString()+tvMiao11.text.toString()
        val miaoqing = miao1+miao2+miao3+miao4
        builder.addFormDataPart("other_param1", miaoqing)//苗情
        if (etBingchou.visibility != View.VISIBLE) {//气象灾害
            builder.addFormDataPart("type", disasterType!!)
        } else {//病虫害
            disasterType = disasterType+", "+tvBingchou1.text+etBingchou.text+tvBingchou2.text
            builder.addFormDataPart("type", disasterType!!)
        }
        if (!TextUtils.isEmpty(tvTime.text.toString())) {
            builder.addFormDataPart("createtime", tvTime.text.toString())
        }
        builder.addFormDataPart("location", position!!)
        if (!TextUtils.isEmpty(etContent.text.toString())) {
            builder.addFormDataPart("content", etContent.text.toString())
        }
        Log.e("loglog", "\n${etTitle.text}\n$gzType\n$gzTime\n$miaoqing\n$disasterType")

        if (dataList.size > 0) {
            for (i in dataList.indices) {
                val dto = dataList[i]
                if (!TextUtils.isEmpty(dto.imgUrl)) {
                    val imgFile = File(compressBitmap(dto.imgUrl))
                    if (imgFile.exists()) {
                        builder.addFormDataPart("pic$i", imgFile.name, RequestBody.create("image/*".toMediaTypeOrNull(), imgFile))
                    }
                }
            }
        }
        if (!TextUtils.isEmpty(tvWordName.tag.toString())) {
            val wordFile = File(tvWordName.tag.toString())
            builder.addFormDataPart("annex1", wordFile.name, RequestBody.create("image/*".toMediaTypeOrNull(), wordFile))
        }
        val body: RequestBody = builder.build()
        Thread(Runnable {
            OkHttpUtil.enqueue(Builder().post(body).url(url).build(), object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("", "")
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        return
                    }
                    val result = response.body!!.string()
                    runOnUiThread {
                        try {
                            val obj = JSONObject(result)
                            if (!obj.isNull("code")) {
                                val code = obj.getString("code")
                                if (TextUtils.equals(code, "200")) {
                                    Toast.makeText(this@DisasterUploadActivity, "提交成功！", Toast.LENGTH_SHORT).show()
                                    loadingView!!.visibility = View.GONE
                                    setResult(Activity.RESULT_OK)
                                    finish()
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
            })
        }).start()
    }

    private fun compressBitmap(imgPath: String): String? {
        var newPath: String? = null
        try {
            val files = File(CONST.SDCARD_PATH)
            if (!files.exists()) {
                files.mkdirs()
            }
            val bitmap = getSmallBitmap(imgPath)
            newPath = files.absolutePath + "/" + System.currentTimeMillis() + ".jpg"
            val fos = FileOutputStream(newPath)
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos)
                if (!bitmap.isRecycled) {
                    bitmap.recycle()
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return newPath
    }

    /**
     * 根据路径获得图片信息并按比例压缩，返回bitmap
     */
    private fun getSmallBitmap(filePath: String?): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true //只解析图片边沿，获取宽高
        BitmapFactory.decodeFile(filePath, options)
        // 计算缩放比
        options.inSampleSize = calculateInSampleSize(options, 720, 1080)
        // 完整解析图片返回bitmap
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(filePath, options)
    }


    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        return inSampleSize
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.llBack -> finish()
            R.id.tvTime, R.id.tvNegtive -> bootTimeLayoutAnimation()
            R.id.tvPositive -> {
                bootTimeLayoutAnimation()
                setTextViewValue()
            }
            R.id.tvLatLng -> {
                val intent = Intent(this, SelectPositionActivity::class.java)
                intent.putExtra("latLng", tvLatLng.text.toString())
                startActivityForResult(intent, 1004)
            }
            R.id.ivWord, R.id.tvWordName -> {
//                startActivityForResult(Intent(this, SelectFileActivity::class.java), 1003)
                intentSdcard()
            }
            R.id.ivWordDelete -> {
                ivWord.visibility = View.VISIBLE
                tvWordName.text = ""
                tvWordName.tag = ""
                tvWordName.visibility = View.GONE
                ivWordDelete.visibility = View.GONE
            }
            R.id.tvSubmit -> //				if (TextUtils.isEmpty(etTitle.getText().toString())) {
//					Toast.makeText(this, "请输入灾情标题！", Toast.LENGTH_SHORT).show();
//					return;
//				}
//				if (TextUtils.isEmpty(tvDisaster.getText().toString())) {
//					Toast.makeText(this, "请选择灾情类型！", Toast.LENGTH_SHORT).show();
//					return;
//				}
//				if (TextUtils.isEmpty(etPosition.getText().toString())) {
//					Toast.makeText(this, "请输入采集地点！", Toast.LENGTH_SHORT).show();
//					return;
//				}
                okHttpPost()
        }
    }

    private var cameraFile: File? = null
    private fun intentCamera() {
        val files = File(CONST.SDCARD_PATH)
        if (!files.exists()) {
            files.mkdirs()
        }
        cameraFile = File(CONST.SDCARD_PATH + "/" + System.currentTimeMillis() + ".jpg")
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val uri: Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", cameraFile!!)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            uri = Uri.fromFile(cameraFile)
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, 1002)
    }

    private fun intentAlbum() {
        val intent = Intent(this, SelectPictureActivity::class.java)
        intent.putExtra("count", dataList.size - 1)
        startActivityForResult(intent, 1001)
    }

    /**
     * 调用本地sdcard
     */
    private fun intentSdcard() {
        val intent = Intent(ACTION_OPEN_DOCUMENT)
//        intent.type = "image/*"//选择图片
//        intent.type = "audio/*"//选择音频
//        intent.type = "audio/*"//选择视频 （mp4 3gp 是android支持的视频格式）
//        intent.type = "video/*;image/*"//同时选择视频和图片
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, 1005)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                1001 -> if (data != null) {
                    val bundle = data.extras
                    if (bundle != null) {
                        if (dataList.size <= 1) {
                            dataList.removeAt(0)
                        } else {
                            dataList.removeAt(dataList.size - 1)
                        }
                        val list: ArrayList<DisasterDto> = bundle.getParcelableArrayList("dataList")
                        dataList.addAll(list)
                        addLastElement()
                        if (dataList.size >= 7) {
                            dataList.removeAt(dataList.size - 1)
                        }
                        if (mAdapter != null) {
                            mAdapter!!.notifyDataSetChanged()
                        }
                    }
                }
                1002 -> {
                    var fis: FileInputStream? = null
                    try {
                        fis = FileInputStream(cameraFile)
                        val bitmap = BitmapFactory.decodeStream(fis)
                        //						ivMyPhoto.setImageBitmap(bitmap);
                        if (dataList.size <= 1) {
                            dataList.removeAt(0)
                        } else {
                            dataList.removeAt(dataList.size - 1)
                        }
                        val dto = DisasterDto()
                        dto.imageName = ""
                        dto.imgUrl = cameraFile!!.absolutePath
                        dataList.add(dto)
                        addLastElement()
                        if (dataList.size >= 7) {
                            dataList.removeAt(dataList.size - 1)
                        }
                        if (mAdapter != null) {
                            mAdapter!!.notifyDataSetChanged()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        try {
                            fis?.close()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                1003 -> {
                    if (data != null) {
                        val bundle = data.extras
                        if (bundle != null) {
                            val dto = bundle.getParcelable("data") as DisasterDto
                            if (!TextUtils.isEmpty(dto.title)) {
                                ivWord.visibility = View.GONE
                                tvWordName.visibility = View.VISIBLE
                                ivWordDelete.visibility = View.VISIBLE
                                tvWordName.text = "附件文档：${dto.title}"
                                tvWordName.tag = dto.filePath
                            }
                        }
                    }
                }
                1004 -> {
                    if (data != null) {
                        val bundle = data.extras
                        if (bundle != null) {
                            val latLng = bundle.getString("latLng")
                            if (!TextUtils.isEmpty(latLng)) {
                                tvLatLng.text = latLng
                            }
                            position = bundle.getString("position")
                        }
                    }
                }
                1005 -> {
                    if (data != null) {
                        val filePath = FileUtils.getFilePathByUri(this, data.data)
                        if (!TextUtils.isEmpty(filePath)) {
                            if (filePath.endsWith(".xlsx") || filePath.endsWith(".xls")) {
                                if (filePath.contains("/")) {
                                    val result = filePath.split("/")
                                    val title = result[result.size-1]
                                    ivWord.visibility = View.GONE
                                    tvWordName.visibility = View.VISIBLE
                                    ivWordDelete.visibility = View.VISIBLE
                                    tvWordName.text = "附件文档：${title}"
                                    tvWordName.tag = filePath
                                }
                            } else {
                                Toast.makeText(this, "请选择excel表格文件", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (clViewPager!!.visibility == View.VISIBLE) {
            scaleColloseAnimation(clViewPager)
            clViewPager!!.visibility = View.GONE
            return false
        } else {
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 初始化viewPager
     */
    private fun initViewPager(current: Int, list: ArrayList<String>) {
        val imageArray = arrayOfNulls<ImageView>(list.size)
        for (i in list.indices) {
            val imgUrl = list[i]
            if (!TextUtils.isEmpty(imgUrl)) {
                val imageView = ImageView(this)
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                val bitmap = BitmapFactory.decodeFile(imgUrl)
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap)
                    imageArray[i] = imageView
                }
            }
        }
        val myViewPagerAdapter = MyViewPagerAdapter(imageArray)
        viewPager.adapter = myViewPagerAdapter
        viewPager.currentItem = current
        viewPager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(arg0: Int) {
                tvCount.text = (arg0 + 1).toString() + "/" + list.size
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
            override fun onPageScrollStateChanged(arg0: Int) {}
        })
    }

    private inner class MyViewPagerAdapter(private val mImageViews: Array<ImageView?>) : PagerAdapter() {
        override fun getCount(): Int {
            return mImageViews.size
        }

        override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
            return arg0 === arg1
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(mImageViews[position])
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val photoView = PhotoView(container.context)
            val drawable = mImageViews[position]!!.drawable
            photoView.setImageDrawable(drawable)
            container.addView(photoView, 0)
            photoView.onPhotoTapListener = OnPhotoTapListener { view, v, v1 ->
                scaleColloseAnimation(clViewPager)
                clViewPager.visibility = View.GONE
            }
            return photoView
        }

    }

    /**
     * 放大动画
     * @param view
     */
    private fun scaleExpandAnimation(view: View?) {
        val animationSet = AnimationSet(true)
        val scaleAnimation = ScaleAnimation(0f, 1.0f, 0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        scaleAnimation.interpolator = LinearInterpolator()
        scaleAnimation.duration = 300
        animationSet.addAnimation(scaleAnimation)
        val alphaAnimation = AlphaAnimation(0f, 1.0f)
        alphaAnimation.duration = 300
        animationSet.addAnimation(alphaAnimation)
        view!!.startAnimation(animationSet)
    }

    /**
     * 缩小动画
     * @param view
     */
    private fun scaleColloseAnimation(view: View?) {
        val animationSet = AnimationSet(true)
        val scaleAnimation = ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        scaleAnimation.interpolator = LinearInterpolator()
        scaleAnimation.duration = 300
        animationSet.addAnimation(scaleAnimation)
        val alphaAnimation = AlphaAnimation(1.0f, 0f)
        alphaAnimation.duration = 300
        animationSet.addAnimation(alphaAnimation)
        view!!.startAnimation(animationSet)
    }

    /**
     * 选择相机或相册
     */
    private fun selectCamera() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.shawn_dialog_camera, null)
        val dialog = Dialog(this, R.style.CustomProgressDialog)
        dialog.setContentView(view)
        dialog.show()
        view.tvCamera.setOnClickListener {
            dialog.dismiss()
            checkCameraAuthority()
        }
        view.tvAlbum.setOnClickListener {
            dialog.dismiss()
            checkStorageAuthority()
        }
    }

    /**
     * 申请相机权限
     */
    private fun checkCameraAuthority() {
        if (Build.VERSION.SDK_INT < 23) {
            intentCamera()
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !== PackageManager.PERMISSION_GRANTED) {
                val permissions = arrayOf(Manifest.permission.CAMERA)
                ActivityCompat.requestPermissions(this, permissions, AuthorityUtil.AUTHOR_CAMERA)
            } else {
                intentCamera()
            }
        }
    }

    /**
     * 申请存储权限
     */
    private fun checkStorageAuthority() {
        if (Build.VERSION.SDK_INT < 23) {
            intentAlbum()
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED) {
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(this, permissions, AuthorityUtil.AUTHOR_STORAGE)
            } else {
                intentAlbum()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            AuthorityUtil.AUTHOR_CAMERA -> if (grantResults.isNotEmpty()) {
                var isAllGranted = true //是否全部授权
                for (gResult in grantResults) {
                    if (gResult != PackageManager.PERMISSION_GRANTED) {
                        isAllGranted = false
                        break
                    }
                }
                if (isAllGranted) { //所有权限都授予
                    intentCamera()
                } else { //只要有一个没有授权，就提示进入设置界面设置
                    AuthorityUtil.intentAuthorSetting(this, "\"" + getString(R.string.app_name) + "\"" + "需要使用您的相机权限，是否前往设置？")
                }
            } else {
                for (permission in permissions) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission!!)) {
                        AuthorityUtil.intentAuthorSetting(this, "\"" + getString(R.string.app_name) + "\"" + "需要使用您的相机权限，是否前往设置？")
                        break
                    }
                }
            }
            AuthorityUtil.AUTHOR_STORAGE -> if (grantResults.isNotEmpty()) {
                var isAllGranted = true //是否全部授权
                for (gResult in grantResults) {
                    if (gResult != PackageManager.PERMISSION_GRANTED) {
                        isAllGranted = false
                        break
                    }
                }
                if (isAllGranted) { //所有权限都授予
                    intentAlbum()
                } else { //只要有一个没有授权，就提示进入设置界面设置
                    AuthorityUtil.intentAuthorSetting(this, "\"" + getString(R.string.app_name) + "\"" + "需要使用您的存储权限，是否前往设置？")
                }
            } else {
                for (permission in permissions) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission!!)) {
                        AuthorityUtil.intentAuthorSetting(this, "\"" + getString(R.string.app_name) + "\"" + "需要使用您的存储权限，是否前往设置？")
                        break
                    }
                }
            }
        }
    }

    private fun initWheelView() {
        val c = Calendar.getInstance()
        val curYear = c[Calendar.YEAR]
        val curMonth = c[Calendar.MONTH] + 1 //通过Calendar算出的月数要+1
        val curDate = c[Calendar.DATE]
        val curHour = c[Calendar.HOUR_OF_DAY]
        val curMinute = c[Calendar.MINUTE]
        val curSecond = c[Calendar.SECOND]
        val numericWheelAdapter1 = NumericWheelAdapter(this, 1950, curYear)
        numericWheelAdapter1.setLabel("年")
        year.viewAdapter = numericWheelAdapter1
        year.isCyclic = false //是否可循环滑动
        year.addScrollingListener(scrollListener)
        year.visibleItems = 7
        val numericWheelAdapter2 = NumericWheelAdapter(this, 1, 12, "%02d")
        numericWheelAdapter2.setLabel("月")
        month.viewAdapter = numericWheelAdapter2
        month.isCyclic = false
        month.addScrollingListener(scrollListener)
        month.visibleItems = 7
        initDay(curYear, curMonth)
        day.isCyclic = false
        day.visibleItems = 7
        val numericWheelAdapter3 = NumericWheelAdapter(this, 1, 23, "%02d")
        numericWheelAdapter3.setLabel("时")
        hour.viewAdapter = numericWheelAdapter3
        hour.isCyclic = false
        hour.addScrollingListener(scrollListener)
        hour.visibleItems = 7
        hour.visibility = View.VISIBLE
        val numericWheelAdapter4 = NumericWheelAdapter(this, 1, 59, "%02d")
        numericWheelAdapter4.setLabel("分")
        minute.viewAdapter = numericWheelAdapter4
        minute.isCyclic = false
        minute.addScrollingListener(scrollListener)
        minute.visibleItems = 7
        minute.visibility = View.VISIBLE
        val numericWheelAdapter5 = NumericWheelAdapter(this, 1, 59, "%02d")
        numericWheelAdapter5.setLabel("秒")
        second.viewAdapter = numericWheelAdapter5
        second.isCyclic = false
        second.addScrollingListener(scrollListener)
        second.visibleItems = 7
        second.visibility = View.VISIBLE
        year.currentItem = curYear - 1950
        month.currentItem = curMonth - 1
        day.currentItem = curDate - 1
        hour.currentItem = curHour - 1
        minute.currentItem = curMinute - 1
        second.currentItem = curSecond - 1
    }

    private val scrollListener: OnWheelScrollListener = object : OnWheelScrollListener {
        override fun onScrollingStarted(wheel: WheelView) {}
        override fun onScrollingFinished(wheel: WheelView) {
            val nYear = year!!.currentItem + 1950 //年
            val nMonth: Int = month.currentItem + 1 //月
            initDay(nYear, nMonth)
        }
    }

    /**
     */
    private fun initDay(arg1: Int, arg2: Int) {
        val numericWheelAdapter = NumericWheelAdapter(this, 1, getDay(arg1, arg2), "%02d")
        numericWheelAdapter.setLabel("日")
        day.viewAdapter = numericWheelAdapter
    }

    /**
     *
     * @param year
     * @param month
     * @return
     */
    private fun getDay(year: Int, month: Int): Int {
        var day = 30
        var flag = false
        flag = when (year % 4) {
            0 -> true
            else -> false
        }
        day = when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            2 -> if (flag) 29 else 28
            else -> 30
        }
        return day
    }

    /**
     */
    private fun setTextViewValue() {
        val yearStr = (year!!.currentItem + 1950).toString()
        val monthStr = if (month.currentItem + 1 < 10) "0" + (month.currentItem + 1) else (month.currentItem + 1).toString()
        val dayStr = if (day.currentItem + 1 < 10) "0" + (day.currentItem + 1) else (day.currentItem + 1).toString()
        val hourStr = if (hour.currentItem + 1 < 10) "0" + (hour.currentItem + 1) else (hour.currentItem + 1).toString()
        val minuteStr = if (minute.currentItem + 1 < 10) "0" + (minute.currentItem + 1) else (minute.currentItem + 1).toString()
        val secondStr = if (second.currentItem + 1 < 10) "0" + (second.currentItem + 1) else (second.currentItem + 1).toString()
        tvTime.text = "$yearStr-$monthStr-$dayStr $hourStr:$minuteStr:$secondStr"
    }

    private fun bootTimeLayoutAnimation() {
        if (layoutDate!!.visibility == View.GONE) {
            timeLayoutAnimation(true, layoutDate)
            layoutDate!!.visibility = View.VISIBLE
        } else {
            timeLayoutAnimation(false, layoutDate)
            layoutDate!!.visibility = View.GONE
        }
    }

    /**
     * 时间图层动画
     * @param flag
     * @param view
     */
    private fun timeLayoutAnimation(flag: Boolean, view: View?) {
        //列表动画
        val animationSet = AnimationSet(true)
        val animation: TranslateAnimation = if (!flag) {
            TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 1f)
        } else {
            TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 1f,
                    Animation.RELATIVE_TO_SELF, 0f)
        }
        animation.duration = 400
        animationSet.addAnimation(animation)
        animationSet.fillAfter = true
        view!!.startAnimation(animationSet)
        animationSet.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(arg0: Animation) {}
            override fun onAnimationRepeat(arg0: Animation) {}
            override fun onAnimationEnd(arg0: Animation) {
                view.clearAnimation()
            }
        })
    }

}
