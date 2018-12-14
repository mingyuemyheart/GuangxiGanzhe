package com.cxwl.shawn.guangxi.ganzhe.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.R;
import com.cxwl.shawn.guangxi.ganzhe.adapter.ShawnFactImageAdapter;
import com.cxwl.shawn.guangxi.ganzhe.dto.FactDto;
import com.cxwl.shawn.guangxi.ganzhe.view.ScrollviewGridview;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 田间实况-蔗田实景
 */
public class ShawnFactImageFragment extends Fragment implements View.OnClickListener {

    private ImageView imageView;
    private TextView tvStationName,tvRecordTime;
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0", Locale.CHINA);
    private ShawnFactImageAdapter mAdapter;
    private List<FactDto> dataList = new ArrayList<>();
    private ScrollView scrollView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shawn_fragment_fact_image, null);
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
        imageView = view.findViewById(R.id.imageView);
        imageView.setOnClickListener(this);
        imageView.setFocusable(true);
        imageView.setFocusableInTouchMode(true);
        imageView.requestFocus();
        tvStationName = view.findViewById(R.id.tvStationName);
        tvRecordTime = view.findViewById(R.id.tvRecordTime);
        scrollView = view.findViewById(R.id.scrollView);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = width;
        params.height = width*3/4;
        imageView.setLayoutParams(params);

        dataList.clear();
        dataList.addAll(getArguments().<FactDto>getParcelableArrayList("dataList"));
        if (dataList.size() > 0) {
            FactDto dto = dataList.get(0);
            dataList.get(0).isSelected = true;
            setValue(dto);
        }

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 初始化gridview
     */
    private void initGridView(View view) {
        ScrollviewGridview gridView = view.findViewById(R.id.gridView);
        mAdapter = new ShawnFactImageAdapter(getActivity(), dataList);
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

            default:
                break;
        }
    }

}
