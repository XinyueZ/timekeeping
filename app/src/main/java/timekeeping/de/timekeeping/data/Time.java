package timekeeping.de.timekeeping.data;

/**
 * Structure of a time instance.
 */
public final class Time {
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
	 */
	public Time(long id, int hour, int minute, long editTime) {
		mId = id;
		mHour = hour;
		mMinute = minute;
		mEditTime = editTime;
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
	 * @param editTime the edit time
	 */
	public void setEditTime(long editTime) {
		mEditTime = editTime;
	}
}
