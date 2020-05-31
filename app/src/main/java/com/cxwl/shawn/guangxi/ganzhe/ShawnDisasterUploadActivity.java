package com.cxwl.shawn.guangxi.ganzhe;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.cxwl.shawn.guangxi.ganzhe.adapter.ShawnDisasterUploadAdapter;
import com.cxwl.shawn.guangxi.ganzhe.adapter.ShawnSelectStationAdapter;
import com.cxwl.shawn.guangxi.ganzhe.common.CONST;
import com.cxwl.shawn.guangxi.ganzhe.common.MyApplication;
import com.cxwl.shawn.guangxi.ganzhe.dto.DisasterDto;
import com.cxwl.shawn.guangxi.ganzhe.dto.FactDto;
import com.cxwl.shawn.guangxi.ganzhe.util.AuthorityUtil;
import com.cxwl.shawn.guangxi.ganzhe.util.CommonUtil;
import com.cxwl.shawn.guangxi.ganzhe.util.OkHttpUtil;
import com.cxwl.shawn.guangxi.ganzhe.view.ScrollviewGridview;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;
import wheelview.NumericWheelAdapter;
import wheelview.OnWheelScrollListener;
import wheelview.WheelView;

/**
 * 灾情反馈
 */
public class ShawnDisasterUploadActivity extends ShawnBaseActivity implements OnClickListener, AMapLocationListener {
	
	private Context mContext;
	private EditText etTitle,etContent,etPosition,etMiao1,etMiao2,etMiao3,etMiao4,etMiao5;
	private TextView tvTextCount,tvCount,tvTime,tvDisaster,tvLatLng;
	private AVLoadingIndicatorView loadingView;
	private ShawnDisasterUploadAdapter mAdapter;
	private List<DisasterDto> dataList = new ArrayList<>();
	private RelativeLayout reViewPager;
	private RelativeLayout layoutDate;
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
	private double lat = 0, lng = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_disaster_upload);
		mContext = this;
		initWidget();
		initWheelView();
	}
	
	private void initWidget() {
        loadingView = findViewById(R.id.loadingView);
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		TextView tvTitle = findViewById(R.id.tvTitle);
		tvTitle.setText("灾情上报");
		TextView tvControl = findViewById(R.id.tvControl);
		tvControl.setText("发布");
		tvControl.setOnClickListener(this);
		tvControl.setVisibility(View.VISIBLE);
		etTitle = findViewById(R.id.etTitle);
		etContent = findViewById(R.id.etContent);
		etContent.addTextChangedListener(contentWatcher);
		tvTextCount = findViewById(R.id.tvTextCount);
		LinearLayout llTime = findViewById(R.id.llTime);
		llTime.setOnClickListener(this);
		tvDisaster = findViewById(R.id.tvDisaster);
		tvDisaster.setOnClickListener(this);
		etPosition = findViewById(R.id.etPosition);
		tvTime = findViewById(R.id.tvTime);
		tvTime.setOnClickListener(this);
		tvCount = findViewById(R.id.tvCount);
		reViewPager = findViewById(R.id.reViewPager);
		layoutDate = findViewById(R.id.layoutDate);
		TextView tvNegtive = findViewById(R.id.tvNegtive);
		tvNegtive.setOnClickListener(this);
		TextView tvPositive = findViewById(R.id.tvPositive);
		tvPositive.setOnClickListener(this);
		tvTime.setText(sdf1.format(new Date()));
		tvLatLng = findViewById(R.id.tvLatLng);
		etMiao1 = findViewById(R.id.etMiao1);
		etMiao2 = findViewById(R.id.etMiao2);
		etMiao3 = findViewById(R.id.etMiao3);
		etMiao4 = findViewById(R.id.etMiao4);
		etMiao5 = findViewById(R.id.etMiao5);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int w = (width-(int) CommonUtil.dip2px(mContext, 24))/3;

		initGridView(w);
		startLocation();
	}

	/**
	 * 开始定位
	 */
	private void startLocation() {
		AMapLocationClientOption mLocationOption = new AMapLocationClientOption();//初始化定位参数
		AMapLocationClient mLocationClient = new AMapLocationClient(mContext);//初始化定位
		mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
		mLocationOption.setNeedAddress(true);//设置是否返回地址信息（默认返回地址信息）
		mLocationOption.setOnceLocation(true);//设置是否只定位一次,默认为false
		mLocationOption.setMockEnable(false);//设置是否允许模拟位置,默认为false，不允许模拟位置
		mLocationOption.setInterval(2000);//设置定位间隔,单位毫秒,默认为2000ms
		mLocationClient.setLocationOption(mLocationOption);//给定位客户端对象设置定位参数
		mLocationClient.setLocationListener(this);
		mLocationClient.startLocation();//启动定位
	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (amapLocation != null && amapLocation.getErrorCode() == 0) {
			lat = amapLocation.getLatitude();
			lng = amapLocation.getLongitude();
			tvLatLng.setText(lat+","+lng);
			if (amapLocation.getProvince().contains(amapLocation.getCity())) {
				etPosition.setText(amapLocation.getCity()+amapLocation.getDistrict()+amapLocation.getStreet()+amapLocation.getAoiName());
			} else {
				etPosition.setText(amapLocation.getProvince()+amapLocation.getCity()+amapLocation.getDistrict()+amapLocation.getStreet()+amapLocation.getAoiName());
			}
		}
	}

	/**
	 * 输入内容监听器
	 */
	private TextWatcher contentWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}
		@Override
		public void afterTextChanged(Editable arg0) {
			if (etContent.getText().length() == 0) {
				tvTextCount.setText("(100字以内)");
			}else {
				int count = 100-etContent.getText().length();
				tvTextCount.setText("(还可输入"+count+"字)");
			}
		}
	};

	/**
	 * 选择站点
	 */
	private void selectStation() {
		final List<FactDto> stations = new ArrayList<>();
		String[] array = getResources().getStringArray(R.array.disaster_type);
		for (int i = 0; i < array.length; i++) {
			FactDto dto = new FactDto();
			dto.stationName = array[i];
			stations.add(dto);
		}

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View stationView = inflater.inflate(R.layout.shawn_dialog_select_station, null);
		ListView listView = stationView.findViewById(R.id.listView);
		ShawnSelectStationAdapter stationAdapter = new ShawnSelectStationAdapter(mContext, stations);
		listView.setAdapter(stationAdapter);
		TextView tvType = stationView.findViewById(R.id.tvType);
		tvType.setText("灾情类型");

		final Dialog dialog = new Dialog(mContext, R.style.CustomProgressDialog);
		dialog.setContentView(stationView);
		dialog.show();

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				dialog.dismiss();
				FactDto dto = stations.get(arg2);
				tvDisaster.setText(dto.stationName);
			}
		});
	}

	private void addLastElement() {
		DisasterDto dto = new DisasterDto();
		dto.isLastItem = true;
		dataList.add(dto);
	}

	ScrollviewGridview gridView;
	private void initGridView(int itemWidth) {
		addLastElement();

		gridView = findViewById(R.id.gridView);
		mAdapter = new ShawnDisasterUploadAdapter(this, dataList, itemWidth);
		gridView.setAdapter(mAdapter);
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				DisasterDto data = dataList.get(position);
				if (data.isLastItem) {//点击添加按钮
					selectCamera();
				}else {
					List<String> imgList = new ArrayList<>();
					for (int i = 0; i < dataList.size(); i++) {
						DisasterDto d = dataList.get(i);
						if (!d.isLastItem) {
							imgList.add(d.imgUrl);
						}
					}
					initViewPager(position, imgList);
					if (reViewPager.getVisibility() == View.GONE) {
						scaleExpandAnimation(reViewPager);
						reViewPager.setVisibility(View.VISIBLE);
						tvCount.setText((position+1)+"/"+imgList.size());
					}
				}
			}
		});
		gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				dialogCache(position);
				return true;
			}
		});
	}

	/**
	 * 清除缓存
	 */
	private void dialogCache(final int position) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.shawn_dialog_cache, null);
		TextView tvContent = view.findViewById(R.id.tvContent);
		TextView tvNegtive = view.findViewById(R.id.tvNegtive);
		TextView tvPositive = view.findViewById(R.id.tvPositive);

		final Dialog dialog = new Dialog(this, R.style.CustomProgressDialog);
		dialog.setContentView(view);
		dialog.show();

		tvContent.setText("确定删除？");
		tvNegtive.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});

		tvPositive.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				dataList.remove(position);
				if (mAdapter != null) {
					mAdapter.notifyDataSetChanged();
				}
			}
		});
	}

	/**
	 * 再请反馈
	 */
	private void OkHttpPost() {
		loadingView.setVisibility(View.VISIBLE);
		final String url = "http://decision-admin.tianqi.cn/home/work2019/decisionZqfk";
		MultipartBody.Builder builder = new MultipartBody.Builder();
		builder.setType(MultipartBody.FORM);
		builder.addFormDataPart("uid", MyApplication.UID);
		builder.addFormDataPart("appid", CONST.APPID);
		builder.addFormDataPart("latlon", lat+","+lng);
		if (!TextUtils.isEmpty(etTitle.getText().toString())) {
			builder.addFormDataPart("title", etTitle.getText().toString());
		}
		if (!TextUtils.isEmpty(etContent.getText().toString())) {
			builder.addFormDataPart("content", etContent.getText().toString());
		}
		if (!TextUtils.isEmpty(tvDisaster.getText().toString())) {
			builder.addFormDataPart("type", tvDisaster.getText().toString());
		}
		etMiao1.setText(TextUtils.isEmpty(etMiao1.getText().toString()) ? "0" : etMiao1.getText().toString());
		etMiao2.setText(TextUtils.isEmpty(etMiao2.getText().toString()) ? "0" : etMiao2.getText().toString());
		etMiao3.setText(TextUtils.isEmpty(etMiao3.getText().toString()) ? "0" : etMiao3.getText().toString());
		etMiao4.setText(TextUtils.isEmpty(etMiao4.getText().toString()) ? "0" : etMiao4.getText().toString());
		etMiao5.setText(TextUtils.isEmpty(etMiao5.getText().toString()) ? "0" : etMiao5.getText().toString());
		String other_param1 = "行距："+etMiao1.getText().toString()+"米；10米苗树：总苗树："+etMiao2.getText().toString()+
				"株、枯心病数"+etMiao3.getText().toString()+
				"株、黑穗病数"+etMiao4.getText().toString()+
				"株、其它病虫数"+etMiao5.getText().toString()+"株";
		Log.e("other_param1", other_param1);
		builder.addFormDataPart("other_param1", other_param1);
		if (!TextUtils.isEmpty(etPosition.getText().toString())) {
			builder.addFormDataPart("location", etPosition.getText().toString());
		}
		if (!TextUtils.isEmpty(tvTime.getText().toString())) {
			builder.addFormDataPart("createtime", tvTime.getText().toString());
		}
		int size = dataList.size();
		if (size > 0) {
			for (int i = 0; i < dataList.size(); i++) {
				DisasterDto dto = dataList.get(i);
				if (!TextUtils.isEmpty(dto.imgUrl)) {
					File imgFile = new File(compressBitmap(dto.imgUrl));
					if (imgFile.exists()) {
						builder.addFormDataPart("pic"+i, imgFile.getName(), RequestBody.create(MediaType.parse("image/*"), imgFile));
					}
				}
			}
		}
		final RequestBody body = builder.build();
		new Thread(new Runnable() {
			@Override
			public void run() {
				OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
						Log.e("", "");
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
											Toast.makeText(mContext, "提交成功！", Toast.LENGTH_SHORT).show();
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

	private String compressBitmap(String imgPath) {
		String newPath = null;
		try {
			File files = new File(CONST.SDCARD_PATH);
			if (!files.exists()) {
				files.mkdirs();
			}

			Bitmap bitmap = getSmallBitmap(imgPath);
			newPath = files.getAbsolutePath()+"/"+System.currentTimeMillis()+".jpg";
			FileOutputStream fos = new FileOutputStream(newPath);
			if (bitmap != null && fos != null) {
				bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);

				if (bitmap != null && !bitmap.isRecycled()) {
					bitmap.recycle();
					bitmap = null;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return newPath;
	}

	/**
	 * 根据路径获得图片信息并按比例压缩，返回bitmap
	 */
	public static Bitmap getSmallBitmap(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;//只解析图片边沿，获取宽高
		BitmapFactory.decodeFile(filePath, options);
		// 计算缩放比
		options.inSampleSize = calculateInSampleSize(options, 720, 1080);
		// 完整解析图片返回bitmap
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}


	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.llBack:
				finish();
				break;
			case R.id.tvDisaster:
				selectStation();
				break;
			case R.id.tvTime:
			case R.id.tvNegtive:
				bootTimeLayoutAnimation();
				break;
			case R.id.tvPositive:
				bootTimeLayoutAnimation();
				setTextViewValue();
				break;
			case R.id.tvControl:
				if (TextUtils.isEmpty(etTitle.getText().toString())) {
					Toast.makeText(mContext, "请输入灾情标题！", Toast.LENGTH_SHORT).show();
					return;
				}
				if (TextUtils.isEmpty(tvDisaster.getText().toString())) {
					Toast.makeText(mContext, "请选择灾情类型！", Toast.LENGTH_SHORT).show();
					return;
				}
				if (TextUtils.isEmpty(etPosition.getText().toString())) {
					Toast.makeText(mContext, "请输入采集地点！", Toast.LENGTH_SHORT).show();
					return;
				}
				OkHttpPost();
				break;
		}
	}

	private File cameraFile;
	private void intentCamera() {
		File files = new File(CONST.SDCARD_PATH);
		if (!files.exists()) {
			files.mkdirs();
		}
		cameraFile = new File(CONST.SDCARD_PATH + "/"+System.currentTimeMillis()+".jpg");
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Uri uri;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			uri = FileProvider.getUriForFile(mContext, getPackageName()+".fileprovider", cameraFile);
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		} else {
			uri = Uri.fromFile(cameraFile);
		}
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intent, 1002);
	}

	private void intentAlbum() {
		Intent intent = new Intent(mContext, ShawnSelectPictureActivity.class);
		intent.putExtra("count", dataList.size()-1);
		startActivityForResult(intent, 1001);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case 1001:
					if (data != null) {
						Bundle bundle = data.getExtras();
						if (bundle != null) {
							if (dataList.size() <= 1) {
								dataList.remove(0);
							}else {
								dataList.remove(dataList.size()-1);
							}

							List<DisasterDto> list = bundle.getParcelableArrayList("dataList");
							dataList.addAll(list);
							addLastElement();
							if (dataList.size() >= 10) {
								dataList.remove(dataList.size()-1);
							}
							if (mAdapter != null) {
								mAdapter.notifyDataSetChanged();
							}
						}
					}
					break;
				case 1002:
					FileInputStream fis = null;
					try {
						fis = new FileInputStream(cameraFile);
						Bitmap bitmap = BitmapFactory.decodeStream(fis);
//						ivMyPhoto.setImageBitmap(bitmap);
						if (dataList.size() <= 1) {
							dataList.remove(0);
						}else {
							dataList.remove(dataList.size()-1);
						}

						DisasterDto dto = new DisasterDto();
						dto.imageName = "";
						dto.imgUrl = cameraFile.getAbsolutePath();
						dataList.add(dto);
						addLastElement();
						if (dataList.size() >= 10) {
							dataList.remove(dataList.size()-1);
						}
						if (mAdapter != null) {
							mAdapter.notifyDataSetChanged();
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							if (fis != null)
								fis.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					break;

				default:
					break;
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (reViewPager.getVisibility() == View.VISIBLE) {
			scaleColloseAnimation(reViewPager);
			reViewPager.setVisibility(View.GONE);
			return false;
		}else {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 初始化viewPager
	 */
	private void initViewPager(int current, final List<String> list) {
		ImageView[] imageArray = new ImageView[list.size()];
		for (int i = 0; i < list.size(); i++) {
			String imgUrl = list.get(i);
			if (!TextUtils.isEmpty(imgUrl)) {
				ImageView imageView = new ImageView(this);
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				Bitmap bitmap = BitmapFactory.decodeFile(imgUrl);
				if (bitmap != null) {
					imageView.setImageBitmap(bitmap);
					imageArray[i] = imageView;
				}
			}
		}

		ViewPager mViewPager = findViewById(R.id.viewPager);
		MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter(imageArray);
		mViewPager.setAdapter(myViewPagerAdapter);
		mViewPager.setCurrentItem(current);

		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				tvCount.setText((arg0+1)+"/"+list.size());
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	private class MyViewPagerAdapter extends PagerAdapter {

		private ImageView[] mImageViews;

		private MyViewPagerAdapter(ImageView[] imageViews) {
			this.mImageViews = imageViews;
		}

		@Override
		public int getCount() {
			return mImageViews.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mImageViews[position]);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			PhotoView photoView = new PhotoView(container.getContext());
			Drawable drawable = mImageViews[position].getDrawable();
			photoView.setImageDrawable(drawable);
			container.addView(photoView, 0);
			photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
				@Override
				public void onPhotoTap(View view, float v, float v1) {
					scaleColloseAnimation(reViewPager);
					reViewPager.setVisibility(View.GONE);
				}
			});
			return photoView;
		}

	}

	/**
	 * 放大动画
	 * @param view
	 */
	private void scaleExpandAnimation(View view) {
		AnimationSet animationSet = new AnimationSet(true);

		ScaleAnimation scaleAnimation = new ScaleAnimation(0,1.0f,0,1.0f,
				Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
		scaleAnimation.setInterpolator(new LinearInterpolator());
		scaleAnimation.setDuration(300);
		animationSet.addAnimation(scaleAnimation);

		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1.0f);
		alphaAnimation.setDuration(300);
		animationSet.addAnimation(alphaAnimation);

		view.startAnimation(animationSet);
	}

	/**
	 * 缩小动画
	 * @param view
	 */
	private void scaleColloseAnimation(View view) {
		AnimationSet animationSet = new AnimationSet(true);

		ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f,0.0f,1.0f,0.0f,
				Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
		scaleAnimation.setInterpolator(new LinearInterpolator());
		scaleAnimation.setDuration(300);
		animationSet.addAnimation(scaleAnimation);

		AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0);
		alphaAnimation.setDuration(300);
		animationSet.addAnimation(alphaAnimation);

		view.startAnimation(animationSet);
	}

	/**
	 * 选择相机或相册
	 */
	private void selectCamera() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.shawn_dialog_camera, null);
		TextView tvCamera = view.findViewById(R.id.tvCamera);
		TextView tvAlbum = view.findViewById(R.id.tvAlbum);

		final Dialog dialog = new Dialog(mContext, R.style.CustomProgressDialog);
		dialog.setContentView(view);
		dialog.show();

		tvCamera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				checkCameraAuthority();
			}
		});
		tvAlbum.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				checkStorageAuthority();
			}
		});

	}

	/**
	 * 申请相机权限
	 */
	private void checkCameraAuthority() {
		if (Build.VERSION.SDK_INT < 23) {
			intentCamera();
		}else {
			if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
				String[] permissions = new String[] {Manifest.permission.CAMERA};
				ActivityCompat.requestPermissions(this, permissions, AuthorityUtil.AUTHOR_CAMERA);
			}else {
				intentCamera();
			}
		}
	}

	/**
	 * 申请存储权限
	 */
	private void checkStorageAuthority() {
		if (Build.VERSION.SDK_INT < 23) {
			intentAlbum();
		}else {
			if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				String[] permissions = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE};
				ActivityCompat.requestPermissions(this, permissions, AuthorityUtil.AUTHOR_STORAGE);
			}else {
				intentAlbum();
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case AuthorityUtil.AUTHOR_CAMERA:
				if (grantResults.length > 0) {
					boolean isAllGranted = true;//是否全部授权
					for (int gResult : grantResults) {
						if (gResult != PackageManager.PERMISSION_GRANTED) {
							isAllGranted = false;
							break;
						}
					}
					if (isAllGranted) {//所有权限都授予
						intentCamera();
					}else {//只要有一个没有授权，就提示进入设置界面设置
						AuthorityUtil.intentAuthorSetting(mContext, "\""+getString(R.string.app_name)+"\""+"需要使用您的相机权限，是否前往设置？");
					}
				}else {
					for (String permission : permissions) {
						if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
							AuthorityUtil.intentAuthorSetting(mContext, "\""+getString(R.string.app_name)+"\""+"需要使用您的相机权限，是否前往设置？");
							break;
						}
					}
				}
				break;
			case AuthorityUtil.AUTHOR_STORAGE:
				if (grantResults.length > 0) {
					boolean isAllGranted = true;//是否全部授权
					for (int gResult : grantResults) {
						if (gResult != PackageManager.PERMISSION_GRANTED) {
							isAllGranted = false;
							break;
						}
					}
					if (isAllGranted) {//所有权限都授予
						intentAlbum();
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

	private WheelView year, month, day, hour, minute, second;
	private void initWheelView() {
		Calendar c = Calendar.getInstance();
		int curYear = c.get(Calendar.YEAR);
		int curMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
		int curDate = c.get(Calendar.DATE);
		int curHour = c.get(Calendar.HOUR_OF_DAY);
		int curMinute = c.get(Calendar.MINUTE);
		int curSecond = c.get(Calendar.SECOND);

		year = findViewById(R.id.year);
		NumericWheelAdapter numericWheelAdapter1=new NumericWheelAdapter(mContext,1950, curYear);
		numericWheelAdapter1.setLabel("年");
		year.setViewAdapter(numericWheelAdapter1);
		year.setCyclic(false);//是否可循环滑动
		year.addScrollingListener(scrollListener);
		year.setVisibleItems(7);

		month = findViewById(R.id.month);
		NumericWheelAdapter numericWheelAdapter2=new NumericWheelAdapter(mContext,1, 12, "%02d");
		numericWheelAdapter2.setLabel("月");
		month.setViewAdapter(numericWheelAdapter2);
		month.setCyclic(false);
		month.addScrollingListener(scrollListener);
		month.setVisibleItems(7);

		day = findViewById(R.id.day);
		initDay(curYear,curMonth);
		day.setCyclic(false);
		day.setVisibleItems(7);

		hour = findViewById(R.id.hour);
		NumericWheelAdapter numericWheelAdapter3=new NumericWheelAdapter(mContext,1, 23, "%02d");
		numericWheelAdapter3.setLabel("时");
		hour.setViewAdapter(numericWheelAdapter3);
		hour.setCyclic(false);
		hour.addScrollingListener(scrollListener);
		hour.setVisibleItems(7);

		minute = findViewById(R.id.minute);
		NumericWheelAdapter numericWheelAdapter4=new NumericWheelAdapter(mContext,1, 59, "%02d");
		numericWheelAdapter4.setLabel("分");
		minute.setViewAdapter(numericWheelAdapter4);
		minute.setCyclic(false);
		minute.addScrollingListener(scrollListener);
		minute.setVisibleItems(7);
		minute.setVisibility(View.VISIBLE);

		second = findViewById(R.id.second);
		NumericWheelAdapter numericWheelAdapter5=new NumericWheelAdapter(mContext,1, 59, "%02d");
		numericWheelAdapter5.setLabel("秒");
		second.setViewAdapter(numericWheelAdapter5);
		second.setCyclic(false);
		second.addScrollingListener(scrollListener);
		second.setVisibleItems(7);
		second.setVisibility(View.VISIBLE);

		year.setCurrentItem(curYear - 1950);
		month.setCurrentItem(curMonth - 1);
		day.setCurrentItem(curDate - 1);
		hour.setCurrentItem(curHour - 1);
		minute.setCurrentItem(curMinute - 1);
		second.setCurrentItem(curSecond - 1);
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
		NumericWheelAdapter numericWheelAdapter=new NumericWheelAdapter(mContext,1, getDay(arg1, arg2), "%02d");
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
		String minuteStr = String.valueOf(((minute.getCurrentItem()+1) < 10) ? "0" + (minute.getCurrentItem()+1) : (minute.getCurrentItem()+1));
		String secondStr = String.valueOf(((second.getCurrentItem()+1) < 10) ? "0" + (second.getCurrentItem()+1) : (second.getCurrentItem()+1));

		tvTime.setText(yearStr+"-"+monthStr+"-"+dayStr+" "+hourStr+":"+minuteStr+":"+secondStr);
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
