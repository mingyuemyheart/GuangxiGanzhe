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

import java.util.List;

/**
 * 蔗田监测-田间实况-小气候查询
 */
public class ShawnFactClimateAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<FactDto> mArrayList;

	private final class ViewHolder{
		TextView tvStationName,tvTemp60,tvTemp150,tvTemp300,tvHumidity60,tvHumidity150,tvHumidity300;
	}

	public ShawnFactClimateAdapter(Context context, List<FactDto> mArrayList) {
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
			convertView = mInflater.inflate(R.layout.shawn_adapter_fact_climate, null);
			mHolder = new ViewHolder();
			mHolder.tvStationName = convertView.findViewById(R.id.tvStationName);
			mHolder.tvTemp60 = convertView.findViewById(R.id.tvTemp60);
			mHolder.tvTemp150 = convertView.findViewById(R.id.tvTemp150);
			mHolder.tvTemp300 = convertView.findViewById(R.id.tvTemp300);
			mHolder.tvHumidity60 = convertView.findViewById(R.id.tvHumidity60);
			mHolder.tvHumidity150 = convertView.findViewById(R.id.tvHumidity150);
			mHolder.tvHumidity300 = convertView.findViewById(R.id.tvHumidity300);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		FactDto dto = mArrayList.get(position);

		if (!TextUtils.isEmpty(dto.stationName)) {
			mHolder.tvStationName.setText(dto.stationName);
		}

		if (!TextUtils.isEmpty(dto.temp60)) {
			mHolder.tvTemp60.setText(dto.temp60);
		}

		if (!TextUtils.isEmpty(dto.temp150)) {
			mHolder.tvTemp150.setText(dto.temp150);
		}

		if (!TextUtils.isEmpty(dto.temp300)) {
			mHolder.tvTemp300.setText(dto.temp300);
		}

		if (!TextUtils.isEmpty(dto.humidity60)) {
			mHolder.tvHumidity60.setText(dto.humidity60);
		}

		if (!TextUtils.isEmpty(dto.humidity150)) {
			mHolder.tvHumidity150.setText(dto.humidity150);
		}

		if (!TextUtils.isEmpty(dto.humidity300)) {
			mHolder.tvHumidity300.setText(dto.humidity300);
		}

		return convertView;
	}

}
