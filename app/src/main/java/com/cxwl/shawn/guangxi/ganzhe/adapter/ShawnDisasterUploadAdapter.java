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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * 灾情反馈
 */
public class ShawnDisasterUploadAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<DisasterDto> mArrayList;
	private RelativeLayout.LayoutParams params;
	private int itemWidth;

	private final class ViewHolder{
		ImageView imageView;
	}

	public ShawnDisasterUploadAdapter(Context context, List<DisasterDto> mArrayList, int itemWidth) {
		mContext = context;
		this.itemWidth = itemWidth;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.shawn_adapter_disaster_upload, null);
			mHolder = new ViewHolder();
			mHolder.imageView = convertView.findViewById(R.id.imageView);
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
		}else {
			mHolder.imageView.setBackgroundColor(mContext.getResources().getColor(R.color.light_gray));
			mHolder.imageView.setImageResource(R.drawable.shawn_icon_plus);
			mHolder.imageView.setPadding(itemWidth/4,itemWidth/4,itemWidth/4,itemWidth/4);
			mHolder.imageView.setLayoutParams(params);
		}

		return convertView;
	}

}
