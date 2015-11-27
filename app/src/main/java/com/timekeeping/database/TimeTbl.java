package com.timekeeping.database;

/**
 * "Time"-table structure.
 *
 * @author Xinyue Zhao
 */
interface TimeTbl {
	static final String ID = "_id";
	static final String HOUR = "_hour";
	static final String MINUTE = "_minute";
	static final String ONOFF = "_on_off";
	static final String TASK = "_task";
	static final String WEEK_DAYS = "_week_days";
	static final String EDIT_TIME = "_edited_time";
	static final String TABLE_NAME = "time";

	//We use rowId as key for each row.
	//See. http://www.sqlite.org/autoinc.html
	/**
	 * Init new table since {@link DatabaseHelper#DATABASE_VERSION} = {@code 1}.
	 */
	static final String SQL_CREATE =
			"CREATE TABLE " + TABLE_NAME + " (" +
					ID + " INTEGER PRIMARY KEY, " +
					HOUR + " INTEGER, " +
					MINUTE + " INTEGER, " +
					ONOFF + " INTEGER, " +
					TASK + " TEXT  DEFAULT \"\", " +
					WEEK_DAYS + " TEXT  DEFAULT \"\", " +
					EDIT_TIME + " INTEGER" +
					");";
}
