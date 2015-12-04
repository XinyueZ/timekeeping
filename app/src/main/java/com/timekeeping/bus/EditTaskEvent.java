package com.timekeeping.bus;


import com.timekeeping.data.Time;

public final class EditTaskEvent extends TimeEvent {
	public EditTaskEvent( int position, Time time ) {
		super(position, time);
	}
}
