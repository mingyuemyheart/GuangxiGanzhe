package com.cxwl.shawn.guangxi.ganzhe;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.common.CONST;
import com.cxwl.shawn.guangxi.ganzhe.dto.ColumnData;
import com.cxwl.shawn.guangxi.ganzhe.fragment.ShawnPdfListFragment;
import com.cxwl.shawn.guangxi.ganzhe.view.MainViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 带有标签页的pdf文档界面
 */
public class ShawnPdfTitleActivity extends FragmentActivity implements OnClickListener {

	private Context mContext;
	private LinearLayout llContainer,llContainer1;
	private MainViewPager viewPager;
	private List<Fragment> fragments = new ArrayList<>();
	private HorizontalScrollView hScrollView1;
	private int width;
	private float density;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_pdf_title);
		mContext = this;
		initWidget();
		initViewPager();
	}

	private void initWidget() {
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		TextView tvTitle = findViewById(R.id.tvTitle);
		llContainer = findViewById(R.id.llContainer);
		llContainer1 = findViewById(R.id.llContainer1);
		hScrollView1 = findViewById(R.id.hScrollView1);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		density = dm.density;

		String title = getIntent().getStringExtra(CONST.ACTIVITY_NAME);
		if (!TextUtils.isEmpty(title)) {
			tvTitle.setText(title);
		}

	}

	/**
	 * 初始化viewPager
	 */
	private void initViewPager() {
		if (getIntent().hasExtra("data")) {
			ColumnData data = getIntent().getParcelableExtra("data");
			if (data != null) {
				List<ColumnData> columnList;
				if (data.child.size() > 0) {
					columnList = new ArrayList<>(data.child);
				} else {
					columnList = new ArrayList<>();
					columnList.add(data);
				}
				int columnSize = columnList.size();
				if (columnSize <= 1) {
					llContainer.setVisibility(View.GONE);
					llContainer1.setVisibility(View.GONE);
				}

				llContainer.removeAllViews();
				llContainer1.removeAllViews();
				for (int i = 0; i < columnSize; i++) {
					ColumnData dto = columnList.get(i);

					TextView tvName = new TextView(mContext);
					tvName.setGravity(Gravity.CENTER);
					tvName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
					tvName.setPadding(0, (int)(density*5), 0, (int)(density*5));
					tvName.setOnClickListener(new MyOnClickListener(i));
					if (i == 0) {
						tvName.setTextColor(getResources().getColor(R.color.colorPrimary));
					}else {
						tvName.setTextColor(getResources().getColor(R.color.text_color3));
					}
					if (!TextUtils.isEmpty(dto.name)) {
						tvName.setText(dto.name);
					}
					tvName.measure(0, 0);
					LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//					params.weight = 1.0f;
					params.width = tvName.getMeasuredWidth();
					params.setMargins((int)(density*10), 0, (int)(density*10), 0);
//					if (columnSize == 1) {
//						params.width = width;
//					}else if (columnSize == 2) {
//						params.width = width/2;
//					}else {
//						params.width = width/3;
//					}
					tvName.setLayoutParams(params);
					llContainer.addView(tvName, i);

					TextView tvBar = new TextView(mContext);
					tvBar.setGravity(Gravity.CENTER);
					tvBar.setOnClickListener(new MyOnClickListener(i));
					if (i == 0) {
						tvBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
					}else {
						tvBar.setBackgroundColor(getResources().getColor(R.color.transparent));
					}
					LayoutParams params1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//					params1.weight = 1.0f;
					params1.width = tvName.getMeasuredWidth();
//					if (columnSize == 1) {
//						params1.width = width;
//					}else if (columnSize == 2) {
//						params1.width = width/2-(int)(density*20);
//					}else {
//						params1.width = width/3-(int)(density*20);
//					}
					params1.height = (int) (density*3);
					params1.setMargins((int)(density*10), 0, (int)(density*10), 0);
					tvBar.setLayoutParams(params1);
					llContainer1.addView(tvBar, i);

					ShawnPdfListFragment fragment = new ShawnPdfListFragment();
					Bundle bundle = new Bundle();
					bundle.putString(CONST.LOCAL_ID, dto.id);
					bundle.putString(CONST.WEB_URL, dto.dataUrl);
					fragment.setArguments(bundle);
					fragments.add(fragment);
				}

				viewPager = findViewById(R.id.viewPager);
				viewPager.setSlipping(true);//设置ViewPager是否可以滑动
				viewPager.setOffscreenPageLimit(fragments.size());
				viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
				viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
			}
		}
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			if (llContainer != null) {
				for (int i = 0; i < llContainer.getChildCount(); i++) {
					TextView tvName = (TextView) llContainer.getChildAt(i);
					if (i == arg0) {
						tvName.setTextColor(getResources().getColor(R.color.colorPrimary));
					}else {
						tvName.setTextColor(getResources().getColor(R.color.text_color3));
					}
				}

				if (llContainer.getChildCount() > 4) {
					hScrollView1.smoothScrollTo(width/4*arg0, 0);
				}

			}

			if (llContainer1 != null) {
				for (int i = 0; i < llContainer1.getChildCount(); i++) {
					TextView tvBar = (TextView) llContainer1.getChildAt(i);
					if (i == arg0) {
						tvBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
					}else {
						tvBar.setBackgroundColor(getResources().getColor(R.color.transparent));
					}
				}
			}

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	/**
	 * 头标点击监听
	 * @author shawn_sun
	 */
	private class MyOnClickListener implements OnClickListener {
		private int index;

		private MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			if (viewPager != null) {
				viewPager.setCurrentItem(index, true);
			}
		}
	}
	
	/**
	 * @ClassName: MyPagerAdapter
	 * @Description: TODO填充ViewPager的数据适配器
	 * @author Panyy
	 * @date 2013 2013年11月6日 下午2:37:47
	 *
	 */
	private class MyPagerAdapter extends FragmentStatePagerAdapter {

		private MyPagerAdapter(FragmentManager fm) {
			super(fm);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

		@Override
		public Fragment getItem(int arg0) {
			return fragments.get(arg0);
		}

		@Override
		public int getItemPosition(Object object) {
			return PagerAdapter.POSITION_NONE;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llBack:
			finish();
			break;

		default:
			break;
		}
	}

}
