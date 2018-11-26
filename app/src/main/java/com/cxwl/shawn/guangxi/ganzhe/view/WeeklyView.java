package com.cxwl.shawn.guangxi.ganzhe.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.cxwl.shawn.guangxi.ganzhe.R;
import com.cxwl.shawn.guangxi.ganzhe.dto.WeatherDto;
import com.cxwl.shawn.guangxi.ganzhe.util.CommonUtil;
import com.cxwl.shawn.guangxi.ganzhe.util.WeatherUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 一周预报曲线图
 */
public class WeeklyView extends View{
	
	private Context mContext;
	private List<WeatherDto> tempList = new ArrayList<>();
	private int maxTemp = 0;//最高温度
	private int minTemp = 0;//最低温度
	private Paint lineP = null;//画线画笔
	private Paint textP = null;//写字画笔
	private Paint roundP = null;//aqi背景颜色画笔
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
	private SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd", Locale.CHINA);
	private int index = 0;
	private int totalDivider = 0;
	private int itemDivider = 1;
	
	public WeeklyView(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	public WeeklyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	
	public WeeklyView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		init();
	}
	
	private void init() {
		lineP = new Paint();
		lineP.setStyle(Paint.Style.STROKE);
		lineP.setStrokeCap(Paint.Cap.ROUND);
		lineP.setAntiAlias(true);
		
		textP = new Paint();
		textP.setAntiAlias(true);
		
		roundP = new Paint();
		roundP.setStyle(Paint.Style.FILL);
		roundP.setStrokeCap(Paint.Cap.ROUND);
		roundP.setAntiAlias(true);
	}
	
	/**
	 * 对polyView进行赋值
	 */
	public void setData(List<WeatherDto> dataList) {
		if (!dataList.isEmpty()) {
            tempList.clear();
            tempList.addAll(dataList);

			maxTemp = tempList.get(0).highTemp;
			minTemp = tempList.get(0).lowTemp;
			for (int i = 0; i < tempList.size(); i++) {
				if (maxTemp <= tempList.get(i).highTemp) {
					maxTemp = tempList.get(i).highTemp;
				}
				if (minTemp >= tempList.get(i).lowTemp) {
					minTemp = tempList.get(i).lowTemp;
				}
			}

			totalDivider = maxTemp-minTemp;
			if (totalDivider <= 5) {
				itemDivider = 1;
			}else if (totalDivider <= 15) {
				itemDivider = 2;
			}else if (totalDivider <= 25) {
				itemDivider = 3;
			}else if (totalDivider <= 40) {
				itemDivider = 4;
			}else {
				itemDivider = 5;
			}
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.TRANSPARENT);
		float w = getWidth();
		float h = getHeight();
		float chartW = w-CommonUtil.dip2px(mContext, 40);
		float chartH = h-CommonUtil.dip2px(mContext, 140);
		float leftMargin = CommonUtil.dip2px(mContext, 20);
		float rightMargin = CommonUtil.dip2px(mContext, 20);
		float topMargin = CommonUtil.dip2px(mContext, 130);

		int size = tempList.size();
		//获取曲线上每个温度点的坐标
		for (int i = 0; i < size; i++) {
			WeatherDto dto = tempList.get(i);

			//获取最高温度对应的坐标点信息
			dto.highX = (chartW/(size-1))*i + leftMargin;
			float highTemp = tempList.get(i).highTemp;
			dto.highY = chartH*Math.abs(maxTemp-highTemp)/totalDivider+topMargin;
			Log.e("highTemp", highTemp+"---"+dto.highY);

			//获取最低温度的对应的坐标点信息
			dto.lowX = (chartW/(size-1))*i + leftMargin;
			float lowTemp = tempList.get(i).lowTemp;
			dto.lowY = chartH*Math.abs(maxTemp-lowTemp)/totalDivider+topMargin;
			Log.e("lowTemp", lowTemp+"---"+dto.lowY);
			
			tempList.set(i, dto);
		}

		for (int i = 0; i < tempList.size(); i++) {
			WeatherDto dto = tempList.get(i);

			if (dto.highPhe.contains("雨")) {
				if (dto.highPhe.contains("中雨")) {
					roundP.setColor(0xff73b0df);
				}else if (dto.highPhe.contains("暴雨")) {
					roundP.setColor(0xff1971b9);
				}else {
					roundP.setColor(0xffe5f3ff);
				}
				RectF rectFBg = new RectF(dto.lowX-CommonUtil.dip2px(mContext, 20f), 0, dto.lowX+CommonUtil.dip2px(mContext, 20f), h-(int)CommonUtil.dip2px(mContext, 5));
				canvas.drawRoundRect(rectFBg, CommonUtil.dip2px(mContext, 3), CommonUtil.dip2px(mContext, 3), roundP);
			}else if (dto.lowPhe.contains("雨")) {
				if (dto.lowPhe.contains("中雨")) {
					roundP.setColor(0xff73b0df);
				}else if (dto.lowPhe.contains("暴雨")) {
					roundP.setColor(0xff1971b9);
				}else {
					roundP.setColor(0xffe5f3ff);
				}
				RectF rectFBg = new RectF(dto.lowX-CommonUtil.dip2px(mContext, 20f), 0, dto.lowX+CommonUtil.dip2px(mContext, 20f), h-(int)CommonUtil.dip2px(mContext, 5));
				canvas.drawRoundRect(rectFBg, CommonUtil.dip2px(mContext, 3), CommonUtil.dip2px(mContext, 3), roundP);
			}

			//绘制周几
			textP.setColor(getResources().getColor(R.color.text_color4));
			textP.setTextSize(getResources().getDimension(R.dimen.level_5));
			if (!TextUtils.isEmpty(dto.week)) {
				String week = dto.week;
                if (i == 0) {
                    week = "今天";
                }else if (i == 1) {
                    week = "明天";
                }
				float weekText = textP.measureText(week);
				canvas.drawText(week, dto.lowX-weekText/2, CommonUtil.dip2px(mContext, 15), textP);
			}

			//绘制日期
			if (!TextUtils.isEmpty(dto.date)) {
				try {
					String date = sdf2.format(sdf1.parse(dto.date));
					float dateText = textP.measureText(date);
					canvas.drawText(date, dto.lowX-dateText/2, CommonUtil.dip2px(mContext, 30), textP);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

			//绘制白天天气现象、图标
			float highPheText = textP.measureText(dto.highPhe);//天气现象字符串占像素宽度
			canvas.drawText(dto.highPhe, dto.highX-highPheText/2, CommonUtil.dip2px(mContext, 50), textP);
			Bitmap b = WeatherUtil.getDayBitmap(mContext, dto.highPheCode);
			Bitmap newBit = ThumbnailUtils.extractThumbnail(b, (int)(CommonUtil.dip2px(mContext, 18)), (int)(CommonUtil.dip2px(mContext, 18)));
			canvas.drawBitmap(newBit, dto.highX-newBit.getWidth()/2, CommonUtil.dip2px(mContext, 55), textP);

			//绘制晚上天气现象、图标
			Bitmap lb = WeatherUtil.getNightBitmap(mContext, dto.lowPheCode);
			Bitmap newLbit = ThumbnailUtils.extractThumbnail(lb, (int)(CommonUtil.dip2px(mContext, 18)), (int)(CommonUtil.dip2px(mContext, 18)));
			canvas.drawBitmap(newLbit, dto.lowX-newLbit.getWidth()/2, CommonUtil.dip2px(mContext, 85), textP);
			float lowPheText = textP.measureText(dto.lowPhe);//天气现象字符串占像素宽度
			canvas.drawText(dto.lowPhe, dto.lowX-lowPheText/2, CommonUtil.dip2px(mContext, 120), textP);

			//绘制预报温度
			float textWidth = textP.measureText(dto.highTemp+"/"+dto.lowTemp+"°");//高温字符串占像素宽度
			canvas.drawText(dto.highTemp+"/"+dto.lowTemp+"°", dto.highX-textWidth/2, CommonUtil.dip2px(mContext, 140), textP);

			//绘制风力风向
			String windDir = mContext.getString(WeatherUtil.getWindDirection(dto.windDir));
			String windForce = WeatherUtil.getDayWindForce(dto.windForce);
			float windDirWidth = textP.measureText(windDir);//低温字符串所占的像素宽度
			float windForceWidth = textP.measureText(windForce);//低温字符串所占的像素宽度
			canvas.drawText(windDir, dto.lowX-windDirWidth/2, CommonUtil.dip2px(mContext, 155), textP);
			canvas.drawText(windForce, dto.lowX-windForceWidth/2, CommonUtil.dip2px(mContext, 170), textP);


			//绘制aqi数值
			if (!TextUtils.isEmpty(dto.aqi)) {
				int aqi = Integer.valueOf(dto.aqi);
				if (aqi > 150) {
					textP.setColor(getResources().getColor(R.color.white));
				}else {
					textP.setColor(getResources().getColor(R.color.text_color3));
				}
				if (aqi <= 50) {
					roundP.setColor(0xff00FF01);
				} else if (aqi <= 100)  {
					roundP.setColor(0xff96EF01);
				} else if (aqi <= 150)  {
					roundP.setColor(0xffFFFF01);
				} else if (aqi <= 200)  {
					roundP.setColor(0xffFF6400);
				} else if (aqi <= 300)  {
					roundP.setColor(0xffFE0000);
				} else {
					roundP.setColor(0xff7E0123);
				}
				RectF rectF = new RectF(dto.lowX-CommonUtil.dip2px(mContext, 12.5f), CommonUtil.dip2px(mContext, 178), dto.lowX+CommonUtil.dip2px(mContext, 12.5f), CommonUtil.dip2px(mContext, 192));
				canvas.drawRoundRect(rectF, CommonUtil.dip2px(mContext, 5), CommonUtil.dip2px(mContext, 5), roundP);
				float tempWidth = textP.measureText(dto.aqi);
				canvas.drawText(dto.aqi, dto.lowX-tempWidth/2, CommonUtil.dip2px(mContext, 190), textP);
			}
		}
		
	}
	
}
