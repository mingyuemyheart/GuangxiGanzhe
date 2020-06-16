package com.cxwl.shawn.guangxi.ganzhe.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
import com.amap.api.services.district.DistrictItem;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearch;
import com.amap.api.services.district.DistrictSearchQuery;
import com.cxwl.shawn.guangxi.ganzhe.R;
import com.cxwl.shawn.guangxi.ganzhe.dto.DisasterDto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommonUtil {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static float dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dpValue * scale;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static float px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return pxValue / scale;
    }

    public static int widthPixels(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int heightPixels(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 显示虚拟键盘
     * @param editText 输入框
     * @param context 上下文
     */
    public static void showInputSoft(EditText editText, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        imm.showSoftInput(editText, 0);
    }

    /**
     * 隐藏虚拟键盘
     * @param editText 输入框
     * @param context 上下文
     */
    public static void hideInputSoft(EditText editText, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     * @param context
     * @return true 表示开启
     */
    public static boolean isLocationOpen(final Context context) {
        LocationManager locationManager  = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    /**
     * 字符串转md5
     * @param text
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String toMD5(String text) {
        StringBuilder sb = null;
        try {
            //获取摘要器 MessageDigest
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            //通过摘要器对字符串的二进制字节数组进行hash计算
            byte[] digest = messageDigest.digest(text.getBytes());

            sb = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                //循环每个字符 将计算结果转化为正整数;
                int digestInt = digest[i] & 0xff;
                //将10进制转化为较短的16进制
                String hexString = Integer.toHexString(digestInt);
                //转化结果如果是个位数会省略0,因此判断并补0
                if (hexString.length() < 2) {
                    sb.append(0);
                }
                //将循环结果添加到缓冲区
                sb.append(hexString);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //返回整个结果
        return sb.toString();
    }

    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    public static int statusBarHeight(Context context) {
        int statusBarHeight = -1;//状态栏高度
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**
     * 获取底部导航栏高度
     * @param context
     * @return
     */
    public static int navigationBarHeight(Context context) {
        int navigationBarHeight = -1;//状态栏高度
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            navigationBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return navigationBarHeight;
    }

    /**
     * 从Assets中读取图片
     */
    public static Bitmap getImageFromAssetsFile(Context context, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 读取assets下文件
     * @param fileName
     * @return
     */
    public static String getFromAssets(Context context, String fileName) {
        String Result = "";
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result;
    }

    /**
     * 根据当前时间获取日期，格式为MM/dd
     * @param i (+1为后一天，-1为前一天，0表示当天)
     * @return
     */
    public static String getDate(int i) {
        String date = null;

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day+i);

        if (c.get(Calendar.MONTH) == 0) {
            date = "01";
        }else if (c.get(Calendar.MONTH) == 1) {
            date = "02";
        }else if (c.get(Calendar.MONTH) == 2) {
            date = "03";
        }else if (c.get(Calendar.MONTH) == 3) {
            date = "04";
        }else if (c.get(Calendar.MONTH) == 4) {
            date = "05";
        }else if (c.get(Calendar.MONTH) == 5) {
            date = "06";
        }else if (c.get(Calendar.MONTH) == 6) {
            date = "07";
        }else if (c.get(Calendar.MONTH) == 7) {
            date = "08";
        }else if (c.get(Calendar.MONTH) == 8) {
            date = "09";
        }else if (c.get(Calendar.MONTH) == 9) {
            date = "10";
        }else if (c.get(Calendar.MONTH) == 10) {
            date = "11";
        }else if (c.get(Calendar.MONTH) == 11) {
            date = "12";
        }

        return date+"/"+c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 根据当前时间获取星期几
     * @param context
     * @param i (+1为后一天，-1为前一天，0表示当天)
     * @return
     */
    public static String getWeek(Context context, int i) {
        String week = null;

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day+i);

        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            week = "周日";
        }else if (c.get(Calendar.DAY_OF_WEEK) == 2) {
            week = "周一";
        }else if (c.get(Calendar.DAY_OF_WEEK) == 3) {
            week = "周二";
        }else if (c.get(Calendar.DAY_OF_WEEK) == 4) {
            week = "周三";
        }else if (c.get(Calendar.DAY_OF_WEEK) == 5) {
            week = "周四";
        }else if (c.get(Calendar.DAY_OF_WEEK) == 6) {
            week = "周五";
        }else if (c.get(Calendar.DAY_OF_WEEK) == 7) {
            week = "周六";
        }

        return week;
    }

    /**
     * 获取listview高度
     * @param listView
     */
    public static int getListViewHeightBasedOnChildren(ListView listView) {
        int height = 0;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return height;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        return height;
    }

    /**
     * 查询高德地图最新行政区划json并保存到本地文件
     * @param context
     * @param adName 行政区划对应名称
     * @param adCode 行政区划id
     */
    public static void drawOnlineDistrict(final Context context, final String adName, final String adCode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DistrictSearch search = new DistrictSearch(context);
                DistrictSearchQuery query = new DistrictSearchQuery();
                query.setKeywords(adCode);//传入关键字
                query.setShowBoundary(true);//是否返回边界值
                search.setQuery(query);
                search.searchDistrictAsyn();
                search.setOnDistrictSearchListener(new DistrictSearch.OnDistrictSearchListener() {
                    @Override
                    public void onDistrictSearched(DistrictResult districtResult) {
                        if (districtResult == null || districtResult.getDistrict() == null) {
                            return;
                        }

                        final DistrictItem item = districtResult.getDistrict().get(0);
                        if (item == null) {
                            return;
                        }

                        String[] polyStr = item.districtBoundary();
                        if (polyStr == null || polyStr.length == 0) {
                            return;
                        }

                        try {
                            JSONObject obj = new JSONObject();
                            obj.put("name", adName);
                            obj.put("id", adCode);

                            JSONArray array1 = new JSONArray();

                            for (String str : polyStr) {
                                String[] latLng = str.split(";");
                                JSONArray array2 = new JSONArray();
                                for (String latstr : latLng) {
                                    String[] lats = latstr.split(",");
                                    JSONArray array3 = new JSONArray();
                                    try {
                                        array3.put(Double.parseDouble(lats[0]));
                                        array3.put(Double.parseDouble(lats[1]));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Log.e("array3", array3.toString());
                                    array2.put(array3);
                                }
                                Log.e("array2", array2.toString());
                                array1.put(array2);
                            }

                            Log.e("array1", array1.toString());
                            obj.put("coordinates", array1);
                            Log.e("obj", obj.toString());

                            File file = new File(Environment.getExternalStorageDirectory()+ File.separator+adCode+".json");
                            CommonUtil.writeExternalFile(file, obj.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });//绑定监听器
            }
        }).start();
    }

    /**
     * 绘制地块
     * @param context
     * @param name
     * @param aMap
     */
    public static void drawLand(final Context context, final String name, final AMap aMap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = CommonUtil.getFromAssets(context, "json/"+name+".json");
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONArray array = new JSONArray(result);
                        LatLngBounds.Builder builder = LatLngBounds.builder();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject itemObj = array.getJSONObject(i);
                            String code = itemObj.getString("code");
                            String name = itemObj.getString("name");
                            if (!itemObj.isNull("data")) {
                                JSONArray dataArray = itemObj.getJSONArray("data");
                                List<LatLng> latLngs = new ArrayList<>();
                                double minLat = 0,minLng = 0,maxLat = 0,maxLng = 0;
                                for (int j = 0; j < dataArray.length(); j++) {
                                    JSONObject dataObj = dataArray.getJSONObject(j);
                                    double lat = dataObj.getDouble("lat");
                                    double lng = dataObj.getDouble("lng");
                                    latLngs.add(new LatLng(lat, lng));
                                    builder.include(new LatLng(lat, lng));

                                    if (j == 0) {
                                        minLat = lat;
                                        minLng = lng;
                                        maxLat = lat;
                                        maxLng = lng;
                                    }
                                    if (maxLat <= lat) {
                                        maxLat = lat;
                                    }
                                    if (maxLng <= lng) {
                                        maxLng = lng;
                                    }
                                    if (minLat >= lat) {
                                        minLat = lat;
                                    }
                                    if (minLng >= lng) {
                                        minLng = lng;
                                    }
                                }
                                PolygonOptions polygonOptions = new PolygonOptions();
                                polygonOptions.addAll(latLngs);
                                polygonOptions.strokeWidth(2);
                                polygonOptions.strokeColor(context.getResources().getColor(R.color.colorPrimary));
                                polygonOptions.fillColor(context.getResources().getColor(R.color.refresh_color2));
                                Polygon polygon = aMap.addPolygon(polygonOptions);
                                polygon.setZIndex(-1000);

                                TextOptions textOptions = new TextOptions();
                                textOptions.text(name);
                                textOptions.position(new LatLng((minLat+maxLat)/2, (minLng+maxLng)/2));
                                textOptions.fontSize(30);
                                textOptions.fontColor(Color.RED);
                                textOptions.backgroundColor(Color.TRANSPARENT);
                                Text text = aMap.addText(textOptions);
                                text.setZIndex(-1000);

                            }
                        }
                        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * SDCard写入文件，注意读写权限
     * @param file
     * @param content
     */
    public static void writeExternalFile(File file, String content) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write(content.getBytes());
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文件内容
     * @param context
     * @param fileName
     * @return
     */
    public static String readFile(Context context, String fileName) {
        try {
            FileInputStream inputStream = context.openFileInput(fileName+".txt");
            byte[] bytes = new byte[1024];
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            while (inputStream.read(bytes) != -1) {
                arrayOutputStream.write(bytes, 0, bytes.length);
            }
            inputStream.close();
            arrayOutputStream.close();
            String result = new String(arrayOutputStream.toByteArray());
            return result;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据当前时间获取日期
     * @param i (+1为后一天，-1为前一天，0表示当天)
     * @return
     */
    public static String getDate(String time, int i) {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmm", Locale.CHINA);
        try {
            Date date = sdf2.parse(time);
            c.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        c.add(Calendar.DAY_OF_MONTH, i);
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        String date = sdf1.format(c.getTime());
        return date;
    }

    /**
     * 根据当前时间获取星期几
     * @param i (+1为后一天，-1为前一天，0表示当天)
     * @return
     */
    public static String getWeek(int i) {
        String week = "";

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_WEEK, i);

        switch (c.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY:
                week = "周日";
                break;
            case Calendar.MONDAY:
                week = "周一";
                break;
            case Calendar.TUESDAY:
                week = "周二";
                break;
            case Calendar.WEDNESDAY:
                week = "周三";
                break;
            case Calendar.THURSDAY:
                week = "周四";
                break;
            case Calendar.FRIDAY:
                week = "周五";
                break;
            case Calendar.SATURDAY:
                week = "周六";
                break;
        }

        return week;
    }

    /**
     * 1小时降水颜色
     * @param rain
     * @return
     */
    public static int factRain1Color(double rain) {
        int color;
        if (rain <= 2) {
            color = 0xffa5f38d;
        }else if (rain <= 4) {
            color = 0xff35af0e;
        }else if (rain <= 6) {
            color = 0xff63b9ff;
        }else if (rain <= 8) {
            color = 0xff0101f9;
        }else if (rain <= 10) {
            color = 0xff0c6b4b;
        }else if (rain <= 20) {
            color = 0xfff008fa;
        }else if (rain <= 50) {
            color = 0xfff14800;
        }else {
            color = 0xff770000;
        }
        return color;
    }

    /**
     * 3小时降水颜色
     * @param rain
     * @return
     */
    public static int factRain3Color(double rain) {
        int color;
        if (rain <= 3) {
            color = 0xffa5f38d;
        }else if (rain <= 10) {
            color = 0xff35af0e;
        }else if (rain <= 20) {
            color = 0xff63b9ff;
        }else if (rain <= 50) {
            color = 0xff0101f9;
        }else if (rain <= 70) {
            color = 0xfffb02fb;
        }else {
            color = 0xff770000;
        }
        return color;
    }

    /**
     * 6小时降水颜色
     * @param rain
     * @return
     */
    public static int factRain6Color(double rain) {
        int color;
        if (rain <= 4) {
            color = 0xffa5f38d;
        }else if (rain <= 13) {
            color = 0xff64bb4c;
        }else if (rain <= 25) {
            color = 0xff63b9ff;
        }else if (rain <= 60) {
            color = 0xff5068d5;
        }else {
            color = 0xffc100cb;
        }
        return color;
    }

    /**
     * 12小时降水颜色
     * @param rain
     * @return
     */
    public static int factRain12Color(double rain) {
        int color;
        if (rain <= 10) {
            color = 0xffa5f38d;
        }else if (rain <= 25) {
            color = 0xff35af0e;
        }else if (rain <= 50) {
            color = 0xff63b9ff;
        }else if (rain <= 100) {
            color = 0xff0101f9;
        }else if (rain <= 250) {
            color = 0xfffb02fb;
        }else {
            color = 0xff6e0604;
        }
        return color;
    }

    /**
     * 24小时降水颜色
     * @param rain
     * @return
     */
    public static int factRain24Color(double rain) {
        int color;
        if (rain <= 10) {
            color = 0xffa5f38d;
        }else if (rain <= 25) {
            color = 0xff35af0e;
        }else if (rain <= 50) {
            color = 0xff63b9ff;
        }else if (rain <= 100) {
            color = 0xff0101f9;
        }else if (rain <= 250) {
            color = 0xfffb02fb;
        }else {
            color = 0xff6e0604;
        }
        return color;
    }

    /**
     * 1小时温度颜色
     * @param temp
     * @return
     */
    public static int factTemp1Color(double temp) {
        int color;
        if (temp <= -8) {
            color = 0xffF2C3F9;
        }else if (temp <= -6) {
            color = 0xffE7A3F0;
        }else if (temp <= -4) {
            color = 0xffD696E4;
        }else if (temp <= -2) {
            color = 0xffC787DD;
        }else if (temp <= 0) {
            color = 0xffB775CF;
        }else if (temp <= 2) {
            color = 0xffA361CB;
        }else if (temp <= 4) {
            color = 0xff8550C4;
        }else if (temp <= 6) {
            color = 0xff6E45AD;
        }else if (temp <= 8) {
            color = 0xff565EA7;
        }else if (temp <= 10) {
            color = 0xff3C7DC1;
        }else if (temp <= 12) {
            color = 0xff35B2E0;
        }else if (temp <= 14) {
            color = 0xff33C5F4;
        }else if (temp <= 16) {
            color = 0xff2DD9C3;
        }else if (temp <= 18) {
            color = 0xff2FD073;
        }else if (temp <= 20) {
            color = 0xff3FBF44;
        }else if (temp <= 22) {
            color = 0xff67CB37;
        }else if (temp <= 24) {
            color = 0xffBBD83E;
        }else if (temp <= 26) {
            color = 0xffE6E638;
        }else if (temp <= 28) {
            color = 0xffECEB2F;
        }else if (temp <= 30) {
            color = 0xffEACA39;
        }else if (temp <= 32) {
            color = 0xffE9983C;
        }else if (temp <= 34) {
            color = 0xffE66136;
        }else if (temp <= 36) {
            color = 0xffDB583C;
        }else if (temp <= 38) {
            color = 0xffC44C3B;
        }else if (temp <= 40) {
            color = 0xffBA3F37;
        }else {
            color = 0xffAB3737;
        }
        return color;
    }

    /**
     * 1小时风速颜色
     * @param wind
     * @return
     */
    public static int factWind1Color(double wind) {
        int color;
        if (wind <= 0.3) {
            color = 0xff98B3BA;
        }else if (wind <= 5.5) {
            color = 0xff73E0DB;
        }else if (wind <= 10.8) {
            color = 0xff61A6DD;
        }else if (wind <= 17.2) {
            color = 0xff3086D3;
        }else if (wind <= 24.5) {
            color = 0xff0063CF;
        }else if (wind <= 32.7) {
            color = 0xff00339D;
        }else if (wind <= 41.5) {
            color = 0xffFBFF01;
        }else if (wind <= 51.0) {
            color = 0xffFF9800;
        }else if (wind <= 61.3) {
            color = 0xffD02ED2;
        }else {
            color = 0xffF80400;
        }
        return color;
    }

    /**
     * 通过旋转角度获取风向字符串
     * @param fx
     * @return
     */
    public static String getWindDirection(float fx) {
        String wind_dir;
        if(fx >= 22.5 && fx < 67.5){
            wind_dir = "东北";
        }else if(fx >= 67.5 && fx < 112.5){
            wind_dir = "东";
        }else if(fx >= 112.5 && fx < 157.5){
            wind_dir = "东南";
        }else if(fx >= 157.5 && fx < 202.5){
            wind_dir = "南";
        }else if(fx >= 202.5 && fx < 247.5){
            wind_dir = "西南";
        }else if(fx >= 247.5 && fx < 292.5){
            wind_dir = "西";
        }else if(fx >= 292.5 && fx < 337.5){
            wind_dir = "西北";
        }else {
            wind_dir = "北";
        }
        return wind_dir;
    }

    /**
     * 获取所有本地图片文件信息
     * @return
     */
    public static List<DisasterDto> getAllLocalImages(Context context) {
        List<DisasterDto> list = new ArrayList<>();
        if (context != null) {
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null,
                    null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE));
                    String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                    String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE));
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                    long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));

                    DisasterDto dto = new DisasterDto();
                    dto.imageName = title;
                    dto.imgUrl = path;
                    list.add(0, dto);
                }
                cursor.close();
            }
        }

        return list;
    }

    /**
     * 格式化问价大小
     * @param size
     * @return
     */
    public static String getFormatSize(long size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return "0K";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()+ "TB";
    }

}
