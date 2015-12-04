package com.timekeeping.bus;

import com.timekeeping.data.Time;

public final class DeleteTimeEvent extends TimeEvent {

	public DeleteTimeEvent( int position, Time time ) {
		super( position, time );
	}
}
