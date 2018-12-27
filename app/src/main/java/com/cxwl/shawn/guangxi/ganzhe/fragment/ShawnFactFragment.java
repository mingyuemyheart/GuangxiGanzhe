package com.cxwl.shawn.guangxi.ganzhe.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.R;
import com.cxwl.shawn.guangxi.ganzhe.dto.ColumnData;
import com.cxwl.shawn.guangxi.ganzhe.dto.FactDto;
import com.cxwl.shawn.guangxi.ganzhe.util.CommonUtil;
import com.cxwl.shawn.guangxi.ganzhe.util.OkHttpUtil;
import com.cxwl.shawn.guangxi.ganzhe.view.MainViewPager;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 蔗田监测-田间实况
 */
public class ShawnFactFragment extends Fragment {

    private LinearLayout llContainer;
    private MainViewPager viewPager;
    private List<Fragment> fragments = new ArrayList<>();
    private int width;
    private float density;
    private List<FactDto> dataList = new ArrayList<>();
    private AVLoadingIndicatorView loadingView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shawn_fragment_fact, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWidget(view);
    }

    private void initWidget(View view) {
        llContainer = view.findViewById(R.id.llContainer);
        loadingView = view.findViewById(R.id.loadingView);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        density = dm.density;

        OkHttpFact(view);
    }

    /**
     * 获取蔗田实况数据
     */
    private void OkHttpFact(final View view) {
        final String url = "http://113.16.174.77:8076/gzqx/dates/getstid?";
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
                                if (!TextUtils.isEmpty(result)) {
                                    try {
                                        dataList.clear();
                                        JSONArray array = new JSONArray(result);
                                        for (int i = 0; i < array.length(); i++) {
                                            FactDto dto = new FactDto();
                                            JSONObject itemObj = array.getJSONObject(i);
                                            if (!itemObj.isNull("Station_Id")) {
                                                dto.stationId = itemObj.getString("Station_Id");
                                            }
                                            if (!itemObj.isNull("Station_Name")) {
                                                dto.stationName = itemObj.getString("Station_Name");
                                            }
                                            if (!itemObj.isNull("Recod_time")) {
                                                dto.time = itemObj.getString("Recod_time");
                                            }
                                            if (!itemObj.isNull("ioc")) {
                                                dto.imgUrl = itemObj.getString("ioc");
                                            }
                                            if (!itemObj.isNull("iocc")) {
                                                dto.imgUrlThumb = itemObj.getString("iocc");
                                            }

                                            if (!itemObj.isNull("day_pre")) {
                                                dto.dayPre = itemObj.getString("day_pre")+"mm";
                                                if (dto.dayPre.contains("9999")) {
                                                    dto.dayPre = "--";
                                                }
                                            }else {
                                                dto.dayPre = "--";
                                            }

                                            if (!itemObj.isNull("tem_air_60cm")) {
                                                dto.temp60 = itemObj.getString("tem_air_60cm")+"℃";
                                                if (dto.temp60.contains("9999")) {
                                                    dto.temp60 = "--";
                                                }
                                            }else {
                                                dto.temp60 = "--";
                                            }

                                            if (!itemObj.isNull("tem_air_150cm")) {
                                                dto.temp150 = itemObj.getString("tem_air_150cm")+"℃";
                                                if (dto.temp150.contains("9999")) {
                                                    dto.temp150 = "--";
                                                }
                                            }else {

                                                dto.temp150 = "--";
                                            }
                                            if (!itemObj.isNull("tem_air_300cm")) {
                                                dto.temp300 = itemObj.getString("tem_air_300cm")+"℃";
                                                if (dto.temp300.contains("9999")) {
                                                    dto.temp300 = "--";
                                                }
                                            }else {
                                                dto.temp300 = "--";
                                            }

                                            if (!itemObj.isNull("Rh_air_60cm")) {
                                                dto.humidity60 = itemObj.getString("Rh_air_60cm")+"%";
                                                if (dto.humidity60.contains("9999")) {
                                                    dto.humidity60 = "--";
                                                }
                                            }else {
                                                dto.humidity60 = "--";
                                            }

                                            if (!itemObj.isNull("Rh_air_150cm")) {
                                                dto.humidity150 = itemObj.getString("Rh_air_150cm")+"%";
                                                if (dto.humidity150.contains("9999")) {
                                                    dto.humidity150 = "--";
                                                }
                                            }else {
                                                dto.humidity150 = "--";
                                            }

                                            if (!itemObj.isNull("Rh_air_300cm")) {
                                                dto.humidity300 = itemObj.getString("Rh_air_300cm")+"%";
                                                if (dto.humidity300.contains("9999")) {
                                                    dto.humidity300 = "--";
                                                }
                                            }else {
                                                dto.humidity300 = "--";
                                            }

                                            if (!itemObj.isNull("tem_gro_20cm")) {
                                                dto.grotemp30 = itemObj.getString("tem_gro_20cm")+"℃";
                                                if (dto.grotemp30.contains("9999")) {
                                                    dto.grotemp30 = "--";
                                                }
                                            }else {
                                                dto.grotemp30 = "--";
                                            }

                                            if (!itemObj.isNull("tem_gro_40cm")) {
                                                dto.grotemp50 = itemObj.getString("tem_gro_40cm")+"℃";
                                                if (dto.grotemp50.contains("9999")) {
                                                    dto.grotemp50 = "--";
                                                }
                                            }else {
                                                dto.grotemp50 = "--";
                                            }

                                            if (!itemObj.isNull("WCOS_20cm")) {
                                                dto.grohumidity20 = itemObj.getString("WCOS_20cm")+"%";
                                                if (dto.grohumidity20.contains("9999")) {
                                                    dto.grohumidity20 = "--";
                                                }
                                            }else {
                                                dto.grohumidity20 = "--";
                                            }

                                            if (!itemObj.isNull("WCOS_40cm")) {
                                                dto.grohumidity40 = itemObj.getString("WCOS_40cm")+"%";
                                                if (dto.grohumidity40.contains("9999")) {
                                                    dto.grohumidity40 = "--";
                                                }
                                            }else {
                                                dto.grohumidity40 = "--";
                                            }

                                            if (!TextUtils.isEmpty(dto.stationName) && !dto.stationName.contains("9999")) {//过滤掉站名为9999的站
                                                if (dto.stationName.contains("甘蔗")) {
                                                    dataList.add(0, dto);
                                                }else {
                                                    dataList.add(dto);
                                                }
                                            }
                                        }

                                        initViewPager(view);

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
     * 初始化viewPager
     */
    private void initViewPager(View view) {
        llContainer.removeAllViews();
        List<ColumnData> columnList = new ArrayList<>();
        ColumnData data = new ColumnData();
        data.name = "蔗田实景";
        columnList.add(data);
        data = new ColumnData();
        data.name = "小气候查询";
        columnList.add(data);
        data = new ColumnData();
        data.name = "相关数据查询";
        columnList.add(data);

        int columnSize = columnList.size();
        for (int i = 0; i < columnSize; i++) {
            ColumnData dto = columnList.get(i);

            TextView tvName = new TextView(getActivity());
            tvName.setGravity(Gravity.CENTER);
            tvName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            tvName.setPadding(0, (int)(density*5), 0, (int)(density*5));
            tvName.setOnClickListener(new MyOnClickListener(i));
            tvName.setTextColor(getResources().getColor(R.color.white));
            if (i == 0) {
                tvName.setBackgroundColor(0xff3EB6A5);
            }else {
                tvName.setBackgroundColor(0xff88CBC2);
            }
            if (!TextUtils.isEmpty(dto.name)) {
                tvName.setText(dto.name);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = 1.0f;
//                    tvName.measure(0, 0);
//                    params.width = tvName.getMeasuredWidth();
            params.setMargins(0, 0, (int)CommonUtil.dip2px(getActivity(), 0.5f), 0);
            if (columnSize == 1) {
                params.width = width;
            }else if (columnSize == 2) {
                params.width = width/2;
            }else {
                params.width = width/3;
            }
            tvName.setLayoutParams(params);
            llContainer.addView(tvName, i);

            Fragment fragment = null;
            if (TextUtils.equals(dto.name, "蔗田实景")) {//蔗田实景
                fragment = new ShawnFactImageFragment();
            }else if (TextUtils.equals(dto.name, "小气候查询")) {//小气候查询
                fragment = new ShawnFactClimateFragment();
            }else if (TextUtils.equals(dto.name, "相关数据查询")) {//相关数据查询
                fragment = new ShawnFactDataFragment();
            }
            if (fragment != null) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("dataList", (ArrayList<? extends Parcelable>) dataList);
                fragment.setArguments(bundle);
                fragments.add(fragment);
            }
        }

        viewPager = view.findViewById(R.id.viewPager);
        viewPager.setSlipping(true);//设置ViewPager是否可以滑动
        viewPager.setOffscreenPageLimit(fragments.size());
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        viewPager.setAdapter(new MyPagerAdapter());
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            if (llContainer != null) {
                for (int i = 0; i < llContainer.getChildCount(); i++) {
                    TextView tvName = (TextView) llContainer.getChildAt(i);
                    if (i == arg0) {
                        tvName.setBackgroundColor(0xff3EB6A5);
                    }else {
                        tvName.setBackgroundColor(0xff88CBC2);
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
     *
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

}
