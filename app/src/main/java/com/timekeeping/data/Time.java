package com.timekeeping.data;

/**
 * Structure of a time instance.
 */
public final class Time implements IActionModeSupport {
	/**
	 * Id of the item, might be retrieved from database.
	 */
	private long mId;
	/**
	 * Hour.
	 */
	private int mHour;
	/**
	 * Minute.
	 */
	private int mMinute;
	/**
	 * A timestamps for the item when it was operated by database.
	 */
	private long mEditTime;
	/**
	 * {@code true=on}, {@code false=off}.
	 */
	private boolean mOnOff;
	/**
	 * Whether item is checked to delete or not.
	 */
	private boolean mCheck;

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
	public Time(long id, int hour, int minute, long editTime, boolean onOff) {
		mId = id;
		mHour = hour;
		mMinute = minute;
		mEditTime = editTime;
		mOnOff = onOff;
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
	 * Gets hour.
	 *
	 * @return the hour
	 */
	public int getHour() {
		return mHour;
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
	 * Sets id.
	 *
	 * @param id
	 * 		the id
	 */
	public void setId(long id) {
		mId = id;
	}

	/**
	 * Sets hour.
	 *
	 * @param hour
	 * 		the hour
	 */
	public void setHour(int hour) {
		mHour = hour;
	}

	/**
	 * Sets minute.
	 *
	 * @param minute
	 * 		the minute
	 */
	public void setMinute(int minute) {
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
	public void setEditTime(long editTime) {
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
	public void setOnOff(boolean onOff) {
		mOnOff = onOff;
	}

	@Override
	public void setCheck(boolean check) {
		mCheck = check;
	}

	@Override
	public boolean isChecked() {
		return mCheck;
	}
}
