package com.cxwl.shawn.guangxi.ganzhe.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.R;
import com.cxwl.shawn.guangxi.ganzhe.dto.FarmDto;

import java.util.List;

/**
 * 留言互动-我的农场
 */
public class ShawnMyFarmAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<FarmDto> mArrayList;

	private final class ViewHolder{
		TextView tvTitle;
	}

	public ShawnMyFarmAdapter(Context context, List<FarmDto> mArrayList) {
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
			convertView = mInflater.inflate(R.layout.shawn_adapter_my_farm, null);
			mHolder = new ViewHolder();
			mHolder.tvTitle = convertView.findViewById(R.id.tvTitle);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		FarmDto dto = mArrayList.get(position);

		if (!TextUtils.isEmpty(dto.name)) {
			mHolder.tvTitle.setText(dto.name);
		}

		return convertView;
	}

}
