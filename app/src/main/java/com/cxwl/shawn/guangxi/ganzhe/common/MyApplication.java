package com.cxwl.shawn.guangxi.ganzhe.common;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class MyApplication extends Application{

	@Override
	public void onCreate() {
		super.onCreate();
		getUserInfo(this);
	}

	//本地保存用户信息参数
	public static String USERGROUP = "";//用户组
	public static String UID = "";//用户id
	public static String USERNAME = "";
	public static String PASSWORD = "";

	public static String USERINFO = "userInfo";//userInfo sharedPreferance名称
	public static class UserInfo {
		private static final String userGroup = "userGroup";
		private static final String uid = "uid";
		private static final String userName = "uName";
		private static final String passWord = "pwd";
	}

	/**
	 * 清除用户信息
	 */
	public static void clearUserInfo(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(USERINFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.clear();
		editor.apply();
		USERGROUP = "";
		UID = "";
		USERNAME = "";
		PASSWORD = "";
	}

	/**
	 * 保存用户信息
	 */
	public static void saveUserInfo(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(USERINFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(UserInfo.userGroup, USERGROUP);
		editor.putString(UserInfo.uid, UID);
		editor.putString(UserInfo.userName, USERNAME);
		editor.putString(UserInfo.passWord, PASSWORD);
		editor.apply();
	}

	/**
	 * 获取用户信息
	 */
	public static void getUserInfo(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(USERINFO, Context.MODE_PRIVATE);
		USERGROUP = sharedPreferences.getString(UserInfo.userGroup, "");
		UID = sharedPreferences.getString(UserInfo.uid, "");
		USERNAME = sharedPreferences.getString(UserInfo.userName, "");
		PASSWORD = sharedPreferences.getString(UserInfo.passWord, "");
	}

}
