package com.timekeeping.data;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Structure of a time instance.
 */
public class Time extends RealmObject implements Serializable {
	/**
	 * Id of the item, might be retrieved from database.
	 */
	@PrimaryKey
	private long    id;
	/**
	 * Hour.
	 */
	private int     hour;
	/**
	 * Minute.
	 */
	private int     minute;
	/**
	 * A timestamps for the item when it was operated by database.
	 */
	private long    editTime;
	/**
	 * {@code true=on}, {@code false=off}.
	 */
	private boolean onOff;
	/**
	 * Task for this time point.
	 */
	private String  task;
	/**
	 * Selected week-days to fire event.
	 */
	private String weekDays = "0,1,2,3,4,5,6,";


	public Time() {
	}

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
		this.id = id;
		this.hour = hour;
		this.minute = minute;
		this.editTime = editTime;
		this.onOff = onOff;
	}

	public Time( long id, int hour, int minute, long editTime, boolean onOff, String task ) {
		this(
				id,
				hour,
				minute,
				editTime,
				onOff
		);
		this.task = task;
	}


	public Time( long id, int hour, int minute, long editTime, boolean onOff, String task, String weekDays ) {
		this(
				id,
				hour,
				minute,
				editTime,
				onOff,
				task
		);
		this.weekDays = weekDays;
	}

	/**
	 * Gets id.
	 *
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets id.
	 *
	 * @param id
	 * 		the id
	 */
	public void setId( long id ) {
		this.id = id;
	}

	/**
	 * Gets hour.
	 *
	 * @return the hour
	 */
	public int getHour() {
		return hour;
	}

	/**
	 * Sets hour.
	 *
	 * @param hour
	 * 		the hour
	 */
	public void setHour( int hour ) {
		this.hour = hour;
	}

	/**
	 * Gets minute.
	 *
	 * @return the minute
	 */
	public int getMinute() {
		return minute;
	}

	/**
	 * Sets minute.
	 *
	 * @param minute
	 * 		the minute
	 */
	public void setMinute( int minute ) {
		this.minute = minute;
	}

	/**
	 * Gets edit time. A timestamps for the item when it was operated by database.
	 *
	 * @return the edit time
	 */
	public long getEditTime() {
		return editTime;
	}

	/**
	 * Sets edit time. A timestamps for the item when it was operated by database.
	 *
	 * @param editTime
	 * 		the edit time
	 */
	public void setEditTime( long editTime ) {
		this.editTime = editTime;
	}

	/**
	 * Is on or off.
	 * <p/>
	 * {@code true=on}, {@code false=off}.
	 *
	 * @return the boolean
	 */
	public boolean isOnOff() {
		return onOff;
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
		this.onOff = onOff;
	}

	/**
	 * Task for this time point.
	 */
	public String getTask() {
		return task;
	}

	/**
	 * Set task for this time point.
	 */
	public void setTask( String task ) {
		this.task = task;
	}


	public String getWeekDays() {
		return weekDays;
	}

	public void setWeekDays( String weekDays ) {
		this.weekDays = weekDays;
	}
}
