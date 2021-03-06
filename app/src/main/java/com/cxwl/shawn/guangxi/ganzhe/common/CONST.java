package com.cxwl.shawn.guangxi.ganzhe.common;

import android.os.Environment;

import com.cxwl.shawn.guangxi.ganzhe.R;

public class CONST {

	public static final String APPID = "35";
	public static String ADCODE = "450103";//定位户会根据定位点改变，为雷达所用
	public static final String imageSuffix = ".png";//图标后缀名
	public static final String lastFile = "..";//返回上级目录
	public static final String doc = ".doc";
	public static final String docx = ".docx";
	public static final String ppt = ".ppt";
	public static final String pptx = ".pptx";
	public static final String pdf = ".pdf";
	public static final String xls = ".xls";
	public static final String xlsx = ".xlsx";
	public static final String txt = ".txt";
	public static final String zip = ".zip";
	public static final String rar = ".rar";

	//下拉刷新progresBar四种颜色
	public static final int color1 = R.color.refresh_color1;
	public static final int color2 = R.color.refresh_color2;
	public static final int color3 = R.color.refresh_color3;
	public static final int color4 = R.color.refresh_color4;

	//预警颜色对应规则
	public static String[] blue = {"01", "_blue"};
	public static String[] yellow = {"02", "_yellow"};
	public static String[] orange = {"03", "_orange"};
	public static String[] red = {"04", "_red"};

	//showType类型，区分本地类或者图文
	public static final String LOCAL = "local";
	public static final String NEWS = "news";
	public static final String URL = "url";
	public static final String PDF = "pdf";

	public static final String WEB_URL = "web_Url";//网页地址的标示
	public static final String ACTIVITY_NAME = "activity_name";//界面名称
	public static final String LOCAL_ID = "local_id";//local_id
	public static final String COLUMN_ID = "column_id";//column_id

	//通用
	public static String SDCARD_PATH = Environment.getExternalStorageDirectory()+"/gxgz";
	public static String FILEALL = "0", FILETYPE1 = "1",FILETYPE2 = "2",FILETYPE3 = "3",FILETYPE4 = "4",FILETYPE5 = "5";//1图片、2视频、3音频、4文档、5文件夹

}
