package com.cxwl.shawn.guangxi.ganzhe;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cxwl.shawn.guangxi.ganzhe.common.CONST;
import com.cxwl.shawn.guangxi.ganzhe.common.MyApplication;
import com.cxwl.shawn.guangxi.ganzhe.dto.ColumnData;
import com.cxwl.shawn.guangxi.ganzhe.util.CommonUtil;
import com.cxwl.shawn.guangxi.ganzhe.util.OkHttpUtil;
import com.cxwl.shawn.guangxi.ganzhe.util.sofia.Sofia;
import com.wang.avi.AVLoadingIndicatorView;

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
 * 登录
 */
public class ShawnLoginActivity extends ShawnBaseActivity implements OnClickListener {
	
	private Context mContext;
	private EditText etUserName,etPwd;
	private List<ColumnData> dataList = new ArrayList<>();
	private AVLoadingIndicatorView loadingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_login);
		mContext = this;
		Sofia.with(this)
				.invasionStatusBar()//设置顶部状态栏缩进
				.statusBarBackground(Color.TRANSPARENT)//设置状态栏颜色
				.invasionNavigationBar()
				.navigationBarBackground(Color.TRANSPARENT);
		initWidget();
	}

	/**
	 * 初始化控件
	 */
	private void initWidget() {
		loadingView = findViewById(R.id.loadingView);
		etUserName = findViewById(R.id.etUserName);
		etPwd = findViewById(R.id.etPwd);
		TextView tvLogin = findViewById(R.id.tvLogin);
		tvLogin.setOnClickListener(this);
		TextView tvRegister = findViewById(R.id.tvRegister);
		tvRegister.setOnClickListener(this);
	}

	/**
	 * 登录
	 */
	private void OkHttpLogin() {
		if (TextUtils.isEmpty(etUserName.getText().toString())) {
			Toast.makeText(mContext, "请输入用户名", Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(etPwd.getText().toString())) {
			Toast.makeText(mContext, "请输入密码", Toast.LENGTH_SHORT).show();
			return;
		}

		loadingView.setVisibility(View.VISIBLE);
		final String url = "http://decision-admin.tianqi.cn/home/Work/login";
		FormBody.Builder builder = new FormBody.Builder();
		builder.add("username", etUserName.getText().toString());
		builder.add("password", etPwd.getText().toString());
		builder.add("appid", CONST.APPID);
		builder.add("device_id", Build.DEVICE+ Build.SERIAL);
		builder.add("platform", "android");
		builder.add("os_version", Build.VERSION.RELEASE);
		builder.add("software_version", CommonUtil.getVersion(mContext));
		builder.add("mobile_type", Build.MODEL);
		builder.add("address", "");
		builder.add("lat", "0");
		builder.add("lng", "0");
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
								if (!TextUtils.isEmpty(result)) {
									try {
										JSONObject object = new JSONObject(result);
										if (!object.isNull("status")) {
											String status  = object.getString("status");
											if (TextUtils.equals(status, "1")) {//成功
												JSONArray array = object.getJSONArray("column");
												dataList.clear();
												for (int i = 0; i < array.length(); i++) {
													JSONObject obj = array.getJSONObject(i);
													ColumnData data = new ColumnData();
													if (!obj.isNull("id")) {
														data.columnId = obj.getString("id");
													}
													if (!obj.isNull("localviewid")) {
														data.id = obj.getString("localviewid");
													}
													if (!obj.isNull("name")) {
														data.name = obj.getString("name");
													}
													if (!obj.isNull("icon")) {
														data.icon = obj.getString("icon");
													}
													if (!obj.isNull("icon2")) {
														data.icon2 = obj.getString("icon2");
													}
													if (!obj.isNull("showtype")) {
														data.showType = obj.getString("showtype");
													}
													if (!obj.isNull("dataurl")) {
														data.dataUrl = obj.getString("dataurl");
													}
													if (!obj.isNull("child")) {
														JSONArray childArray = obj.getJSONArray("child");
														for (int j = 0; j < childArray.length(); j++) {
															JSONObject childObj = childArray.getJSONObject(j);
															ColumnData dto = new ColumnData();
															if (!childObj.isNull("id")) {
																dto.columnId = childObj.getString("id");
															}
															if (!childObj.isNull("localviewid")) {
																dto.id = childObj.getString("localviewid");
															}
															if (!childObj.isNull("name")) {
																dto.name = childObj.getString("name");
															}
															if (!childObj.isNull("icon")) {
																dto.icon = childObj.getString("icon");
															}
															if (!childObj.isNull("icon2")) {
																dto.icon2 = childObj.getString("icon2");
															}
															if (!childObj.isNull("showtype")) {
																dto.showType = childObj.getString("showtype");
															}
															if (!childObj.isNull("dataurl")) {
																dto.dataUrl = childObj.getString("dataurl");
															}
															if (!childObj.isNull("child")) {
																JSONArray childArray2 = childObj.getJSONArray("child");
																for (int m = 0; m < childArray2.length(); m++) {
																	JSONObject childObj2 = childArray2.getJSONObject(m);
																	ColumnData d = new ColumnData();
																	if (!childObj2.isNull("id")) {
																		d.columnId = childObj2.getString("id");
																	}
																	if (!childObj2.isNull("localviewid")) {
																		d.id = childObj2.getString("localviewid");
																	}
																	if (!childObj2.isNull("name")) {
																		d.name = childObj2.getString("name");
																	}
																	if (!childObj2.isNull("desc")) {
																		d.desc = childObj2.getString("desc");
																	}
																	if (!childObj2.isNull("icon")) {
																		d.icon = childObj2.getString("icon");
																	}
																	if (!childObj2.isNull("showtype")) {
																		d.showType = childObj2.getString("showtype");
																	}
																	if (!childObj2.isNull("dataurl")) {
																		d.dataUrl = childObj2.getString("dataurl");
																	}
																	dto.child.add(d);
																}
															}
															data.child.add(dto);
														}
													}
													dataList.add(data);
												}

												if (!object.isNull("info")) {
													JSONObject obj = new JSONObject(object.getString("info"));
													MyApplication.USERGROUP = obj.getString("usergroup");
													MyApplication.UID = obj.getString("id");
													MyApplication.USERNAME = etUserName.getText().toString();
													MyApplication.PASSWORD = etPwd.getText().toString();
													if (!obj.isNull("name")) {
														MyApplication.NAME = obj.getString("name");
													}
													if (!obj.isNull("mobile")) {
														MyApplication.MOBILE = obj.getString("mobile");
													}
													MyApplication.saveUserInfo(mContext);

													Intent intent = new Intent(mContext, ShawnMainActivity.class);
													Bundle bundle = new Bundle();
													bundle.putParcelableArrayList("dataList", (ArrayList<? extends Parcelable>) dataList);
													intent.putExtras(bundle);
													startActivity(intent);
													finish();
												}
											}else {
												//失败
												if (!object.isNull("msg")) {
													final String msg = object.getString("msg");
													if (msg != null) {
														Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
													}
												}
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
			case R.id.tvLogin:
				OkHttpLogin();
				break;
			case R.id.tvRegister:
				startActivityForResult(new Intent(this, ShawnRegisterActivity.class), 1001);

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
					Bundle bundle = data.getExtras();
					String userName = bundle.getString("userName");
					String pwd = bundle.getString("pwd");
					etUserName.setText(userName);
					etPwd.setText(pwd);
					OkHttpLogin();
					break;

				default:
					break;
			}
		}
	}

}
