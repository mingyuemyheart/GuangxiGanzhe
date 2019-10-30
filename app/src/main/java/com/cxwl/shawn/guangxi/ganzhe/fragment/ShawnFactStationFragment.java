package com.cxwl.shawn.guangxi.ganzhe.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.R;
import com.cxwl.shawn.guangxi.ganzhe.adapter.ShawnSelectStationAdapter;
import com.cxwl.shawn.guangxi.ganzhe.dto.FactDto;
import com.cxwl.shawn.guangxi.ganzhe.util.OkHttpUtil;
import com.cxwl.shawn.guangxi.ganzhe.view.FactStationHumidityView;
import com.cxwl.shawn.guangxi.ganzhe.view.FactStationLandView;
import com.cxwl.shawn.guangxi.ganzhe.view.FactStationRainView;
import com.cxwl.shawn.guangxi.ganzhe.view.FactStationSpeedView;
import com.cxwl.shawn.guangxi.ganzhe.view.FactStationTempView;
import com.cxwl.shawn.guangxi.ganzhe.view.FactStationWaterView;
import com.wang.avi.AVLoadingIndicatorView;

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
 * 蔗田监测-田间实况-站点查询
 */
public class ShawnFactStationFragment extends Fragment implements View.OnClickListener {

	private ShawnSelectStationAdapter stationAdapter;
	private List<FactDto> stations = new ArrayList<>();
	private TextView tvStation;
	private String stationId;
	private List<FactDto> lastStations = new ArrayList<>();//上一年30天内数据
	private List<FactDto> currentStations = new ArrayList<>();//当前年30年内数据
	private LinearLayout llContainer1,llContainer2,llContainer3,llContainer4,llContainer5,llContainer6;
	private int width;
	private AVLoadingIndicatorView loadingView;
	private Handler mHandler = new Handler();

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.shawn_fragment_fact_station, null);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initWidget(view);
	}

	private void initWidget(View view) {
		loadingView = view.findViewById(R.id.loadingView);
		LinearLayout llStation = view.findViewById(R.id.llStation);
		llStation.setOnClickListener(this);
		tvStation = view.findViewById(R.id.tvStation);
		llContainer1 = view.findViewById(R.id.llContainer1);
		llContainer2 = view.findViewById(R.id.llContainer2);
		llContainer3 = view.findViewById(R.id.llContainer3);
		llContainer4 = view.findViewById(R.id.llContainer4);
		llContainer5 = view.findViewById(R.id.llContainer5);
		llContainer6 = view.findViewById(R.id.llContainer6);

		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;

		OkHttpStations();
	}

	/**
	 * 选择站点
	 * @param context
	 */
	private void selectStation(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View stationView = inflater.inflate(R.layout.shawn_dialog_select_station, null);
		ListView listView = stationView.findViewById(R.id.listView);
		stationAdapter = new ShawnSelectStationAdapter(getActivity(), stations);
		listView.setAdapter(stationAdapter);

		final Dialog dialog = new Dialog(context, R.style.CustomProgressDialog);
		dialog.setContentView(stationView);
		dialog.show();

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				dialog.dismiss();
				FactDto dto = stations.get(arg2);
				tvStation.setText(dto.stationName);
				stationId = dto.stationId;
				OkHttpLast(stationId);
			}
		});
	}

	/**
	 * 获取站点信息
	 */
	private void OkHttpStations() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final String url = "http://113.16.174.77:8076/gzqx/dates/getid?";
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
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								if (!TextUtils.isEmpty(result)) {
									try {
										stations.clear();
										JSONArray array = new JSONArray(result);
										for (int i = 0; i < array.length(); i++) {
											JSONObject itemObj = array.getJSONObject(i);
											FactDto dto = new FactDto();
											dto.stationId = itemObj.getString("STATION_NUMBER");
											dto.stationName = itemObj.getString("STATION_NAME");
											stations.add(dto);

											if (i == 0) {
												tvStation.setText(dto.stationName);
												stationId = dto.stationId;
												OkHttpLast(stationId);
											}
										}
										if (stationAdapter != null) {
											stationAdapter.notifyDataSetChanged();
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

	/**
	 * 获取上一年单站数据
	 * @param stationId
	 */
	private void OkHttpLast(final String stationId) {
		loadingView.setVisibility(View.VISIBLE);
		final String url = "http://113.16.174.77:8076/gzqx/dates/gettjls?id="+stationId;
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
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								loadingView.setVisibility(View.GONE);
								if (!TextUtils.isEmpty(result)) {
									try {
										lastStations.clear();
										JSONArray array = new JSONArray(result);
										for (int i = 0; i < array.length(); i++) {
											JSONObject itemObj = array.getJSONObject(i);
											FactDto dto = new FactDto();

//											if (!itemObj.isNull("OBSERVATION_DATA_DATE")) {
//												dto.time = itemObj.getString("OBSERVATION_DATA_DATE");
//											}

											if (!itemObj.isNull("TEMP")) {
												dto.TEMPL = itemObj.getString("TEMP");
												if (dto.TEMPL.contains("9999")) {
													dto.TEMPL = "--";
												}
											}else {
												dto.TEMPL = "--";
											}

											if (!itemObj.isNull("RELA_HUMI")) {
												dto.RELA_HUMIL = itemObj.getString("RELA_HUMI");
												if (dto.RELA_HUMIL.contains("9999")) {
													dto.RELA_HUMIL = "--";
												}
											}else {
												dto.RELA_HUMIL = "--";
											}

											if (!itemObj.isNull("BUCKET_ACC_RAIN_COUNT")) {
												dto.BUCKET_ACC_RAIN_COUNTL = itemObj.getString("BUCKET_ACC_RAIN_COUNT");
												if (dto.BUCKET_ACC_RAIN_COUNTL.contains("9999")) {
													dto.BUCKET_ACC_RAIN_COUNTL = "--";
												}
											}else {
												dto.BUCKET_ACC_RAIN_COUNTL = "--";
											}

											if (!itemObj.isNull("SMVP_5CM_AVE")) {
												dto.SMVP_5CM_AVEL = itemObj.getString("SMVP_5CM_AVE");
												if (dto.SMVP_5CM_AVEL.contains("9999")) {
													dto.SMVP_5CM_AVEL = "--";
												}
											}else {
												dto.SMVP_5CM_AVEL = "--";
											}

											if (!itemObj.isNull("AVE_WS_2MIN")) {
												dto.AVE_WS_2MINL = itemObj.getString("AVE_WS_2MIN");
												if (dto.AVE_WS_2MINL.contains("9999")) {
													dto.AVE_WS_2MINL = "--";
												}
											}else {
												dto.AVE_WS_2MINL = "--";
											}

											if (!itemObj.isNull("LAND_PT_TEMP")) {
												dto.LAND_PT_TEMPL = itemObj.getString("LAND_PT_TEMP");
												if (dto.LAND_PT_TEMPL.contains("9999")) {
													dto.LAND_PT_TEMPL = "--";
												}
											}else {
												dto.LAND_PT_TEMPL = "--";
											}

											lastStations.add(dto);
										}

										OkHttpCurrent(stationId);

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

	/**
	 * 获取当前年单站数据
	 * @param stationId
	 */
	private void OkHttpCurrent(String stationId) {
		loadingView.setVisibility(View.VISIBLE);
		final String url = "http://113.16.174.77:8076/gzqx/dates/gettj?id="+stationId;
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
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								loadingView.setVisibility(View.GONE);
								if (!TextUtils.isEmpty(result)) {
									try {
										currentStations.clear();
										JSONArray array = new JSONArray(result);
										for (int i = 0; i < array.length(); i++) {
											JSONObject itemObj = array.getJSONObject(i);
											FactDto dto = new FactDto();

											if (!itemObj.isNull("OBSERVATION_DATA_DATE")) {
												dto.time = itemObj.getString("OBSERVATION_DATA_DATE");
											}

											if (!itemObj.isNull("TEMP")) {
												dto.TEMP = itemObj.getString("TEMP");
												if (dto.TEMP.contains("9999")) {
													dto.TEMP = "--";
												}
											}else {
												dto.TEMP = "--";
											}

											if (!itemObj.isNull("RELA_HUMI")) {
												dto.RELA_HUMI = itemObj.getString("RELA_HUMI");
												if (dto.RELA_HUMI.contains("9999")) {
													dto.RELA_HUMI = "--";
												}
											}else {
												dto.RELA_HUMI = "--";
											}

											if (!itemObj.isNull("BUCKET_ACC_RAIN_COUNT")) {
												dto.BUCKET_ACC_RAIN_COUNT = itemObj.getString("BUCKET_ACC_RAIN_COUNT");
												if (dto.BUCKET_ACC_RAIN_COUNT.contains("9999")) {
													dto.BUCKET_ACC_RAIN_COUNT = "--";
												}
											}else {
												dto.BUCKET_ACC_RAIN_COUNT = "--";
											}

											if (!itemObj.isNull("SMVP_5CM_AVE")) {
												dto.SMVP_5CM_AVE = itemObj.getString("SMVP_5CM_AVE");
												if (dto.SMVP_5CM_AVE.contains("9999")) {
													dto.SMVP_5CM_AVE = "--";
												}
											}else {
												dto.SMVP_5CM_AVE = "--";
											}

											if (!itemObj.isNull("AVE_WS_2MIN")) {
												dto.AVE_WS_2MIN = itemObj.getString("AVE_WS_2MIN");
												if (dto.AVE_WS_2MIN.contains("9999")) {
													dto.AVE_WS_2MIN = "--";
												}
											}else {
												dto.AVE_WS_2MIN = "--";
											}

											if (!itemObj.isNull("LAND_PT_TEMP")) {
												dto.LAND_PT_TEMP = itemObj.getString("LAND_PT_TEMP");
												if (dto.LAND_PT_TEMP.contains("9999")) {
													dto.LAND_PT_TEMP = "--";
												}
											}else {
												dto.LAND_PT_TEMP = "--";
											}

											currentStations.add(dto);
										}

										FactStationTempView factStationTempView = new FactStationTempView(getActivity());
										factStationTempView.setData(lastStations, currentStations);
										llContainer1.removeAllViews();
										llContainer1.addView(factStationTempView, width*3, width/2);

										FactStationHumidityView factStationHumidityView = new FactStationHumidityView(getActivity());
										factStationHumidityView.setData(lastStations, currentStations);
										llContainer2.removeAllViews();
										llContainer2.addView(factStationHumidityView, width*3, width/2);

										FactStationRainView factStationRainView = new FactStationRainView(getActivity());
										factStationRainView.setData(lastStations, currentStations);
										llContainer3.removeAllViews();
										llContainer3.addView(factStationRainView, width*3, width/2);

										FactStationWaterView factStationWaterView = new FactStationWaterView(getActivity());
										factStationWaterView.setData(lastStations, currentStations);
										llContainer4.removeAllViews();
										llContainer4.addView(factStationWaterView, width*3, width/2);

										FactStationSpeedView factStationSpeedView = new FactStationSpeedView(getActivity());
										factStationSpeedView.setData(lastStations, currentStations);
										llContainer5.removeAllViews();
										llContainer5.addView(factStationSpeedView, width*3, width/2);

										FactStationLandView factStationLandView = new FactStationLandView(getActivity());
										factStationLandView.setData(lastStations, currentStations);
										llContainer6.removeAllViews();
										llContainer6.addView(factStationLandView, width*3, width/2);

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
			case R.id.llStation:
				selectStation(getActivity());
				break;
		}
	}

}
