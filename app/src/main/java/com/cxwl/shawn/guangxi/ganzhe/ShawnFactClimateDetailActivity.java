package com.cxwl.shawn.guangxi.ganzhe;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.dto.FactDto;
import com.cxwl.shawn.guangxi.ganzhe.util.OkHttpUtil;
import com.cxwl.shawn.guangxi.ganzhe.view.FactHumidity150View;
import com.cxwl.shawn.guangxi.ganzhe.view.FactHumidity300View;
import com.cxwl.shawn.guangxi.ganzhe.view.FactHumidity60View;
import com.cxwl.shawn.guangxi.ganzhe.view.FactTemp150View;
import com.cxwl.shawn.guangxi.ganzhe.view.FactTemp300View;
import com.cxwl.shawn.guangxi.ganzhe.view.FactTemp60View;
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
 * 蔗田监测-田间实况-小气候数据-站点详情界面
 */
public class ShawnFactClimateDetailActivity extends ShawnBaseActivity implements View.OnClickListener {

    private Context mContext;
    private List<FactDto> dataList = new ArrayList<>();
    private LinearLayout llContainer1,llContainer2,llContainer3,llContainer4,llContainer5,llContainer6;
    private int width;
    private AVLoadingIndicatorView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shawn_activity_fact_climate_detail);
        mContext = this;
        initWidget();
    }

    private void initWidget() {
        loadingView = findViewById(R.id.loadingView);
        LinearLayout llBack = findViewById(R.id.llBack);
        llBack.setOnClickListener(this);
        TextView tvTitle = findViewById(R.id.tvTitle);
        llContainer1 = findViewById(R.id.llContainer1);
        llContainer2 = findViewById(R.id.llContainer2);
        llContainer3 = findViewById(R.id.llContainer3);
        llContainer4 = findViewById(R.id.llContainer4);
        llContainer5 = findViewById(R.id.llContainer5);
        llContainer6 = findViewById(R.id.llContainer6);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;

        if (getIntent().hasExtra("data")) {
            FactDto data = getIntent().getParcelableExtra("data");
            if (data != null) {
                if (!TextUtils.isEmpty(data.stationName)) {
                    tvTitle.setText(data.stationName);
                }
                if (!TextUtils.isEmpty(data.stationId)) {
                    OkHttpSingle(data.stationId);
                }
            }
         }
    }

    /**
     * 获取单站数据
     * @param stationId
     */
    private void OkHttpSingle(String stationId) {
        final String url = "http://113.16.174.77:8076/gzqx/dates/getstidday?ids="+stationId;
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
                                        JSONArray array0 = new JSONArray(result);
                                        if (array0.length() > 0) {
                                            dataList.clear();
                                            JSONArray array = array0.getJSONArray(0);
                                            for (int i = 0; i < array.length(); i++) {
                                                JSONObject itemObj = array.getJSONObject(i);
                                                FactDto dto = new FactDto();
                                                if (!itemObj.isNull("Recod_time")) {
                                                    dto.time = itemObj.getString("Recod_time");
                                                }
                                                if (!itemObj.isNull("tem_air_60cm")) {
                                                    dto.temp60 = itemObj.getString("tem_air_60cm");
                                                    if (dto.temp60.contains("9999")) {
                                                        dto.temp60 = "--";
                                                    }
                                                }
                                                if (!itemObj.isNull("tem_air_150cm")) {
                                                    dto.temp150 = itemObj.getString("tem_air_150cm");
                                                    if (dto.temp150.contains("9999")) {
                                                        dto.temp150 = "--";
                                                    }
                                                }
                                                if (!itemObj.isNull("tem_air_300cm")) {
                                                    dto.temp300 = itemObj.getString("tem_air_300cm");
                                                    if (dto.temp300.contains("9999")) {
                                                        dto.temp300 = "--";
                                                    }
                                                }
                                                if (!itemObj.isNull("Rh_air_60cm")) {
                                                    dto.humidity60 = itemObj.getString("Rh_air_60cm");
                                                    if (dto.humidity60.contains("9999")) {
                                                        dto.humidity60 = "--";
                                                    }
                                                }
                                                if (!itemObj.isNull("Rh_air_150cm")) {
                                                    dto.humidity150 = itemObj.getString("Rh_air_150cm");
                                                    if (dto.humidity150.contains("9999")) {
                                                        dto.humidity150 = "--";
                                                    }
                                                }
                                                if (!itemObj.isNull("Rh_air_300cm")) {
                                                    dto.humidity300 = itemObj.getString("Rh_air_300cm");
                                                    if (dto.humidity300.contains("9999")) {
                                                        dto.humidity300 = "--";
                                                    }
                                                }
//                                                if (!itemObj.isNull("tem_gro_30cm")) {
//                                                    dto.grotemp30 = itemObj.getString("tem_gro_30cm");
//                                                    if (dto.grotemp30.contains("9999")) {
//                                                        dto.grotemp30 = "--";
//                                                    }
//                                                }
//                                                if (!itemObj.isNull("tem_gro_50cm")) {
//                                                    dto.grotemp50 = itemObj.getString("tem_gro_50cm");
//                                                    if (dto.grotemp50.contains("9999")) {
//                                                        dto.grotemp50 = "--";
//                                                    }
//                                                }
//                                                if (!itemObj.isNull("WCOS_20cm")) {
//                                                    dto.grohumidity20 = itemObj.getString("WCOS_20cm");
//                                                    if (dto.grohumidity20.contains("9999")) {
//                                                        dto.grohumidity20 = "--";
//                                                    }
//                                                }
//                                                if (!itemObj.isNull("WCOS_40cm")) {
//                                                    dto.grohumidity40 = itemObj.getString("WCOS_40cm");
//                                                    if (dto.grohumidity40.contains("9999")) {
//                                                        dto.grohumidity40 = "--";
//                                                    }
//                                                }

                                                dataList.add(dto);
                                            }

                                            FactTemp60View factTemp60View = new FactTemp60View(mContext);
                                            factTemp60View.setData(dataList);
                                            llContainer1.removeAllViews();
                                            llContainer1.addView(factTemp60View, width*4, width/2);

                                            FactTemp150View factTemp150View = new FactTemp150View(mContext);
                                            factTemp150View.setData(dataList);
                                            llContainer2.removeAllViews();
                                            llContainer2.addView(factTemp150View, width*4, width/2);

                                            FactTemp300View factTemp300View = new FactTemp300View(mContext);
                                            factTemp300View.setData(dataList);
                                            llContainer3.removeAllViews();
                                            llContainer3.addView(factTemp300View, width*4, width/2);

                                            FactHumidity60View factHumidity60View = new FactHumidity60View(mContext);
                                            factHumidity60View.setData(dataList);
                                            llContainer4.removeAllViews();
                                            llContainer4.addView(factHumidity60View, width*4, width/2);

                                            FactHumidity150View factHumidity150View = new FactHumidity150View(mContext);
                                            factHumidity150View.setData(dataList);
                                            llContainer5.removeAllViews();
                                            llContainer5.addView(factHumidity150View, width*4, width/2);

                                            FactHumidity300View factHumidity300View = new FactHumidity300View(mContext);
                                            factHumidity300View.setData(dataList);
                                            llContainer6.removeAllViews();
                                            llContainer6.addView(factHumidity300View, width*4, width/2);

                                        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llBack:
                finish();
                break;
        }
    }

}
