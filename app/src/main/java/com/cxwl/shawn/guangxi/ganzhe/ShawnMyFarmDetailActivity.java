package com.cxwl.shawn.guangxi.ganzhe;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.adapter.ShawnFactImageAdapter;
import com.cxwl.shawn.guangxi.ganzhe.common.CONST;
import com.cxwl.shawn.guangxi.ganzhe.dto.ColumnData;
import com.cxwl.shawn.guangxi.ganzhe.dto.FactDto;
import com.cxwl.shawn.guangxi.ganzhe.util.OkHttpUtil;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 交流互动-我的农场-详情
 */
public class ShawnMyFarmDetailActivity extends ShawnBaseActivity implements View.OnClickListener {

    private ImageView imageView;
    private TextView tvStationName,tvRecordTime,tvTemp60,tvTemp150,tvTemp300,tvHumidity60,tvHumidity150,tvHumidity300;
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0", Locale.CHINA);
    private List<FactDto> dataList = new ArrayList<>();
    private ScrollView scrollView;
    private AVLoadingIndicatorView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shawn_activity_my_farm_detail);
        initWidget();
    }

    private void initWidget() {
        loadingView = findViewById(R.id.loadingView);
        LinearLayout llBack = findViewById(R.id.llBack);
        llBack.setOnClickListener(this);
        TextView tvTitle = findViewById(R.id.tvTitle);
        imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(this);
        imageView.setFocusable(true);
        imageView.setFocusableInTouchMode(true);
        imageView.requestFocus();
        tvStationName = findViewById(R.id.tvStationName);
        tvRecordTime = findViewById(R.id.tvRecordTime);
        scrollView = findViewById(R.id.scrollView);
        tvTemp60 = findViewById(R.id.tvTemp60);
        tvTemp150 = findViewById(R.id.tvTemp150);
        tvTemp300 = findViewById(R.id.tvTemp300);
        tvHumidity60 = findViewById(R.id.tvHumidity60);
        tvHumidity150 = findViewById(R.id.tvHumidity150);
        tvHumidity300 = findViewById(R.id.tvHumidity300);
        TextView tvControl = findViewById(R.id.tvControl);
        tvControl.setText("智能灌溉");
        tvControl.setVisibility(View.VISIBLE);
        tvControl.setOnClickListener(this);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = width;
        params.height = width*3/4;
        imageView.setLayoutParams(params);

        String title = getIntent().getStringExtra(CONST.ACTIVITY_NAME);
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }

        OkHttpFact();
    }

    /**
     * 获取蔗田实况数据
     */
    private void OkHttpFact() {
        final String url = "http://113.16.174.77:8076/gzqx/dates/getstid?";
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
                                        dataList.clear();
                                        JSONArray array = new JSONArray(result);
                                        for (int i = 0; i < array.length(); i++) {
                                            FactDto dto = new FactDto();
                                            JSONObject itemObj = array.getJSONObject(i);
                                            if (!itemObj.isNull("Station_Id")) {
                                                dto.stationId = itemObj.getString("Station_Id");
                                            }
                                            if (!itemObj.isNull("Station_Name")) {
                                                dto.stationName = itemObj.getString("Station_Name");
                                            }
                                            if (!itemObj.isNull("Recod_time")) {
                                                dto.time = itemObj.getString("Recod_time");
                                            }
                                            if (!itemObj.isNull("ioc")) {
                                                dto.imgUrl = itemObj.getString("ioc");
                                            }
                                            if (!itemObj.isNull("iocc")) {
                                                dto.imgUrlThumb = itemObj.getString("iocc");
                                            }
                                            if (!itemObj.isNull("tem_air_60cm")) {
                                                dto.temp60 = itemObj.getString("tem_air_60cm")+"℃";
                                                if (dto.temp60.contains("9999")) {
                                                    dto.temp60 = "--";
                                                }
                                            }
                                            if (!itemObj.isNull("tem_air_150cm")) {
                                                dto.temp150 = itemObj.getString("tem_air_150cm")+"℃";
                                                if (dto.temp150.contains("9999")) {
                                                    dto.temp150 = "--";
                                                }
                                            }
                                            if (!itemObj.isNull("tem_air_300cm")) {
                                                dto.temp300 = itemObj.getString("tem_air_300cm")+"℃";
                                                if (dto.temp300.contains("9999")) {
                                                    dto.temp300 = "--";
                                                }
                                            }
                                            if (!itemObj.isNull("Rh_air_60cm")) {
                                                dto.humidity60 = itemObj.getString("Rh_air_60cm")+"%";
                                                if (dto.humidity60.contains("9999")) {
                                                    dto.humidity60 = "--";
                                                }
                                            }
                                            if (!itemObj.isNull("Rh_air_150cm")) {
                                                dto.humidity150 = itemObj.getString("Rh_air_150cm")+"%";
                                                if (dto.humidity150.contains("9999")) {
                                                    dto.humidity150 = "--";
                                                }
                                            }
                                            if (!itemObj.isNull("Rh_air_300cm")) {
                                                dto.humidity300 = itemObj.getString("Rh_air_300cm")+"%";
                                                if (dto.humidity300.contains("9999")) {
                                                    dto.humidity300 = "--";
                                                }
                                            }
                                            if (!itemObj.isNull("tem_gro_30cm")) {
                                                dto.grotemp30 = itemObj.getString("tem_gro_30cm")+"℃";
                                                if (dto.grotemp30.contains("9999")) {
                                                    dto.grotemp30 = "--";
                                                }
                                            }
                                            if (!itemObj.isNull("tem_gro_50cm")) {
                                                dto.grotemp50 = itemObj.getString("tem_gro_50cm")+"℃";
                                                if (dto.grotemp50.contains("9999")) {
                                                    dto.grotemp50 = "--";
                                                }
                                            }
                                            if (!itemObj.isNull("WCOS_20cm")) {
                                                dto.grohumidity20 = itemObj.getString("WCOS_20cm")+"%";
                                                if (dto.grohumidity20.contains("9999")) {
                                                    dto.grohumidity20 = "--";
                                                }
                                            }
                                            if (!itemObj.isNull("WCOS_40cm")) {
                                                dto.grohumidity40 = itemObj.getString("WCOS_40cm")+"%";
                                                if (dto.grohumidity40.contains("9999")) {
                                                    dto.grohumidity40 = "--";
                                                }
                                            }

                                            if (!dto.stationName.contains("9999")) {//过滤掉站名为9999的站
                                                if (dto.stationName.contains("甘蔗")) {
                                                    dataList.add(0, dto);
                                                }else {
                                                    dataList.add(dto);
                                                }
                                            }

                                            if (TextUtils.equals(dto.stationId, "FSGZ2")) {
                                                setValue(dto);
                                            }

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

    /**
     * 切换图片信息
     * @param dto
     */
    private void setValue(FactDto dto) {
        if (!TextUtils.isEmpty(dto.imgUrl)) {
            Picasso.get().load(dto.imgUrl).error(R.drawable.shawn_icon_seat_bitmap).into(imageView);
        }
        if (!TextUtils.isEmpty(dto.stationName)) {
            tvStationName.setText(dto.stationName);
        }
        if (!TextUtils.isEmpty(dto.time)) {
            try {
                tvRecordTime.setText(sdf1.format(sdf2.parse(dto.time))+"更新");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(dto.temp60)) {
            tvTemp60.setText("温度(60cm)："+dto.temp60);
        }
        if (!TextUtils.isEmpty(dto.temp150)) {
            tvTemp150.setText("温度(150cm)："+dto.temp150);
        }
        if (!TextUtils.isEmpty(dto.temp300)) {
            tvTemp300.setText("温度(300cm)："+dto.temp300);
        }
        if (!TextUtils.isEmpty(dto.humidity60)) {
            tvHumidity60.setText("湿度(60cm)："+dto.humidity60);
        }
        if (!TextUtils.isEmpty(dto.humidity150)) {
            tvHumidity150.setText("湿度(150cm)："+dto.humidity150);
        }
        if (!TextUtils.isEmpty(dto.humidity300)) {
            tvHumidity300.setText("湿度(300cm)："+dto.humidity300);
        }

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llBack:
                finish();
                break;
            case R.id.tvControl:
                ColumnData dto = new ColumnData();
                List<ColumnData> childList = new ArrayList<>();
                ColumnData data = new ColumnData();
                data.name = "阀门A";
                data.id = "2001";
                data.showType = CONST.LOCAL;
                data.dataUrl = "http://113.16.174.77:8076/gzqx/dates/getallid?deviceid=1&datatype=json";
                childList.add(data);
                data = new ColumnData();
                data.name = "阀门B";
                data.id = "2002";
                data.showType = CONST.LOCAL;
                data.dataUrl = "http://113.16.174.77:8076/gzqx/dates/getallid?deviceid=2&datatype=json";
                childList.add(data);
                dto.child.addAll(childList);

                Intent intent = new Intent(this, ShawnColumnsActivity.class);
                intent.putExtra(CONST.ACTIVITY_NAME, "智能灌溉");
                Bundle bundle = new Bundle();
                bundle.putParcelable("data", dto);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }

}
