package com.cxwl.shawn.guangxi.ganzhe;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.adapter.ShawnForeAdapter;
import com.cxwl.shawn.guangxi.ganzhe.dto.FactDto;
import com.cxwl.shawn.guangxi.ganzhe.util.OkHttpUtil;
import com.wang.avi.AVLoadingIndicatorView;

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
 * 预报灌溉量
 */
public class ShawnForeActivity extends ShawnBaseActivity implements View.OnClickListener {

	private TextView tvPrompt;
	private ShawnForeAdapter mAdapter;
	private List<FactDto> dataList = new ArrayList<>();
	private AVLoadingIndicatorView loadingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_fore);
		initWidget();
		initListView();
	}

	private void initWidget() {
		TextView tvTitle = findViewById(R.id.tvTitle);
		tvTitle.setText("预报灌溉量");
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		loadingView = findViewById(R.id.loadingView);
		tvPrompt = findViewById(R.id.tvPrompt);

		okHttpList(getIntent().getStringExtra("valve_id"));
	}

	private void initListView() {
		ListView listView = findViewById(R.id.listView);
		mAdapter = new ShawnForeAdapter(this, dataList);
		listView.setAdapter(mAdapter);
	}

	/**
	 * 获取蔗田实况数据
	 */
	private void okHttpList(String valveId) {
		loadingView.setVisibility(View.VISIBLE);
		if (TextUtils.equals(valveId, "3880")) {
			valveId = "1";
		} else if (TextUtils.equals(valveId, "3881")) {
			valveId = "2";
		}
		final String url = String.format("http://113.16.174.77:8076/gzqx/dates/getallid?deviceid=%s&datatype=json", valveId);
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
								loadingView.setVisibility(View.GONE);
								if (!TextUtils.isEmpty(result)) {
									try {
										JSONObject obj = new JSONObject(result);
										if (!obj.isNull("DS")) {
											dataList.clear();
											JSONArray array = obj.getJSONArray("DS");
											for (int i = 0; i < array.length(); i++) {
												FactDto dto = new FactDto();
												JSONObject itemObj = array.getJSONObject(i);
												if (!itemObj.isNull("DT")) {
													dto.time = itemObj.getString("DT").replace("T", " ");
												}
												if (!itemObj.isNull("RH")) {
													dto.RH = itemObj.getString("RH");
												}else {
													dto.RH = "--";
												}
												if (!itemObj.isNull("Fc")) {
													dto.Fc = itemObj.getString("Fc");
												} else {
													dto.Fc = "--";
												}
												if (!itemObj.isNull("Fi")) {
													dto.Fi = itemObj.getString("Fi");
												} else {
													dto.Fi = "--";
												}
												if (!itemObj.isNull("P")) {
													dto.P = itemObj.getString("P");
												} else {
													dto.P = "--";
												}
												if (!itemObj.isNull("ET0")) {
													dto.ET0 = itemObj.getString("ET0");
												} else {
													dto.ET0 = "--";
												}
												if (!itemObj.isNull("Kc")) {
													dto.Kc = itemObj.getString("Kc");
												} else {
													dto.Kc = "--";
												}
												if (!itemObj.isNull("ForecastDays")) {
													dto.ForecastDays = itemObj.getString("ForecastDays");
												} else {
													dto.ForecastDays = "--";
												}
												if (!itemObj.isNull("I")) {
													dto.I = itemObj.getString("I");
												} else {
													dto.I = "--";
												}
												dataList.add(dto);
											}
										}

										if (dataList.size() > 0) {
											tvPrompt.setVisibility(View.GONE);
										}else {
											tvPrompt.setVisibility(View.VISIBLE);
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
