package com.cxwl.shawn.guangxi.ganzhe;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
import com.cxwl.shawn.guangxi.ganzhe.util.OkHttpUtil;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 智慧服务-智慧灌溉-地块信息
 */
public class ShawnLandActivity extends ShawnBaseActivity implements View.OnClickListener {

    private Context mContext;
    private MapView mapView;
    private AMap aMap;
    private AVLoadingIndicatorView loadingView;
    private TextView tvLand1,tvLand2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shawn_activity_land);
        mContext = this;
        initWidget();
        initAmap(savedInstanceState);
    }

    /**
     * 初始化高德地图
     */
    private void initAmap(Bundle savedInstanceState) {
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
        }

//        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, zoom));
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                OkHttpLand();
            }
        });
    }



    private void initWidget() {
        LinearLayout llBack = findViewById(R.id.llBack);
        llBack.setOnClickListener(this);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("地块信息");
        loadingView = findViewById(R.id.loadingView);
        tvLand1 = findViewById(R.id.tvLand1);
        tvLand2 = findViewById(R.id.tvLand2);
    }

    /**
     * 获取地块信息
     */
    private void OkHttpLand() {
        final String url = "http://decision-admin.tianqi.cn/home/other/gxgz_zt_areas";
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtil.enqueue(new Request.Builder().url(url).build(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        final String result = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!TextUtils.isEmpty(result)) {
                                    try {
                                        String land1Name = "", land2Name = "";
                                        JSONArray array = new JSONArray(result);
                                        LatLngBounds.Builder builder = LatLngBounds.builder();
                                        for (int i = 0; i < array.length(); i++) {
                                            JSONObject itemObj = array.getJSONObject(i);
                                            int code = itemObj.getInt("code");
                                            String name = itemObj.getString("name");
                                            if (!itemObj.isNull("data")) {
                                                JSONArray dataArray = itemObj.getJSONArray("data");
                                                List<LatLng> latLngs = new ArrayList<>();
                                                double minLat = 0,minLng = 0,maxLat = 0,maxLng = 0;
                                                for (int j = 0; j < dataArray.length(); j++) {
                                                    JSONObject dataObj = dataArray.getJSONObject(j);
                                                    double lat = dataObj.getDouble("lat");
                                                    double lng = dataObj.getDouble("lng");
                                                    latLngs.add(new LatLng(lat, lng));
                                                    builder.include(new LatLng(lat, lng));

                                                    if (j == 0) {
                                                        minLat = lat;
                                                        minLng = lng;
                                                        maxLat = lat;
                                                        maxLng = lng;
                                                    }
                                                    if (maxLat <= lat) {
                                                        maxLat = lat;
                                                    }
                                                    if (maxLng <= lng) {
                                                        maxLng = lng;
                                                    }
                                                    if (minLat >= lat) {
                                                        minLat = lat;
                                                    }
                                                    if (minLng >= lng) {
                                                        minLng = lng;
                                                    }
                                                }
                                                PolygonOptions polygonOptions = new PolygonOptions();
                                                polygonOptions.addAll(latLngs);
                                                polygonOptions.strokeWidth(2);
                                                polygonOptions.strokeColor(getResources().getColor(R.color.colorPrimary));
                                                if (code <= 5) {
                                                    land1Name = land1Name+name+"、";
                                                    polygonOptions.fillColor(0xffE6FF75);
                                                }else {
                                                    land2Name = land2Name+name+"、";
                                                    polygonOptions.fillColor(0xffF955FF);
                                                }
                                                Polygon polygon = aMap.addPolygon(polygonOptions);
                                                polygon.setZIndex(-1000);

                                                TextOptions textOptions = new TextOptions();
                                                textOptions.text(name);
                                                textOptions.position(new LatLng((minLat+maxLat)/2, (minLng+maxLng)/2));
                                                textOptions.fontSize(30);
                                                if (code <= 5) {
                                                    textOptions.fontColor(Color.RED);
                                                }else {
                                                    textOptions.fontColor(Color.YELLOW);
                                                }
                                                textOptions.backgroundColor(Color.TRANSPARENT);
                                                Text text = aMap.addText(textOptions);
                                                text.setZIndex(-1000);
                                            }
                                        }
                                        tvLand1.setText("阀门A："+land1Name.substring(0, land1Name.length()-1));
                                        tvLand2.setText("阀门B："+land2Name.substring(0, land2Name.length()-1));
                                        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                loadingView.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }
        }).start();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llBack:
                finish();
                break;
        }
    }

}
