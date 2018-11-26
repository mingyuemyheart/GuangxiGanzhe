package com.cxwl.shawn.guangxi.ganzhe.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.cxwl.shawn.guangxi.ganzhe.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DBManager {
	public static final String DB_NAME = "stations.db";
	public static final String TABLE_NAME1 = "SITES";//站点监测站表名称
	public static final String TABLE_NAME2 = "WARNING_GUIDE";//预警指南表名称
	public static final String TABLE_NAME3 = "WARNING_ID";//预警id表名称
	public static String DB_PATH = "";
	private Context mContext;
	private SQLiteDatabase database;

	public DBManager(Context context) {
		this.mContext = context;
		DB_PATH = "/data"+ Environment.getDataDirectory().getAbsolutePath() + "/"+ context.getPackageName(); // 存放路径
	}

	/**
	 * 被调用方法
	 */
	public void openDateBase() {
		this.database = this.openDateBase(DB_PATH + "/" + DB_NAME);
	}

	/**
	 * 打开数据库
	 * 
	 * @param dbFile
	 * @return SQLiteDatabase
	 * @author sy
	 */
	private SQLiteDatabase openDateBase(String dbFile) {
		File file = new File(dbFile);
		if (!file.exists()) {
			// // 打开raw中得数据库文件，获得stream流
			InputStream stream = this.mContext.getResources().openRawResource(R.raw.stations);
			try {
				// 将获取到的stream 流写入道data中
				FileOutputStream outputStream = new FileOutputStream(dbFile);
				byte[] buffer = new byte[400000];
				int count = 0;
				while ((count = stream.read(buffer)) > 0) {
					outputStream.write(buffer, 0, count);
				}
				outputStream.close();
				stream.close();
				SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
				return db;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return database;
	}

	public void closeDatabase() {
		if (database != null && database.isOpen()) {
			this.database.close();
		}
	}
}
