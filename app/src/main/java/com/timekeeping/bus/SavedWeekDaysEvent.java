package com.timekeeping.bus;


import com.timekeeping.data.Time;

public final class SavedWeekDaysEvent {
	private Time mTime;

	public SavedWeekDaysEvent(Time time) {
		mTime = time;

	}


	public Time getTime() {
		return mTime;
	}
}
