package com.cxwl.shawn.guangxi.ganzhe.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.cxwl.shawn.guangxi.ganzhe.dto.FactDto;
import com.cxwl.shawn.guangxi.ganzhe.util.CommonUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 蔗田监测-田间实况--站点查询-地面温度
 */
public class FactStationLandView extends View {

	private Context mContext;
	private List<FactDto> currentStations = new ArrayList<>();
	private List<FactDto> lastStations = new ArrayList<>();
	private float maxValue = 0, minValue = 0;
	private Paint lineP,textP;//画线画笔
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0", Locale.CHINA);
	private SimpleDateFormat sdf2 = new SimpleDateFormat("MM月dd日", Locale.CHINA);
    private SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
	private int totalDivider = 0;
	private int itemDivider = 1;

	public FactStationLandView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public FactStationLandView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public FactStationLandView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		init();
	}

	private void init() {
		lineP = new Paint();
		lineP.setStyle(Style.STROKE);
		lineP.setStrokeCap(Paint.Cap.ROUND);
		lineP.setAntiAlias(true);
		
		textP = new Paint();
		textP.setAntiAlias(true);
	}
	
	/**
	 * 对cubicView进行赋值
	 */
	public void setData(List<FactDto> lastStations, List<FactDto> currentStations) {
		if (!currentStations.isEmpty()) {
			this.lastStations.clear();
			this.lastStations.addAll(lastStations);
			this.currentStations.clear();
			this.currentStations.addAll(currentStations);
			if (this.currentStations.isEmpty()) {
				return;
			}

			float max = 0,min = 0;
			for (int i = 0; i < currentStations.size(); i++) {
				FactDto dto = currentStations.get(i);
				if (!TextUtils.isEmpty(dto.LAND_PT_TEMP) && !TextUtils.equals(dto.LAND_PT_TEMP, "--")) {
					max = Float.valueOf(dto.LAND_PT_TEMP);
					min = Float.valueOf(dto.LAND_PT_TEMP);
				}
			}

			maxValue = max;
			minValue = min;
			for (int i = 0; i < currentStations.size(); i++) {
				FactDto dto = currentStations.get(i);
				if (!TextUtils.isEmpty(dto.LAND_PT_TEMP) && !TextUtils.equals(dto.LAND_PT_TEMP, "--")) {
					if (maxValue <= Float.valueOf(dto.LAND_PT_TEMP)) {
						maxValue = Float.valueOf(dto.LAND_PT_TEMP);
					}
					if (minValue >= Float.valueOf(dto.LAND_PT_TEMP)) {
						minValue = Float.valueOf(dto.LAND_PT_TEMP);
					}
				}
			}

			if (maxValue == 0 && minValue == 0) {
				maxValue = 5;
				minValue = 0;
				totalDivider = (int) (maxValue-minValue);
			}else {
				totalDivider = (int) (maxValue-minValue);
				if (totalDivider <= 5) {
					itemDivider = 4;
				}else if (totalDivider <= 10) {
					itemDivider = 5;
				}else if (totalDivider <= 15) {
					itemDivider = 6;
				}else if (totalDivider <= 20) {
					itemDivider = 7;
				}else {
					itemDivider = 8;
				}

				maxValue = maxValue+itemDivider*2;
				minValue = minValue-itemDivider*2;
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (currentStations.isEmpty()) {
			return;
		}

		canvas.drawColor(Color.TRANSPARENT);
		float w = canvas.getWidth();
		float h = canvas.getHeight();
		float chartW = w-CommonUtil.dip2px(mContext, 50);
		float chartH = h-CommonUtil.dip2px(mContext, 45);
		float leftMargin = CommonUtil.dip2px(mContext, 30);
		float rightMargin = CommonUtil.dip2px(mContext, 20);
		float topMargin = CommonUtil.dip2px(mContext, 10);
		float bottomMargin = CommonUtil.dip2px(mContext, 35);

		int size = currentStations.size();
		float columnWidth = chartW/size;
		//获取曲线上每个温度点的坐标
		for (int i = 0; i < size; i++) {
			FactDto dto = currentStations.get(i);
			dto.x = columnWidth*i + leftMargin;

			float value = minValue;
			if (!TextUtils.isEmpty(dto.LAND_PT_TEMP) && !TextUtils.equals(dto.LAND_PT_TEMP, "--")) {
				value = Float.valueOf(dto.LAND_PT_TEMP);
			}
			dto.y = chartH-chartH*Math.abs(value)/(Math.abs(maxValue)+Math.abs(minValue)) + topMargin;
			currentStations.set(i, dto);
		}

		for (int i = 0; i < lastStations.size(); i++) {
			FactDto dto = lastStations.get(i);
			dto.x = columnWidth*i + leftMargin;

			float value = minValue;
			if (!TextUtils.isEmpty(dto.LAND_PT_TEMPL) && !TextUtils.equals(dto.LAND_PT_TEMPL, "--")) {
				value = Float.valueOf(dto.LAND_PT_TEMPL);
			}
			dto.y = chartH-chartH*Math.abs(value)/(Math.abs(maxValue)+Math.abs(minValue)) + topMargin;
			lastStations.set(i, dto);
		}


		lineP.setStyle(Style.FILL);
		for (int i = 0; i < size-1; i++) {
			FactDto dto = currentStations.get(i);
			FactDto dto2 = currentStations.get(i+1);
			//绘制区域
			Path rectPath = new Path();
			rectPath.moveTo(dto.x, topMargin);
			rectPath.lineTo(dto2.x, topMargin);
			rectPath.lineTo(dto2.x, h-bottomMargin);
			rectPath.lineTo(dto.x, h-bottomMargin);
			rectPath.close();
			if (i%8 == 0 || i%8 == 1 || i%8 == 2 || i%8 == 3) {
				lineP.setColor(Color.WHITE);
			}else {
				lineP.setColor(0xfff9f9f9);
			}
			canvas.drawPath(rectPath, lineP);
		}

		//绘制纵向分割线
		for (int i = 0; i < size; i++) {
			FactDto dto = currentStations.get(i);
			Path linePath = new Path();
			linePath.moveTo(dto.x, topMargin);
			linePath.lineTo(dto.x, h-bottomMargin);
			linePath.close();
			lineP.setColor(0xfff1f1f1);
			lineP.setStyle(Style.STROKE);
			canvas.drawPath(linePath, lineP);
		}

		//绘制横向分割线
		for (int i = (int) minValue; i <= maxValue; i+=itemDivider) {
			float dividerY;
			dividerY = chartH-chartH*Math.abs(i)/(Math.abs(maxValue)+Math.abs(minValue)) + topMargin;
			lineP.setColor(0xfff1f1f1);
			canvas.drawLine(leftMargin, dividerY, w-rightMargin, dividerY, lineP);
			textP.setColor(0xff999999);
			textP.setTextSize(CommonUtil.dip2px(mContext, 10));
			canvas.drawText(String.valueOf(i), CommonUtil.dip2px(mContext, 5), dividerY, textP);
		}

		//绘制当前年
		lineP.setColor(0xff77A8DA);
		lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 1.5f));
		lineP.setAntiAlias(true);
		for (int i = 0; i < size-1; i++) {
			FactDto dto = currentStations.get(i);
			FactDto dto2 = currentStations.get(i+1);

			float x1 = dto.x;
			float y1 = dto.y;
			float x2 = dto2.x;
			float y2 = dto2.y;

			float wt = (x1 + x2) / 2;

			float x3 = wt;
			float y3 = y1;
			float x4 = wt;
			float y4 = y2;

			Path linePath = new Path();
			linePath.moveTo(x1, y1);
			linePath.cubicTo(x3, y3, x4, y4, x2, y2);
			canvas.drawPath(linePath, lineP);
		}

		textP.setColor(0xff77A8DA);
		textP.setTextSize(CommonUtil.dip2px(mContext, 10));
		for (int i = 0; i < size; i++) {
			FactDto dto = currentStations.get(i);

			//绘制曲线上每个点的数据值
			if (!TextUtils.isEmpty(dto.LAND_PT_TEMP) && !TextUtils.equals(dto.LAND_PT_TEMP, "--")) {
				float value = Float.valueOf(dto.LAND_PT_TEMP);
				if (value != 0) {
					float tempWidth = textP.measureText(dto.LAND_PT_TEMP+"");
					canvas.drawText(dto.LAND_PT_TEMP+"", dto.x-tempWidth/2, dto.y-(int)CommonUtil.dip2px(mContext, 5), textP);
				}
			}
		}

		//绘制上一年
		lineP.setColor(0xffadadad);
		lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 1.5f));
		lineP.setAntiAlias(true);
		for (int i = 0; i < lastStations.size()-1; i++) {
			FactDto dto = lastStations.get(i);
			FactDto dto2 = lastStations.get(i+1);

			float x1 = dto.x;
			float y1 = dto.y;
			float x2 = dto2.x;
			float y2 = dto2.y;

			float wt = (x1 + x2) / 2;

			float x3 = wt;
			float y3 = y1;
			float x4 = wt;
			float y4 = y2;

			Path linePath = new Path();
			linePath.moveTo(x1, y1);
			linePath.cubicTo(x3, y3, x4, y4, x2, y2);
			canvas.drawPath(linePath, lineP);
		}

		textP.setColor(0xffadadad);
		textP.setTextSize(CommonUtil.dip2px(mContext, 10));
		for (int i = 0; i < size; i++) {
			FactDto dto = lastStations.get(i);

			//绘制曲线上每个点的数据值
			if (!TextUtils.isEmpty(dto.LAND_PT_TEMPL) && !TextUtils.equals(dto.LAND_PT_TEMPL, "--")) {
				float value = Float.valueOf(dto.LAND_PT_TEMPL);
				if (value != 0) {
					float tempWidth = textP.measureText(dto.LAND_PT_TEMPL+"");
					canvas.drawText(dto.LAND_PT_TEMPL+"", dto.x-tempWidth/2, dto.y+(int)CommonUtil.dip2px(mContext, 10), textP);
				}
			}
		}

		//绘制时间
		textP.setColor(0xff999999);
		textP.setTextSize(CommonUtil.dip2px(mContext, 10));
		for (int i = 0; i < size; i+=2) {
			FactDto dto = currentStations.get(i);

			//绘制24小时
			try {
				if (!TextUtils.isEmpty(dto.time)) {
					String time,time2 = "";
					if (dto.time.contains("T")) {
					    if (i == 0) {
                            time2 = sdf3.format(sdf1.parse(dto.time.replace("T", " ")));
                        }
						time = sdf2.format(sdf1.parse(dto.time.replace("T", " ")));
					}else {
                        if (i == 0) {
                            time2 = sdf3.format(sdf1.parse(dto.time));
                        }
						time = sdf2.format(sdf1.parse(dto.time));
					}
					if (!TextUtils.isEmpty(time)) {
						float text = textP.measureText(time);
						canvas.drawText(time, dto.x-text/2, h-CommonUtil.dip2px(mContext, 20f), textP);
						if (i == 0) {
                            canvas.drawText(time2, dto.x-text/2, h-CommonUtil.dip2px(mContext, 5f), textP);
                        }
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		lineP.reset();
		textP.reset();
		
	}
	
}
