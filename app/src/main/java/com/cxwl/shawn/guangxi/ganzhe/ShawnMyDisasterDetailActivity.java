package com.cxwl.shawn.guangxi.ganzhe;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.dto.DisasterDto;
import com.squareup.picasso.Picasso;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 我上传的灾情反馈-详情
 */
public class ShawnMyDisasterDetailActivity extends ShawnBaseActivity implements OnClickListener {

	private TextView tvCount;
	private RelativeLayout reViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_my_disaster_detail);
		initWidget();
	}

	private void initWidget() {
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		TextView tvTitle = findViewById(R.id.tvTitle);
		tvTitle.setText("灾情详情");
		TextView tvName = findViewById(R.id.tvName);
		TextView tvContent = findViewById(R.id.tvContent);
		TextView tvType = findViewById(R.id.tvType);
		TextView tvMiao = findViewById(R.id.tvMiao);
		TextView tvAddr = findViewById(R.id.tvAddr);
		TextView tvLatLng = findViewById(R.id.tvLatLng);
		TextView tvTime = findViewById(R.id.tvTime);
		tvCount = findViewById(R.id.tvCount);
		reViewPager = findViewById(R.id.reViewPager);
		final LinearLayout llContainer = findViewById(R.id.llContainer);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;

		final DisasterDto data = getIntent().getParcelableExtra("data");
		if (data != null) {
			if (!TextUtils.isEmpty(data.title)) {
				tvName.setText(data.title);
			}
			if (!TextUtils.isEmpty(data.content)) {
				tvContent.setText(data.content);
			}
			if (!TextUtils.isEmpty(data.disasterType)) {
				tvType.setText("灾情类型："+data.disasterType);
			}
			if (!TextUtils.isEmpty(data.miao)) {
				tvMiao.setText("苗情："+data.miao);
			}
			if (!TextUtils.isEmpty(data.addr)) {
				tvAddr.setText("时间地点："+data.addr);
			}
			if (!TextUtils.isEmpty(data.latlon)) {
				tvLatLng.setText("经纬度："+data.latlon);
			}
			if (!TextUtils.isEmpty(data.time)) {
				tvTime.setText("事件时间："+data.time);
			}
			if (data.imgList.size() > 0) {
				llContainer.removeAllViews();
				final int size = data.imgList.size();
				for (int i = 0; i < size; i++) {
					String imgUrl = data.imgList.get(i);
					ImageView imageView = new ImageView(this);
					imageView.setTag(imgUrl);
					imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
					int w = width/3;
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w, w);
					imageView.setLayoutParams(params);
					Picasso.get().load(imgUrl).into(imageView);
					llContainer.addView(imageView);

					imageView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							String tag = v.getTag()+"";
							for (int j = 0; j < llContainer.getChildCount(); j++) {
								ImageView iv = (ImageView) llContainer.getChildAt(j);
								if (TextUtils.equals(iv.getTag()+"", tag)) {
									initViewPager(j, data.imgList);
									if (reViewPager.getVisibility() == View.GONE) {
										scaleExpandAnimation(reViewPager);
										reViewPager.setVisibility(View.VISIBLE);
										tvCount.setText((j+1)+"/"+size);
									}
									break;
								}
							}
						}
					});

				}
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.llBack:
				finish();
				break;

		}
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
				Picasso.get().load(imgUrl).into(imageView);
				imageArray[i] = imageView;
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

}
