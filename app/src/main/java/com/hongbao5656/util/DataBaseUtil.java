package com.hongbao5656.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.hongbao5656.R;
/**
 * copy数据库到apk包
 *
 * @author NGJ
 *
 */
public class DataBaseUtil {
	private Context context;
	public static String dbName = "region.db";// 数据库的名字
	private static String DATABASE_PATH;// 数据库在手机里的路径
	public DataBaseUtil(Context context) {
		this.context = context;
		String packageName = context.getPackageName();
		DATABASE_PATH="/data/data/"+packageName+"/databases/";
	}

	/**
	 * 判断数据库文件是否存在
	 * @return false or true
	 */
	public boolean checkDataBase() {
		SQLiteDatabase db = null;
		try {
			String databaseFilename = DATABASE_PATH + dbName;
			db = SQLiteDatabase.openDatabase(databaseFilename, null,SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		if (db != null) {
			db.close();
		}
		return db != null ? true : false;
	}


	/**
	 * 复制数据库到手机指定文件夹下
	 * @throws IOException
	 */
	public void copyDataBase() throws IOException {
		String databaseFilenames = DATABASE_PATH + dbName;
		File dir = new File(DATABASE_PATH);
		if (!dir.exists()) dir.mkdir();// 判断文件夹是否存在，不存在就新建一个

		FileOutputStream os = new FileOutputStream(databaseFilenames);// 得到数据库文件的写入流

		InputStream is = context.getResources().openRawResource(R.raw.region);// 得到数据库文件的输入流
		byte[] bytes = new byte[8192];
		int len = 0;
		while ((len = is.read(bytes)) > 0) {
			os.write(bytes, 0, len);
			os.flush();
		}
		is.close();
		os.close();
	}
}
