package com.cxwl.shawn.guangxi.ganzhe.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Shader;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.cxwl.shawn.guangxi.ganzhe.ShawnMainActivity;
import com.cxwl.shawn.guangxi.ganzhe.dto.WeatherDto;
import com.cxwl.shawn.guangxi.ganzhe.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 24小时实况温度曲线图
 */
public class HourlyView extends View{

	private Context mContext;
	private List<WeatherDto> tempList = new ArrayList<>();
	private int maxValue = 0;
	private int minValue = 0;
	private int min = 0;
	private Paint lineP,shaderPaint;//画线画笔
	private float width = 0;
	private ShawnMainActivity activity;

	public HourlyView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public HourlyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public HourlyView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		init();
	}

	private void init() {
		lineP = new Paint();
		lineP.setStyle(Style.STROKE);
		lineP.setStrokeCap(Paint.Cap.ROUND);
		lineP.setAntiAlias(true);
		lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 1.5f));

		shaderPaint = new Paint();
		shaderPaint.setStyle(Style.FILL);
		shaderPaint.setStrokeCap(Paint.Cap.ROUND);
		shaderPaint.setAntiAlias(true);
		shaderPaint.setStrokeWidth(CommonUtil.dip2px(mContext, 1));
	}

	/**
	 * 对cubicView进行赋值
	 */
	public void setData(List<WeatherDto> dataList, float width, ShawnMainActivity activity) {
		this.activity = activity;
		this.width = width;
		if (!dataList.isEmpty()) {
			min  = dataList.get(0).hourlyTemp;
			for (int i = 0; i < dataList.size(); i++) {
				if (min >= dataList.get(i).hourlyTemp) {
					min = dataList.get(i).hourlyTemp;
				}
			}
			for (int i = 0; i < dataList.size(); i++) {
				int hourlyTemp = dataList.get(i).hourlyTemp;
				dataList.get(i).hourlyTemp = (hourlyTemp - min);
			}
			tempList.addAll(dataList);

			maxValue = tempList.get(0).hourlyTemp;
			minValue = tempList.get(0).hourlyTemp;
			for (int i = 0; i < tempList.size(); i++) {
				if (maxValue <= tempList.get(i).hourlyTemp) {
					maxValue = tempList.get(i).hourlyTemp;
				}
				if (minValue >= tempList.get(i).hourlyTemp) {
					minValue = tempList.get(i).hourlyTemp;
				}
			}

			maxValue = maxValue+1;
			minValue = minValue-3;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (tempList.isEmpty()) {
			return;
		}

		canvas.drawColor(Color.TRANSPARENT);
		float w = canvas.getWidth();
		float h = canvas.getHeight();
		float chartW = w-CommonUtil.dip2px(mContext, width/2);
		float chartH = h-CommonUtil.dip2px(mContext, 35);
		float leftMargin = CommonUtil.dip2px(mContext, width/4);
		float rightMargin = CommonUtil.dip2px(mContext, width/4);
		float topMargin = CommonUtil.dip2px(mContext, 35);
		float bottomMargin = CommonUtil.dip2px(mContext, 0);
		float chartMaxH = chartH * maxValue / (Math.abs(maxValue)+Math.abs(minValue));//同时存在正负值时，正值高度

		int size = tempList.size();
		//获取曲线上每个温度点的坐标
		for (int i = 0; i < size; i++) {
			WeatherDto dto = tempList.get(i);
			dto.x = (chartW/(size-1))*i + leftMargin;

			float value = tempList.get(i).hourlyTemp;
			if (value >= 0) {
				dto.y = chartMaxH - chartH*Math.abs(value)/(Math.abs(maxValue)+Math.abs(minValue)) + topMargin;
				if (minValue >= 0) {
					dto.y = chartH - chartH*Math.abs(value)/(Math.abs(maxValue)+Math.abs(minValue)) + topMargin;
				}
			}else {
				dto.y = chartMaxH + chartH*Math.abs(value)/(Math.abs(maxValue)+Math.abs(minValue)) + topMargin;
				if (maxValue < 0) {
					dto.y = chartH*Math.abs(value)/(Math.abs(maxValue)+Math.abs(minValue)) + topMargin;
				}
			}
			tempList.set(i, dto);
		}

		Message msg = new Message();
		msg.what = activity.MGS_SCROLL;
		msg.arg1 = min;
		msg.obj = tempList;
		activity.handler.sendMessage(msg);

		//绘制区域
		//新建一个线性渐变，前两个参数是渐变开始的点坐标，第三四个参数是渐变结束的点的坐标。连接这2个点就拉出一条渐变线了，玩过PS的都懂。然后那个数组是渐变的颜色。下一个参数是渐变颜色的分布，如果为空，每个颜色就是均匀分布的。最后是模式，这里设置的是循环渐变
		Shader mShader = new LinearGradient(w/2,h,w/2,bottomMargin,new int[] {0xffD1F6E4,0xffD1F6E4},null,Shader.TileMode.REPEAT);
		shaderPaint.setShader(mShader);
		for (int i = 0; i < size-1; i++) {
			float x1 = tempList.get(i).x;
			float y1 = tempList.get(i).y;
			float x2 = tempList.get(i+1).x;
			float y2 = tempList.get(i+1).y;

			float wt = (x1 + x2) / 2;

			float x3 = wt;
			float y3 = y1;
			float x4 = wt;
			float y4 = y2;

			if (i != size-1) {
				Path rectPath = new Path();
				rectPath.moveTo(x1, y1);
				rectPath.cubicTo(x3, y3, x4, y4, x2, y2);
				rectPath.lineTo(x2, h-bottomMargin);
				rectPath.lineTo(x1, h-bottomMargin);
				rectPath.close();
				canvas.drawPath(rectPath, shaderPaint);

				Path linePath = new Path();
				linePath.moveTo(x1, y1);
				linePath.cubicTo(x3, y3, x4, y4, x2, y2);
				lineP.setColor(0xfff0ac01);
				canvas.drawPath(linePath, lineP);
			}
		}

	}

}
