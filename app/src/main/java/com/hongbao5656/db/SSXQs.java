package com.hongbao5656.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hongbao5656.entity.MyLocation;
import com.hongbao5656.entity.Region;
import com.hongbao5656.entity.Region2;

import java.util.ArrayList;

/**
 * 省市县区数据库处理了类
 * @author dm
 *
 */
public class SSXQs {
	private DatabaseManager dm;
	private DBHelper dbh;
	private SQLiteDatabase db;

	public SSXQs(Context context) {
		this.dm = DatabaseManager.getInstance(context);
		this.dbh = DBHelper.getInstance(context);
		db = dbh.getWritableDatabase();
	}

	/**
	 * 插入行政区域数据
	 * @return
	 */
	public void insertDatas(ContentValues values) {
		try {
			db.beginTransaction();
			db.insert(DBHelper.TABLE_REGION, null, values);
			db.setTransactionSuccessful();// 标记事务成功. c 
//			App.flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();// 结束事务,如果没有标记成功,则数据库回滚.
			dm.closeDatabase(); // correct way
		}
	}



	/**
	 * 插入经纬度数据
	 * @return
	 */
	public void insertLocationDatas(ContentValues values) {
		try {
			db.beginTransaction();
			db.insert(DBHelper.TABLE_LOCATION, null, values);
			db.setTransactionSuccessful();// 标记事务成功. c
//			App.flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();// 结束事务,如果没有标记成功,则数据库回滚.
			dm.closeDatabase(); // correct way
		}
	}

	/**
	 * 清空数据表数据
	 * @return
	 */
	public void clearTableData() {
		try {
			db.beginTransaction();
			db.execSQL("delete from "+DBHelper.TABLE_LOCATION);
			db.setTransactionSuccessful();// 标记事务成功. c
//			App.flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();// 结束事务,如果没有标记成功,则数据库回滚.
			dm.closeDatabase(); // correct way
		}
	}


	/**
	 * 查询所有省/市/县区
	 * @return
	 */
	public ArrayList<Region> queryDatas(int ParentId) {
		String sql = "SELECT * FROM " + DBHelper.TABLE_REGION
				+ " where ParentId=" + ParentId + " order by Id asc";
		ArrayList<Region> regions = new ArrayList<Region>();
		try {
			db.beginTransaction();// 开始事务
			// 执行数据库
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					Region region = new Region();
					region.Id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Id")));
					region.ParentId = Integer.parseInt(cursor.getString(cursor.getColumnIndex("ParentId")));
					region.Auxiliary = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Auxiliary")));
					region.Name = cursor.getString(cursor.getColumnIndex("Name"));
					region.lat = Double.parseDouble(cursor.getString(cursor.getColumnIndex("lat")));
					region.mlong = Double.parseDouble(cursor.getString(cursor.getColumnIndex("mlong")));
					regions.add(region);
				}
				cursor.close();
			}
			db.setTransactionSuccessful();// 标记事务成功.
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();// 结束事务,如果没有标记成功,则数据库回滚.
			// db.close(); Don't close it directly!
		}
		return regions;
	}

	/**
	 * 查询所有经纬度数据
	 * @return
	 */
	public ArrayList<MyLocation> queryLocationDatas() {
		String sql = "SELECT * FROM " + DBHelper.TABLE_LOCATION;
		ArrayList<MyLocation> myLocations = new ArrayList<MyLocation>();
		try {
			db.beginTransaction();// 开始事务
			// 执行数据库
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					MyLocation myLocation = new MyLocation();
					myLocation.clienttime = Long.parseLong(cursor.getString(cursor.getColumnIndex("clienttime")));
					myLocation.latitude = Float.parseFloat(cursor.getString(cursor.getColumnIndex("Latitude")));
					myLocation.longitude = Float.parseFloat(cursor.getString(cursor.getColumnIndex("Longitude")));
					myLocation.msg = cursor.getString(cursor.getColumnIndex("msg"));
					myLocations.add(myLocation);
				}
				cursor.close();
			}
			db.setTransactionSuccessful();// 标记事务成功.
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();// 结束事务,如果没有标记成功,则数据库回滚.
			// db.close(); Don't close it directly!
		}
		return myLocations;
	}




	/**
	 * 查询所有省/市/县区_keyword
	 * SELECT * FROM TABLE_REGION where Name like '%石%' order by Id asc
	 * @return
	 */
	public ArrayList<Region2> queryDatas4Keyword(String keyword) {
		String sql = "SELECT * FROM " + DBHelper.TABLE_REGION
				+ " where Name like " + keyword + "order by Id asc";
		ArrayList<Region2> regions = new ArrayList<Region2>();
		try {
			db.beginTransaction();// 开始事务
			// 执行数据库
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					Region2 region = new Region2();
					region.Id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Id")));
					region.ParentId = Integer.parseInt(cursor.getString(cursor.getColumnIndex("ParentId")));
					region.Auxiliary = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Auxiliary")));
					region.lat = Double.parseDouble(cursor.getString(cursor.getColumnIndex("lat")));
					region.mlong = Double.parseDouble(cursor.getString(cursor.getColumnIndex("mlong")));
					region.Name = cursor.getString(cursor.getColumnIndex("Name"));
					if (region.ParentId==0){//一级
						region.ParentNmae = "";
						region.GrandpaNmae = "";
					}else if(region.Auxiliary==2&&region.ParentId!=0){//二级
						Region pr = queryRegion(region.ParentId);
						region.ParentNmae = pr.Name;
						region.ParentId = pr.Id;
						region.GrandpaNmae = "";
					}else if(region.Auxiliary==3){//三级
						Region cr = queryRegion(region.ParentId);
						region.ParentNmae = cr.Name;
						region.ParentId = cr.Id;

						Region gr = queryRegion(cr.ParentId);
						if (gr.Id==0){
							region.GrandpaNmae = "";
							region.GrandpaId = gr.Id;
						}else{
							region.GrandpaNmae = gr.Name;
							region.GrandpaId = gr.Id;
						}
					}

					regions.add(region);
				}
				cursor.close();
			}
			db.setTransactionSuccessful();// 标记事务成功.
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();// 结束事务,如果没有标记成功,则数据库回滚.
			// db.close(); Don't close it directly!
		}
		return regions;
	}




	public Region queryRegion(int id) {
		String sql = "SELECT * FROM " + DBHelper.TABLE_REGION
				+ " where  Id=" + id;
		Region region = new Region();
		try {
			db.beginTransaction();// 开始事务
			// 执行数据库
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					region.Id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Id")));
					region.ParentId = Integer.parseInt(cursor.getString(cursor.getColumnIndex("ParentId")));
					region.Auxiliary = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Auxiliary")));
					region.Name = cursor.getString(cursor.getColumnIndex("Name"));
					region.lat = Double.parseDouble(cursor.getString(cursor.getColumnIndex("lat")));
					region.mlong = Double.parseDouble(cursor.getString(cursor.getColumnIndex("mlong")));
				}
				cursor.close();
			}
			db.setTransactionSuccessful();// 标记事务成功.
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();// 结束事务,如果没有标记成功,则数据库回滚.
			// db.close(); Don't close it directly!
		}
		return region;
	}


	public Region queryRegion(String name) {
		String sql = "SELECT * FROM " + DBHelper.TABLE_REGION
//				+ " where  Name=" +"\""+"台州"+"\"";
				+ " where  Name=" +"\""+name+"\"";
		Region region = new Region();
		try {
			db.beginTransaction();// 开始事务
			// 执行数据库
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					region.Id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Id")));
					region.ParentId = Integer.parseInt(cursor.getString(cursor.getColumnIndex("ParentId")));
					region.Auxiliary = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Auxiliary")));
					region.Name = cursor.getString(cursor.getColumnIndex("Name"));
					region.lat = Double.parseDouble(cursor.getString(cursor.getColumnIndex("lat")));
					region.mlong = Double.parseDouble(cursor.getString(cursor.getColumnIndex("mlong")));
				}
				cursor.close();
			}
			db.setTransactionSuccessful();// 标记事务成功.
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();// 结束事务,如果没有标记成功,则数据库回滚.
			// db.close(); Don't close it directly!
		}
		return region;
	}

	public int queryParentId(int id) {
		String sql = "SELECT * FROM " + DBHelper.TABLE_REGION
				+ " where  Id=" + id;
		Region region = new Region();
		try {
			db.beginTransaction();// 开始事务
			// 执行数据库
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					region.Id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Id")));
					region.ParentId = Integer.parseInt(cursor.getString(cursor.getColumnIndex("ParentId")));
					region.Auxiliary = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Auxiliary")));
					region.Name = cursor.getString(cursor.getColumnIndex("Name"));
					region.lat = Double.parseDouble(cursor.getString(cursor.getColumnIndex("lat")));
					region.mlong = Double.parseDouble(cursor.getString(cursor.getColumnIndex("mlong")));
				}
				cursor.close();
			}
			db.setTransactionSuccessful();// 标记事务成功.
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();// 结束事务,如果没有标记成功,则数据库回滚.
			// db.close(); Don't close it directly!
		}
		return region.ParentId;
	}




}
