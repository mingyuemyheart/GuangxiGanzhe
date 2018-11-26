package com.cxwl.shawn.guangxi.ganzhe;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.cxwl.shawn.guangxi.ganzhe.adapter.ShawnMainAdapter;
import com.cxwl.shawn.guangxi.ganzhe.common.CONST;
import com.cxwl.shawn.guangxi.ganzhe.common.MyApplication;
import com.cxwl.shawn.guangxi.ganzhe.dto.ColumnData;
import com.cxwl.shawn.guangxi.ganzhe.dto.WeatherDto;
import com.cxwl.shawn.guangxi.ganzhe.manager.DataCleanManager;
import com.cxwl.shawn.guangxi.ganzhe.util.AuthorityUtil;
import com.cxwl.shawn.guangxi.ganzhe.util.AutoUpdateUtil;
import com.cxwl.shawn.guangxi.ganzhe.util.CommonUtil;
import com.cxwl.shawn.guangxi.ganzhe.util.OkHttpUtil;
import com.cxwl.shawn.guangxi.ganzhe.util.SecretUrlUtil;
import com.cxwl.shawn.guangxi.ganzhe.util.WeatherUtil;
import com.cxwl.shawn.guangxi.ganzhe.view.HourlyItemView;
import com.cxwl.shawn.guangxi.ganzhe.view.HourlyView;
import com.cxwl.shawn.guangxi.ganzhe.view.MyHorizontalScrollView;
import com.cxwl.shawn.guangxi.ganzhe.view.WeeklyView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.com.weather.api.WeatherAPI;
import cn.com.weather.beans.Weather;
import cn.com.weather.constants.Constants.Language;
import cn.com.weather.listener.AsyncResponseHandler;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class ShawnMainActivity extends ShawnBaseActivity implements OnClickListener, AMapLocationListener, MyApplication.NavigationListener{

    private Context mContext = null;
    private TextView tvLocation,tvTime,tvPhe,tvTemperature,tvHumidity,tvRain,tvWind,tvForeTime1,tvForeTime2;
    private ImageView ivPhe;
    private RelativeLayout reTitle;
    private LinearLayout llContent,llContainer1,llContainer2,llContainer3;
    private long mExitTime;//记录点击完返回按钮后的long型时间
    private GridView gridView;
    private ShawnMainAdapter mAdapter;
    private List<ColumnData> dataList = new ArrayList<>();
    private String cityId = "101300101",cityName = "南宁市青秀区";
    private double lat = 22.785879, lng = 108.494024;
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmm", Locale.CHINA);
    private SimpleDateFormat sdf2 = new SimpleDateFormat("HH", Locale.CHINA);
    private SimpleDateFormat sdf3 = new SimpleDateFormat("MM月dd日", Locale.CHINA);
    private SimpleDateFormat sdf4 = new SimpleDateFormat("MM月dd日 HH:mm", Locale.CHINA);
    private List<WeatherDto> aqiList = new ArrayList<>();//空气指数list
    private int width, height, gridViewHeight;
    private float density;
    private SwipeRefreshLayout refreshLayout;//下拉刷新布局

    //侧拉页面
    private DrawerLayout drawerlayout;
    private RelativeLayout reLeft;
    private TextView tvCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shawn_activity_main);
        mContext = this;
        initRefreshLayout();
        initWidget();
        initGridView();
        checkAuthority();
        MyApplication.setNavigationListener(this);
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
                checkAuthority();
            }
        });
    }

    private void refresh() {
        if (CommonUtil.isLocationOpen(mContext)) {
            startLocation();
        }else {
            locationDialog(mContext);
        }
    }

    /**
     * 初始化控件
     */
    private void initWidget() {
        AutoUpdateUtil.checkUpdate(this, mContext, "105", getString(R.string.app_name), true);

        tvLocation = findViewById(R.id.tvLocation);
        tvLocation.setOnClickListener(this);
        tvLocation.setFocusable(true);
        tvLocation.setFocusableInTouchMode(true);
        tvLocation.requestFocus();
        tvLocation.setText(cityName);
        ImageView ivSetting = findViewById(R.id.ivSetting);
        ivSetting.setOnClickListener(this);
        ImageView ivSearch = findViewById(R.id.ivSearch);
        ivSearch.setOnClickListener(this);
        tvTime = findViewById(R.id.tvTime);
        tvPhe = findViewById(R.id.tvPhe);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvRain = findViewById(R.id.tvRain);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvWind = findViewById(R.id.tvWind);
        ivPhe = findViewById(R.id.ivPhe);
        llContent = findViewById(R.id.llContent);
        tvForeTime1 = findViewById(R.id.tvForeTime1);
        tvForeTime2 = findViewById(R.id.tvForeTime2);
        llContainer1 = findViewById(R.id.llContainer1);
        llContainer2 = findViewById(R.id.llContainer2);
        llContainer3 = findViewById(R.id.llContainer3);
        reTitle = findViewById(R.id.reTitle);
        MyHorizontalScrollView hScrollView1 = findViewById(R.id.hScrollView1);
        hScrollView1.setScrollListener(scrollListener);

        //侧拉页面
        drawerlayout = findViewById(R.id.drawerlayout);
        drawerlayout.setVisibility(View.VISIBLE);
        reLeft = findViewById(R.id.reLeft);
        TextView tvUserName = findViewById(R.id.tvUserName);
        TextView tvLogout = findViewById(R.id.tvLogout);
        tvLogout.setOnClickListener(this);
        tvCache = findViewById(R.id.tvCache);
        LinearLayout llClearCache = findViewById(R.id.llClearCache);
        llClearCache.setOnClickListener(this);
        LinearLayout llVersion = findViewById(R.id.llVersion);
        llVersion.setOnClickListener(this);
        TextView tvVersion = findViewById(R.id.tvVersion);
        tvVersion.setText(CommonUtil.getVersion(mContext));
        LinearLayout llFeedBack = findViewById(R.id.llFeedBack);
        llFeedBack.setOnClickListener(this);
        reLeft = findViewById(R.id.reLeft);

        getDeviceWidthHeight();

        LayoutParams params = reLeft.getLayoutParams();
        params.width = width-(int) CommonUtil.dip2px(mContext, 50);
        reLeft.setLayoutParams(params);

        SharedPreferences sharedPreferences = getSharedPreferences(MyApplication.USERINFO, Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString(MyApplication.USERNAME, null);
        if (userName != null) {
            tvUserName.setText(userName);
        }

        getCache();
    }

    private void getDeviceWidthHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
        density = dm.density;
    }

    /**
     * 开始定位
     */
    private void startLocation() {
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();//初始化定位参数
        AMapLocationClient mLocationClient = new AMapLocationClient(mContext);//初始化定位
        mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
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
            CONST.ADCODE = amapLocation.getAdCode();
            tvLocation.setText(amapLocation.getDistrict());
            cityName = amapLocation.getDistrict();
            lat = amapLocation.getLatitude();
            lng = amapLocation.getLongitude();
            OkHttpAqi();

        }
    }

    /**
     * 获取7天aqi
     */
    private void OkHttpAqi() {
        if (!refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(true);
        }
        final String url = SecretUrlUtil.airForecast(lng, lat);
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
                                        if (!obj.isNull("series")) {
                                            aqiList.clear();
                                            JSONArray array = obj.getJSONArray("series");
                                            for (int i = 0; i < array.length(); i++) {
                                                WeatherDto data = new WeatherDto();
                                                data.aqi = String.valueOf(array.get(i));
                                                aqiList.add(data);
                                            }
                                        }
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
                                }

                                OkHttpGeo(lng, lat);

                            }
                        });
                    }
                });
            }
        }).start();
    }

    /**
     * 获取天气数据
     */
    private void OkHttpGeo(final double lng, final double lat) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtil.enqueue(new Request.Builder().url(SecretUrlUtil.geo(lng, lat)).build(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        String result = response.body().string();
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                JSONObject obj = new JSONObject(result);
                                if (!obj.isNull("geo")) {
                                    JSONObject geoObj = obj.getJSONObject("geo");
                                    if (!geoObj.isNull("id")) {
                                        cityId = geoObj.getString("id");
                                        getWeatherInfo();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }).start();
    }

    private void getWeatherInfo() {
        if (TextUtils.isEmpty(cityId)) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                WeatherAPI.getWeather2(mContext, cityId, Language.ZH_CN, new AsyncResponseHandler() {
                    @Override
                    public void onComplete(final Weather content) {
                        super.onComplete(content);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String result = content.toString();
                                if (!TextUtils.isEmpty(result)) {
                                    try {
                                        JSONObject obj = new JSONObject(result);

                                        //实况信息
                                        if (!obj.isNull("l")) {
                                            JSONObject l = obj.getJSONObject("l");
                                            if (!l.isNull("l7")) {
                                                String time = l.getString("l7");
                                                if (time != null) {
                                                    tvTime.setText(sdf3.format(new Date())+time+"发布");
                                                }
                                            }
                                            if (!l.isNull("l1")) {
                                                String factTemp = WeatherUtil.lastValue(l.getString("l1"));
                                                if (!TextUtils.isEmpty(factTemp)) {
                                                    tvTemperature.setText(factTemp+"°");
                                                }
                                            }
                                            if (!l.isNull("l5")) {
                                                String pheCode = WeatherUtil.lastValue(l.getString("l5"));
                                                Drawable drawable = getResources().getDrawable(R.drawable.phenomenon_drawable);
                                                try {
                                                    long current = sdf2.parse(sdf2.format(new Date())).getTime();
                                                    if (current >= 5 && current < 18) {
                                                        drawable = getResources().getDrawable(R.drawable.phenomenon_drawable);
                                                    }else {
                                                        drawable = getResources().getDrawable(R.drawable.phenomenon_drawable_night);
                                                    }
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                                drawable.setLevel(Integer.valueOf(pheCode));
                                                ivPhe.setImageDrawable(drawable);
                                                tvPhe.setText(getString(WeatherUtil.getWeatherId(Integer.valueOf(pheCode))));
                                            }
                                            if (!l.isNull("l6")) {
                                                String factRain = WeatherUtil.lastValue(l.getString("l6"));
                                                tvRain.setText("降水量："+factRain+"mm");
                                            }
                                            if (!l.isNull("l2")) {
                                                String humidity = WeatherUtil.lastValue(l.getString("l2"));
                                                tvHumidity.setText("相对湿度：" + humidity + "%");
                                            }
                                            if (!l.isNull("l4")) {
                                                String windDir = WeatherUtil.lastValue(l.getString("l4"));
                                                if (!l.isNull("l3")) {
                                                    String windForce = WeatherUtil.lastValue(l.getString("l3"));
                                                    tvWind.setText(getString(WeatherUtil.getWindDirection(Integer.valueOf(windDir))) + " " +
                                                            WeatherUtil.getFactWindForce(Integer.valueOf(windForce)));
                                                }
                                            }
                                        }

                                        //逐小时预报信息
                                        if (!obj.isNull("jh")) {
                                            JSONArray jh = obj.getJSONArray("jh");
                                            List<WeatherDto> hourlyList = new ArrayList<>();
                                            for (int i = 0; i < jh.length(); i++) {
                                                JSONObject itemObj = jh.getJSONObject(i);
                                                WeatherDto dto = new WeatherDto();
                                                dto.hourlyCode = Integer.valueOf(itemObj.getString("ja"));
                                                dto.hourlyTemp = Integer.valueOf(itemObj.getString("jb"));
                                                dto.hourlyDirCode = Integer.valueOf(itemObj.getString("jc"));
                                                dto.hourlyForce = Integer.valueOf(itemObj.getString("jd"));
                                                dto.hourlyTime = itemObj.getString("jf");
                                                hourlyList.add(dto);
                                            }

                                            HourlyView hourlyView = new HourlyView(mContext);
                                            hourlyView.setData(hourlyList, width*2/density, ShawnMainActivity.this);
                                            llContainer1.removeAllViews();
                                            llContainer1.addView(hourlyView, (int)(CommonUtil.dip2px(mContext, width*2/density)), (int)(CommonUtil.dip2px(mContext, 100)));
                                        }

                                        //15天预报信息
                                        if (!obj.isNull("f")) {
                                            List<WeatherDto> weeklyList = new ArrayList<>();
                                            JSONObject f = obj.getJSONObject("f");
                                            String f0 = f.getString("f0");
                                            try {
                                                tvForeTime1.setText(sdf4.format(sdf1.parse(f0))+"发布");
                                                tvForeTime2.setText(sdf4.format(sdf1.parse(f0))+"发布");
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            JSONArray f1 = f.getJSONArray("f1");
                                            for (int i = 0; i < f1.length(); i++) {
                                                WeatherDto dto = new WeatherDto();

                                                //预报时间
                                                dto.date = CommonUtil.getDate(f0, i);//日期
                                                dto.week = CommonUtil.getWeek(i);//星期几

                                                //预报内容
                                                JSONObject weeklyObj = f1.getJSONObject(i);
                                                //晚上
                                                dto.lowPheCode = Integer.valueOf(weeklyObj.getString("fb"));
                                                dto.lowPhe = getString(WeatherUtil.getWeatherId(Integer.valueOf(weeklyObj.getString("fb"))));
                                                dto.lowTemp = Integer.valueOf(weeklyObj.getString("fd"));
                                                dto.windDir = Integer.valueOf(weeklyObj.getString("ff"));
                                                dto.windForce = Integer.valueOf(weeklyObj.getString("fh"));

                                                //白天
                                                dto.highPheCode = Integer.valueOf(weeklyObj.getString("fa"));
                                                dto.highPhe = getString(WeatherUtil.getWeatherId(Integer.valueOf(weeklyObj.getString("fa"))));
                                                dto.highTemp = Integer.valueOf(weeklyObj.getString("fc"));
                                                dto.windDir = Integer.valueOf(weeklyObj.getString("fe"));
                                                dto.windForce = Integer.valueOf(weeklyObj.getString("fg"));

                                                if (aqiList.size() > 0 && i < aqiList.size()) {
                                                    String aqiValue = aqiList.get(i).aqi;
                                                    if (!TextUtils.isEmpty(aqiValue)) {
                                                        dto.aqi = aqiValue;
                                                    }
                                                }
                                                weeklyList.add(dto);
                                            }
                                            if (mAdapter != null) {
                                                mAdapter.notifyDataSetChanged();
                                            }

                                            //一周预报曲线
                                            WeeklyView weeklyView = new WeeklyView(mContext);
                                            weeklyView.setData(weeklyList);
                                            llContainer2.removeAllViews();
                                            llContainer2.addView(weeklyView, width*2-(int)CommonUtil.dip2px(mContext, 40), (int)(CommonUtil.dip2px(mContext, 200)));

                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                refreshLayout.setRefreshing(false);
                                llContent.setVisibility(View.VISIBLE);

                            }
                        });
                    }

                    @Override
                    public void onError(Throwable error, String content) {
                        super.onError(error, content);
                    }
                });

            }
        }).start();
    }

    public static final int MGS_SCROLL = 100000;
    public static final int MGS_VALUE = 100001;
    private int min = 0, index = 0;
    private List<WeatherDto> tempList = new ArrayList<>();
    private HourlyItemView hourItemView;
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            if (msg.what == MGS_SCROLL) {
                min = msg.arg1;
                tempList.clear();
                tempList.addAll((Collection<? extends WeatherDto>) msg.obj);

                for (int i = 0; i < tempList.size(); i++) {
                    tempList.get(i).x = tempList.get(0).x;
                }

                hourItemView = new HourlyItemView(mContext);
                hourItemView.setData(tempList.get(index), min, ShawnMainActivity.this);
                llContainer3.removeAllViews();
                llContainer3.addView(hourItemView);
            }
        };
    };

    private MyHorizontalScrollView.ScrollListener scrollListener = new MyHorizontalScrollView.ScrollListener() {
        @Override
        public void onScrollChanged(MyHorizontalScrollView hScrollView, int x, int y, int oldx, int oldy) {
            float itemWidth = width/tempList.size();
            index = (int) (x/itemWidth);
            if (index >= tempList.size()) {
                index = tempList.size()-1;
            }
            Log.e("index2", index+"");

            Message msg = new Message();
            msg.what = MGS_VALUE;
            msg.arg1 = min;
            msg.obj = tempList.get(index);
            hourItemView.handler.sendMessage(msg);
        }
    };

    private void initGridView() {
        dataList.clear();
        dataList.addAll(getIntent().getExtras().<ColumnData>getParcelableArrayList("dataList"));
        gridView = findViewById(R.id.gridView);
        mAdapter = new ShawnMainAdapter(mContext, dataList);
        gridView.setAdapter(mAdapter);
        onLayoutMeasure();
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                ColumnData dto = dataList.get(arg2);
                Intent intent;
                if (TextUtils.equals(dto.showType, CONST.LOCAL)) {
                    if (TextUtils.equals(dto.id, "1")) {//蔗田监测
                        intent = new Intent(mContext, ShawnColumnsActivity.class);
                        intent.putExtra(CONST.ACTIVITY_NAME, dto.name);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("data", dto);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }else if (TextUtils.equals(dto.id, "2")) {//智慧服务
                        intent = new Intent(mContext, ShawnProductActivity.class);
                        intent.putExtra(CONST.ACTIVITY_NAME, dto.name);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("data", dto);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }else if (TextUtils.equals(dto.id, "3")) {//交流互动
                        intent = new Intent(mContext, ShawnProductActivity.class);
                        intent.putExtra(CONST.ACTIVITY_NAME, dto.name);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("data", dto);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void getCache() {
        try {
            tvCache.setText(DataCleanManager.getCacheSize(mContext));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除缓存
     */
    private void dialogCache() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.shawn_dialog_cache, null);
        TextView tvContent = view.findViewById(R.id.tvContent);
        TextView tvNegtive = view.findViewById(R.id.tvNegtive);
        TextView tvPositive = view.findViewById(R.id.tvPositive);

        final Dialog dialog = new Dialog(this, R.style.CustomProgressDialog);
        dialog.setContentView(view);
        dialog.show();

        tvContent.setText("确定清除缓存？");
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
                DataCleanManager.clearCache(mContext);
                getCache();
            }
        });
    }

    /**
     * 登出对话框
     */
    private void dialogLogout() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.shawn_dialog_cache, null);
        TextView tvContent = view.findViewById(R.id.tvContent);
        TextView tvNegtive = view.findViewById(R.id.tvNegtive);
        TextView tvPositive = view.findViewById(R.id.tvPositive);

        final Dialog dialog = new Dialog(mContext, R.style.CustomProgressDialog);
        dialog.setContentView(view);
        dialog.show();

        tvContent.setText("确定要退出登录？");
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
                MyApplication.clearUserInfo(mContext);
                startActivity(new Intent(mContext, ShawnLoginActivity.class));
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (drawerlayout != null) {
                if (drawerlayout.isDrawerOpen(reLeft)) {
                    drawerlayout.closeDrawer(reLeft);
                }else {
                    if ((System.currentTimeMillis() - mExitTime) > 2000) {
                        Toast.makeText(mContext, "再按一次退出"+getString(R.string.app_name), Toast.LENGTH_SHORT).show();
                        mExitTime = System.currentTimeMillis();
                    } else {
                        finish();
                    }
                }
            }else {
                finish();
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivSetting:
                if (drawerlayout.isDrawerOpen(reLeft)) {
                    drawerlayout.closeDrawer(reLeft);
                }else {
                    drawerlayout.openDrawer(reLeft);
                }
                break;
            case R.id.ivSearch:
                startActivityForResult(new Intent(mContext, ShawnCityActivity.class), 1001);
                break;
            case R.id.tvLocation:
//                Intent intentLo = new Intent(mContext, ForecastActivity.class);
//                intentLo.putExtra("cityName", cityName);
//                intentLo.putExtra("cityId", cityId);
//                intentLo.putExtra("lng", lng);
//                intentLo.putExtra("lat", lat);
//                startActivity(intentLo);
                break;

                //侧拉页面
            case R.id.llClearCache:
                dialogCache();
                break;
            case R.id.llVersion:
                AutoUpdateUtil.checkUpdate(this, mContext, "105", getString(R.string.app_name), false);
                break;
            case R.id.tvLogout:
                dialogLogout();
                break;
            case R.id.llFeedBack:
                startActivity(new Intent(this, ShawnFeedbackActivity.class));
                break;

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
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {
                            cityName = bundle.getString("cityName");
                            lng = bundle.getDouble("lng");
                            lat = bundle.getDouble("lat");
                            tvLocation.setText(cityName);
                            OkHttpAqi();
                        }
                    }
                    break;

                default:
                    break;
            }
        }else if (resultCode == 0) {
            switch (requestCode) {
                case 1000:
                    startLocation();
                    break;

                default:
                    break;
            }
        }
    }

    private void locationDialog(Context context) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.shawn_dialog_cache, null);
        TextView tvContent = view.findViewById(R.id.tvContent);
        TextView tvNegtive = view.findViewById(R.id.tvNegtive);
        TextView tvPositive = view.findViewById(R.id.tvPositive);
        tvContent.setText("\""+getString(R.string.app_name)+"\""+"需要获取您的定位权限，否则部分功能无法运行，是否开启定位权限");

        final Dialog dialog = new Dialog(context, R.style.CustomProgressDialog);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.show();

        tvNegtive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                getWeatherInfo();
            }
        });

        tvPositive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 1000);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        onLayoutMeasure();
    }

    @Override
    public void showNavigation(boolean show) {
        if (show) {
            narrowAnimation(gridView);
        }else {
            enlargeAnimation(gridView);
        }
        onLayoutMeasure();
    }

    /**
     * 缩小动画
     */
    private void narrowAnimation(View view) {
        if (view == null) {
            return;
        }

        android.view.animation.ScaleAnimation animation = new android.view.animation.ScaleAnimation(
                1.0f, 1.0f, 1.0f, 1.0f,
                android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
                android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f
        );
        animation.setDuration(500);
        animation.setFillAfter(true);
        view.startAnimation(animation);
    }

    /**
     * 放大动画
     */
    private void enlargeAnimation(View view) {
        if (view == null) {
            return;
        }
        float fromY = 1.0f-1.0f* CommonUtil.navigationBarHeight(mContext)/gridViewHeight;
        android.view.animation.ScaleAnimation animation = new android.view.animation.ScaleAnimation(
                1.0f, 1.0f, fromY, 1.0f,
                android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
                android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f
        );
        animation.setDuration(500);
        animation.setFillAfter(true);
        view.startAnimation(animation);
    }

    /**
     * 判断navigation是否显示，重新计算页面布局
     */
    private void onLayoutMeasure() {
        getDeviceWidthHeight();

        int statusBarHeight = -1;//状态栏高度
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        reTitle.measure(0, 0);
        int height1 = reTitle.getMeasuredHeight();
        llContent.measure(0, 0);
        int height2 = llContent.getMeasuredHeight();

        gridViewHeight = height-statusBarHeight-height1-height2;

        if (mAdapter != null) {
            mAdapter.height = gridViewHeight;
            mAdapter.notifyDataSetChanged();
        }

    }

    //需要申请的所有权限
    private String[] allPermissions = new String[] {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
    };

    //拒绝的权限集合
    public static List<String> deniedList = new ArrayList<>();
    /**
     * 申请定位权限
     */
    private void checkAuthority() {
        if (Build.VERSION.SDK_INT < 23) {
            refresh();
        }else {
            deniedList.clear();
            for (String permission : allPermissions) {
                if (ContextCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED) {
                    deniedList.add(permission);
                }
            }
            if (deniedList.isEmpty()) {//所有权限都授予
                refresh();
            }else {
                String[] permissions = deniedList.toArray(new String[deniedList.size()]);//将list转成数组
                ActivityCompat.requestPermissions(ShawnMainActivity.this, permissions, AuthorityUtil.AUTHOR_LOCATION);
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
                        refresh();
                    }else {//只要有一个没有授权，就提示进入设置界面设置
                        AuthorityUtil.intentAuthorSetting(mContext, "\""+getString(R.string.app_name)+"\""+"需要使用您的位置权限、电话权限，是否前往设置？");
                    }
                }else {
                    for (String permission : permissions) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(ShawnMainActivity.this, permission)) {
                            AuthorityUtil.intentAuthorSetting(mContext, "\""+getString(R.string.app_name)+"\""+"需要使用您的位置权限、电话权限，是否前往设置？");
                            break;
                        }
                    }
                }
                break;
        }
    }

}
