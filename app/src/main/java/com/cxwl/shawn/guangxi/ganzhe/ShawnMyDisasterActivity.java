package com.cxwl.shawn.guangxi.ganzhe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cxwl.shawn.guangxi.ganzhe.adapter.ShawnMyDisasterAdapter;
import com.cxwl.shawn.guangxi.ganzhe.common.CONST;
import com.cxwl.shawn.guangxi.ganzhe.dto.DisasterDto;
import com.cxwl.shawn.guangxi.ganzhe.util.OkHttpUtil;

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
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import wheelview.NumericWheelAdapter;
import wheelview.OnWheelScrollListener;
import wheelview.WheelView;

/**
 * 我上传的灾情反馈
 */
public class ShawnMyDisasterActivity extends ShawnBaseActivity implements OnClickListener {

	private int page = 1;
	private ShawnMyDisasterAdapter mAdapter;
	private List<DisasterDto> dataList = new ArrayList<>();
	private SwipeRefreshLayout refreshLayout;//下拉刷新布局
	private EditText etSearch;
	private TextView tvControl,tvStart,tvStartTime,tvEnd,tvEndTime;
	private RelativeLayout layoutDate;
	private boolean isStart = true;
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_my_disaster);
		initRefreshLayout();
		initWidget();
		initListView();
		initWheelView();
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
		etSearch = findViewById(R.id.etSearch);
		TextView tvSearch = findViewById(R.id.tvSearch);
		tvSearch.setOnClickListener(this);
		tvControl = findViewById(R.id.tvControl);
		tvControl.setText("时间段");
		tvControl.setVisibility(View.VISIBLE);
		tvControl.setOnClickListener(this);
		tvStart = findViewById(R.id.tvStart);
		tvStartTime = findViewById(R.id.tvStartTime);
		tvStartTime.setOnClickListener(this);
		tvStartTime.setText(sdf1.format(new Date()));
		tvEnd = findViewById(R.id.tvEnd);
		tvEndTime = findViewById(R.id.tvEndTime);
		tvEndTime.setOnClickListener(this);
		tvEndTime.setText(sdf1.format(new Date()));
		layoutDate = findViewById(R.id.layoutDate);
		TextView tvNegtive = findViewById(R.id.tvNegtive);
		tvNegtive.setOnClickListener(this);
		TextView tvPositive = findViewById(R.id.tvPositive);
		tvPositive.setOnClickListener(this);

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
		refreshLayout.setVisibility(View.VISIBLE);
		final String url = "http://decision-admin.tianqi.cn/home/work2019/decisionZqfkSelect";
		FormBody.Builder builder = new FormBody.Builder();
		builder.add("page", page+"");
		builder.add("key", etSearch.getText().toString());
		if (TextUtils.equals("时间段", tvControl.getText().toString())) {
			builder.add("sel_time", tvStartTime.getText().toString());
		} else {
			builder.add("st", tvStartTime.getText().toString());
			builder.add("et", tvEndTime.getText().toString());
		}
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
												if (!itemObj.isNull("latlon")) {
													dto.latlon = itemObj.getString("latlon");
												}
												if (!itemObj.isNull("addtime")) {
													dto.time = itemObj.getString("addtime");
												}
												if (!itemObj.isNull("other_param1")) {
													dto.miao = itemObj.getString("other_param1");
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
				if (TextUtils.equals("时间段", tvControl.getText().toString())) {
					tvControl.setText("选择时间");
					tvStart.setText("开始时间：");
					tvEnd.setVisibility(View.VISIBLE);
					tvEndTime.setVisibility(View.VISIBLE);
				} else {
					tvControl.setText("时间段");
					tvStart.setText("选择时间：");
					tvEnd.setVisibility(View.GONE);
					tvEndTime.setVisibility(View.GONE);
				}
				break;
			case R.id.tvStartTime:
				isStart = true;
				bootTimeLayoutAnimation();
				break;
			case R.id.tvNegtive:
				bootTimeLayoutAnimation();
				break;
			case R.id.tvPositive:
				bootTimeLayoutAnimation();
				if (isStart) {
					tvStartTime.setText(setTextViewValue());
				} else {
					tvEndTime.setText(setTextViewValue());
				}
				break;
			case R.id.tvEndTime:
				isStart = false;
				bootTimeLayoutAnimation();
				break;
			case R.id.tvSearch:
				if (TextUtils.equals("时间段", tvControl.getText().toString())) {
					try {
						long start = sdf1.parse(tvStartTime.getText().toString()).getTime();
						long end = sdf1.parse(tvEndTime.getText().toString()).getTime();
						if (start > end) {
							Toast.makeText(ShawnMyDisasterActivity.this, "开始时间不能大于结束时间", Toast.LENGTH_SHORT).show();
							return;
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}

				dataList.clear();
				OkHttpList();
				break;
		}
	}

	private WheelView year, month, day;
	private void initWheelView() {
		Calendar c = Calendar.getInstance();
		int curYear = c.get(Calendar.YEAR);
		int curMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
		int curDate = c.get(Calendar.DATE);

		year = findViewById(R.id.year);
		NumericWheelAdapter numericWheelAdapter1=new NumericWheelAdapter(this,1950, curYear);
		numericWheelAdapter1.setLabel("年");
		year.setViewAdapter(numericWheelAdapter1);
		year.setCyclic(false);//是否可循环滑动
		year.addScrollingListener(scrollListener);
		year.setVisibleItems(7);

		month = findViewById(R.id.month);
		NumericWheelAdapter numericWheelAdapter2=new NumericWheelAdapter(this,1, 12, "%02d");
		numericWheelAdapter2.setLabel("月");
		month.setViewAdapter(numericWheelAdapter2);
		month.setCyclic(false);
		month.addScrollingListener(scrollListener);
		month.setVisibleItems(7);

		day = findViewById(R.id.day);
		initDay(curYear,curMonth);
		day.setCyclic(false);
		day.setVisibleItems(7);

		year.setCurrentItem(curYear - 1950);
		month.setCurrentItem(curMonth - 1);
		day.setCurrentItem(curDate - 1);
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
		NumericWheelAdapter numericWheelAdapter=new NumericWheelAdapter(this,1, getDay(arg1, arg2), "%02d");
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
	private String setTextViewValue() {
		String yearStr = String.valueOf(year.getCurrentItem()+1950);
		String monthStr = String.valueOf((month.getCurrentItem() + 1) < 10 ? "0" + (month.getCurrentItem() + 1) : (month.getCurrentItem() + 1));
		String dayStr = String.valueOf(((day.getCurrentItem()+1) < 10) ? "0" + (day.getCurrentItem()+1) : (day.getCurrentItem()+1));
		return yearStr+"-"+monthStr+"-"+dayStr;
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
