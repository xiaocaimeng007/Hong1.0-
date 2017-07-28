package com.hongbao5656.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.aaron.library.MLog;
import com.hongbao5656.entity.MyLocation;
import com.hongbao5656.entity.Region;
import com.hongbao5656.entity.Region2;

import java.io.File;
import java.util.ArrayList;

/**
 * 数据库帮助类
 */
public class DBHelper extends SQLiteOpenHelper {
	private static DBHelper mInstance = null;
	/** 数据库名称 ,以当前登录用户id为数据库名 **/
	public static String DATABASE_NAME;
	/** 数据库版本号 **/
	private static final int DATABASE_VERSION = 2;
	/**
	 * 表名：省市县区数据
	 */
	public static final String TABLE_REGION = "TABLE_REGION";
	/**
	 * 表名：经纬度
	 */
//	public static final String TABLE_LOCATION = "TABLE_LOCATION";
	public static final String TABLE_LOCATION = "NEW_TABLE_LOCATION";
	private Context context;

	private DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	/** 单例模式 **/
	public static synchronized DBHelper getInstance(Context context) {
		if (mInstance == null) {
			DATABASE_NAME = "/data/data/" + context.getPackageName()
					+ "/databases" + File.separator + "region.db";
			File dirs = new File(DATABASE_NAME).getParentFile();
			if (!dirs.exists()) {
				dirs.mkdirs();
			}
			mInstance = new DBHelper(context);
			DatabaseManager.initializeInstance(mInstance);
		}
		return mInstance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		/** 向数据中添加表 **/
		db.execSQL(createSSQXSql());
		db.execSQL(createLocationSql());
		MLog.i("创建经纬度数据表","创建经纬度数据表的onCreate方法" );
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/** 可以拿到当前数据库的版本信息 与之前数据库的版本信息 用来更新数据库 **/
		// db.execSQL("DROP TABLE IF EXISTS  " + DATABASE_NAME);
		// onCreate(db);
		if (oldVersion < newVersion) {
			db.execSQL(createSSQXSql());
			db.execSQL(createLocationSql());
			MLog.i("创建经纬度数据表","创建经纬度数据表的onUpgrade方法" );
		}
	}

	/**
	 * 创建省市县区数据表
	 * @return
	 */
	private String createSSQXSql() {
		StringBuffer buff = new StringBuffer();
		buff.append("CREATE TABLE if not exists ")
				.append(TABLE_REGION)
				.append("(Id int,")
				.append("ParentId int,")
				.append("Name TEXT,")
				.append("lat double,")
				.append("mlong double,")
				.append("Auxiliary int)");
		return buff.toString();
	}

	/**
	 * 删除数据库
	 * @param context
	 * @return
	 */
	public boolean deleteDatabase(Context context) {
		return context.deleteDatabase(DATABASE_NAME);
	}


	/**
	 * 清除数据表数据
	 * @param context
	 * @return
	 */
	public void clearTableData(Context context) {
		new SSXQs(context).clearTableData();
	}

	/**
	 * 行政区域数据查询操作
	 * @param context
	 * @param ParentId
	 * @return
	 */
	public ArrayList<Region> queryDatas(Context context,int ParentId) {
		return new SSXQs(context).queryDatas(ParentId);
	}

	/**
	 * 根据id查询ParentId
	 * @param context
	 * @param ParentId
	 * @return
	 */
	public int queryParentId(Context context,int ParentId) {
		return new SSXQs(context).queryParentId(ParentId);
	}


	/**
	 * 行政区域数据查询操作_keyword
	 * @param context
	 * @return
	 */
	public ArrayList<Region2> queryDatas4Keyword(Context context, String keyword) {
		return new SSXQs(context).queryDatas4Keyword(keyword);
	}

	/**
	 * 行政区域数据查询操作
	 * @param context
	 * @return
	 */
	public Region queryRegion(Context context,int Id) {
		return new SSXQs(context).queryRegion(Id);
	}


	/**
	 * 行政区域数据查询操作
	 * @param context
	 * @return
	 */
	public Region queryRegion(Context context,String name) {
		return new SSXQs(context).queryRegion(name);
	}

	/**
	 * 经纬度查询
	 * @param context
	 * @param cityId
	 * @return
	 */
	public Region queryLatLong(Context context,int cityId) {
		return new SSXQs(context).queryRegion(cityId);
	}

	/**
	 * 行政区域数据添加操作
	 *
	 * //存储数据库
	 ContentValues values = new ContentValues();
	 values.put("Id", region.Id);
	 values.put("ParentId", region.ParentId);
	 values.put("Auxiliary", region.Auxiliary);
	 values.put("Name", region.ParentId);
	 DBHelper.getInstance(context).insertDatas(values);
	 * @param values
	 */
	public void insertDatas(ContentValues values) {
		new SSXQs(context).insertDatas(values);
	}



	/**
	 * 创建经纬度数据表
	 * @return
	 */
	private String createLocationSql() {
		MLog.i("创建经纬度数据表","创建经纬度数据表");
		StringBuffer buff = new StringBuffer();
		buff.append("CREATE TABLE if not exists ")
				.append(TABLE_LOCATION)
				.append("(clienttime long,")
				.append("Latitude double,")
				.append("msg text,")
				.append("Longitude double)");
		return buff.toString();
	}

	/**
	 * 经纬度数据查询操作
	 * @param context
	 * @return
	 */
	public ArrayList<MyLocation> queryLocationDatas(Context context) {
		return new SSXQs(context).queryLocationDatas();
	}

	/**
	 * 经纬度域数据添加操作
	 *
	 * //存储数据库
	 ContentValues values = new ContentValues();
	 values.put("Id", region.Id);
	 values.put("ParentId", region.ParentId);
	 values.put("Auxiliary", region.Auxiliary);
	 values.put("Name", region.ParentId);
	 DBHelper.getInstance(context).insertLocationDatas(values);
	 * @param values
	 */
	public void insertLocationDatas(ContentValues values) {
		new SSXQs(context).insertLocationDatas(values);
	}

}
