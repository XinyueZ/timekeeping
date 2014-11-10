package com.timekeeping.bus;

import com.timekeeping.data.Time;


public final class SwitchOnOffTimeEvent extends  TimeEvent{
	/**
	 * Constructor of {@link com.timekeeping.bus.TimeEvent}.
	 *
	 * @param time
	 * 		A {@link com.timekeeping.data.Time} object.
	 */
	protected SwitchOnOffTimeEvent(Time time) {
		super(time);
	}
}
