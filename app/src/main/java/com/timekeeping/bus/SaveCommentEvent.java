package com.timekeeping.bus;


import com.timekeeping.data.Time;

public final class SaveCommentEvent extends TimeEvent{
	private String mComment;

	public SaveCommentEvent( int position, Time time , String comment) {
		super(position, time);
		mComment = comment;
	}


	public String getComment() {
		return mComment;
	}
}
