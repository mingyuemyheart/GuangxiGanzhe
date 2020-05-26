package com.cxwl.shawn.guangxi.ganzhe.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cxwl.shawn.guangxi.ganzhe.R;
import com.cxwl.shawn.guangxi.ganzhe.ShawnForeActivity;
import com.cxwl.shawn.guangxi.ganzhe.dto.FactDto;
import com.cxwl.shawn.guangxi.ganzhe.util.OkHttpUtil;
import com.cxwl.shawn.guangxi.ganzhe.view.TempView;
import com.cxwl.shawn.guangxi.ganzhe.view.WaterView;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 智能灌溉
 */
public class ShawnGuangaiFragment extends Fragment implements View.OnClickListener {

    private ImageView ivOpen,ivClose,ivRefresh;
    private TextView tvOpen,tvClose,tvRefresh,tvForeGuangai,tvParkName,tvDeviceName,tvDeviceState,tvSpeed,tvStationId,tvWaterId,tvClass,tvType,tvBorn,tvCover,tvAddr,tvWater,tvTemp,tvChartName;
    private List<FactDto> waterList = new ArrayList<>();
    private List<FactDto> tempList = new ArrayList<>();
    private int width;
    private LinearLayout llContainer1,llContainer2;
    private AVLoadingIndicatorView loadingView;
    private String valve_id = "3880";//阀门id
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
    private LinearLayout llBar1,llBar2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.shawn_fragment_guangai, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWidget(view);
    }

    private void initWidget(View view) {
        loadingView = view.findViewById(R.id.loadingView);
        llContainer1 = view.findViewById(R.id.llContainer1);
        llContainer2 = view.findViewById(R.id.llContainer2);
        tvOpen = view.findViewById(R.id.tvOpen);
        tvOpen.setOnClickListener(this);
        tvParkName = view.findViewById(R.id.tvParkName);
        tvDeviceName = view.findViewById(R.id.tvDeviceName);
        tvDeviceState = view.findViewById(R.id.tvDeviceState);
        tvSpeed = view.findViewById(R.id.tvSpeed);
        tvStationId = view.findViewById(R.id.tvStationId);
        tvWaterId = view.findViewById(R.id.tvWaterId);
        tvClass = view.findViewById(R.id.tvClass);
        tvType = view.findViewById(R.id.tvType);
        tvBorn = view.findViewById(R.id.tvBorn);
        tvCover = view.findViewById(R.id.tvCover);
        tvAddr = view.findViewById(R.id.tvAddr);
        tvWater = view.findViewById(R.id.tvWater);
        tvWater.setOnClickListener(this);
        tvTemp = view.findViewById(R.id.tvTemp);
        tvTemp.setOnClickListener(this);
        tvChartName = view.findViewById(R.id.tvChartName);
        llBar1 = view.findViewById(R.id.llBar1);
        llBar2 = view.findViewById(R.id.llBar2);
        ivOpen = view.findViewById(R.id.ivOpen);
        ivOpen.setOnClickListener(this);
        ivRefresh = view.findViewById(R.id.ivRefresh);
        ivRefresh.setOnClickListener(this);
        tvRefresh = view.findViewById(R.id.tvRefresh);
        tvRefresh.setOnClickListener(this);
        ivClose = view.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(this);
        tvClose = view.findViewById(R.id.tvClose);
        tvClose.setOnClickListener(this);
        tvForeGuangai = view.findViewById(R.id.tvForeGuangai);
        tvForeGuangai.setOnClickListener(this);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;

        valve_id = getArguments().getString("valve_id");
        tvChartName.setText(sdf2.format(new Date())+"逐小时土壤体积含水量曲线图");

        okHttpBase();
    }

    /**
     * 操作阀门开关
     */
    private void okHttpSwitch(final String isOpen) {
        loadingView.setVisibility(View.VISIBLE);
        final String url = String.format("http://113.16.174.77:8076/gzqx/dates/getkg?ValveID=%s&operate=%s", valve_id, isOpen);
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
                        if (!isAdded() || TextUtils.isEmpty(result)) {
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject obj = new JSONObject(result);
                                    if (!obj.isNull("status")) {
                                        String status = obj.getString("status");
                                        if (TextUtils.equals(status, "200")) {
                                            okHttpBase();
                                        }
                                    }
                                    if (!obj.isNull("msg")) {
                                        String msg = obj.getString("msg");
                                        if (!TextUtils.isEmpty(msg)) {
                                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        }).start();
    }

    /**
     * 获取基本信息
     */
    private void okHttpBase() {
        final String url = "http://113.16.174.77:8076/gzqx/dates/getjbxx";
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
                        if (!isAdded() || TextUtils.isEmpty(result)) {
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONArray array = new JSONArray(result);
                                    if (array.length() > 1) {
                                        for (int i = 0; i < array.length(); i++) {
                                            JSONObject itemObj = array.getJSONObject(i);
                                            if (TextUtils.equals(valve_id, itemObj.getString("ValveID"))) {
                                                tvParkName.setText(itemObj.getString("AreaName"));
                                                tvDeviceName.setText(itemObj.getString("DeviceName"));
                                                tvSpeed.setText(itemObj.getString("WaterSpeed"));
                                                tvStationId.setText(itemObj.getString("Bind_Stat"));
                                                tvWaterId.setText(itemObj.getString("Bind_Soil_Stat"));
                                                tvClass.setText(itemObj.getString("CropTYpe"));
                                                tvType.setText(itemObj.getString("SoilType"));

                                                tvCover.setText(itemObj.getString("FGD"));
                                                tvAddr.setText(itemObj.getString("Place"));

                                                okHttpState(itemObj.getString("ValveID"));
                                                okHttpWater(itemObj.getString("Bind_Soil_Stat"));
                                                okHttpTemp(itemObj.getString("Bind_Stat"));
                                                break;
                                            }
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        }).start();
    }

    /**
     * 检查阀门状态
     */
    private void okHttpState(String valveId) {
        ivRefresh.setImageResource(R.drawable.icon_refresh_press);
        tvRefresh.setVisibility(View.VISIBLE);
        final String url = "http://113.16.174.77:8076/gzqx/dates/getzt?ValveID="+valveId;
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
                        if (!isAdded() || TextUtils.isEmpty(result)) {
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ivRefresh.setImageResource(R.drawable.icon_refresh);
                                try {
                                    JSONObject obj = new JSONObject(result);
                                    if (!obj.isNull("is_open")) {
                                        String status = obj.getString("is_open");
                                        if (TextUtils.equals(status, "1")) {
                                            ivOpen.setImageResource(R.drawable.icon_open);
                                            ivClose.setImageResource(R.drawable.icon_close);
                                            tvDeviceState.setText("开启");
                                        } else {
                                            ivOpen.setImageResource(R.drawable.icon_close);
                                            ivClose.setImageResource(R.drawable.icon_open);
                                            tvDeviceState.setText("关闭");
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        }).start();
    }

    /**
     * 获取土壤体积含水量数据
     */
    private void okHttpWater(String waterId) {
        final String url = "http://113.16.174.77:8076/gzqx/dates/gethsl?Bind_Soil_Stat="+waterId;
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
                        if (!isAdded()) {
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingView.setVisibility(View.GONE);
                                waterList.clear();
                                if (!TextUtils.isEmpty(result)) {
                                    try {
                                        JSONArray array = new JSONArray(result);
                                        for (int i = 0; i < array.length(); i++) {
                                            JSONObject obj = array.getJSONObject(i);
                                            FactDto dto = new FactDto();
                                            if (!obj.isNull("SMVP_10CM_AVE")) {
                                                dto.SMVP_10CM_AVE = obj.getString("SMVP_10CM_AVE");
                                            } else {
                                                dto.SMVP_10CM_AVE = "0.0";
                                            }
                                            if (!obj.isNull("SMVP_20CM_AVE")) {
                                                dto.SMVP_20CM_AVE = obj.getString("SMVP_20CM_AVE");
                                            }else {
                                                dto.SMVP_20CM_AVE = "0.0";
                                            }
                                            if (!obj.isNull("SMVP_30CM_AVE")) {
                                                dto.SMVP_30CM_AVE = obj.getString("SMVP_30CM_AVE");
                                            }else {
                                                dto.SMVP_30CM_AVE = "0.0";
                                            }
                                            if (!obj.isNull("SMVP_40CM_AVE")) {
                                                dto.SMVP_40CM_AVE = obj.getString("SMVP_40CM_AVE");
                                            }else {
                                                dto.SMVP_40CM_AVE = "0.0";
                                            }
                                            if (!obj.isNull("SMVP_50CM_AVE")) {
                                                dto.SMVP_50CM_AVE = obj.getString("SMVP_50CM_AVE");
                                            }else {
                                                dto.SMVP_50CM_AVE = "0.0";
                                            }
                                            if (!obj.isNull("OBSERVATION_DATA_DATE")) {
                                                dto.time = obj.getString("OBSERVATION_DATA_DATE");
                                            }
                                            waterList.add(dto);
                                        }

                                        WaterView waterView = new WaterView(getActivity());
                                        waterView.setData(waterList);
                                        llContainer1.removeAllViews();
                                        llContainer1.addView(waterView, width, width/2);
                                        llBar1.setVisibility(View.VISIBLE);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                });
            }
        }).start();
    }

    /**
     * 获取温度数据
     */
    private void okHttpTemp(String stationId) {
        final String url = "http://113.16.174.77:8076/gzqx/dates/getwdd?Bind_Stat="+stationId;
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
                        if (!isAdded()) {
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingView.setVisibility(View.GONE);
                                tempList.clear();
                                if (!TextUtils.isEmpty(result)) {
                                    try {
                                        JSONArray array = new JSONArray(result);
                                        for (int i = 0; i < array.length(); i++) {
                                            JSONObject obj = array.getJSONObject(i);
                                            FactDto dto = new FactDto();
                                            if (!obj.isNull("V12001")) {
                                                dto.SMVP_10CM_AVE = (Float.valueOf(obj.getString("V12001"))/10)+"";
                                            } else {
                                                dto.SMVP_10CM_AVE = "0.0";
                                            }
                                            if (!obj.isNull("V12052")) {
                                                dto.SMVP_20CM_AVE = (Float.valueOf(obj.getString("V12052"))/10)+"";
                                            }else {
                                                dto.SMVP_20CM_AVE = "0.0";
                                            }
                                            if (!obj.isNull("V12053")) {
                                                dto.SMVP_30CM_AVE = (Float.valueOf(obj.getString("V12053"))/10)+"";
                                            }else {
                                                dto.SMVP_30CM_AVE = "0.0";
                                            }
                                            if (!obj.isNull("VDATE")) {
                                                dto.time = obj.getString("VDATE");
                                            }
                                            tempList.add(dto);
                                        }

                                        TempView tempView = new TempView(getActivity());
                                        tempView.setData(tempList);
                                        llContainer2.removeAllViews();
                                        llContainer2.addView(tempView, width, width/2);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
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
            case R.id.ivOpen:
            case R.id.tvOpen:
                okHttpSwitch("1");
                break;
            case R.id.ivClose:
            case R.id.tvClose:
                okHttpSwitch("0");
                break;
            case R.id.ivRefresh:
            case R.id.tvRefresh:
                okHttpBase();
                break;
            case R.id.tvWater:
                tvWater.setTextColor(Color.WHITE);
                tvTemp.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvWater.setBackgroundResource(R.drawable.shawn_bg_corner_left_blue);
                tvTemp.setBackgroundResource(R.drawable.shawn_bg_corner_right_white);
                tvChartName.setText(sdf2.format(new Date())+"逐小时土壤体积含水量曲线图");
                llContainer1.setVisibility(View.VISIBLE);
                llBar1.setVisibility(View.VISIBLE);
                llContainer2.setVisibility(View.GONE);
                llBar2.setVisibility(View.GONE);
                break;
            case R.id.tvTemp:
                tvWater.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvTemp.setTextColor(Color.WHITE);
                tvWater.setBackgroundResource(R.drawable.shawn_bg_corner_left_white);
                tvTemp.setBackgroundResource(R.drawable.shawn_bg_corner_right_blue);
                tvChartName.setText(sdf2.format(new Date())+"逐小时温度曲线图");
                llContainer1.setVisibility(View.GONE);
                llBar1.setVisibility(View.GONE);
                llContainer2.setVisibility(View.VISIBLE);
                llBar2.setVisibility(View.VISIBLE);
                break;
            case R.id.tvForeGuangai:
                Intent intent = new Intent(getActivity(), ShawnForeActivity.class);
                intent.putExtra("valve_id", valve_id);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

}
