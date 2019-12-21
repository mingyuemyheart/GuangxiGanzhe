package com.cxwl.shawn.guangxi.ganzhe.adapter;

import android.content.Context;
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
 * 蔗田监测-田间实况-相关数据查询
 */
public class ShawnFactDataAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<FactDto> mArrayList;

	private final class ViewHolder{
		TextView tvStationName,tvTEMP,tvRELA_HUMI,tvMOST_WD,tvMOST_WS,tvMAX_WD,tvMAX_WS,
				tvSMVP_5CM_AVE,tvSMVP_10CM_AVE,tvSMVP_20CM_AVE,tvSMVP_30CM_AVE,tvSMVP_40CM_AVE,
				tvLAND_10CM_TEMP,tvLAND_15CM_TEMP,tvLAND_20CM_TEMP,tvLAND_30CM_TEMP,tvLAND_40CM_TEMP;
	}

	public ShawnFactDataAdapter(Context context, List<FactDto> mArrayList) {
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
			convertView = mInflater.inflate(R.layout.shawn_adapter_fact_data, null);
			mHolder = new ViewHolder();
			mHolder.tvStationName = convertView.findViewById(R.id.tvStationName);
			mHolder.tvTEMP = convertView.findViewById(R.id.tvTEMP);
			mHolder.tvRELA_HUMI = convertView.findViewById(R.id.tvRELA_HUMI);
			mHolder.tvMOST_WD = convertView.findViewById(R.id.tvMOST_WD);
			mHolder.tvMOST_WS = convertView.findViewById(R.id.tvMOST_WS);
			mHolder.tvMAX_WD = convertView.findViewById(R.id.tvMAX_WD);
			mHolder.tvMAX_WS = convertView.findViewById(R.id.tvMAX_WS);
			mHolder.tvSMVP_5CM_AVE = convertView.findViewById(R.id.tvSMVP_5CM_AVE);
			mHolder.tvSMVP_10CM_AVE = convertView.findViewById(R.id.tvSMVP_10CM_AVE);
			mHolder.tvSMVP_20CM_AVE = convertView.findViewById(R.id.tvSMVP_20CM_AVE);
			mHolder.tvSMVP_30CM_AVE = convertView.findViewById(R.id.tvSMVP_30CM_AVE);
			mHolder.tvSMVP_40CM_AVE = convertView.findViewById(R.id.tvSMVP_40CM_AVE);
			mHolder.tvLAND_10CM_TEMP = convertView.findViewById(R.id.tvLAND_10CM_TEMP);
			mHolder.tvLAND_15CM_TEMP = convertView.findViewById(R.id.tvLAND_15CM_TEMP);
			mHolder.tvLAND_20CM_TEMP = convertView.findViewById(R.id.tvLAND_20CM_TEMP);
			mHolder.tvLAND_30CM_TEMP = convertView.findViewById(R.id.tvLAND_30CM_TEMP);
			mHolder.tvLAND_40CM_TEMP = convertView.findViewById(R.id.tvLAND_40CM_TEMP);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		FactDto dto = mArrayList.get(position);

		if (!TextUtils.isEmpty(dto.stationName)) {
			mHolder.tvStationName.setText(dto.stationName);
		}
		if (!TextUtils.isEmpty(dto.TEMP)) {
			mHolder.tvTEMP.setText(dto.TEMP);
		}
		if (!TextUtils.isEmpty(dto.RELA_HUMI)) {
			mHolder.tvRELA_HUMI.setText(dto.RELA_HUMI);
		}
		if (!TextUtils.isEmpty(dto.MOST_WD)) {
			mHolder.tvMOST_WD.setText(dto.MOST_WD);
		}
		if (!TextUtils.isEmpty(dto.MOST_WS)) {
			mHolder.tvMOST_WS.setText(dto.MOST_WS);
		}
		if (!TextUtils.isEmpty(dto.MAX_WD)) {
			mHolder.tvMAX_WD.setText(dto.MAX_WD);
		}
		if (!TextUtils.isEmpty(dto.MAX_WS)) {
			mHolder.tvMAX_WS.setText(dto.MAX_WS);
		}
		if (!TextUtils.isEmpty(dto.SMVP_5CM_AVE)) {
			mHolder.tvSMVP_5CM_AVE.setText(dto.SMVP_5CM_AVE);
		}
		if (!TextUtils.isEmpty(dto.SMVP_10CM_AVE)) {
			mHolder.tvSMVP_10CM_AVE.setText(dto.SMVP_10CM_AVE);
		}
		if (!TextUtils.isEmpty(dto.SMVP_20CM_AVE)) {
			mHolder.tvSMVP_20CM_AVE.setText(dto.SMVP_20CM_AVE);
		}
		if (!TextUtils.isEmpty(dto.SMVP_30CM_AVE)) {
			mHolder.tvSMVP_30CM_AVE.setText(dto.SMVP_30CM_AVE);
		}
		if (!TextUtils.isEmpty(dto.SMVP_40CM_AVE)) {
			mHolder.tvSMVP_40CM_AVE.setText(dto.SMVP_40CM_AVE);
		}
		if (!TextUtils.isEmpty(dto.LAND_10CM_TEMP)) {
			mHolder.tvLAND_10CM_TEMP.setText(dto.LAND_10CM_TEMP);
		}
		if (!TextUtils.isEmpty(dto.LAND_15CM_TEMP)) {
			mHolder.tvLAND_15CM_TEMP.setText(dto.LAND_15CM_TEMP);
		}
		if (!TextUtils.isEmpty(dto.LAND_20CM_TEMP)) {
			mHolder.tvLAND_20CM_TEMP.setText(dto.LAND_20CM_TEMP);
		}
		if (!TextUtils.isEmpty(dto.LAND_30CM_TEMP)) {
			mHolder.tvLAND_30CM_TEMP.setText(dto.LAND_30CM_TEMP);
		}
		if (!TextUtils.isEmpty(dto.LAND_40CM_TEMP)) {
			mHolder.tvLAND_40CM_TEMP.setText(dto.LAND_40CM_TEMP);
		}

		return convertView;
	}

}
