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
 * 蔗田监测-田间实况-相关数据查询
 */
public class ShawnFactDataAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<FactDto> mArrayList;

	private final class ViewHolder{
		TextView tvStationName,tvTemp30,tvTemp50,tvHumidity20,tvHumidity40;
	}

	public ShawnFactDataAdapter(Context context, List<FactDto> mArrayList) {
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
			convertView = mInflater.inflate(R.layout.shawn_adapter_fact_data, null);
			mHolder = new ViewHolder();
			mHolder.tvStationName = convertView.findViewById(R.id.tvStationName);
			mHolder.tvTemp30 = convertView.findViewById(R.id.tvTemp30);
			mHolder.tvTemp50 = convertView.findViewById(R.id.tvTemp50);
			mHolder.tvHumidity20 = convertView.findViewById(R.id.tvHumidity20);
			mHolder.tvHumidity40 = convertView.findViewById(R.id.tvHumidity40);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		FactDto dto = mArrayList.get(position);

		if (!TextUtils.isEmpty(dto.stationName)) {
			mHolder.tvStationName.setText(dto.stationName);
		}

		if (!TextUtils.isEmpty(dto.grotemp30)) {
			mHolder.tvTemp30.setText(dto.grotemp30);
		}

		if (!TextUtils.isEmpty(dto.grotemp50)) {
			mHolder.tvTemp50.setText(dto.grotemp50);
		}

		if (!TextUtils.isEmpty(dto.grohumidity20)) {
			mHolder.tvHumidity20.setText(dto.grohumidity20);
		}

		if (!TextUtils.isEmpty(dto.grohumidity40)) {
			mHolder.tvHumidity40.setText(dto.grohumidity40);
		}

		return convertView;
	}

}
