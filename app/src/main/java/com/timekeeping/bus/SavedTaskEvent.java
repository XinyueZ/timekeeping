package com.timekeeping.bus;


import com.timekeeping.data.Time;

public final class SavedTaskEvent {
	private Time mTime;

	public SavedTaskEvent(Time time) {
		mTime = time;

	}


	public Time getTime() {
		return mTime;
	}
}
