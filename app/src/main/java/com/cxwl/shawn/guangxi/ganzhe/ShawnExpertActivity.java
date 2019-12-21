package com.cxwl.shawn.guangxi.ganzhe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.adapter.ShawnExpertAdapter;
import com.cxwl.shawn.guangxi.ganzhe.common.CONST;
import com.cxwl.shawn.guangxi.ganzhe.dto.WADto;
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
 * 交流互动-专家联盟
 * @author shawn_sun
 */
public class ShawnExpertActivity extends ShawnBaseActivity implements OnClickListener{
	
	private Context mContext;
	private ShawnExpertAdapter mAdapter;
	private List<WADto> dataList = new ArrayList<>();
	private TextView tvPrompt;
	private AVLoadingIndicatorView loadingView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_expert);
		mContext = this;
		initWidget();
		initListView();
	}

	private void initWidget() {
		loadingView = findViewById(R.id.loadingView);
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		TextView tvTitle = findViewById(R.id.tvTitle);
		tvPrompt = findViewById(R.id.tvPrompt);

		String title = getIntent().getStringExtra(CONST.ACTIVITY_NAME);
		if (!TextUtils.isEmpty(title)) {
			tvTitle.setText(title);
		}

		OkHttpList();
	}

	private void initListView() {
		ListView listView = findViewById(R.id.listView);
		mAdapter = new ShawnExpertAdapter(mContext, dataList);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				WADto dto = dataList.get(arg2);
				Intent intent = new Intent(mContext, ShawnExpertQuestionActivity.class);
				intent.putExtra(CONST.ACTIVITY_NAME, "问题咨询");
				Bundle bundle = new Bundle();
				bundle.putParcelable("data", dto);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	/**
	 * 获取专家列表
	 */
	private void OkHttpList() {
		final String url = "http://guangxi.decision.tianqi.cn/getAllexpert?appid="+CONST.APPID;
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
										if (!obj.isNull("list")) {
											dataList.clear();
											JSONArray array = obj.getJSONArray("list");
											for (int i = 0; i < array.length(); i++) {
												JSONObject itemObj = array.getJSONObject(i);
												WADto dto = new WADto();
												if (!itemObj.isNull("id")) {
													dto.expertId = itemObj.getString("id");
												}
												if (!itemObj.isNull("userId")) {
													dto.userId = itemObj.getString("userId");
												}
												if (!itemObj.isNull("name")) {
													dto.name = itemObj.getString("name");
												}
												if (!itemObj.isNull("brief")) {
													dto.breif = itemObj.getString("brief");
												}
												if (!itemObj.isNull("tag")) {
													dto.label = itemObj.getString("tag");
												}
												if (!itemObj.isNull("research")) {
													dto.research = itemObj.getString("research");
												}
												if (!itemObj.isNull("tagBrief")) {
													dto.shanchang = itemObj.getString("tagBrief");
												}
												if (!itemObj.isNull("phone")) {
													dto.phone = itemObj.getString("phone");
												}
												if (!itemObj.isNull("picUrl")) {
													JSONArray itemArray = itemObj.getJSONArray("picUrl");
													if (itemArray.length() > 0) {
														dto.imgUrl = itemArray.getString(0);
													}
												}
												dataList.add(dto);
											}

											if (dataList.size() <= 0) {
												tvPrompt.setVisibility(View.VISIBLE);
											}

											if (mAdapter != null) {
												mAdapter.notifyDataSetChanged();
											}

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
		case R.id.llBack:
			finish();
			break;

		default:
			break;
		}
	}

}
