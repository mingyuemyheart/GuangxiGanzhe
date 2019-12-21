package com.cxwl.shawn.guangxi.ganzhe.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.R;
import com.cxwl.shawn.guangxi.ganzhe.ShawnFactDataDetailActivity;
import com.cxwl.shawn.guangxi.ganzhe.adapter.ShawnFactDataAdapter;
import com.cxwl.shawn.guangxi.ganzhe.dto.FactDto;
import com.cxwl.shawn.guangxi.ganzhe.util.CommonUtil;
import com.cxwl.shawn.guangxi.ganzhe.util.OkHttpUtil;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 蔗田监测-田间实况-相关数据查询
 */
public class ShawnFactDataFragment extends Fragment {

	private TextView tvPrompt;
	private ShawnFactDataAdapter mAdapter;
	private List<FactDto> dataList = new ArrayList<>();
	private AVLoadingIndicatorView loadingView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.shawn_fragment_fact_data, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initWidget(view);
		initListView(view);
	}

	private void initWidget(View view) {
		loadingView = view.findViewById(R.id.loadingView);
		tvPrompt = view.findViewById(R.id.tvPrompt);

		OkHttpList();
	}

	private void initListView(View view) {
		ListView listView = view.findViewById(R.id.listView);
		mAdapter = new ShawnFactDataAdapter(getActivity(), dataList);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				FactDto dto = dataList.get(arg2);
				Intent intent = new Intent(getActivity(), ShawnFactDataDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable("data", dto);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	/**
	 * 获取蔗田实况数据
	 */
	private void OkHttpList() {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHH", Locale.CHINA);
		String date = sdf1.format(new Date().getTime()-1000*60*60);
		final String url = "http://113.16.174.77:8076/gzqx/dates/getgzhom?hom="+date;
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
						if (!isAdded()) {
							return;
						}
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								loadingView.setVisibility(View.GONE);
								if (!TextUtils.isEmpty(result)) {
									try {
										dataList.clear();
										JSONArray array = new JSONArray(result);
										for (int i = 0; i < array.length(); i++) {
											FactDto dto = new FactDto();
											JSONObject itemObj = array.getJSONObject(i);
											if (!itemObj.isNull("STATION_NUMBER")) {
												dto.stationId = itemObj.getString("STATION_NUMBER");
											}
											if (!itemObj.isNull("STATION_NAME")) {
												dto.stationName = itemObj.getString("STATION_NAME");
											}
											if (!itemObj.isNull("TEMP")) {
												dto.TEMP = itemObj.getString("TEMP")+"℃";
											} else {
												dto.TEMP = "--";
											}
											if (!itemObj.isNull("RELA_HUMI")) {
												dto.RELA_HUMI = itemObj.getString("RELA_HUMI")+"%";
											} else {
												dto.RELA_HUMI = "--";
											}
											if (!itemObj.isNull("MOST_WD")) {
												dto.MOST_WD = itemObj.getString("MOST_WD");
												dto.MOST_WD = CommonUtil.getWindDirection(Float.parseFloat(dto.MOST_WD))+"风";
											} else {
												dto.MOST_WD = "--";
											}
											if (!itemObj.isNull("MOST_WS")) {
												dto.MOST_WS = itemObj.getString("MOST_WS")+"m/s";
											} else {
												dto.MOST_WS = "--";
											}
											if (!itemObj.isNull("MAX_WD")) {
												dto.MAX_WD = itemObj.getString("MAX_WD");
												dto.MAX_WD = CommonUtil.getWindDirection(Float.parseFloat(dto.MAX_WD))+"风";
											} else {
												dto.MAX_WD = "--";
											}
											if (!itemObj.isNull("MAX_WS")) {
												dto.MAX_WS = itemObj.getString("MAX_WS")+"m/s";
											} else {
												dto.MAX_WS = "--";
											}
											if (!itemObj.isNull("SMVP_5CM_AVE")) {
												dto.SMVP_5CM_AVE = itemObj.getString("SMVP_5CM_AVE")+"%";
											} else {
												dto.SMVP_5CM_AVE = "--";
											}
											if (!itemObj.isNull("SMVP_10CM_AVE")) {
												dto.SMVP_10CM_AVE = itemObj.getString("SMVP_10CM_AVE")+"%";
											} else {
												dto.SMVP_10CM_AVE = "--";
											}
											if (!itemObj.isNull("SMVP_20CM_AVE")) {
												dto.SMVP_20CM_AVE = itemObj.getString("SMVP_20CM_AVE")+"%";
											} else {
												dto.SMVP_20CM_AVE = "--";
											}
											if (!itemObj.isNull("SMVP_30CM_AVE")) {
												dto.SMVP_30CM_AVE = itemObj.getString("SMVP_30CM_AVE")+"%";
											} else {
												dto.SMVP_30CM_AVE = "--";
											}
											if (!itemObj.isNull("SMVP_40CM_AVE")) {
												dto.SMVP_40CM_AVE = itemObj.getString("SMVP_40CM_AVE")+"%";
											} else {
												dto.SMVP_40CM_AVE = "--";
											}
											if (!itemObj.isNull("LAND_10CM_TEMP")) {
												dto.LAND_10CM_TEMP = itemObj.getString("LAND_10CM_TEMP")+"℃";
											} else {
												dto.LAND_10CM_TEMP = "--";
											}
											if (!itemObj.isNull("LAND_15CM_TEMP")) {
												dto.LAND_15CM_TEMP = itemObj.getString("LAND_15CM_TEMP")+"℃";
											} else {
												dto.LAND_15CM_TEMP = "--";
											}
											if (!itemObj.isNull("LAND_20CM_TEMP")) {
												dto.LAND_20CM_TEMP = itemObj.getString("LAND_20CM_TEMP")+"℃";
											} else {
												dto.LAND_20CM_TEMP = "--";
											}
											if (!itemObj.isNull("LAND_30CM_TEMP")) {
												dto.LAND_30CM_TEMP = itemObj.getString("LAND_30CM_TEMP")+"℃";
											} else {
												dto.LAND_30CM_TEMP = "--";
											}
											if (!itemObj.isNull("LAND_40CM_TEMP")) {
												dto.LAND_40CM_TEMP = itemObj.getString("LAND_40CM_TEMP")+"℃";
											} else {
												dto.LAND_40CM_TEMP = "--";
											}
											dataList.add(dto);
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

}
