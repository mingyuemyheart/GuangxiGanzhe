package com.cxwl.shawn.guangxi.ganzhe.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.R;
import com.cxwl.shawn.guangxi.ganzhe.dto.RadarDto;

import java.util.List;

/**
 * 天气雷达
 */
public class ShawnRadarHeaderAdapter extends BaseAdapter {

	private Context mContext;
	private List<RadarDto> mArrayList;
	private LayoutInflater mInflater;

	public ShawnRadarHeaderAdapter(Context context, List<RadarDto> mArrayList) {
		this.mContext = context;
		this.mArrayList = mArrayList;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	private class ContentViewHolder {
		TextView tvName;
	}

	@Override
	public int getCount() {
		return mArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return mArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ContentViewHolder mHolder;
		if (convertView == null) {
			mHolder = new ContentViewHolder();
			convertView = mInflater.inflate(R.layout.shawn_adapter_radar_header, null);
			mHolder.tvName = convertView.findViewById(R.id.tvName);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ContentViewHolder) convertView.getTag();
		}

		RadarDto dto = mArrayList.get(position);
		if (dto.isSelected) {
			mHolder.tvName.setBackgroundResource(R.drawable.shawn_bg_corner_item_press);
			mHolder.tvName.setTextColor(Color.WHITE);
		}else {
			mHolder.tvName.setBackgroundResource(R.drawable.shawn_bg_corner_item_transparent);
			mHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
		}
		mHolder.tvName.setText(dto.sectionName);

		return convertView;
	}

}
