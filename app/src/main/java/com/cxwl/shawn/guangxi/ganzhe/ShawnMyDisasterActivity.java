package com.cxwl.shawn.guangxi.ganzhe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.adapter.ShawnMyDisasterAdapter;
import com.cxwl.shawn.guangxi.ganzhe.common.CONST;
import com.cxwl.shawn.guangxi.ganzhe.dto.DisasterDto;
import com.cxwl.shawn.guangxi.ganzhe.util.OkHttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 我上传的灾情反馈
 */
public class ShawnMyDisasterActivity extends ShawnBaseActivity implements OnClickListener {

	private int page = 1;
	private ShawnMyDisasterAdapter mAdapter;
	private List<DisasterDto> dataList = new ArrayList<>();
	private SwipeRefreshLayout refreshLayout;//下拉刷新布局
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_my_disaster);
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
				refresh();
			}
		});
	}

	private void refresh() {
		dataList.clear();
		page = 1;
		OkHttpList();
	}
	
	private void initWidget() {
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		TextView tvTitle = findViewById(R.id.tvTitle);
		TextView tvControl = findViewById(R.id.tvControl);
		tvControl.setText("灾情上报");
		tvControl.setVisibility(View.VISIBLE);
		tvControl.setOnClickListener(this);

		String title = getIntent().getStringExtra(CONST.ACTIVITY_NAME);
		if (!TextUtils.isEmpty(title)) {
			tvTitle.setText(title);
		}

		refresh();
	}

	private void initListView() {
		ListView listView = findViewById(R.id.listView);
		mAdapter = new ShawnMyDisasterAdapter(this, dataList);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				DisasterDto data = dataList.get(position);
				Intent intent = new Intent(ShawnMyDisasterActivity.this, ShawnMyDisasterDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable("data", data);
				intent.putExtras(bundle);
				startActivity(intent);

			}
		});
		listView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && view.getLastVisiblePosition() == view.getCount() - 1) {
					page++;
					OkHttpList();
				}
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			}
		});
	}

	private void OkHttpList() {
		final String url = "http://decision-admin.tianqi.cn/home/work2019/decisionZqfkSelect";
		FormBody.Builder builder = new FormBody.Builder();
		builder.add("page", page+"");
        final RequestBody body = builder.build();
		new Thread(new Runnable() {
			@Override
			public void run() {
				OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
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
								refreshLayout.setRefreshing(false);
								if (!TextUtils.isEmpty(result)) {
									try {
										JSONObject obj = new JSONObject(result);
										if (!obj.isNull("data")) {
											JSONArray array = obj.getJSONArray("data");
											for (int i = 0; i < array.length(); i++) {
												JSONObject itemObj = array.getJSONObject(i);
												DisasterDto dto = new DisasterDto();
												if (!itemObj.isNull("title")) {
													dto.title = itemObj.getString("title");
												}
												if (!itemObj.isNull("content")) {
													dto.content = itemObj.getString("content");
												}
												if (!itemObj.isNull("type")) {
													dto.disasterType = itemObj.getString("type");
												}
												if (!itemObj.isNull("location")) {
													dto.addr = itemObj.getString("location");
												}
												if (!itemObj.isNull("addtime")) {
													dto.time = itemObj.getString("addtime");
												}
												if (!itemObj.isNull("pic")) {
													JSONArray imgArray = itemObj.getJSONArray("pic");
													for (int j = 0; j < imgArray.length(); j++) {
														dto.imgList.add(imgArray.getString(j));
													}
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
			case R.id.tvControl:
				startActivityForResult(new Intent(this, ShawnDisasterUploadActivity.class), 1001);
				break;

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case 1001: {
					refresh();
				}
			}
		}
	}
}
