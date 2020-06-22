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
import com.cxwl.shawn.guangxi.ganzhe.dto.ColumnData;
import com.cxwl.shawn.guangxi.ganzhe.util.CommonUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 主界面
 */
public class ColumnAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<ColumnData> mArrayList;
	public int height;

	private final class ViewHolder{
		TextView tvName;
		ImageView icon;
	}

	public ColumnAdapter(Context context, List<ColumnData> mArrayList) {
		mContext = context;
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
			convertView = mInflater.inflate(R.layout.adapter_column, null);
			mHolder = new ViewHolder();
			mHolder.tvName = convertView.findViewById(R.id.tvName);
			mHolder.icon = convertView.findViewById(R.id.icon);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		try {
			ColumnData dto = mArrayList.get(position);

			if (!TextUtils.isEmpty(dto.name)) {
				mHolder.tvName.setText(dto.name);
			}

			if (!TextUtils.isEmpty(dto.icon)) {
				Picasso.get().load(dto.icon).into(mHolder.icon);
			}

			if (height > 0) {
				ViewGroup.LayoutParams params = convertView.getLayoutParams();
				params.height = height;
				convertView.setLayoutParams(params);
				notifyDataSetChanged();
			}else {
				convertView.setPadding(0, (int)CommonUtil.dip2px(mContext, 20), 0, (int)CommonUtil.dip2px(mContext, 20));
			}

		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		return convertView;
	}

}
