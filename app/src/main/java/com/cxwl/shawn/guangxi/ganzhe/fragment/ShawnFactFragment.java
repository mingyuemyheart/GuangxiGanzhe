package com.cxwl.shawn.guangxi.ganzhe.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.R;
import com.cxwl.shawn.guangxi.ganzhe.adapter.ShawnFactAdapter;
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
 * 田间实况
 */
public class ShawnFactFragment extends Fragment implements View.OnClickListener {

    private ImageView imageView;
    private TextView tvStationName,tvRecordTime;
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0", Locale.CHINA);
    private ShawnFactAdapter mAdapter;
    private List<FactDto> dataList = new ArrayList<>();
    private AVLoadingIndicatorView loadingView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shawn_fragment_fact, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWidget(view);
        initGridView(view);
    }

    /**
     * 初始化控件
     */
    private void initWidget(View view) {
        loadingView = view.findViewById(R.id.loadingView);
        imageView = view.findViewById(R.id.imageView);
        imageView.setOnClickListener(this);
        tvStationName = view.findViewById(R.id.tvStationName);
        tvRecordTime = view.findViewById(R.id.tvRecordTime);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = width;
        params.height = width*3/4;
        imageView.setLayoutParams(params);

        OkHttpFact();
    }

    /**
     * 初始化gridview
     */
    private void initGridView(View view) {
        GridView gridView = view.findViewById(R.id.gridView);
        mAdapter = new ShawnFactAdapter(getActivity(), dataList);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
                for (int i = 0; i < dataList.size(); i++) {
                    FactDto dto = dataList.get(i);
                    if (i == arg2) {
                        dto.isSelected = true;
                    }else {
                        dto.isSelected = false;
                    }
                }
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }

                FactDto dto = dataList.get(arg2);
                setValue(dto);

            }
        });
    }

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
    }

    /**
     * 获取蔗田实景数据
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
                        if (!isAdded()) {
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
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
                                            if (i == 0) {
                                                dto.isSelected = true;
                                                setValue(dto);
                                            }else {
                                                dto.isSelected = false;
                                            }

                                            if (!dto.stationName.contains("9999")) {//过滤掉站名为9999的站
                                                dataList.add(dto);
                                            }
                                        }

                                        if (mAdapter != null) {
                                            mAdapter.notifyDataSetChanged();
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

            default:
                break;
        }
    }

}
