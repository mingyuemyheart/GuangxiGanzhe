package com.cxwl.shawn.guangxi.ganzhe;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cxwl.shawn.guangxi.ganzhe.common.CONST;
import com.cxwl.shawn.guangxi.ganzhe.common.MyApplication;
import com.cxwl.shawn.guangxi.ganzhe.util.AuthorityUtil;
import com.cxwl.shawn.guangxi.ganzhe.util.GetPathFromUri4kitkat;
import com.cxwl.shawn.guangxi.ganzhe.util.OkHttpUtil;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 农场上传
 */
public class ShawnFarmPostActivity extends ShawnBaseActivity implements OnClickListener {
	
	private Context mContext = null;
	private EditText etName, etAddr, etType, etArea, etPeriod, etOutput, etManager, etDisInfo;
	private ImageView imageView;
	private LinearLayout llAdd;
	private AVLoadingIndicatorView loadingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_farm_post);
		mContext = this;
		initWidget();
	}

	/**
	 * 初始化控件
	 */
	private void initWidget() {
		loadingView = findViewById(R.id.loadingView);
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		TextView tvTitle = findViewById(R.id.tvTitle);
		etName = findViewById(R.id.etName);
		etAddr = findViewById(R.id.etAddr);
		etType = findViewById(R.id.etType);
		etArea = findViewById(R.id.etArea);
		etPeriod = findViewById(R.id.etPeriod);
		etOutput = findViewById(R.id.etOutput);
		etManager = findViewById(R.id.etManager);
		etDisInfo = findViewById(R.id.etDisInfo);
		imageView = findViewById(R.id.imageView);
		imageView.setOnClickListener(this);
		llAdd = findViewById(R.id.llAdd);
		llAdd.setOnClickListener(this);
		TextView tvControl = findViewById(R.id.tvControl);
		tvControl.setVisibility(View.VISIBLE);
		tvControl.setText("提交");
		tvControl.setOnClickListener(this);

		String title = getIntent().getStringExtra(CONST.ACTIVITY_NAME);
		if (!TextUtils.isEmpty(title)) {
			tvTitle.setText(title);
		}

	}
	
	private void submit() {
		if (checkInfo()) {
			loadingView.setVisibility(View.VISIBLE);
			OkHttpSubmit();
		}
	}
	
	/**
	 * 验证用户信息
	 */
	private boolean checkInfo() {
		if (TextUtils.isEmpty(etName.getText().toString())) {
			Toast.makeText(mContext, "请输入农场/公司名称", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(etAddr.getText().toString())) {
			Toast.makeText(mContext, "请输入地址", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(etType.getText().toString())) {
			Toast.makeText(mContext, "请输入种植品种", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(etArea.getText().toString())) {
			Toast.makeText(mContext, "请输入面积", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(etPeriod.getText().toString())) {
			Toast.makeText(mContext, "请输入生长期", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(etOutput.getText().toString())) {
			Toast.makeText(mContext, "请输入产量", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	/**
	 * 用户注册
	 */
	private void OkHttpSubmit() {
		final String url = "http://decision-admin.tianqi.cn/Home/work2019/gxgz_upload_imgs";
		MultipartBody.Builder builder = new MultipartBody.Builder();
		builder.setType(MultipartBody.FORM);
		builder.addFormDataPart("uid", MyApplication.UID);
		builder.addFormDataPart("name", etName.getText().toString());
		builder.addFormDataPart("addr", etAddr.getText().toString());
		builder.addFormDataPart("farm_type", etType.getText().toString());
		builder.addFormDataPart("farm_area", etArea.getText().toString());
		builder.addFormDataPart("farm_manager", etManager.getText().toString());
		builder.addFormDataPart("dis_info", etDisInfo.getText().toString());
		builder.addFormDataPart("growth_period", etPeriod.getText().toString());
		builder.addFormDataPart("output", etOutput.getText().toString());
		String imgPath = imageView.getTag()+"";
		File imgFile = new File(imgPath);
		if (imgFile.exists()) {
			builder.addFormDataPart("pic", imgFile.getName(), RequestBody.create(MediaType.parse("image/*"), imgFile));
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
								loadingView.setVisibility(View.GONE);
								if (!TextUtils.isEmpty(result)) {
									try {
										JSONObject object = new JSONObject(result);
										if (!object.isNull("code")) {
											int code  = object.getInt("code");
											if (code == 200) {//成功
												Toast.makeText(mContext, "上传成功", Toast.LENGTH_SHORT).show();
												finish();
											}else {
												//失败
												if (!object.isNull("msg")) {
													String msg = object.getString("msg");
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
			case R.id.llAdd:
			case R.id.imageView:
				checkAuthority();
				break;
			case R.id.tvControl:
				submit();
				break;

		default:
			break;
		}
	}

	private void openAlbum() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, 1001);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case 1001://读取相册
					if (data != null) {
						Uri uri = data.getData();
						String filePath = GetPathFromUri4kitkat.getPath(mContext, uri);
						if (filePath == null) {
							Toast.makeText(mContext, "图片没找到", Toast.LENGTH_SHORT).show();
							return;
						}

						Bitmap bitmap = BitmapFactory.decodeFile(filePath);
						if (bitmap != null) {
							imageView.setImageBitmap(bitmap);
							imageView.setTag(filePath);
							imageView.setVisibility(View.VISIBLE);
							llAdd.setVisibility(View.GONE);
						}
					}else {
						Toast.makeText(mContext, "图片没找到", Toast.LENGTH_SHORT).show();
						return;
					}
					break;

				default:
					break;
			}
		}
	}

	//需要申请的所有权限
	private String[] allPermissions = new String[] {
			Manifest.permission.WRITE_EXTERNAL_STORAGE
	};

	//拒绝的权限集合
	public static List<String> deniedList = new ArrayList<>();
	/**
	 * 申请定位权限
	 */
	private void checkAuthority() {
		if (Build.VERSION.SDK_INT < 23) {
			openAlbum();
		}else {
			deniedList.clear();
			for (String permission : allPermissions) {
				if (ContextCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED) {
					deniedList.add(permission);
				}
			}
			if (deniedList.isEmpty()) {//所有权限都授予
				openAlbum();
			}else {
				String[] permissions = deniedList.toArray(new String[deniedList.size()]);//将list转成数组
				ActivityCompat.requestPermissions(ShawnFarmPostActivity.this, permissions, AuthorityUtil.AUTHOR_LOCATION);
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case AuthorityUtil.AUTHOR_LOCATION:
				if (grantResults.length > 0) {
					boolean isAllGranted = true;//是否全部授权
					for (int gResult : grantResults) {
						if (gResult != PackageManager.PERMISSION_GRANTED) {
							isAllGranted = false;
							break;
						}
					}
					if (isAllGranted) {//所有权限都授予
						openAlbum();
					}else {//只要有一个没有授权，就提示进入设置界面设置
						AuthorityUtil.intentAuthorSetting(mContext, "\""+getString(R.string.app_name)+"\""+"需要使用您的存储权限，是否前往设置？");
					}
				}else {
					for (String permission : permissions) {
						if (!ActivityCompat.shouldShowRequestPermissionRationale(ShawnFarmPostActivity.this, permission)) {
							AuthorityUtil.intentAuthorSetting(mContext, "\""+getString(R.string.app_name)+"\""+"需要使用您的存储权限，是否前往设置？");
							break;
						}
					}
				}
				break;
		}
	}
	
}
