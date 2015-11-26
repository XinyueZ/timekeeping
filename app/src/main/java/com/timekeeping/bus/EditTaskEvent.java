package com.timekeeping.bus;


import com.timekeeping.data.Time;

public final class EditTaskEvent {
	private Time mTime;


	public EditTaskEvent(Time time) {
		mTime = time;
	}


	public Time getTime() {
		return mTime;
	}
}
