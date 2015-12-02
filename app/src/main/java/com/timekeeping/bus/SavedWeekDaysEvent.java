package com.timekeeping.bus;


import com.timekeeping.data.Time;

public final class SavedWeekDaysEvent {
	private Time   mTime;
	private String mWeekDays;

	public SavedWeekDaysEvent( Time time, String weekDays ) {
		mTime = time;
		mWeekDays = weekDays;
	}


	public Time getTime() {
		return mTime;
	}


	public String getWeekDays() {
		return mWeekDays;
	}
}
