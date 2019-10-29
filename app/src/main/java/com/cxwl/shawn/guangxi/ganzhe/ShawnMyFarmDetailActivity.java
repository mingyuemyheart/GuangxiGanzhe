package com.cxwl.shawn.guangxi.ganzhe;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.dto.FarmDto;
import com.squareup.picasso.Picasso;

/**
 * 交流互动-我的农场-详情
 */
public class ShawnMyFarmDetailActivity extends ShawnBaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shawn_activity_my_farm_detail);
        initWidget();
    }

    private void initWidget() {
        LinearLayout llBack = findViewById(R.id.llBack);
        llBack.setOnClickListener(this);
        TextView tvTitle = findViewById(R.id.tvTitle);
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(this);
        imageView.setFocusable(true);
        imageView.setFocusableInTouchMode(true);
        imageView.requestFocus();
        TextView tvName = findViewById(R.id.tvName);
        TextView tvAddr = findViewById(R.id.tvAddr);
        TextView tvType = findViewById(R.id.tvType);
        TextView tvArea = findViewById(R.id.tvArea);
        TextView tvPeriod = findViewById(R.id.tvPeriod);
        TextView tvOutput = findViewById(R.id.tvOutput);
        TextView tvManager = findViewById(R.id.tvManager);
        TextView tvDisInfo = findViewById(R.id.tvDisInfo);
//        TextView tvControl = findViewById(R.id.tvControl);
//        tvControl.setText("智能灌溉");
//        tvControl.setVisibility(View.VISIBLE);
//        tvControl.setOnClickListener(this);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = width;
        params.height = width*3/4;
        imageView.setLayoutParams(params);

        FarmDto data = getIntent().getParcelableExtra("data");
        if (data != null) {
            if (!TextUtils.isEmpty(data.imgUrl)) {
                Picasso.get().load(data.imgUrl).error(R.drawable.shawn_icon_seat_bitmap).into(imageView);
            }
            if (!TextUtils.isEmpty(data.name)) {
                tvTitle.setText(data.name);
                tvName.setText(data.name);
            }
            if (!TextUtils.isEmpty(data.addr)) {
                tvAddr.setText("地址："+data.addr);
            }
            if (!TextUtils.isEmpty(data.type)) {
                tvType.setText("种植类型："+data.type);
            }
            if (!TextUtils.isEmpty(data.area)) {
                tvArea.setText("面积："+data.area);
            }
            if (!TextUtils.isEmpty(data.period)) {
                tvPeriod.setText("生长周期："+data.period);
            }
            if (!TextUtils.isEmpty(data.output)) {
                tvOutput.setText("产量："+data.output);
            }
            if (!TextUtils.isEmpty(data.manager)) {
                tvManager.setText("田间管理："+data.manager);
            }
            if (!TextUtils.isEmpty(data.disInfo)) {
                tvDisInfo.setText("灾害情况："+data.disInfo);
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llBack:
                finish();
                break;
//            case R.id.tvControl:
//                ColumnData dto = new ColumnData();
//                List<ColumnData> childList = new ArrayList<>();
//                ColumnData data = new ColumnData();
//                data.name = "阀门A";
//                data.id = "2001";
//                data.showType = CONST.LOCAL;
//                data.dataUrl = "http://113.16.174.77:8076/gzqx/dates/getallid?deviceid=1&datatype=json";
//                childList.add(data);
//                data = new ColumnData();
//                data.name = "阀门B";
//                data.id = "2002";
//                data.showType = CONST.LOCAL;
//                data.dataUrl = "http://113.16.174.77:8076/gzqx/dates/getallid?deviceid=2&datatype=json";
//                childList.add(data);
//                dto.child.addAll(childList);
//
//                Intent intent = new Intent(this, ShawnColumnsActivity.class);
//                intent.putExtra(CONST.ACTIVITY_NAME, "智能灌溉");
//                Bundle bundle = new Bundle();
//                bundle.putParcelable("data", dto);
//                intent.putExtras(bundle);
//                startActivity(intent);
//                break;
        }
    }

}
