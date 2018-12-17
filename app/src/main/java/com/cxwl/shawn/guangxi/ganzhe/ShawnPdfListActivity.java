package com.cxwl.shawn.guangxi.ganzhe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.adapter.ShawnPDFListAdapter;
import com.cxwl.shawn.guangxi.ganzhe.common.CONST;
import com.cxwl.shawn.guangxi.ganzhe.dto.ColumnData;
import com.cxwl.shawn.guangxi.ganzhe.util.OkHttpUtil;

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
 * 单个pdf文档列表
 */
public class ShawnPdfListActivity extends ShawnBaseActivity implements View.OnClickListener {

	private Context mContext;
	private ShawnPDFListAdapter mAdapter = null;
	private List<ColumnData> dataList = new ArrayList<>();
	private TextView tvPrompt = null;
	private SwipeRefreshLayout refreshLayout;//下拉刷新布局

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_pdf_list);
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
		refreshLayout.post(new Runnable() {
			@Override
			public void run() {
				refreshLayout.setRefreshing(true);
			}
		});
		refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				refresh();
			}
		});
	}

	private void initWidget() {
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		TextView tvTitle = findViewById(R.id.tvTitle);
		tvPrompt = findViewById(R.id.tvPrompt);

		String title = getIntent().getStringExtra(CONST.ACTIVITY_NAME);
		if (!TextUtils.isEmpty(title)) {
			tvTitle.setText(title);
		}

		refresh();
	}

	private void refresh() {
		dataList.clear();
		String url = getIntent().getStringExtra(CONST.WEB_URL);
		if (!TextUtils.isEmpty(url)) {
			OkHttpList(url);
		}
	}

	private void initListView() {
		ListView listView = findViewById(R.id.listView);
		mAdapter = new ShawnPDFListAdapter(mContext, dataList);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ColumnData dto = dataList.get(arg2);
				Intent intent = new Intent(mContext, ShawnPDFActivity.class);
				intent.putExtra(CONST.ACTIVITY_NAME, dto.title);
				intent.putExtra(CONST.WEB_URL, dto.detailUrl);
				startActivity(intent);
			}
		});
	}

	private void OkHttpList(final String url) {
		if (TextUtils.isEmpty(url)) {
			refreshLayout.setRefreshing(false);
			tvPrompt.setVisibility(View.VISIBLE);
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
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (!TextUtils.isEmpty(result)) {
									try {
										JSONObject obj = new JSONObject(result);
										if (!obj.isNull("XYB")) {
											JSONArray array = obj.getJSONArray("XYB");
											for (int i = 0; i < array.length(); i++) {
												ColumnData dto = new ColumnData();
												dto.detailUrl = array.getString(i);
												dto.title = dto.detailUrl;
												String sub = "http://113.16.174.77:8076/gzwd/XYB\\";
												if (!TextUtils.isEmpty(dto.detailUrl) && dto.detailUrl.contains(sub)) {
													dto.title = dto.detailUrl.substring(sub.length(), dto.detailUrl.length()-4);
												}
												dataList.add(dto);
											}
										}else if (!obj.isNull("ZTFW")) {
											JSONArray array = obj.getJSONArray("ZTFW");
											for (int i = 0; i < array.length(); i++) {
												ColumnData dto = new ColumnData();
												dto.detailUrl = array.getString(i);
												dto.title = dto.detailUrl;
												String sub = "http://113.16.174.77:8076/gzwd/ZTFW\\";
												if (!TextUtils.isEmpty(dto.detailUrl) && dto.detailUrl.contains(sub)) {
													dto.title = dto.detailUrl.substring(sub.length(), dto.detailUrl.length()-4);
												}
												dataList.add(dto);
											}
										}else if (!obj.isNull("ZSJC")) {
											JSONArray array = obj.getJSONArray("ZSJC");
											for (int i = 0; i < array.length(); i++) {
												ColumnData dto = new ColumnData();
												dto.detailUrl = array.getString(i);
												dto.title = dto.detailUrl;
												String sub = "http://113.16.174.77:8076/gzwd/ZSJC\\";
												if (!TextUtils.isEmpty(dto.detailUrl) && dto.detailUrl.contains(sub)) {
													dto.title = dto.detailUrl.substring(sub.length(), dto.detailUrl.length()-4);
												}
												dataList.add(dto);
											}
										}else if (!obj.isNull("ZTS")) {
											JSONArray array = obj.getJSONArray("ZTS");
											for (int i = 0; i < array.length(); i++) {
												ColumnData dto = new ColumnData();
												dto.detailUrl = array.getString(i);
												dto.title = dto.detailUrl;
												String sub = "http://113.16.174.77:8076/gzwd/ZTS\\";
												if (!TextUtils.isEmpty(dto.detailUrl) && dto.detailUrl.contains(sub)) {
													dto.title = dto.detailUrl.substring(sub.length(), dto.detailUrl.length()-4);
												}
												dataList.add(dto);
											}
										}else if (!obj.isNull("CLYB")) {
											JSONArray array = obj.getJSONArray("CLYB");
											for (int i = 0; i < array.length(); i++) {
												ColumnData dto = new ColumnData();
												dto.detailUrl = array.getString(i);
												dto.title = dto.detailUrl;
												String sub = "http://113.16.174.77:8076/gzwd/CLYB\\";
												if (!TextUtils.isEmpty(dto.detailUrl) && dto.detailUrl.contains(sub)) {
													dto.title = dto.detailUrl.substring(sub.length(), dto.detailUrl.length()-4);
												}
												dataList.add(dto);
											}
										}

										if (mAdapter != null) {
											mAdapter.notifyDataSetChanged();
										}

										if (dataList.size() == 0) {
											tvPrompt.setVisibility(View.VISIBLE);
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}

								refreshLayout.setRefreshing(false);

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
