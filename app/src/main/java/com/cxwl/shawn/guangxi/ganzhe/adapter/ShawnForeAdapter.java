package com.cxwl.shawn.guangxi.ganzhe.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.R;
import com.cxwl.shawn.guangxi.ganzhe.dto.FactDto;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * 蔗田监测-田间实况-相关数据查询
 */
public class ShawnForeAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<FactDto> mArrayList;
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
	private SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd", Locale.CHINA);

	private final class ViewHolder{
		TextView tvDate,tvSoilRH,tvRH,tvFc,tvFi,tvP,tvET0,
				tvKc,tvForecastDays,tvI,tvI2;
	}

	public ShawnForeAdapter(Context context, List<FactDto> mArrayList) {
		this.mArrayList = mArrayList;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.shawn_adapter_fore, null);
			mHolder = new ViewHolder();
			mHolder.tvDate = convertView.findViewById(R.id.tvDate);
			mHolder.tvSoilRH = convertView.findViewById(R.id.tvSoilRH);
			mHolder.tvRH = convertView.findViewById(R.id.tvRH);
			mHolder.tvFc = convertView.findViewById(R.id.tvFc);
			mHolder.tvFi = convertView.findViewById(R.id.tvFi);
			mHolder.tvP = convertView.findViewById(R.id.tvP);
			mHolder.tvET0 = convertView.findViewById(R.id.tvET0);
			mHolder.tvKc = convertView.findViewById(R.id.tvKc);
			mHolder.tvForecastDays = convertView.findViewById(R.id.tvForecastDays);
			mHolder.tvI = convertView.findViewById(R.id.tvI);
			mHolder.tvI2 = convertView.findViewById(R.id.tvI2);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		FactDto dto = mArrayList.get(position);

		if (!TextUtils.isEmpty(dto.time)) {
			try {
				mHolder.tvDate.setText(sdf2.format(sdf1.parse(dto.time)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if (!TextUtils.isEmpty(dto.SoilRH)) {
			mHolder.tvSoilRH.setText(dto.SoilRH);
		}
		if (!TextUtils.isEmpty(dto.RH)) {
			mHolder.tvRH.setText(dto.RH);
		}
		if (!TextUtils.isEmpty(dto.Fc)) {
			mHolder.tvFc.setText(dto.Fc);
		}
		if (!TextUtils.isEmpty(dto.Fi)) {
			mHolder.tvFi.setText(dto.Fi);
		}
		if (!TextUtils.isEmpty(dto.P)) {
			mHolder.tvP.setText(dto.P);
		}
		if (!TextUtils.isEmpty(dto.ET0)) {
			mHolder.tvET0.setText(dto.ET0);
		}
		if (!TextUtils.isEmpty(dto.Kc)) {
			mHolder.tvKc.setText(dto.Kc);
		}
		if (!TextUtils.isEmpty(dto.ForecastDays)) {
			mHolder.tvForecastDays.setText(dto.ForecastDays);
		}
		if (!TextUtils.isEmpty(dto.I)) {
			mHolder.tvI.setText(dto.I);
			if (TextUtils.equals(dto.I, "--")) {
				mHolder.tvI2.setText("--");
			} else {
				float value = Float.valueOf(dto.I)*0.667f;
				float result = new BigDecimal(value).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
				mHolder.tvI2.setText(result+"");
			}
		}

		return convertView;
	}

}
