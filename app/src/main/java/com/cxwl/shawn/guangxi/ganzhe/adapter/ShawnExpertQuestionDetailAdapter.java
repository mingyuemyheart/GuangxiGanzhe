package com.cxwl.shawn.guangxi.ganzhe.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.R;
import com.cxwl.shawn.guangxi.ganzhe.dto.WADto;

import java.util.List;

/**
 * 专家联盟-问题咨询-问题详情
 * @author shawn_sun
 */
public class ShawnExpertQuestionDetailAdapter extends BaseAdapter{

	private Context mContext;
	private LayoutInflater mInflater;
	private List<WADto> mArrayList;

	private final class ViewHolder{
		TextView tvContent, tvTime, tvName, tvExpert;
	}

	public ShawnExpertQuestionDetailAdapter(Context context, List<WADto> mArrayList) {
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
			convertView = mInflater.inflate(R.layout.shawn_adapter_expert_question_detail, null);
			mHolder = new ViewHolder();
			mHolder.tvContent = convertView.findViewById(R.id.tvContent);
			mHolder.tvTime = convertView.findViewById(R.id.tvTime);
			mHolder.tvName = convertView.findViewById(R.id.tvName);
			mHolder.tvExpert = convertView.findViewById(R.id.tvExpert);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		WADto dto = mArrayList.get(position);
		if (!TextUtils.isEmpty(dto.content)) {
			mHolder.tvContent.setText(dto.content);
		}
		if (!TextUtils.isEmpty(dto.time)) {
			mHolder.tvTime.setText(dto.time);
		}
		if (!TextUtils.isEmpty(dto.userName)) {
			mHolder.tvName.setText(dto.userName);
		}
		if (TextUtils.equals(dto.isexpert, "1")) {//专家回答的
			convertView.setBackgroundColor(mContext.getResources().getColor(R.color.light_gray));
			mHolder.tvExpert.setVisibility(View.VISIBLE);
		}else {
			convertView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
			mHolder.tvExpert.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}

}
