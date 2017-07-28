package com.hongbao5656.db;

import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 多线程下安全访问数据库
 * href="http://blog.csdn.net/sky_monkey/article/details/23820021">
 */
public class DatabaseManager {

	private AtomicInteger mOpenCounter = new AtomicInteger();

	private static DatabaseManager instance;
	private static SQLiteOpenHelper mDatabaseHelper;
	private SQLiteDatabase mDatabase;

	public static synchronized void initializeInstance(SQLiteOpenHelper helper) {
		if (instance == null) {
			instance = new DatabaseManager();
			mDatabaseHelper = helper;
		}
	}

	public static synchronized DatabaseManager getInstance(Context context) {
		if (instance == null) {
			// throw new IllegalStateException(
			// DatabaseManager.class.getSimpleName()
			// +
			// " is not initialized, call initializeInstance(..) method first.");
			DBHelper.getInstance(context);
		}

		return instance;
	}

	public synchronized SQLiteDatabase openDatabase() {
		if (mOpenCounter.incrementAndGet() == 1) {
			// Opening new database
			mDatabase = mDatabaseHelper.getReadableDatabase();
		}
		return mDatabase;
	}

	public synchronized void closeDatabase() {
		if (mOpenCounter.decrementAndGet() == 0) {
			// Closing database
			mDatabase.close();

		}
	}
}