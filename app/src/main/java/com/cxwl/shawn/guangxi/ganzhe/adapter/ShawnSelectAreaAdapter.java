package com.cxwl.shawn.guangxi.ganzhe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.R;
import com.cxwl.shawn.guangxi.ganzhe.dto.CityDto;

import java.util.List;

/**
 * 选择关注区域
 * @author shawn_sun
 */
public class ShawnSelectAreaAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private List<CityDto> mArrayList;
	
	private final class ViewHolder{
		TextView tvArea;
	}
	
	public ShawnSelectAreaAdapter(Context context, List<CityDto> mArrayList) {
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
			convertView = mInflater.inflate(R.layout.shawn_adapter_select_area, null);
			mHolder = new ViewHolder();
			mHolder.tvArea = convertView.findViewById(R.id.tvArea);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		try {
			CityDto dto = mArrayList.get(position);
			mHolder.tvArea.setText(dto.areaName);
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		
		return convertView;
	}

}
