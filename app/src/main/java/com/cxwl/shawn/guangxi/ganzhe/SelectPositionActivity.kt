package com.cxwl.shawn.guangxi.ganzhe;

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.geocoder.RegeocodeResult
import com.amap.api.services.help.Inputtips
import com.amap.api.services.help.InputtipsQuery
import com.amap.api.services.help.Tip
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.cxwl.shawn.guangxi.ganzhe.adapter.PositionAdapter
import com.cxwl.shawn.guangxi.ganzhe.dto.DisasterDto
import com.cxwl.shawn.guangxi.ganzhe.util.CommonUtil
import kotlinx.android.synthetic.main.activity_select_position.*
import kotlinx.android.synthetic.main.layout_marker_info.view.*
import kotlinx.android.synthetic.main.shawn_layout_title.*
import java.math.BigDecimal

/**
 * 选择地点
 */
class SelectPositionActivity : ShawnBaseActivity(), View.OnClickListener, AMap.OnMapClickListener, AMap.OnMarkerClickListener, AMap.InfoWindowAdapter, GeocodeSearch.OnGeocodeSearchListener {

    private var aMap: AMap? = null
    private val zoom = 20.0f
    private var clickMarker: Marker? = null
    private var geocoderSearch: GeocodeSearch? = null
    private var addr: String? = null
    private var mAdapter: PositionAdapter? = null
    private val dataList: ArrayList<DisasterDto> = ArrayList()
    private var isFirstSearch = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_position)
        initWidget()
        initListView()
        initAmap(savedInstanceState)
    }

    private fun initWidget() {
        llBack.setOnClickListener(this)
        tvTitle.text = "经纬度选择"
        tvControl.text = "确定"
        tvControl.visibility = View.VISIBLE
        tvControl.setOnClickListener(this)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                dataList.clear()
                listView.visibility = View.GONE
                if (mAdapter != null) {
                    mAdapter!!.notifyDataSetChanged()
                }
                poiSearch(s.toString())
            }
        })

        geocoderSearch = GeocodeSearch(this)
        geocoderSearch!!.setOnGeocodeSearchListener(this)
    }

    private fun poiSearch(keyWord: String) {
        //第二个参数传入null或者“”代表在全国进行检索，否则按照传入的city进行检索
        val inputQuery = InputtipsQuery(keyWord, "")
        inputQuery.cityLimit = true //限制在当前城市
        val inputTips = Inputtips(this, inputQuery)
        inputTips.setInputtipsListener { p0, p1 ->
            dataList.clear()
            for (i in 0 until p0!!.size) {
                val tip = p0[i]
                val dto = DisasterDto()
                dto.title = tip.district+tip.name
                dto.latlon = "${tip.point.latitude},${tip.point.longitude}"
                dataList.add(dto)
            }
            listView.visibility = View.VISIBLE
            if (mAdapter != null) {
                mAdapter!!.notifyDataSetChanged()
            }
        }
        inputTips.requestInputtipsAsyn()
    }

    private fun initListView() {
        mAdapter = PositionAdapter(this, dataList)
        listView.adapter = mAdapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val data = dataList[position]
            if (isFirstSearch) {
                isFirstSearch = false
                poiSearch(data.title)
            } else {
                listView.visibility = View.GONE
                CommonUtil.hideInputSoft(etSearch, this)
                addr = data.title
                if (data.latlon.contains(",")) {
                    val latLngs = data.latlon.split(",")
                    addMarker(LatLng(latLngs[0].toDouble(), latLngs[1].toDouble()))
                }
            }
        }
    }

    /**
     * 初始化高德地图
     */
    private fun initAmap(savedInstanceState: Bundle?) {
        mapView.onCreate(savedInstanceState)
        if (aMap == null) {
            aMap = mapView.map
        }

        var latLng = intent.getStringExtra("latLng")
        latLng = latLng.replace("E", "")
        latLng = latLng.replace("N", "")
        val lagLngs = latLng.split(",")
        aMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lagLngs[0].toDouble(), lagLngs[1].toDouble()), zoom))
        aMap!!.uiSettings.isMyLocationButtonEnabled = false // 设置默认定位按钮是否显示
        aMap!!.uiSettings.isZoomControlsEnabled = false
        aMap!!.uiSettings.isRotateGesturesEnabled = false
        aMap!!.setOnMapClickListener(this)
        aMap!!.setOnMarkerClickListener(this)
        aMap!!.setInfoWindowAdapter(this)
        aMap!!.setOnMapLoadedListener {
            addMarker(LatLng(lagLngs[0].toDouble(), lagLngs[1].toDouble()))
        }
    }

    /**
     * 通过经纬度获取地理位置信息
     * @param lat
     * @param lng
     */
    private fun searchAddrByLatLng(lat: Double, lng: Double) {
        //latLonPoint参数表示一个Latlng，第二参数表示范围多少米，GeocodeSearch.AMAP表示是国测局坐标系还是GPS原生坐标系
        val query = RegeocodeQuery(LatLonPoint(lat, lng), 200.0f, GeocodeSearch.AMAP)
        geocoderSearch!!.getFromLocationAsyn(query)
    }

    override fun onGeocodeSearched(p0: GeocodeResult?, p1: Int) {
    }

    override fun onRegeocodeSearched(result: RegeocodeResult?, rCode: Int) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result?.regeocodeAddress != null && result.regeocodeAddress.formatAddress != null) {
                addr = if (result.regeocodeAddress.city.contains(result.regeocodeAddress.province)) {
                    result.regeocodeAddress.city + result.regeocodeAddress.district + result.regeocodeAddress.streetNumber
                } else {
                    result.regeocodeAddress.province + result.regeocodeAddress.city + result.regeocodeAddress.district + result.regeocodeAddress.streetNumber
                }
            }
        }
    }

    private fun addMarker(latLng: LatLng?) {
        searchAddrByLatLng(latLng!!.latitude, latLng!!.longitude)
        val options = MarkerOptions()
        options.position(LatLng(BigDecimal(latLng!!.latitude).setScale(6, BigDecimal.ROUND_HALF_UP).toDouble(), BigDecimal(latLng!!.longitude).setScale(6, BigDecimal.ROUND_HALF_UP).toDouble()))
        options.anchor(0.5f, 1.0f)
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location))
        if (clickMarker != null) {
            clickMarker!!.remove()
        }
        clickMarker = aMap!!.addMarker(options)
        aMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
    }

    override fun onMapClick(latLng: LatLng?) {
        addMarker(latLng)
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        if (marker != null) {
            clickMarker = marker
            if (clickMarker!!.isInfoWindowShown) {
                clickMarker!!.hideInfoWindow()
            } else {
                marker.showInfoWindow()
            }
        }
        return true
    }

    override fun getInfoWindow(marker: Marker?): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View? {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.layout_marker_info, null)
        view.tvName.text = "${marker.position.latitude}N,${marker.position.longitude}E"
        return view
    }

    /**
     * 方法必须重写
     */
    override fun onResume() {
        super.onResume()
        if (mapView != null) {
            mapView!!.onResume()
        }
    }

    /**
     * 方法必须重写
     */
    override fun onPause() {
        super.onPause()
        if (mapView != null) {
            mapView!!.onPause()
        }
    }

    /**
     * 方法必须重写
     */
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (mapView != null) {
            mapView!!.onSaveInstanceState(outState)
        }
    }

    /**
     * 方法必须重写
     */
    override fun onDestroy() {
        super.onDestroy()
        if (mapView != null) {
            mapView!!.onDestroy()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.llBack -> finish()
            R.id.tvControl -> {
                val intent = Intent()
                intent.putExtra("latLng", "${clickMarker!!.position.latitude}N,${clickMarker!!.position.longitude}E")
                intent.putExtra("position", addr)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

}
