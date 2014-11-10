package com.timekeeping.bus;

import com.timekeeping.data.Time;

/**
 * The abstract class for all events that need a {@link com.timekeeping.data.Time} object.
 *
 * @author Xinyue Zhao
 */
public abstract class TimeEvent {
	/**
	 * A {@link com.timekeeping.data.Time} object.
	 */
	private Time mTime;

	/**
	 * Constructor of {@link com.timekeeping.bus.TimeEvent}.
	 *
	 * @param time
	 * 		A {@link com.timekeeping.data.Time} object.
	 */
	protected TimeEvent(Time time) {
		mTime = time;
	}

	/**
	 * Get the {@link com.timekeeping.data.Time} object.
	 *
	 * @return The time.
	 */
	public Time getTime() {
		return mTime;
	}
}
