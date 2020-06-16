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
import com.cxwl.shawn.guangxi.ganzhe.common.CONST;
import com.cxwl.shawn.guangxi.ganzhe.dto.DisasterDto;
import com.cxwl.shawn.guangxi.ganzhe.util.CommonUtil;

import java.util.List;

/**
 * 资源库-上传-选择文件
 */
public class SelectFileAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<DisasterDto> mArrayList;

	private final class ViewHolder{
		TextView tvTitle,tvSize;
		ImageView imageView;
	}

	public SelectFileAdapter(Context context, List<DisasterDto> mArrayList) {
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
			convertView = mInflater.inflate(R.layout.adapter_select_file, null);
			mHolder = new ViewHolder();
			mHolder.imageView = convertView.findViewById(R.id.imageView);
			mHolder.tvTitle = convertView.findViewById(R.id.tvTitle);
			mHolder.tvSize = convertView.findViewById(R.id.tvSize);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		final DisasterDto dto = mArrayList.get(position);

		if (!TextUtils.isEmpty(dto.title)) {
			mHolder.tvTitle.setText(dto.title);
		}

		if (TextUtils.equals(dto.fileType, CONST.FILETYPE5)) {
			mHolder.tvSize.setText("");
		}else {
			mHolder.tvSize.setText(CommonUtil.getFormatSize(dto.fileSize));
		}

		//1图片、2视频、3音频、4文档、5文件夹
		if (TextUtils.equals(dto.fileType, CONST.FILETYPE1)) {
			mHolder.imageView.setImageResource(R.drawable.shawn_icon_image);
		}else if (TextUtils.equals(dto.fileType, CONST.FILETYPE2)) {
			mHolder.imageView.setImageResource(R.drawable.shawn_icon_video);
		}else if (TextUtils.equals(dto.fileType, CONST.FILETYPE3)) {
			mHolder.imageView.setImageResource(R.drawable.shawn_icon_txt);
		}else if (TextUtils.equals(dto.fileType, CONST.FILETYPE4)) {
			String lowCase = dto.filePath.toLowerCase();
			if (lowCase.contains(CONST.doc) || lowCase.endsWith(CONST.docx)) {
				mHolder.imageView.setImageResource(R.drawable.shawn_icon_word);
			}else if (lowCase.endsWith(CONST.ppt) || lowCase.endsWith(CONST.pptx)) {
				mHolder.imageView.setImageResource(R.drawable.shawn_icon_ppt);
			}else if (lowCase.endsWith(CONST.pdf)) {
				mHolder.imageView.setImageResource(R.drawable.shawn_icon_pdf);
			}else if (lowCase.endsWith(CONST.xls) || lowCase.endsWith(CONST.xlsx)) {
				mHolder.imageView.setImageResource(R.drawable.shawn_icon_xls);
			}else {
				mHolder.imageView.setImageResource(R.drawable.shawn_icon_txt);
			}
		}else if (TextUtils.equals(dto.fileType, CONST.FILETYPE5)) {
			mHolder.imageView.setImageResource(R.drawable.shawn_icon_files);
		}else {
			mHolder.imageView.setImageResource(R.drawable.shawn_icon_txt);
		}

		return convertView;
	}

}
