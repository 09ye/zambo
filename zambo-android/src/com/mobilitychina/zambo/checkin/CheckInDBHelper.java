package com.mobilitychina.zambo.checkin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.TextUtils;

import com.mobilitychina.util.Log;
import com.mobilitychina.zambo.app.ZamboApplication;
import com.mobilitychina.zambo.checkin.CheckInOfflineManager.CheckInMsg;

/**
 * 签到历史记录存入数据库,记录每个客户的最近签到时间和当天签到次数 custId<br>
 * datelineId<br>
 * longitude<br>
 * latitude<br>
 * accuracy<br>
 * datetime :yyyy-MM-dd HH:mm:ss
 */
public class CheckInDBHelper {

	private static final String LOG_TAG = CheckInDBHelper.class.getSimpleName();

	private static CheckInDBHelper _instance;
	protected SQLiteDatabase mDb;
	private String checkinTable = "checkin";

	public static CheckInDBHelper instance() {
		if (_instance == null) {
			_instance = new CheckInDBHelper(ZamboApplication.getInstance());
		}
		return _instance;
	}

	private CheckInDBHelper(Context c) {
		try {
			mDb = createDataBase(c);
		} catch (IOException e) {
			Log.e(LOG_TAG, "createDataBase failed", e);
		}
	}

	private SQLiteDatabase createDataBase(Context c) throws IOException {
		File mContentRoot = null;
		if (isSDCardValid()) {
			mContentRoot = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
					+ "Android" + File.separator + "database" + File.separator + c.getPackageName() + File.separator
					+ "db" + File.separator);
			if (!mContentRoot.exists()) {
				mContentRoot.mkdirs();
			}
		} else {
			mContentRoot = c.getDir("upload", Context.MODE_PRIVATE);
		}
		File path = new File(mContentRoot, "checkinrecord.db");

		SQLiteDatabase db = null;
		try {
			db = SQLiteDatabase.openOrCreateDatabase(path, null);
		} catch (Exception e) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS ").append(checkinTable).append(" (");
		sb.append("id INTEGER PRIMARY KEY AUTOINCREMENT, ");
		sb.append("custId varchar(10), ");
		sb.append("datelineId varchar(10), ");
		sb.append("longitude varchar(10), ");
		sb.append("latitude varchar(10), ");
		sb.append("accuracy varchar(10), ");
		sb.append("datetime varchar(20), ");
		sb.append("num INTEGER);");

		db.execSQL(sb.toString());
		return db;
	}

	public synchronized void close() {
		if (mDb != null) {
			mDb.close();
		}
	}

	public synchronized ArrayList<CheckInMsg> query() {
		Cursor c = mDb.query(checkinTable, null, null, null, null, null, "id DESC");
		ArrayList<CheckInMsg> list = new ArrayList<CheckInMsg>();

		while (c.moveToNext()) {
			CheckInMsg checkInMsg = new CheckInMsg(c.getString(1), c.getString(2), c.getString(3), c.getString(4),
					c.getString(5), c.getString(6));
			checkInMsg.setCheckInNumPerDay(c.getInt(7));
			list.add(checkInMsg);
		}
		c.close();
		return list;
	}

	public synchronized CheckInMsg queryByCustId(String custId) {
		// Cursor c = mDb.query(checkinTable, null, "custId=?", new String[] {
		// custId }, null, null, "id DESC");
		//
		// if (c.moveToFirst()) {
		// CheckInMsg checkInMsg = new CheckInMsg(c.getString(1),
		// c.getString(2), c.getString(3), c.getString(4),
		// c.getString(5), c.getString(6));
		// checkInMsg.setCheckInNumPerDay(c.getInt(7));
		// c.close();
		// return checkInMsg;
		// }

		ArrayList<CheckInMsg> list = query();
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public synchronized boolean insertOrUpdate(CheckInMsg checkInMsg) {
		if (checkInMsg == null) {
			return false;
		}
		checkInMsg.setCheckInNumPerDay(1);
		// 检查是否已存在
		ArrayList<CheckInMsg> list = query();
		if (list.size() > 0) {
			CheckInMsg lastCheckInMsg = list.get(0);
			if (isSameDay(checkInMsg.datetime(), lastCheckInMsg.datetime())) {
				checkInMsg.setCheckInNumPerDay(lastCheckInMsg.checkInNumPerDay() + 1);
			}
		}
		clear();
		
		ContentValues cv = new ContentValues();
		cv.put("custId", checkInMsg.custId());
		cv.put("datelineId", checkInMsg.datelineId());
		cv.put("longitude", checkInMsg.longitude());
		cv.put("latitude", checkInMsg.latitude());
		cv.put("accuracy", checkInMsg.accuracy());
		cv.put("datetime", checkInMsg.datetime());
		cv.put("num", checkInMsg.checkInNumPerDay());
		return mDb.insert(checkinTable, null, cv) != -1;
	}

	private synchronized int update(CheckInMsg checkInMsg, int num) {
		ContentValues cv = new ContentValues();
		cv.put("datelineId", checkInMsg.datelineId());
		cv.put("longitude", checkInMsg.longitude());
		cv.put("latitude", checkInMsg.latitude());
		cv.put("accuracy", checkInMsg.accuracy());
		cv.put("datetime", checkInMsg.datetime());
		cv.put("num", num);
		String[] args = { checkInMsg.custId() };
		return mDb.update(checkinTable, cv, "custId=?", args);
	}

	public synchronized int deleteByCustId(String custId) {
		return mDb.delete(checkinTable, "custId=?", new String[] { custId });
	}

	public synchronized void clear() {
		mDb.execSQL("DELETE FROM " + checkinTable);
	}

	private boolean isSDCardValid() {
//		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		return false;
	}

	private static boolean isSameDay(String date1, String date2) {
		if (TextUtils.isEmpty(date1) || TextUtils.isEmpty(date2)) {
			return false;
		}
		return date1.substring(0, 10).equals(date2.substring(0, 10));
	}
}
