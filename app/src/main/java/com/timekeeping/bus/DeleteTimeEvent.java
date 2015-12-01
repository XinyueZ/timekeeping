package com.timekeeping.bus;

import com.timekeeping.data.Time;

public final class DeleteTimeEvent extends TimeEvent {
	/**
	 * Constructor of {@link com.timekeeping.bus.TimeEvent}.
	 *
	 * @param time
	 * 		A {@link com.timekeeping.data.Time} object.
	 */
	public DeleteTimeEvent( Time time ) {
		super( time );
	}
}
