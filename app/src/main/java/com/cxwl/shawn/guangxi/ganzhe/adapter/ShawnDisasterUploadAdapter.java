package com.cxwl.shawn.guangxi.ganzhe.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cxwl.shawn.guangxi.ganzhe.R;
import com.cxwl.shawn.guangxi.ganzhe.dto.DisasterDto;
import com.cxwl.shawn.guangxi.ganzhe.util.CommonUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * 灾情反馈
 */
public class ShawnDisasterUploadAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<DisasterDto> mArrayList;
	private RelativeLayout.LayoutParams params;

	private final class ViewHolder{
		ImageView imageView,ivDelete;
	}

	public ShawnDisasterUploadAdapter(Context context, List<DisasterDto> mArrayList) {
		int itemWidth = (CommonUtil.widthPixels(context) - (int)CommonUtil.dip2px(context, 24f)) / 3;
		this.mArrayList = mArrayList;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		params = new RelativeLayout.LayoutParams(itemWidth, itemWidth);
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder mHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.shawn_adapter_disaster_upload, null);
			mHolder = new ViewHolder();
			mHolder.imageView = convertView.findViewById(R.id.imageView);
			mHolder.ivDelete = convertView.findViewById(R.id.ivDelete);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		DisasterDto dto = mArrayList.get(position);

		if (!dto.isLastItem) {
			if (!TextUtils.isEmpty(dto.imgUrl)) {
				File file = new File(dto.imgUrl);
				if (file.exists()) {
					Picasso.get().load(file).centerCrop().resize(200, 200).into(mHolder.imageView);
					mHolder.imageView.setPadding(0,0,0,0);
					mHolder.imageView.setLayoutParams(params);
				}
			}
			mHolder.ivDelete.setVisibility(View.VISIBLE);
			mHolder.ivDelete.setTag(position);
		}else {
			mHolder.imageView.setImageResource(R.drawable.shawn_icon_plus);
			mHolder.imageView.setLayoutParams(params);
			mHolder.ivDelete.setVisibility(View.INVISIBLE);
		}

		mHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mArrayList.remove(position);
				notifyDataSetChanged();
			}
		});

		return convertView;
	}

}
