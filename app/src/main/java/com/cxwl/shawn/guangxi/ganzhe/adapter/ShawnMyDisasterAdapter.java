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
import com.cxwl.shawn.guangxi.ganzhe.dto.DisasterDto;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 我上传的灾情反馈
 */
public class ShawnMyDisasterAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<DisasterDto> mArrayList;

	private final class ViewHolder{
		ImageView imageView;
		TextView tvContent,tvType,tvAddr,tvTime;
	}

	public ShawnMyDisasterAdapter(Context context, List<DisasterDto> mArrayList) {
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
			convertView = mInflater.inflate(R.layout.shawn_adapter_my_disaster, null);
			mHolder = new ViewHolder();
			mHolder.imageView = convertView.findViewById(R.id.imageView);
			mHolder.tvContent = convertView.findViewById(R.id.tvContent);
			mHolder.tvType = convertView.findViewById(R.id.tvType);
			mHolder.tvAddr = convertView.findViewById(R.id.tvAddr);
			mHolder.tvTime = convertView.findViewById(R.id.tvTime);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		DisasterDto dto = mArrayList.get(position);

		if (dto.imgList.size() > 0) {
			String imgUrl = dto.imgList.get(0);
			if (!TextUtils.isEmpty(imgUrl)) {
				Picasso.get().load(imgUrl).into(mHolder.imageView);
			}
		}

		if (!TextUtils.isEmpty(dto.content)) {
			mHolder.tvContent.setText(dto.content);
		} else {
			mHolder.tvContent.setText("");
		}

		if (!TextUtils.isEmpty(dto.addr)) {
			mHolder.tvAddr.setText("上传地点："+dto.addr);
		}

		if (!TextUtils.isEmpty(dto.time)) {
			mHolder.tvTime.setText("上传时间："+dto.time);
		}

		if (!TextUtils.isEmpty(dto.disasterType)) {
			mHolder.tvType.setText("上传类型："+dto.disasterType);
		}

		return convertView;
	}

}
