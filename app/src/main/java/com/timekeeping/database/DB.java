package com.timekeeping.database;


import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.timekeeping.data.Time;


/**
 * Defines methods that operate on database.
 * <p/>
 * <b>Singleton pattern.</b>
 * <p/>
 * <p/>
 *
 * @author Xinyue Zhao
 */
public final class DB {
	/**
	 * {@link android.content.Context}.
	 */
	private        Context        mContext;
	/**
	 * Impl singleton pattern.
	 */
	private static DB             sInstance;
	/**
	 * Helper class that create, delete, update tables of database.
	 */
	private        DatabaseHelper mDatabaseHelper;
	/**
	 * The database object.
	 */
	private        SQLiteDatabase mDB;

	/**
	 * Constructor of {@link DB}. Impl singleton pattern so that it is private.
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 */
	private DB( Context cxt ) {
		mContext = cxt;
	}

	/**
	 * Get instance of  {@link  DB} singleton.
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 *
	 * @return The {@link DB} singleton.
	 */
	public static DB getInstance( Context cxt ) {
		if( sInstance == null ) {
			sInstance = new DB( cxt );
		}
		return sInstance;
	}

	/**
	 * Open database.
	 */
	public synchronized void open() {
		mDatabaseHelper = new DatabaseHelper( mContext );
		mDB = mDatabaseHelper.getWritableDatabase();
	}

	/**
	 * Close database.
	 */
	public synchronized void close() {
		mDatabaseHelper.close();
	}

	/**
	 * Add a time.
	 *
	 * @param item
	 * 		{@link  com.timekeeping.data.Time} to insert.
	 *
	 * @return {@code true} if insert is success.
	 */
	public synchronized boolean addTime( Time item ) {
		if( mDB == null || !mDB.isOpen() ) {
			open();
		}
		boolean success = false;
		try {
			long          rowId = -1;
			ContentValues v     = new ContentValues();
			v.put( TimeTbl.HOUR, item.getHour() );
			v.put( TimeTbl.MINUTE, item.getMinute() );
			v.put( TimeTbl.ONOFF, item.isOnOff() ? 1 : 0 );
			v.put( TimeTbl.TASK, item.getTask() );
			v.put( TimeTbl.WEEK_DAYS, item.getWeekDays() );
			v.put( TimeTbl.EDIT_TIME, System.currentTimeMillis() );
			rowId = mDB.insert( TimeTbl.TABLE_NAME, null, v );
			item.setId( rowId );
			success = rowId != -1;
		} finally {
			close();
		}
		return success;
	}


	/**
	 * Update a time on time-table.
	 *
	 * @param item
	 * 		{@link com.timekeeping.data.Time} to update.
	 *
	 * @return {@code true} if insert is success.
	 */
	public synchronized boolean updateTime( Time item ) {
		if( mDB == null || !mDB.isOpen() ) {
			open();
		}
		boolean success = false;
		try {
			long          rowId = -1;
			ContentValues v     = new ContentValues();
			v.put( TimeTbl.HOUR, item.getHour() );
			v.put( TimeTbl.MINUTE, item.getMinute() );
			v.put( TimeTbl.ONOFF, item.isOnOff() ? 1 : 0 );
			v.put( TimeTbl.TASK, item.getTask() );
			v.put( TimeTbl.WEEK_DAYS, item.getWeekDays() );
			v.put( TimeTbl.EDIT_TIME, System.currentTimeMillis() );
			String[] args = new String[] { item.getId() + "" };
			rowId = mDB.update( TimeTbl.TABLE_NAME, v, TimeTbl.ID + " = ?", args );
			success = rowId != -1;
		} finally {
			close();
		}
		return success;
	}


	/**
	 * Remove one time from DB.
	 *
	 * @param item
	 * 		The time to remove.
	 *
	 * @return The count of rows remain in DB after removed item.
	 * <p/>
	 * Return -1 if there's error when removed data.
	 */
	public synchronized int removeTime( Time item ) {
		if( mDB == null || !mDB.isOpen() ) {
			open();
		}
		int     rowsRemain = -1;
		boolean success;
		try {
			long     rowId;
			String   whereClause = TimeTbl.ID + "=?";
			String[] whereArgs   = new String[] { String.valueOf( item.getId() ) };
			rowId = mDB.delete( TimeTbl.TABLE_NAME, whereClause, whereArgs );
			success = rowId > 0;
			if( success ) {
				Cursor c = mDB.query( TimeTbl.TABLE_NAME, new String[] { TimeTbl.ID }, null, null, null, null, null );
				rowsRemain = c.getCount();
			} else {
				rowsRemain = -1;
			}
		} finally {
			close();
		}
		return rowsRemain;
	}


	/**
	 * Sort direction.
	 */
	public enum Sort {
		DESC( "DESC" ), ASC( "ASC" );
		/**
		 * Text represents this enum.
		 */
		private String nm;

		/**
		 * Init {@link com.timekeeping.database.DB.Sort}.
		 *
		 * @param nm
		 * 		{@code DESC or ASC}.
		 */
		Sort( String nm ) {
			this.nm = nm;
		}

		@Override
		public String toString() {
			return nm;
		}
	}

	/**
	 * Returns all {@link com.timekeeping.data.Time}s from DB order by the time of edition.
	 *
	 * @param sort
	 * 		"DESC" or "ASC".
	 *
	 * @return All {@link  }s from DB order by the time of edition.
	 */
	public synchronized List<Time> getTimes( Sort sort ) {
		if( mDB == null || !mDB.isOpen() ) {
			open();
		}
		Cursor c = mDB.query( TimeTbl.TABLE_NAME, null, null, null, null, null, TimeTbl.EDIT_TIME + " " + sort.toString() );
		Time       item = null;
		List<Time> list = new LinkedList<Time>();
		try {

			while( c.moveToNext() ) {
				item = new Time( c.getLong( c.getColumnIndex( TimeTbl.ID ) ), c.getInt( c.getColumnIndex( TimeTbl.HOUR ) ),
								 c.getInt( c.getColumnIndex( TimeTbl.MINUTE ) ), c.getLong( c.getColumnIndex( TimeTbl.EDIT_TIME ) ),
								 c.getInt( c.getColumnIndex( TimeTbl.ONOFF ) ) == 1, c.getString( c.getColumnIndex( TimeTbl.TASK ) ),
								 c.getString( c.getColumnIndex( TimeTbl.WEEK_DAYS ) )
				);
				list.add( item );
			}
		} finally {
			if( c != null ) {
				c.close();
			}
			close();
			return list;
		}
	}

	/**
	 * To check whether the {@link com.timekeeping.data.Time} with hour and minute is in DB or not. To prevent from storing duplicated item.
	 *
	 * @param item
	 * 		The item to check.
	 *
	 * @return {@link true} if find.
	 */
	public boolean findTime( Time item ) {
		if( mDB == null || !mDB.isOpen() ) {
			open();
		}
		boolean success;
		try {
			String   whereClause = TimeTbl.HOUR + "=? AND " + TimeTbl.MINUTE + "=?";
			String[] whereArgs   = new String[] { String.valueOf( item.getHour() ) , String.valueOf( item.getMinute() ) };
			Cursor c = mDB.query( TimeTbl.TABLE_NAME, new String[] { TimeTbl.ID }, whereClause, whereArgs, null, null, null );
			success = c.getCount() >= 1;
		} finally {
			close();
		}
		return success;
	}
}
