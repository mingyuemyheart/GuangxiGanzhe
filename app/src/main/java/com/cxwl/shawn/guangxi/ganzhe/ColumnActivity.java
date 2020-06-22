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

import com.cxwl.shawn.guangxi.ganzhe.adapter.ColumnAdapter;
import com.cxwl.shawn.guangxi.ganzhe.common.CONST;
import com.cxwl.shawn.guangxi.ganzhe.dto.ColumnData;

import java.util.List;

/**
 * 多个栏目
 * 智慧服务
 */
public class ColumnActivity extends ShawnBaseActivity implements View.OnClickListener {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_column);
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
        ColumnData data = getIntent().getParcelableExtra("data");
        if (data == null) {
            return;
        }
        final List<ColumnData> columnList = data.child;
        ListView listView = findViewById(R.id.listView);
        ColumnAdapter mAdapter = new ColumnAdapter(this, columnList);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                ColumnData dto = columnList.get(arg2);
                Intent intent;
                if (TextUtils.equals(dto.showType, CONST.LOCAL)) {
                    if (TextUtils.equals(dto.id, "201")) {//智能灌溉
                        intent = new Intent(mContext, ShawnColumnsActivity.class);
                        intent.putExtra(CONST.ACTIVITY_NAME, dto.name);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("data", dto);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }else if (TextUtils.equals(dto.id, "202") || TextUtils.equals(dto.id, "203") || TextUtils.equals(dto.id, "303")) {//涨势监测、适宜性分析、服务材料
                        intent = new Intent(mContext, ShawnPdfTitleActivity.class);
                        intent.putExtra(CONST.ACTIVITY_NAME, dto.name);
                        intent.putExtra(CONST.WEB_URL, dto.dataUrl);
                        intent.putExtra(CONST.LOCAL_ID, dto.id);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("data", dto);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }else if (TextUtils.equals(dto.id, "301")) {//我的农场
                        intent = new Intent(mContext, ShawnMyFarmActivity.class);
                        intent.putExtra(CONST.ACTIVITY_NAME, dto.name);
                        startActivity(intent);
                    }else if (TextUtils.equals(dto.id, "304")) {//农场上传
                        intent = new Intent(mContext, ShawnFarmPostActivity.class);
                        intent.putExtra(CONST.ACTIVITY_NAME, dto.name);
                        startActivity(intent);
                    }else if (TextUtils.equals(dto.id, "401") || TextUtils.equals(dto.id, "403")) {//农情灾情上报
                        intent = new Intent(mContext, DisasterUploadActivity.class);
                        intent.putExtra(CONST.ACTIVITY_NAME, dto.name);
                        startActivity(intent);
                    }else if (TextUtils.equals(dto.id, "402")) {//历史农情查询
                        intent = new Intent(mContext, DisasterActivity.class);
                        intent.putExtra(CONST.ACTIVITY_NAME, dto.name);
                        intent.putExtra(CONST.WEB_URL, dto.dataUrl);
                        startActivity(intent);
                    }
                }
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
