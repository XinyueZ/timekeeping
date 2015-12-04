package com.timekeeping.bus;

import com.timekeeping.data.Time;

/**
 * The abstract class for all events that need a {@link com.timekeeping.data.Time} object.
 *
 * @author Xinyue Zhao
 */
public abstract class TimeEvent {
	/**
	 * The position of defined {@link Time}.
	 */
	private int mPosition;
	/**
	 * A {@link com.timekeeping.data.Time} object.
	 */
	private Time mTime;

	/**
	 * Constructor of {@link com.timekeeping.bus.TimeEvent}.
	 *
	 * @param  position The position of defined {@link Time}.
	 * @param time
	 * 		A {@link com.timekeeping.data.Time} object.
	 */
	protected TimeEvent( int position, Time time ) {
		mPosition = position;
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

	/**
	 *
	 * @return The position of defined {@link Time}.
	 */
	public int getPosition() {
		return mPosition;
	}
}
