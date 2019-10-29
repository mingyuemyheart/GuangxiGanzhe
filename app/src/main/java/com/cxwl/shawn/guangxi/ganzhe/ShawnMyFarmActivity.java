package com.cxwl.shawn.guangxi.ganzhe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.adapter.ShawnMyFarmAdapter;
import com.cxwl.shawn.guangxi.ganzhe.common.CONST;
import com.cxwl.shawn.guangxi.ganzhe.dto.FarmDto;
import com.cxwl.shawn.guangxi.ganzhe.util.OkHttpUtil;

import org.jetbrains.annotations.NotNull;
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
 * 交流互动-我的农场
 */
public class ShawnMyFarmActivity extends ShawnBaseActivity implements View.OnClickListener {

    private Context mContext;
    private ShawnMyFarmAdapter mAdapter;
    private List<FarmDto> dataList = new ArrayList<>();
    private SwipeRefreshLayout refreshLayout;//下拉刷新布局

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shawn_activity_my_farm);
        mContext = this;
        initRefreshLayout();
        initWidget();
        initListView();
    }

    /**
     * 初始化下拉刷新布局
     */
    private void initRefreshLayout() {
        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeResources(CONST.color1, CONST.color2, CONST.color3, CONST.color4);
        refreshLayout.setProgressViewEndTarget(true, 400);
        refreshLayout.setRefreshing(true);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                OkHttpList();
            }
        });
    }

    private void initWidget() {
        LinearLayout llBack = findViewById(R.id.llBack);
        llBack.setOnClickListener(this);
        TextView tvTitle = findViewById(R.id.tvTitle);

        String title = getIntent().getStringExtra(CONST.ACTIVITY_NAME);
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }

        OkHttpList();
    }

    private void initListView() {
        ListView listView = findViewById(R.id.listView);
        mAdapter = new ShawnMyFarmAdapter(this, dataList);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FarmDto dto = dataList.get(position);
                Intent intent = new Intent(mContext, ShawnMyFarmDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("data", dto);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void OkHttpList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String url = "https://decision-admin.tianqi.cn/Home/work2019/gxgz_get_upload_imgs";
                OkHttpUtil.enqueue(new Request.Builder().url(url).build(), new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    }
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        final String result = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshLayout.setRefreshing(false);
                                if (!TextUtils.isEmpty(result)) {
                                    try {
                                        dataList.clear();
                                        JSONArray array = new JSONArray(result);
                                        for (int i = 0; i < array.length(); i++) {
                                            FarmDto dto = new FarmDto();
                                            JSONObject itemObj = array.getJSONObject(i);
                                            dto.name = itemObj.getString("name");
                                            dto.imgUrl = itemObj.getString("img_url");
                                            dto.addr = itemObj.getString("addr");
                                            dto.type = itemObj.getString("farm_type");
                                            dto.area = itemObj.getString("farm_area");
                                            dto.period = itemObj.getString("growth_period");
                                            dto.output = itemObj.getString("output");
                                            dto.manager = itemObj.getString("farm_manager");
                                            dto.disInfo = itemObj.getString("dis_info");
                                            if (!itemObj.isNull("imgs")) {
                                                JSONArray imgs = itemObj.getJSONArray("imgs");
                                                for (int j = 0; j < imgs.length(); j++) {
                                                    dto.imgUrls.add(imgs.getString(j));
                                                }
                                            }
                                            dataList.add(dto);
                                        }

                                        if (mAdapter != null) {
                                            mAdapter.notifyDataSetChanged();
                                        }
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
            case R.id.llBack:
                finish();
                break;
        }
    }

}
