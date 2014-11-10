package com.timekeeping.bus;

import com.timekeeping.data.Time;


public final class EditTimeEvent extends  TimeEvent {
	/**
	 * Constructor of {@link com.timekeeping.bus.TimeEvent}.
	 *
	 * @param time
	 * 		A {@link com.timekeeping.data.Time} object.
	 */
	protected EditTimeEvent(Time time) {
		super(time);
	}
}
