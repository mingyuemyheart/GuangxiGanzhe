package com.cxwl.shawn.guangxi.ganzhe.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.R;
import com.cxwl.shawn.guangxi.ganzhe.ShawnFactClimateDetailActivity;
import com.cxwl.shawn.guangxi.ganzhe.ShawnFactDataDetailActivity;
import com.cxwl.shawn.guangxi.ganzhe.adapter.ShawnFactDataAdapter;
import com.cxwl.shawn.guangxi.ganzhe.dto.FactDto;

import java.util.ArrayList;
import java.util.List;

/**
 * 蔗田监测-田间实况-相关数据查询
 */
public class ShawnFactDataFragment extends Fragment {
	
	private ShawnFactDataAdapter mAdapter;
	private List<FactDto> dataList = new ArrayList<>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.shawn_fragment_fact_data, null);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initWidget(view);
		initListView(view);
	}

	private void initWidget(View view) {
		TextView tvPrompt = view.findViewById(R.id.tvPrompt);

		dataList.clear();
		dataList.addAll(getArguments().<FactDto>getParcelableArrayList("dataList"));
		if (dataList.size() > 0) {
			tvPrompt.setVisibility(View.GONE);
		}else {
			tvPrompt.setVisibility(View.VISIBLE);
		}

		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	private void initListView(View view) {
		ListView listView = view.findViewById(R.id.listView);
		mAdapter = new ShawnFactDataAdapter(getActivity(), dataList);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				FactDto dto = dataList.get(arg2);
				Intent intent = new Intent(getActivity(), ShawnFactDataDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable("data", dto);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

}
