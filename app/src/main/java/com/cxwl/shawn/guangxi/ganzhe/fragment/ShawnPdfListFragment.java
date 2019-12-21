package com.cxwl.shawn.guangxi.ganzhe.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.R;
import com.cxwl.shawn.guangxi.ganzhe.ShawnPDFActivity;
import com.cxwl.shawn.guangxi.ganzhe.ShawnWebviewActivity;
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
 * pdf文档列表
 */
public class ShawnPdfListFragment extends Fragment {
	
	private ShawnPDFListAdapter mAdapter = null;
	private List<ColumnData> dataList = new ArrayList<>();
	private TextView tvPrompt = null;
	private SwipeRefreshLayout refreshLayout;//下拉刷新布局

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.shawn_fragment_pdf_list, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initRefreshLayout(view);
		initWidget(view);
		initListView(view);
	}

	/**
	 * 初始化下拉刷新布局
	 */
	private void initRefreshLayout(View view) {
		refreshLayout = view.findViewById(R.id.refreshLayout);
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

	private void initWidget(View view) {
		tvPrompt = view.findViewById(R.id.tvPrompt);

		refresh();
	}

	private void refresh() {
		dataList.clear();
		String url = getArguments().getString(CONST.WEB_URL);
		if (!TextUtils.isEmpty(url)) {
			OkHttpList(url);
		}
	}

	private void initListView(View view) {
		ListView listView = view.findViewById(R.id.listView);
		mAdapter = new ShawnPDFListAdapter(getActivity(), dataList);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ColumnData dto = dataList.get(arg2);
				Intent intent;
				if (dto.detailUrl.endsWith(".pdf") || dto.detailUrl.endsWith(".PDF")) {
					intent = new Intent(getActivity(), ShawnPDFActivity.class);
				} else {
					intent = new Intent(getActivity(), ShawnWebviewActivity.class);
				}
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
		final String columnId = getArguments().getString(CONST.COLUMN_ID);
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
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (!TextUtils.isEmpty(result)) {
									try {
										if (TextUtils.equals(columnId, "609") || TextUtils.equals(columnId, "570")) {
											JSONArray array = new JSONArray(result);
											for (int i = 0; i < array.length(); i++) {
												JSONObject itemObj = array.getJSONObject(i);
												ColumnData dto = new ColumnData();
												if (!itemObj.isNull("Url")) {
													dto.detailUrl = itemObj.getString("Url");
												}
												if (!itemObj.isNull("Title")) {
													dto.title = itemObj.getString("Title");
												}
												if (!itemObj.isNull("DT")) {
													dto.time = itemObj.getString("DT");
												}
												dataList.add(dto);
											}
										} else {
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
	
}
