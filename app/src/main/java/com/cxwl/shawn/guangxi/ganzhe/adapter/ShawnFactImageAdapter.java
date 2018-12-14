package com.cxwl.shawn.guangxi.ganzhe.adapter;

import android.content.Context;
import android.graphics.Color;
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
 * 蔗田监测-田间实况-蔗田实景
 */
public class ShawnFactImageAdapter extends BaseAdapter {

	private Context mContext;
	private List<FactDto> mArrayList;
	private LayoutInflater mInflater;

	public ShawnFactImageAdapter(Context context, List<FactDto> mArrayList) {
		this.mContext = context;
		this.mArrayList = mArrayList;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	private class ViewHolder {
		TextView tvStationName;
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
		ViewHolder mHolder;
		if (convertView == null) {
			mHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.shawn_adapter_fact_image, null);
			mHolder.tvStationName = convertView.findViewById(R.id.tvStationName);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		FactDto dto = mArrayList.get(position);

		if (TextUtils.isEmpty(dto.imgUrl) || !dto.imgUrl.endsWith(".jpg")) {//判断无效图片地址
			mHolder.tvStationName.setBackgroundResource(R.drawable.shawn_bg_corner_red);
			mHolder.tvStationName.setTextColor(Color.WHITE);
		}else {
			if (dto.isSelected) {
				mHolder.tvStationName.setBackgroundResource(R.drawable.shawn_bg_corner_item_press);
				mHolder.tvStationName.setTextColor(Color.WHITE);
			}else {
				mHolder.tvStationName.setBackgroundResource(R.drawable.shawn_bg_corner_item);
				mHolder.tvStationName.setTextColor(mContext.getResources().getColor(R.color.text_color4));
			}
		}

		if (!TextUtils.isEmpty(dto.stationName)) {
			mHolder.tvStationName.setText(dto.stationName);
		}

		return convertView;
	}

}
