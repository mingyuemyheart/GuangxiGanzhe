package com.cxwl.shawn.guangxi.ganzhe.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cxwl.shawn.guangxi.ganzhe.R;
import com.cxwl.shawn.guangxi.ganzhe.common.CONST;
import com.cxwl.shawn.guangxi.ganzhe.dto.FactDto;
import com.cxwl.shawn.guangxi.ganzhe.util.CommonUtil;
import com.cxwl.shawn.guangxi.ganzhe.util.OkHttpUtil;
import com.cxwl.shawn.guangxi.ganzhe.view.ForeGuangaiView;
import com.cxwl.shawn.guangxi.ganzhe.view.ForeHumidityView;
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
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 智能灌溉
 */
public class ShawnGuangaiFragment extends Fragment implements View.OnClickListener {

    private TextView tvState,tvSwitch;
    private List<FactDto> dataList = new ArrayList<>();
    private int width;
    private LinearLayout llContainer1,llContainer2;
    private AVLoadingIndicatorView loadingView;
    private String valve_id = "3881";//阀门id
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shawn_fragment_guangai, null);
        return view;
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
        tvState = view.findViewById(R.id.tvState);
        tvSwitch = view.findViewById(R.id.tvSwitch);
        tvSwitch.setOnClickListener(this);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;

        valve_id = getArguments().getString("valve_id");

        OkHttpCheck();
        OkHttpList();
    }

    /**
     * 检查阀门状态
     */
    private void OkHttpCheck() {
        final String url = "http://www.jjr.vip/?r=api/post_check";
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("user_id", "8888889031");
        builder.add("user_token", CommonUtil.toMD5("weather888"+sdf1.format(new Date())+"asdcsfdf~!%h"));
        builder.add("device_id", "2222222320");
        builder.add("valve_id", valve_id);
        builder.add("timestamp", new Date().getTime()/1000+"");
        final RequestBody body = builder.build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
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
                                        if (TextUtils.equals(status, "1")) {//成功
                                            if (!obj.isNull("is_open")) {
                                                String is_open = obj.getString("is_open");
                                                if (TextUtils.equals(is_open, "1")) {//开启
                                                    tvState.setText("阀门状态：开启");
                                                    tvSwitch.setText("关闭阀门");
                                                    tvSwitch.setTag(is_open);
                                                }else {
                                                    tvState.setText("阀门状态：关闭");
                                                    tvSwitch.setText("开启阀门");
                                                    tvSwitch.setTag(is_open);
                                                }
                                            }
                                        }else {
                                            if (!obj.isNull("msg")) {
                                                String msg = obj.getString("msg");
                                                if (!TextUtils.isEmpty(msg)) {
                                                    tvState.setText("阀门状态："+msg);
                                                }
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
     * 操作阀门开关
     */
    private void OkHttpSwitch() {
        loadingView.setVisibility(View.VISIBLE);
        final String url = "http://www.jjr.vip/?r=api/post_switch";
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("user_id", "8888889031");
        builder.add("user_token", CommonUtil.toMD5("weather888"+sdf1.format(new Date())+"asdcsfdf~!%h"));
        builder.add("device_id", "2222222320");
        builder.add("valve_id", valve_id);
        builder.add("timestamp", new Date().getTime()/1000+"");
        String tag = tvSwitch.getTag()+"";
        if (TextUtils.equals(tag, "1")) {
            builder.add("operate","0");
        }else {
            builder.add("operate","1");
        }
        final RequestBody body = builder.build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
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
                                    if (!obj.isNull("msg")) {
                                        String msg = obj.getString("msg");
                                        if (!TextUtils.isEmpty(msg)) {
                                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                                            OkHttpCheck();
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
     * 获取30预报灌溉量数据
     */
    private void OkHttpList() {
        final String url = getArguments().getString(CONST.WEB_URL);
        if (TextUtils.isEmpty(url)) {
            loadingView.setVisibility(View.GONE);
            return;
        }
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
                                if (!TextUtils.isEmpty(result)) {
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("DS")) {
                                            dataList.clear();
                                            JSONArray array = obj.getJSONArray("DS");
                                            for (int i = array.length()-1; i >= 0; i--) {
                                                FactDto dto = new FactDto();
                                                JSONObject itemObj = array.getJSONObject(i);
                                                if (!itemObj.isNull("DT")) {
                                                    dto.time = itemObj.getString("DT");
                                                }
                                                if (!itemObj.isNull("I")) {
                                                    String value = itemObj.getString("I");
                                                    if (TextUtils.equals(value, "null")) {
                                                        dto.foreGuangai = 0;
                                                    }else {
                                                        dto.foreGuangai = Float.valueOf(value);
                                                    }
                                                }
                                                if (!itemObj.isNull("RH")) {
                                                    String value = itemObj.getString("RH");
                                                    if (TextUtils.equals(value, "null")) {
                                                        dto.humidity = 0;
                                                    }else {
                                                        dto.humidity = Float.valueOf(value);
                                                    }
                                                }
                                                dataList.add(dto);
                                            }

                                            ForeHumidityView foreHumidityView = new ForeHumidityView(getActivity());
                                            foreHumidityView.setData(dataList);
                                            llContainer1.removeAllViews();
                                            llContainer1.addView(foreHumidityView, width*2, width/2);

                                            ForeGuangaiView foreGuangaiView = new ForeGuangaiView(getActivity());
                                            foreGuangaiView.setData(dataList);
                                            llContainer2.removeAllViews();
                                            llContainer2.addView(foreGuangaiView, width*2, width/2);
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
            case R.id.tvSwitch:
                OkHttpSwitch();
                break;
            default:
                break;
        }
    }

}
