package com.cxwl.shawn.guangxi.ganzhe.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.R;
import com.cxwl.shawn.guangxi.ganzhe.dto.WADto;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 交流互动-专家联盟
 */
public class ShawnExpertAdapter extends BaseAdapter{
	
	private Context mContext;
	private LayoutInflater mInflater;
	private List<WADto> mArrayList;
	
	private final class ViewHolder{
		ImageView imageView;
		TextView tvName,tvBreif,tvLabel;
	}
	
	public ShawnExpertAdapter(Context context, List<WADto> mArrayList) {
		mContext = context;
		this.mArrayList = mArrayList;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			convertView = mInflater.inflate(R.layout.shawn_adapter_expert, null);
			mHolder = new ViewHolder();
			mHolder.imageView = convertView.findViewById(R.id.imageView);
			mHolder.tvName = convertView.findViewById(R.id.tvName);
			mHolder.tvBreif = convertView.findViewById(R.id.tvBreif);
			mHolder.tvLabel = convertView.findViewById(R.id.tvLabel);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		WADto dto = mArrayList.get(position);
		if (!TextUtils.isEmpty(dto.name)) {
			mHolder.tvName.setText(dto.name);
		}
		if (!TextUtils.isEmpty(dto.breif)) {
			mHolder.tvBreif.setText(dto.breif);
		}
		if (!TextUtils.isEmpty(dto.label)) {
			mHolder.tvLabel.setText(dto.label);
		}
		if (!TextUtils.isEmpty(dto.imgUrl)) {
			Picasso.get().load(dto.imgUrl).error(R.drawable.shawn_icon_seat_bitmap).into(mHolder.imageView);
		}

		return convertView;
	}

}
