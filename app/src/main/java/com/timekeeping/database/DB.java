package com.timekeeping.database;


import java.util.LinkedList;
import java.util.List;

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
		Cursor     c    = mDB.query(
				TimeTbl.TABLE_NAME,
				null,
				null,
				null,
				null,
				null,
				TimeTbl.EDIT_TIME + " " + sort.toString()
		);
		Time       item = null;
		List<Time> list = new LinkedList<Time>();
		try {

			while( c.moveToNext() ) {
				item = new Time( c.getLong( c.getColumnIndex( TimeTbl.ID ) ),
								 c.getInt( c.getColumnIndex( TimeTbl.HOUR ) ),
								 c.getInt( c.getColumnIndex( TimeTbl.MINUTE ) ),
								 c.getLong( c.getColumnIndex( TimeTbl.EDIT_TIME ) ),
								 c.getInt( c.getColumnIndex( TimeTbl.ONOFF ) ) == 1,
								 c.getString( c.getColumnIndex( TimeTbl.TASK ) ),
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


}
