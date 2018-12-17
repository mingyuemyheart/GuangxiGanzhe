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
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cxwl.shawn.guangxi.ganzhe.common.CONST;
import com.cxwl.shawn.guangxi.ganzhe.common.MyApplication;
import com.cxwl.shawn.guangxi.ganzhe.dto.WADto;
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
 * 专家联盟-问题咨询-问题发布
 * @author shawn_sun
 */
public class ShawnExpertQuestionPostActivity extends ShawnBaseActivity implements OnClickListener {
	
	private Context mContext;
	private LinearLayout llContainer, llAdd;
	private EditText etTitle, etContent;
	private int width = 0;
	private float density = 0;
	private AVLoadingIndicatorView loadingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_expert_question_post);
		mContext = this;
		initWidget();
	}

	private void initWidget() {
		loadingView = findViewById(R.id.loadingView);
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		TextView tvTitle = findViewById(R.id.tvTitle);
		TextView tvControl = findViewById(R.id.tvControl);
		tvControl.setText("发布");
		tvControl.setVisibility(View.VISIBLE);
		tvControl.setOnClickListener(this);
		etTitle = findViewById(R.id.etTitle);
		etContent = findViewById(R.id.etContent);
		llContainer = findViewById(R.id.llContainer);
		llAdd = findViewById(R.id.llAdd);
		llAdd.setOnClickListener(this);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		density = dm.density;
		
		String title = getIntent().getStringExtra(CONST.ACTIVITY_NAME);
		if (title != null) {
			tvTitle.setText(title);
		}

	}

	/**
	 * 检查上传条件
	 * @return
	 */
	private boolean checkCondition() {
		if (TextUtils.isEmpty(etTitle.getText().toString())) {
			Toast.makeText(mContext, "请输入标题", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

    /**
     * 发布一个问题
     */
	private void OkHttpPublishQuestion() {
		final String url = "http://shanxi.decision.tianqi.cn/Home/api/sx_zhny_expert_userupload";
		final WADto data = getIntent().getExtras().getParcelable("data");
	    if (data == null) {
	        return;
        }
		loadingView.setVisibility(View.VISIBLE);
	    new Thread(new Runnable() {
            @Override
            public void run() {
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);
                builder.addFormDataPart("expertId", data.expertId);
                builder.addFormDataPart("userId", MyApplication.UID);
                builder.addFormDataPart("userName", MyApplication.USERNAME);
				builder.addFormDataPart("title", etTitle.getText().toString());
				builder.addFormDataPart("content", etContent.getText().toString());
				for (int i = 0; i < llContainer.getChildCount(); i++) {
					ImageView imageView = (ImageView) llContainer.getChildAt(i);
					String imgPath = imageView.getTag()+"";
					File imgFile = new File(imgPath);
					if (imgFile.exists()) {
						builder.addFormDataPart("picture"+(i+1), imgFile.getName(), RequestBody.create(MediaType.parse("image/*"), imgFile));
					}
				}
                RequestBody body = builder.build();
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
											loadingView.setVisibility(View.GONE);
											setResult(RESULT_OK);
											finish();
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

    private void openAlbum() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, 1);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llBack:
			finish();
			break;
		case R.id.llAdd:
			checkAuthority();
			break;
		case R.id.tvControl:
			if (checkCondition()) {
				OkHttpPublishQuestion();
			}
			break;

		default:
			break;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case 1:
					if (data != null) {
						Uri uri = data.getData();
						String filePath = GetPathFromUri4kitkat.getPath(mContext, uri);
						if (filePath == null) {
							Toast.makeText(mContext, "图片没找到", Toast.LENGTH_SHORT).show();
							return;
						}

						ImageView imageView = new ImageView(mContext);
						imageView.setScaleType(ImageView.ScaleType.FIT_XY);
						imageView.setTag(filePath);
						int w = (width-(int)(density*30))/3;
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w, w-(int)(density*10));
						params.rightMargin = (int)(density*5);
						imageView.setLayoutParams(params);
						Bitmap bitmap = BitmapFactory.decodeFile(filePath);
						if (bitmap != null) {
							imageView.setImageBitmap(bitmap);
							llContainer.addView(imageView);
							if (llContainer.getChildCount() >= 3) {
								llAdd.setVisibility(View.GONE);
							}else {
								llAdd.setVisibility(View.VISIBLE);
							}
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
				ActivityCompat.requestPermissions(this, permissions, AuthorityUtil.AUTHOR_LOCATION);
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
						if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
							AuthorityUtil.intentAuthorSetting(mContext, "\""+getString(R.string.app_name)+"\""+"需要使用您的存储权限，是否前往设置？");
							break;
						}
					}
				}
				break;
		}
	}
	
}
