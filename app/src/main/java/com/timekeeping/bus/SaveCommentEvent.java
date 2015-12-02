package com.timekeeping.bus;


import com.timekeeping.data.Time;

public final class SaveCommentEvent {
	private Time   mTime;
	private String mComment;

	public SaveCommentEvent( Time time , String comment) {
		mTime = time;
		mComment = comment;
	}


	public Time getTime() {
		return mTime;
	}


	public String getComment() {
		return mComment;
	}
}
