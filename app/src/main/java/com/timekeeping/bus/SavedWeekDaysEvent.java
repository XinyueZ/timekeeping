package com.timekeeping.bus;


import com.timekeeping.data.Time;

public final class SavedWeekDaysEvent extends TimeEvent {
	private String mWeekDays;

	public SavedWeekDaysEvent( int position, Time time, String weekDays ) {
		super(position, time);
		mWeekDays = weekDays;
	}




	public String getWeekDays() {
		return mWeekDays;
	}
}
