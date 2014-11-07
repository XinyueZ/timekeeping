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
	 * Instantiates a new Time.
	 *
	 * @param id
	 * 		Id of the item, might be retrieved from database.
	 * @param hour
	 * 		the hour
	 * @param minute
	 * 		the minute
	 */
	public Time(long id, int hour, int minute) {
		mId = id;
		mHour = hour;
		mMinute = minute;
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
	 * @param id the id
	 */
	public void setId(long id) {
		mId = id;
	}
}
