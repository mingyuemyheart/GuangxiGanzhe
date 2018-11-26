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
import com.cxwl.shawn.guangxi.ganzhe.view.stickygridheaders.StickyGridHeadersSimpleAdapter;

import java.util.List;

/**
 * 天气雷达
 */
public class ShawnRadarAdapter extends BaseAdapter implements StickyGridHeadersSimpleAdapter {

	private Context mContext;
	private List<RadarDto> mArrayList;
	private LayoutInflater mInflater;

	public ShawnRadarAdapter(Context context, List<RadarDto> mArrayList) {
		this.mContext = context;
		this.mArrayList = mArrayList;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	private class HeaderViewHolder {
		TextView tvName;
	}

	@Override
	public long getHeaderId(int position) {
		return mArrayList.get(position).section;
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder mHeaderHolder;
		if (convertView == null) {
			mHeaderHolder = new HeaderViewHolder();
			convertView = mInflater.inflate(R.layout.shawn_adapter_radar_header, null);
			mHeaderHolder.tvName = convertView.findViewById(R.id.tvName);
			convertView.setTag(mHeaderHolder);
		} else {
			mHeaderHolder = (HeaderViewHolder) convertView.getTag();
		}

		RadarDto dto = mArrayList.get(position);
		mHeaderHolder.tvName.setText(dto.radarName);

		return convertView;
	}
	
	private class ContentViewHolder {
		TextView tvCityName;
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
			convertView = mInflater.inflate(R.layout.shawn_adapter_radar_content, null);
			mHolder.tvCityName = convertView.findViewById(R.id.tvCityName);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ContentViewHolder) convertView.getTag();
		}

		RadarDto dto = mArrayList.get(position);
		if (dto.isSelected) {
			mHolder.tvCityName.setBackgroundResource(R.drawable.shawn_bg_corner_item_press);
			mHolder.tvCityName.setTextColor(Color.WHITE);
		}else {
			mHolder.tvCityName.setBackgroundResource(R.drawable.shawn_bg_corner_item);
			mHolder.tvCityName.setTextColor(mContext.getResources().getColor(R.color.text_color4));
		}
		mHolder.tvCityName.setText(dto.radarName);

		return convertView;
	}

}
