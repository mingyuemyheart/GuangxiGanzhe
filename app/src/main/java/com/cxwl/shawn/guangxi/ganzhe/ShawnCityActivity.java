package com.cxwl.shawn.guangxi.ganzhe;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.adapter.ShawnCityLocalAdapter;
import com.cxwl.shawn.guangxi.ganzhe.adapter.ShawnCityNationAdapter;
import com.cxwl.shawn.guangxi.ganzhe.adapter.ShawnCitySearchAdapter;
import com.cxwl.shawn.guangxi.ganzhe.dto.CityDto;
import com.cxwl.shawn.guangxi.ganzhe.manager.DBManager;
import com.cxwl.shawn.guangxi.ganzhe.view.stickygridheaders.StickyGridHeadersGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 城市选择
 */
public class ShawnCityActivity extends ShawnBaseActivity implements OnClickListener{
	
	private Context mContext;
	private TextView tvProvince,tvNational;
	private LinearLayout llGroup,llGridView;

	//搜索城市后的结果列表
	private ListView listView;
	private ShawnCitySearchAdapter searchAdapter;
	private List<CityDto> searchList = new ArrayList<>();

	//省内热门
	private StickyGridHeadersGridView pGridView;
	private List<CityDto> pList = new ArrayList<>();
	private int section = 1;
	private HashMap<String, Integer> sectionMap = new HashMap<>();
	
	//全国热门
	private GridView nGridView;
	private List<CityDto> nList = new ArrayList<>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_city);
		mContext = this;
		initWidget();
		initListView();
		initPGridView();
		initNGridView();
	}
	
	/**
	 * 初始化控件
	 */
	private void initWidget() {
		EditText etSearch = findViewById(R.id.etSearch);
		etSearch.addTextChangedListener(watcher);
		tvProvince = findViewById(R.id.tvProvince);
		tvProvince.setOnClickListener(this);
		tvNational = findViewById(R.id.tvNational);
		tvNational.setOnClickListener(this);
		llGroup = findViewById(R.id.llGroup);
		llGridView = findViewById(R.id.llGridView);
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		TextView tvTitle = findViewById(R.id.tvTitle);
		tvTitle.setText("城市选择");
	}
	
	private TextWatcher watcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}
		@Override
		public void afterTextChanged(Editable arg0) {
			searchList.clear();
			if (arg0.toString().trim().equals("")) {
				listView.setVisibility(View.GONE);
				llGroup.setVisibility(View.VISIBLE);
				llGridView.setVisibility(View.VISIBLE);
			}else {
				listView.setVisibility(View.VISIBLE);
				llGridView.setVisibility(View.GONE);
				llGroup.setVisibility(View.GONE);
				getCityInfo(arg0.toString().trim());
			}

		}
	};
	
	/**
	 * 迁移到天气详情界面
	 */
	private void intentWeatherDetail(CityDto data) {
		Intent intent = new Intent();
		intent.putExtra("cityName", data.disName);
		intent.putExtra("cityId", data.cityId);
		intent.putExtra("lat", data.lat);
		intent.putExtra("lng", data.lng);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	/**
	 * 初始化listview
	 */
	private void initListView() {
		listView = findViewById(R.id.listView);
		searchAdapter = new ShawnCitySearchAdapter(mContext, searchList);
		listView.setAdapter(searchAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				intentWeatherDetail(searchList.get(arg2));
			}
		});
	}
	
	/**
	 * 初始化省内热门gridview
	 */
	private void initPGridView() {
		String[] stations = getResources().getStringArray(R.array.pro_hotCity);
		for (String station : stations) {
			String[] value = station.split(",");
			CityDto dto = new CityDto();
			dto.cityId = value[0];
			dto.disName = value[1];
			dto.lat = Double.valueOf(value[2]);
			dto.lng = Double.valueOf(value[3]);
			dto.level = value[4];
			dto.sectionName = value[5];
			pList.add(dto);
		}
		
		for (int i = 0; i < pList.size(); i++) {
			CityDto sectionDto = pList.get(i);
			if (!sectionMap.containsKey(sectionDto.sectionName)) {
				sectionDto.section = section;
				sectionMap.put(sectionDto.sectionName, section);
				section++;
			}else {
				sectionDto.section = sectionMap.get(sectionDto.sectionName);
			}
			pList.set(i, sectionDto);
		}
		
		pGridView = findViewById(R.id.pGridView);
		ShawnCityLocalAdapter pAdapter = new ShawnCityLocalAdapter(mContext, pList);
		pGridView.setAdapter(pAdapter);
		pGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				intentWeatherDetail(pList.get(arg2));
			}
		});
	}
	
	/**
	 * 初始化全国热门
	 */
	private void initNGridView() {
		nList.clear();
		String[] stations = getResources().getStringArray(R.array.nation_hotCity);
		for (String station : stations) {
			String[] value = station.split(",");
			CityDto dto = new CityDto();
			dto.cityId = value[0];
			dto.disName = value[1];
			dto.lat = Double.valueOf(value[2]);
			dto.lng = Double.valueOf(value[3]);
			nList.add(dto);
		}

		nGridView = findViewById(R.id.nGridView);
		ShawnCityNationAdapter nAdapter = new ShawnCityNationAdapter(mContext, nList);
		nGridView.setAdapter(nAdapter);
		nGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				intentWeatherDetail(nList.get(arg2));
			}
		});
	}
	
	/**
	 * 获取城市信息
	 */
	private void getCityInfo(String keyword) {
		searchList.clear();
		DBManager dbManager = new DBManager(mContext);
		dbManager.openDateBase();
		dbManager.closeDatabase();
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME, null);
		Cursor cursor = database.rawQuery("select * from "+DBManager.TABLE_NAME3+" where pro like "+"\"%"+keyword+"%\""+" or city like "+"\"%"+keyword+"%\""+" or dis like "+"\"%"+keyword+"%\"",null);
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			CityDto dto = new CityDto();
			dto.provinceName = cursor.getString(cursor.getColumnIndex("pro"));
			dto.cityName = cursor.getString(cursor.getColumnIndex("city"));
			dto.disName = cursor.getString(cursor.getColumnIndex("dis"));
			dto.cityId = cursor.getString(cursor.getColumnIndex("cid"));
			dto.lat = cursor.getDouble(cursor.getColumnIndex("lat"));
			dto.lng = cursor.getDouble(cursor.getColumnIndex("lng"));
			searchList.add(dto);
		}
		if (searchAdapter != null) {
			searchAdapter.notifyDataSetChanged();
		}
		cursor.close();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llBack:
			finish();
			break;
		case R.id.tvProvince:
			tvProvince.setTextColor(getResources().getColor(R.color.white));
			tvNational.setTextColor(getResources().getColor(R.color.colorPrimary));
			tvProvince.setBackgroundResource(R.drawable.shawn_bg_corner_left_blue);
			tvNational.setBackgroundResource(R.drawable.shawn_bg_corner_right_white);
			pGridView.setVisibility(View.VISIBLE);
			nGridView.setVisibility(View.GONE);
			break;
		case R.id.tvNational:
			tvProvince.setTextColor(getResources().getColor(R.color.colorPrimary));
			tvNational.setTextColor(getResources().getColor(R.color.white));
			tvProvince.setBackgroundResource(R.drawable.shawn_bg_corner_left_white);
			tvNational.setBackgroundResource(R.drawable.shawn_bg_corner_right_blue);
			pGridView.setVisibility(View.GONE);
			nGridView.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}
	}
}
