package com.cxwl.shawn.guangxi.ganzhe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.adapter.ShawnExpertQuestionAdapter;
import com.cxwl.shawn.guangxi.ganzhe.common.CONST;
import com.cxwl.shawn.guangxi.ganzhe.dto.WADto;
import com.cxwl.shawn.guangxi.ganzhe.util.OkHttpUtil;
import com.squareup.picasso.Picasso;
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
 * 专家联盟-问题咨询
 * @author shawn_sun
 */
public class ShawnExpertQuestionActivity extends ShawnBaseActivity implements OnClickListener {
	
	private Context mContext;
	private WADto data;
	private ShawnExpertQuestionAdapter mAdapter;
	private List<WADto> dataList = new ArrayList<>();
	private AVLoadingIndicatorView loadingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_expert_question);
		mContext = this;
		initWidget();
		initListView();
	}

	private void refresh() {
		dataList.clear();
		if (!TextUtils.isEmpty(data.expertId)) {
			OkHttpList(data.expertId);
		}
	}
	
	private void initWidget() {
		loadingView = findViewById(R.id.loadingView);
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		TextView tvTitle = findViewById(R.id.tvTitle);
		ImageView ivPortrait = findViewById(R.id.ivPortrait);
		TextView tvName = findViewById(R.id.tvName);
		TextView tvBreif = findViewById(R.id.tvBreif);
		TextView tvPublish = findViewById(R.id.tvPublish);
		tvPublish.setOnClickListener(this);

		String title = getIntent().getStringExtra(CONST.ACTIVITY_NAME);
		if (title != null) {
			tvTitle.setText(title);
		}
		
		data = getIntent().getExtras().getParcelable("data");
		if (data != null) {
			if (!TextUtils.isEmpty(data.imgUrl)) {
				Picasso.get().load(data.imgUrl).error(R.drawable.shawn_icon_seat_bitmap).into(ivPortrait);
			}
			if (!TextUtils.isEmpty(data.name)) {
				tvName.setText(data.name);
			}
			if (!TextUtils.isEmpty(data.breif)) {
				tvBreif.setText(data.breif);
			}

			refresh();
		}

	}
	
	private void initListView() {
		ListView listView = findViewById(R.id.listView);
		mAdapter = new ShawnExpertQuestionAdapter(mContext, dataList);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				WADto dto = dataList.get(position);
				Intent intent = new Intent(mContext, ShawnExpertQuestionDetailActivity.class);
				intent.putExtra(CONST.ACTIVITY_NAME, "问题问答");
				Bundle bundle = new Bundle();
				bundle.putParcelable("data", dto);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	/**
	 * 获取问题列表
	 */
	private void OkHttpList(final String expertId) {
		final String url = String.format("http://guangxi.decision.tianqi.cn/getMsgWithE_id?eid=%s&appid=%s", expertId, CONST.APPID);
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
										if (!obj.isNull("data")) {
											JSONArray array = obj.getJSONArray("data");
											for (int i = 0; i < array.length(); i++) {
												JSONObject itemObj = array.getJSONObject(i);
												WADto dto = new WADto();
												if (!itemObj.isNull("e_id")) {
													dto.expertId = itemObj.getString("e_id");
												}
												if (!itemObj.isNull("id")) {
													dto.questionId = itemObj.getString("id");
												}
												if (!itemObj.isNull("message")) {
													dto.title = itemObj.getString("message");
												}
												if (!itemObj.isNull("m_time")) {
													dto.time = itemObj.getString("m_time");
												}
												if (!itemObj.isNull("picture")) {
													List<String> imgs = new ArrayList<>();
													JSONArray imgArray = itemObj.getJSONArray("picture");
													for (int j = 0; j < imgArray.length(); j++) {
														imgs.add(imgArray.getString(j));
													}
													dto.imgList.addAll(imgs);
												}
												dataList.add(dto);
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
		case R.id.tvPublish:
			Intent intent = new Intent(mContext, ShawnExpertQuestionPostActivity.class);
			intent.putExtra(CONST.ACTIVITY_NAME, "问题发布");
			Bundle bundle = new Bundle();
			bundle.putParcelable("data", data);
			intent.putExtras(bundle);
			startActivityForResult(intent, 1001);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case 1001:
					refresh();
					break;
			}
		}
	}

}
