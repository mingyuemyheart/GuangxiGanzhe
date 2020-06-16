package com.cxwl.shawn.guangxi.ganzhe;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.common.CONST;
import com.cxwl.shawn.guangxi.ganzhe.dto.ColumnData;
import com.cxwl.shawn.guangxi.ganzhe.fragment.ShawnFactFragment;
import com.cxwl.shawn.guangxi.ganzhe.fragment.ShawnGuangaiFragment;
import com.cxwl.shawn.guangxi.ganzhe.fragment.ShawnPDFFragment;
import com.cxwl.shawn.guangxi.ganzhe.fragment.ShawnRadarFragment;
import com.cxwl.shawn.guangxi.ganzhe.util.CommonUtil;
import com.cxwl.shawn.guangxi.ganzhe.view.MainViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 多个标签页
 * 蔗田监测
 */
public class ShawnColumnsActivity extends FragmentActivity implements View.OnClickListener {

    private Context mContext;
    private LinearLayout llContainer;
    private MainViewPager viewPager;
    private List<Fragment> fragments = new ArrayList<>();
    private HorizontalScrollView hScrollView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shawn_activity_columns);
        mContext = this;
        initWidget();
        initViewPager();
    }

    private void initWidget() {
        LinearLayout llBack = findViewById(R.id.llBack);
        llBack.setOnClickListener(this);
        TextView tvTitle = findViewById(R.id.tvTitle);
        llContainer = findViewById(R.id.llContainer);
        hScrollView1 = findViewById(R.id.hScrollView1);

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
                List<ColumnData> columnList = data.child;
                int columnSize = columnList.size();
                if (columnSize <= 1) {
                    llContainer.setVisibility(View.GONE);
                }

                llContainer.removeAllViews();
                for (int i = 0; i < columnSize; i++) {
                    ColumnData dto = columnList.get(i);

                    TextView tvName = new TextView(mContext);
                    tvName.setGravity(Gravity.CENTER);
                    tvName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    tvName.setPadding(0, (int)CommonUtil.dip2px(this, 5), 0, (int)CommonUtil.dip2px(this, 5));
                    tvName.setOnClickListener(new MyOnClickListener(i));
                    if (i == 0) {
                        tvName.setTextColor(getResources().getColor(R.color.white));
                        tvName.setBackgroundColor(0xff5DBB8B);
                    }else {
                        tvName.setTextColor(getResources().getColor(R.color.text_color4));
                        tvName.setBackgroundColor(getResources().getColor(R.color.light_gray));
                    }
                    if (!TextUtils.isEmpty(dto.name)) {
                        tvName.setText(dto.name);
                        if (TextUtils.equals(dto.name, "阀门A")) {
                            tvName.setText("A区轮灌组");
                        } else if (TextUtils.equals(dto.name, "阀门B")) {
                            tvName.setText("B区轮灌组");
                        }
                    }
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.weight = 1.0f;
//                    tvName.measure(0, 0);
//                    params.width = tvName.getMeasuredWidth();
                    params.setMargins(0, 0, (int)CommonUtil.dip2px(mContext, 0.5f), 0);
					if (columnSize == 1) {
						params.width = CommonUtil.widthPixels(this);
					}else if (columnSize == 2) {
						params.width = CommonUtil.widthPixels(this)/2;
					}else {
						params.width = CommonUtil.widthPixels(this)/3;
					}
                    tvName.setLayoutParams(params);
                    llContainer.addView(tvName, i);

                    Fragment fragment = null;
                    if (TextUtils.equals(dto.showType, CONST.LOCAL)) {
                        if (TextUtils.equals(dto.id, "101")) {//天气雷达
                            fragment = new ShawnRadarFragment();
                        }else if (TextUtils.equals(dto.id, "102")) {//田间实况
                            fragment = new ShawnFactFragment();
                        }else if (TextUtils.equals(dto.id, "103")) {//农事焦点
                            fragment = new ShawnPDFFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString(CONST.WEB_URL, dto.dataUrl);
                            fragment.setArguments(bundle);
                        }else if (TextUtils.equals(dto.id, "2001")) {//智能灌溉-阀门1
                            fragment = new ShawnGuangaiFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString(CONST.WEB_URL, dto.dataUrl);
                            bundle.putString("valve_id", "3880");
                            fragment.setArguments(bundle);
                        }else if (TextUtils.equals(dto.id, "2002")) {//智能灌溉-阀门2
                            fragment = new ShawnGuangaiFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString(CONST.WEB_URL, dto.dataUrl);
                            bundle.putString("valve_id", "3881");
                            fragment.setArguments(bundle);
                        }
                    }
                    if (fragment != null) {
                        fragments.add(fragment);
                    }
                }

                viewPager = findViewById(R.id.viewPager);
                viewPager.setSlipping(true);//设置ViewPager是否可以滑动
                viewPager.setOffscreenPageLimit(fragments.size());
                viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
                viewPager.setAdapter(new MyPagerAdapter());
            }
        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            if (llContainer != null) {
                for (int i = 0; i < llContainer.getChildCount(); i++) {
                    TextView tvName = (TextView) llContainer.getChildAt(i);
                    if (i == arg0) {
                        tvName.setTextColor(getResources().getColor(R.color.white));
                        tvName.setBackgroundColor(0xff5DBB8B);
                    }else {
                        tvName.setTextColor(getResources().getColor(R.color.text_color4));
                        tvName.setBackgroundColor(getResources().getColor(R.color.light_gray));
                    }
                }

                if (llContainer.getChildCount() > 4) {
                    hScrollView1.smoothScrollTo(CommonUtil.widthPixels(mContext)/4*arg0, 0);
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
    private class MyOnClickListener implements View.OnClickListener {
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
     */
    private class MyPagerAdapter extends PagerAdapter {
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(fragments.get(position).getView());
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = fragments.get(position);
            if (!fragment.isAdded()) { // 如果fragment还没有added
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add(fragment, fragment.getClass().getSimpleName());
                ft.commit();
                /**
                 * 在用FragmentTransaction.commit()方法提交FragmentTransaction对象后
                 * 会在进程的主线程中,用异步的方式来执行。
                 * 如果想要立即执行这个等待中的操作,就要调用这个方法(只能在主线程中调用)。
                 * 要注意的是,所有的回调和相关的行为都会在这个调用中被执行完成,因此要仔细确认这个方法的调用位置。
                 */
                getFragmentManager().executePendingTransactions();
            }

            if (fragment.getView().getParent() == null) {
                container.addView(fragment.getView()); // 为viewpager增加布局
            }
            return fragment.getView();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llBack:
                finish();
                break;
        }
    }

}
