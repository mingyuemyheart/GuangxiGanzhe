package com.cxwl.shawn.guangxi.ganzhe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.adapter.ShawnMyFarmAdapter;
import com.cxwl.shawn.guangxi.ganzhe.common.CONST;
import com.cxwl.shawn.guangxi.ganzhe.dto.FactDto;

import java.util.ArrayList;
import java.util.List;

/**
 * 交流互动-我的农场
 */
public class ShawnMyFarmActivity extends ShawnBaseActivity implements View.OnClickListener {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shawn_activity_my_farm);
        mContext = this;
        initWidget();
        initListView();
    }

    private void initWidget() {
        LinearLayout llBack = findViewById(R.id.llBack);
        llBack.setOnClickListener(this);
        TextView tvTitle = findViewById(R.id.tvTitle);

        String title = getIntent().getStringExtra(CONST.ACTIVITY_NAME);
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
    }

    private void initListView() {
        final List<FactDto> dataList = new ArrayList<>();
        FactDto dto = new FactDto();
        dto.title = "甘蔗气象智能灌溉试验区";
        dataList.add(dto);

        ListView listView = findViewById(R.id.listView);
        ShawnMyFarmAdapter mAdapter = new ShawnMyFarmAdapter(this, dataList);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FactDto dto = dataList.get(position);
                Intent intent = new Intent(mContext, ShawnMyFarmDetailActivity.class);
                intent.putExtra(CONST.ACTIVITY_NAME, dto.title);
                startActivity(intent);
            }
        });
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
