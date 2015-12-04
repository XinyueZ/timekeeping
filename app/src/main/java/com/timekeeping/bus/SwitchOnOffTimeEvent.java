package com.timekeeping.bus;

import com.timekeeping.data.Time;


public final class SwitchOnOffTimeEvent extends TimeEvent {
	public SwitchOnOffTimeEvent(int position, Time time ) {
		super( position, time );
	}
}
