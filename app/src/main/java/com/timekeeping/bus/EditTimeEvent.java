package com.timekeeping.bus;

import com.timekeeping.data.Time;


public final class EditTimeEvent extends TimeEvent {
	public EditTimeEvent(int position, Time time ) {
		super( position, time );
	}
}
