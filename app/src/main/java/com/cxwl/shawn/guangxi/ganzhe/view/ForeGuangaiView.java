package com.cxwl.shawn.guangxi.ganzhe.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.media.ThumbnailUtils;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.cxwl.shawn.guangxi.ganzhe.R;
import com.cxwl.shawn.guangxi.ganzhe.dto.FactDto;
import com.cxwl.shawn.guangxi.ganzhe.util.CommonUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 预报灌溉量
 */
public class ForeGuangaiView extends View {

	private Context mContext;
	private List<FactDto> tempList = new ArrayList<>();
	private float maxValue = 0, minValue = 0;
	private Paint lineP,textP;//画线画笔
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
	private SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd", Locale.CHINA);
	private Bitmap bitmap;

	public ForeGuangaiView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public ForeGuangaiView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public ForeGuangaiView(Context context, AttributeSet attrs, int defStyleAttr) {
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
		
		bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(getResources(), R.drawable.shawn_icon_marker_rain),
				(int)(CommonUtil.dip2px(mContext, 25)), (int)(CommonUtil.dip2px(mContext, 25)));
	}
	
	/**
	 * 对cubicView进行赋值
	 */
	public void setData(List<FactDto> dataList) {
		if (!dataList.isEmpty()) {
			tempList.clear();
			tempList.addAll(dataList);
			if (tempList.isEmpty()) {
				return;
			}

			maxValue = tempList.get(0).foreGuangai;
			for (int i = 0; i < tempList.size(); i++) {
				FactDto dto = tempList.get(i);
				if (maxValue <= dto.foreGuangai) {
					maxValue = dto.foreGuangai;
				}
			}

			if (maxValue == 0 && minValue == 0) {
				maxValue = 100;
			}else {
				int itemDivider = 20;
				maxValue = (float) (Math.ceil(maxValue)+itemDivider*2);
			}
			minValue = 0;
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
		float chartW = w-CommonUtil.dip2px(mContext, 50);
		float chartH = h-CommonUtil.dip2px(mContext, 45);
		float leftMargin = CommonUtil.dip2px(mContext, 30);
		float rightMargin = CommonUtil.dip2px(mContext, 20);
		float topMargin = CommonUtil.dip2px(mContext, 10);
		float bottomMargin = CommonUtil.dip2px(mContext, 35);
		
		int size = tempList.size();
		float columnWidth = chartW/size;
		//获取曲线上每个温度点的坐标
		for (int i = 0; i < size; i++) {
			FactDto dto = tempList.get(i);
			dto.x = columnWidth*i + columnWidth/2 + leftMargin;
			
			float value = dto.foreGuangai;
			dto.y = chartH-chartH*Math.abs(value)/(Math.abs(maxValue)+Math.abs(minValue)) + topMargin;
			tempList.set(i, dto);
		}

		//绘制区域
		lineP.setStyle(Style.FILL);
		for (int i = 0; i < size-1; i++) {
			FactDto dto = tempList.get(i);
			Path rectPath = new Path();
			rectPath.moveTo(dto.x-columnWidth/2, topMargin);
			rectPath.lineTo(dto.x+columnWidth/2, topMargin);
			rectPath.lineTo(dto.x+columnWidth/2, h-bottomMargin);
			rectPath.lineTo(dto.x-columnWidth/2, h-bottomMargin);
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
			FactDto dto = tempList.get(i);
			Path linePath = new Path();
			linePath.moveTo(dto.x-columnWidth/2, topMargin);
			linePath.lineTo(dto.x-columnWidth/2, h-bottomMargin);
			linePath.close();
			lineP.setColor(0xfff1f1f1);
			lineP.setStyle(Style.STROKE);
			canvas.drawPath(linePath, lineP);
		}

		//绘制横向分割线
		int itemDivider = 20;
		for (int i = (int) minValue; i <= maxValue; i+=itemDivider) {
			float dividerY;
			dividerY = chartH-chartH*Math.abs(i)/(Math.abs(maxValue)+Math.abs(minValue)) + topMargin;
			lineP.setColor(0xfff1f1f1);
			canvas.drawLine(leftMargin, dividerY, w-rightMargin, dividerY, lineP);
			textP.setColor(0xff999999);
			textP.setTextSize(CommonUtil.dip2px(mContext, 10));
			canvas.drawText(String.valueOf(i), CommonUtil.dip2px(mContext, 5), dividerY, textP);
		}

		//绘制柱形图
		for (int i = 0; i < size; i++) {
			FactDto dto = tempList.get(i);

			lineP.setColor(0xff77A8DA);
			lineP.setStyle(Style.FILL);
			lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 1));
			canvas.drawRect(dto.x-columnWidth/4, dto.y, dto.x+columnWidth/4, h-bottomMargin, lineP);
			
			//绘制曲线上每个点的数据值
			if (dto.foreGuangai != 0) {
				textP.setColor(0xff77A8DA);
				textP.setTextSize(CommonUtil.dip2px(mContext, 10));
				float tempWidth = textP.measureText(dto.foreGuangai+"");
				canvas.drawBitmap(bitmap, dto.x-bitmap.getWidth()/2, dto.y-bitmap.getHeight()-CommonUtil.dip2px(mContext, 2.5f), textP);
				canvas.drawText(dto.foreGuangai+"", dto.x-tempWidth/2, dto.y-bitmap.getHeight()/2, textP);
			}
		}

		//绘制时间
		textP.setColor(0xff999999);
		textP.setTextSize(CommonUtil.dip2px(mContext, 10));
		for (int i = 0; i < size; i+=2) {
			FactDto dto = tempList.get(i);

			//绘制24小时
			try {
				if (!TextUtils.isEmpty(dto.time)) {
					String time;
					if (dto.time.contains("T")) {
						time = sdf2.format(sdf1.parse(dto.time.replace("T", " ")));
					}else {
						time = sdf2.format(sdf1.parse(dto.time));
					}
					if (!TextUtils.isEmpty(time)) {
						float text = textP.measureText(time);
						canvas.drawText(time, dto.x-text/2, h-CommonUtil.dip2px(mContext, 20f), textP);
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
