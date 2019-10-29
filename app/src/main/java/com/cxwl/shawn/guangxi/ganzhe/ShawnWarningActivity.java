package com.cxwl.shawn.guangxi.ganzhe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.adapter.ShawnWarningAdapter;
import com.cxwl.shawn.guangxi.ganzhe.dto.WarningDto;

import java.util.List;

public class ShawnWarningActivity extends ShawnBaseActivity implements View.OnClickListener {

    private TextView tvPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shawn_activity_warning);
        initWidget();
        initListView();
    }

    /**
     * 初始化控件
     */
    private void initWidget() {
        LinearLayout llBack = findViewById(R.id.llBack);
        llBack.setOnClickListener(this);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("预警信息");
        tvPrompt = findViewById(R.id.tvPrompt);
    }

    /**
     * 初始化listview
     */
    private void initListView() {
        final List<WarningDto> warningList = getIntent().getParcelableArrayListExtra("warningList");
        if (warningList == null || warningList.size() <= 0) {
            tvPrompt.setVisibility(View.VISIBLE);
        } else {
            tvPrompt.setVisibility(View.GONE);
        }
        ListView listView = findViewById(R.id.listView);
        ShawnWarningAdapter mAdapter = new ShawnWarningAdapter(this, warningList);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                WarningDto data = warningList.get(arg2);
                Intent intent = new Intent(ShawnWarningActivity.this, ShawnWarningDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("data", data);
                intent.putExtras(bundle);
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
