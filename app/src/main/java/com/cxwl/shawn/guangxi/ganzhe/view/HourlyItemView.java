package com.cxwl.shawn.guangxi.ganzhe.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.cxwl.shawn.guangxi.ganzhe.R;
import com.cxwl.shawn.guangxi.ganzhe.ShawnMainActivity;
import com.cxwl.shawn.guangxi.ganzhe.dto.WeatherDto;
import com.cxwl.shawn.guangxi.ganzhe.util.CommonUtil;
import com.cxwl.shawn.guangxi.ganzhe.util.WeatherUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 逐小时item
 * @author shawn_sun
 *
 */
public class HourlyItemView extends View{
	
	private Context mContext;
	private Paint lineP,textP;//画线画笔
	private WeatherDto dto;
	private int min = 0;
	private SimpleDateFormat sdf0 = new SimpleDateFormat("yyyyMMddHHmm", Locale.CHINA);
	private SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm", Locale.CHINA);
	private SimpleDateFormat sdf2 = new SimpleDateFormat("HH", Locale.CHINA);
	private ShawnMainActivity activity;
	
	public HourlyItemView(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	public HourlyItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	
	public HourlyItemView(Context context, AttributeSet attrs, int defStyleAttr) {
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
		textP.setTextSize(CommonUtil.dip2px(mContext, 12));
		textP.setColor(getResources().getColor(R.color.text_color4));
	}
	
	/**
	 * 对cubicView进行赋值
	 */
	public void setData(WeatherDto dto, int min, ShawnMainActivity activity) {
		this.dto = dto;
		this.min = min;
		this.activity = activity;
	}

	@SuppressLint("HandlerLeak")
	public Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if (msg.what == activity.MGS_VALUE) {
				dto = (WeatherDto) msg.obj;
				min = msg.arg1;
				postInvalidate();
			}
		}
	};
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		//绘制曲线上每个点的白点
		lineP.setColor(0xff1771B7);
		lineP.setStyle(Paint.Style.STROKE);
		lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 8));
		canvas.drawPoint(dto.x, dto.y, lineP);
		
		//绘制曲线上每个点的数据值
		String value = (dto.hourlyTemp+min)+"°"+" "+getResources().getString(WeatherUtil.getWindDirection(dto.hourlyDirCode))+dto.hourlyForce+"级";
		float tempWidth = textP.measureText(value);
		canvas.drawText(value, dto.x+(int)(CommonUtil.dip2px(mContext, 5)), dto.y-(int)(CommonUtil.dip2px(mContext, 25)), textP);
		
		try {
			long current = sdf2.parse(sdf2.format(sdf0.parse(dto.hourlyTime))).getTime();
			Bitmap lb;
			if (current >= 5 && current < 18) {
				lb = WeatherUtil.getDayBitmap(mContext, dto.hourlyCode);
			}else {
				lb = WeatherUtil.getNightBitmap(mContext, dto.hourlyCode);
			}
			Bitmap newLbit = ThumbnailUtils.extractThumbnail(lb, (int)(CommonUtil.dip2px(mContext, 18)), (int)(CommonUtil.dip2px(mContext, 18)));
			canvas.drawBitmap(newLbit, dto.x-newLbit.getWidth(), dto.y-(int)(CommonUtil.dip2px(mContext, 40)), textP);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		//绘制时间
		try {
			String time = sdf1.format(sdf0.parse(dto.hourlyTime));
			float timeWidth = textP.measureText(time);
			canvas.drawText(time, dto.x-timeWidth/2, dto.y-(int)(CommonUtil.dip2px(mContext, 10)), textP);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
