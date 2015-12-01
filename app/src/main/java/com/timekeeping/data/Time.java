package com.timekeeping.data;

import java.io.Serializable;

/**
 * Structure of a time instance.
 */
public final class Time implements Serializable {
	/**
	 * Id of the item, might be retrieved from database.
	 */
	private long    mId;
	/**
	 * Hour.
	 */
	private int     mHour;
	/**
	 * Minute.
	 */
	private int     mMinute;
	/**
	 * A timestamps for the item when it was operated by database.
	 */
	private long    mEditTime;
	/**
	 * {@code true=on}, {@code false=off}.
	 */
	private boolean mOnOff;
	/**
	 * Task for this time point.
	 */
	private String  mTask;
	/**
	 * Selected week-days to fire event.
	 */
	private String mWeekDays = "0,1,2,3,4,5,6,";

	/**
	 * Instantiates a new Time.
	 *
	 * @param id
	 * 		Id of the item, might be retrieved from database.
	 * @param hour
	 * 		the hour
	 * @param minute
	 * 		the minute
	 * @param editTime
	 * 		A timestamps for the item when it was operated by database.
	 * @param onOff
	 * 		Status of this {@link Time}, {@code true=on}, {@code false=off}.
	 */
	public Time( long id, int hour, int minute, long editTime, boolean onOff ) {
		mId = id;
		mHour = hour;
		mMinute = minute;
		mEditTime = editTime;
		mOnOff = onOff;
	}

	public Time( long id, int hour, int minute, long editTime, boolean onOff, String task ) {
		this( id, hour, minute, editTime, onOff );
		mTask = task;
	}


	public Time( long id, int hour, int minute, long editTime, boolean onOff, String task, String weekDays ) {
		this( id, hour, minute, editTime, onOff, task );
		mWeekDays = weekDays;
	}

	/**
	 * Gets id.
	 *
	 * @return the id
	 */
	public long getId() {
		return mId;
	}
	/**
	 * Sets id.
	 *
	 * @param id
	 * 		the id
	 */
	public void setId( long id ) {
		mId = id;
	}
	/**
	 * Gets hour.
	 *
	 * @return the hour
	 */
	public int getHour() {
		return mHour;
	}
	/**
	 * Sets hour.
	 *
	 * @param hour
	 * 		the hour
	 */
	public void setHour( int hour ) {
		mHour = hour;
	}
	/**
	 * Gets minute.
	 *
	 * @return the minute
	 */
	public int getMinute() {
		return mMinute;
	}
	/**
	 * Sets minute.
	 *
	 * @param minute
	 * 		the minute
	 */
	public void setMinute( int minute ) {
		mMinute = minute;
	}

	/**
	 * Gets edit time. A timestamps for the item when it was operated by database.
	 *
	 * @return the edit time
	 */
	public long getEditTime() {
		return mEditTime;
	}

	/**
	 * Sets edit time. A timestamps for the item when it was operated by database.
	 *
	 * @param editTime
	 * 		the edit time
	 */
	public void setEditTime( long editTime ) {
		mEditTime = editTime;
	}

	/**
	 * Is on or off.
	 * <p/>
	 * {@code true=on}, {@code false=off}.
	 *
	 * @return the boolean
	 */
	public boolean isOnOff() {
		return mOnOff;
	}

	/**
	 * Sets on or off.
	 * <p/>
	 * {@code true=on}, {@code false=off}.
	 *
	 * @param onOff
	 * 		the on off
	 */
	public void setOnOff( boolean onOff ) {
		mOnOff = onOff;
	}

	/**
	 * Task for this time point.
	 */
	public String getTask() {
		return mTask;
	}

	/**
	 * Set task for this time point.
	 */
	public void setTask( String task ) {
		mTask = task;
	}


	public String getWeekDays() {
		return mWeekDays;
	}

	public void setWeekDays( String weekDays ) {
		mWeekDays = weekDays;
	}
}
