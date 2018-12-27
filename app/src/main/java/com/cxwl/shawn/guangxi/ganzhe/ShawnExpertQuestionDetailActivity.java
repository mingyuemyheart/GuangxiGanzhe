package com.cxwl.shawn.guangxi.ganzhe;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.adapter.ShawnExpertQuestionDetailAdapter;
import com.cxwl.shawn.guangxi.ganzhe.common.CONST;
import com.cxwl.shawn.guangxi.ganzhe.common.MyApplication;
import com.cxwl.shawn.guangxi.ganzhe.dto.WADto;
import com.cxwl.shawn.guangxi.ganzhe.util.CommonUtil;
import com.cxwl.shawn.guangxi.ganzhe.util.EmojiMapUtil;
import com.cxwl.shawn.guangxi.ganzhe.util.OkHttpUtil;
import com.cxwl.shawn.guangxi.ganzhe.view.ScrollviewListview;
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
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 专家联盟-问题咨询-问题详情
 * @author shawn_sun
 */
public class ShawnExpertQuestionDetailActivity extends ShawnBaseActivity implements OnClickListener {

	private Context mContext;
	private ShawnExpertQuestionDetailAdapter mAdapter;
	private List<WADto> dataList = new ArrayList<>();
	private WADto data;
	private EditText etContent;
	private ImageView ivClear;
	private TextView tvSubmit;
	private ScrollView scrollView;
	private AVLoadingIndicatorView loadingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_expert_question_detail);
		mContext = this;
		initWidget();
		initListView();
	}

	private void refresh(boolean scrollToDown) {
		dataList.clear();
		if (!TextUtils.isEmpty(data.questionId)) {
			String url = String.format("http://guangxi.decision.tianqi.cn/getCommentWithMid?mid=%s&appid=%s", data.questionId, CONST.APPID);
			OkHttpList(url, scrollToDown);
		}
	}

	private void initWidget() {
		loadingView = findViewById(R.id.loadingView);
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		TextView tvTitle = findViewById(R.id.tvTitle);
		TextView tvAsk = findViewById(R.id.tvAsk);
		TextView tvContent = findViewById(R.id.tvContent);
		TextView tvTime = findViewById(R.id.tvTime);
		LinearLayout llContainer = findViewById(R.id.llContainer);
		etContent = findViewById(R.id.etContent);
		etContent.addTextChangedListener(watcher);
		ivClear = findViewById(R.id.ivClear);
		ivClear.setOnClickListener(this);
		tvSubmit = findViewById(R.id.tvSubmit);
		tvSubmit.setOnClickListener(this);
		scrollView = findViewById(R.id.scrollView);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		float density = dm.density;

		String title = getIntent().getStringExtra(CONST.ACTIVITY_NAME);
		if (title != null) {
			tvTitle.setText(title);
		}

		data = getIntent().getExtras().getParcelable("data");
		if (data != null) {
			if (!TextUtils.isEmpty(data.title)) {
				tvAsk.setText(data.title);
			}
			if (!TextUtils.isEmpty(data.content)) {
				tvContent.setText(data.content);
				tvContent.setVisibility(View.VISIBLE);
			}
			if (!TextUtils.isEmpty(data.time)) {
				tvTime.setText(data.time);
			}

			if (data.imgList.size() > 0) {
				llContainer.setVisibility(View.VISIBLE);
				llContainer.removeAllViews();
				int w = (width-(int)(density*30))/3;
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w, w-(int)(density*10));
				params.rightMargin = (int)(density*5);
				for (int i = 0; i < data.imgList.size(); i++) {
					String imgUrl = data.imgList.get(i);
					if (!TextUtils.isEmpty(imgUrl)) {
						ImageView imageView = new ImageView(mContext);
						imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
						Picasso.get().load(imgUrl).error(R.drawable.shawn_icon_seat_bitmap).into(imageView);
						imageView.setLayoutParams(params);
						llContainer.addView(imageView);
					}
				}
			}

			refresh(false);

		}
	}

	/**
	 * 评论监听
	 */
	private TextWatcher watcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}
		@Override
		public void afterTextChanged(Editable arg0) {
			if (!TextUtils.isEmpty(etContent.getText().toString())) {
				ivClear.setVisibility(View.VISIBLE);
				tvSubmit.setVisibility(View.VISIBLE);
			}else {
				ivClear.setVisibility(View.GONE);
				tvSubmit.setVisibility(View.GONE);
			}
		}
	};

	private void initListView() {
		ScrollviewListview listView = findViewById(R.id.listView);
		mAdapter = new ShawnExpertQuestionDetailAdapter(mContext, dataList);
		listView.setAdapter(mAdapter);
	}

	private void OkHttpList(final String url, final boolean scrollToDown) {
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
												if (!itemObj.isNull("commentary")) {
													dto.content = EmojiMapUtil.replaceCheatSheetEmojis(itemObj.getString("commentary"));
												}
												if (!itemObj.isNull("ctime")) {
													dto.time = itemObj.getString("ctime");
												}
												if (!itemObj.isNull("isexpertreply")) {
													dto.isexpert = itemObj.getString("isexpertreply");
												}
												if (!itemObj.isNull("username")) {
													dto.userName = itemObj.getString("username");
												}
												dataList.add(dto);
											}

											if (mAdapter != null) {
												mAdapter.notifyDataSetChanged();
											}

											if (scrollToDown) {
												new Handler().post(new Runnable() {
													@Override
													public void run() {
														scrollView.fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部
													}
												});

											}
											clearContent();
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

	/**
	 * 隐藏虚拟键盘
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (etContent != null) {
			CommonUtil.hideInputSoft(etContent, mContext);
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 专家回答或用户提问
	 */
	private void OkHttpSubmit() {
		final String url = "http://guangxi.decision.tianqi.cn/addCommentary";
		if (data == null) {
			return;
		}
		MultipartBody.Builder builder = new MultipartBody.Builder();
		builder.setType(MultipartBody.FORM);
		builder.addFormDataPart("mid", data.questionId);
		builder.addFormDataPart("uid", MyApplication.UID);
		builder.addFormDataPart("commentary", EmojiMapUtil.replaceUnicodeEmojis(etContent.getText().toString()));
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
								try {
									JSONObject obj = new JSONObject(result);
									if (!obj.isNull("code")) {
										String code = obj.getString("code");
										if (TextUtils.equals(code, "200")) {
											refresh(true);
										}
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
					}
				});
			}
		}).start();
	}

	/**
	 * 清空输入内容
	 */
	private void clearContent() {
		if (etContent != null) {
			etContent.setText("");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llBack:
			finish();
			break;
		case R.id.tvSubmit:
			if (!TextUtils.isEmpty(etContent.getText().toString())) {
				CommonUtil.hideInputSoft(etContent, mContext);
				OkHttpSubmit();
			}
			break;
		case R.id.ivClear:
			clearContent();
			break;

		default:
			break;
		}
	}
	
}
