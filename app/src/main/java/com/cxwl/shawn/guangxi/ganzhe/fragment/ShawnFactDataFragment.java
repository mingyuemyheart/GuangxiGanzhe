package com.cxwl.shawn.guangxi.ganzhe.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import wheelview.NumericWheelAdapter;
import wheelview.OnWheelScrollListener;
import wheelview.WheelView;

/**
 * 蔗田监测-田间实况-相关数据查询
 */
public class ShawnFactDataFragment extends Fragment implements View.OnClickListener {

	private TextView tvPrompt;
	private ShawnFactDataAdapter mAdapter;
	private List<FactDto> dataList = new ArrayList<>();
	private AVLoadingIndicatorView loadingView;
	private RelativeLayout layoutDate;
	private TextView tvDay, tvHour;
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHH", Locale.CHINA);
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
	private SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.shawn_fragment_fact_data, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initWidget(view);
		initWheelView(view);
		initListView(view);
	}

	private void initWidget(View view) {
		loadingView = view.findViewById(R.id.loadingView);
		tvPrompt = view.findViewById(R.id.tvPrompt);

		layoutDate = view.findViewById(R.id.layoutDate);
		LinearLayout llTime = view.findViewById(R.id.llTime);
		llTime.setOnClickListener(this);
		tvDay = view.findViewById(R.id.tvDay);
		tvHour = view.findViewById(R.id.tvHour);
		TextView tvNegtive = view.findViewById(R.id.tvNegtive);
		tvNegtive.setOnClickListener(this);
		TextView tvPositive = view.findViewById(R.id.tvPositive);
		tvPositive.setOnClickListener(this);

		String date = sdf1.format(new Date().getTime()-1000*60*60);
		try {
			tvDay.setText(sdf2.format(sdf3.parse(date.substring(0, 8))));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		tvHour.setText(date.substring(8, 10)+"时");
		OkHttpList(date);
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
	private void OkHttpList(String date) {
		loadingView.setVisibility(View.VISIBLE);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.llTime:
			case R.id.tvNegtive:
				bootTimeLayoutAnimation();
				break;
			case R.id.tvPositive:
				bootTimeLayoutAnimation();
				setTextViewValue();
				break;
		}
	}

	private WheelView year, month, day, hour;
	private void initWheelView(View view) {
		Calendar c = Calendar.getInstance();
		int curYear = c.get(Calendar.YEAR);
		int curMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
		int curDate = c.get(Calendar.DATE);
		int curHour = c.get(Calendar.HOUR_OF_DAY);

		year = view.findViewById(R.id.year);
		NumericWheelAdapter numericWheelAdapter1=new NumericWheelAdapter(getActivity(),1950, curYear);
		numericWheelAdapter1.setLabel("年");
		year.setViewAdapter(numericWheelAdapter1);
		year.setCyclic(false);//是否可循环滑动
		year.addScrollingListener(scrollListener);
		year.setVisibleItems(7);

		month = view.findViewById(R.id.month);
		NumericWheelAdapter numericWheelAdapter2=new NumericWheelAdapter(getActivity(),1, 12, "%02d");
		numericWheelAdapter2.setLabel("月");
		month.setViewAdapter(numericWheelAdapter2);
		month.setCyclic(false);
		month.addScrollingListener(scrollListener);
		month.setVisibleItems(7);

		day = view.findViewById(R.id.day);
		initDay(curYear,curMonth);
		day.setCyclic(false);
		day.setVisibleItems(7);

		hour = view.findViewById(R.id.hour);
		NumericWheelAdapter numericWheelAdapter3=new NumericWheelAdapter(getActivity(),1, 23, "%02d");
		numericWheelAdapter3.setLabel("时");
		hour.setViewAdapter(numericWheelAdapter3);
		hour.setCyclic(false);
		hour.addScrollingListener(scrollListener);
		hour.setVisibleItems(7);

		year.setCurrentItem(curYear - 1950);
		month.setCurrentItem(curMonth - 1);
		day.setCurrentItem(curDate - 1);
		hour.setCurrentItem(curHour - 1);
	}

	private OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
		@Override
		public void onScrollingStarted(WheelView wheel) {
		}
		@Override
		public void onScrollingFinished(WheelView wheel) {
			int n_year = year.getCurrentItem() + 1950;//年
			int n_month = month.getCurrentItem() + 1;//月
			initDay(n_year,n_month);
		}
	};

	/**
	 */
	private void initDay(int arg1, int arg2) {
		NumericWheelAdapter numericWheelAdapter=new NumericWheelAdapter(getActivity(),1, getDay(arg1, arg2), "%02d");
		numericWheelAdapter.setLabel("日");
		day.setViewAdapter(numericWheelAdapter);
	}

	/**
	 *
	 * @param year
	 * @param month
	 * @return
	 */
	private int getDay(int year, int month) {
		int day = 30;
		boolean flag = false;
		switch (year % 4) {
			case 0:
				flag = true;
				break;
			default:
				flag = false;
				break;
		}
		switch (month) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				day = 31;
				break;
			case 2:
				day = flag ? 29 : 28;
				break;
			default:
				day = 30;
				break;
		}
		return day;
	}

	/**
	 */
	private void setTextViewValue() {
		String yearStr = String.valueOf(year.getCurrentItem()+1950);
		String monthStr = String.valueOf((month.getCurrentItem() + 1) < 10 ? "0" + (month.getCurrentItem() + 1) : (month.getCurrentItem() + 1));
		String dayStr = String.valueOf(((day.getCurrentItem()+1) < 10) ? "0" + (day.getCurrentItem()+1) : (day.getCurrentItem()+1));
		String hourStr = String.valueOf(((hour.getCurrentItem()+1) < 10) ? "0" + (hour.getCurrentItem()+1) : (hour.getCurrentItem()+1));

		tvDay.setText(yearStr+"年"+monthStr+"月"+dayStr+"日");
		tvHour.setText(hourStr+"时");
		OkHttpList(yearStr+monthStr+dayStr+hourStr);
	}

	private void bootTimeLayoutAnimation() {
		if (layoutDate.getVisibility() == View.GONE) {
			timeLayoutAnimation(true, layoutDate);
			layoutDate.setVisibility(View.VISIBLE);
		}else {
			timeLayoutAnimation(false, layoutDate);
			layoutDate.setVisibility(View.GONE);
		}
	}

	/**
	 * 时间图层动画
	 * @param flag
	 * @param view
	 */
	private void timeLayoutAnimation(boolean flag, final View view) {
		//列表动画
		AnimationSet animationSet = new AnimationSet(true);
		TranslateAnimation animation;
		if (!flag) {
			animation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF,0f,
					Animation.RELATIVE_TO_SELF,0f,
					Animation.RELATIVE_TO_SELF,0f,
					Animation.RELATIVE_TO_SELF,1f);
		}else {
			animation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF,0f,
					Animation.RELATIVE_TO_SELF,0f,
					Animation.RELATIVE_TO_SELF,1f,
					Animation.RELATIVE_TO_SELF,0f);
		}
		animation.setDuration(400);
		animationSet.addAnimation(animation);
		animationSet.setFillAfter(true);
		view.startAnimation(animationSet);
		animationSet.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			@Override
			public void onAnimationEnd(Animation arg0) {
				view.clearAnimation();
			}
		});
	}

}
