package com.timekeeping.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Classical helper pattern on Android DB ops.
 *
 * @author Xinyue Zhao
 */
public final class DatabaseHelper extends SQLiteOpenHelper {
	/**
	 * DB name.
	 */
	public static final String DATABASE_NAME = "timekeepingDB";
	/**
	 * Init version of DB.
	 */
	private static final int DATABASE_VERSION = 3;

	/**
	 * Constructor of {@link DatabaseHelper}.
	 *
	 * @param context
	 * 		{@link android.content.Context}.
	 */
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TimeTbl.SQL_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch (oldVersion) {
		case 1:
			db.execSQL("ALTER TABLE " + TimeTbl.TABLE_NAME + " ADD COLUMN " + TimeTbl.TASK + " TEXT DEFAULT \"\"");
			db.execSQL("ALTER TABLE " + TimeTbl.TABLE_NAME + " ADD COLUMN " + TimeTbl.WEEK_DAYS + " TEXT DEFAULT \"0,1,2,3,4,5,6,\"");
			break;
		case 2:
			db.execSQL("ALTER TABLE " + TimeTbl.TABLE_NAME + " ADD COLUMN " + TimeTbl.WEEK_DAYS + " TEXT DEFAULT \"0,1,2,3,4,5,6,\"");
			break;
		}
	}
}
