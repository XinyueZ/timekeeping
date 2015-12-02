package com.timekeeping.app.noactivities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.timekeeping.app.activities.WakeUpActivity;
import com.timekeeping.app.fragments.WakeUpFragment;
import com.timekeeping.data.Time;

public class WakeUpReceiver extends BroadcastReceiver {
	public static final String EXTRAS_TIME = WakeUpFragment.class.getName() + ".EXTRAS.time";
	public static final String EXTRAS_IF_ERROR = WakeUpFragment.class.getName() + ".EXTRAS.if.error";
	public static final String ACTION_WAKE_UP = "WakeUpReceiver.ACTION.wake.up";

	public WakeUpReceiver() {
	}

	@Override
	public void onReceive( Context context, Intent intent ) {
		if( intent != null && TextUtils.equals(
				intent.getAction(),
				ACTION_WAKE_UP
		) ) {
			WakeUpActivity.showInstance( context,
										 (Time) intent.getSerializableExtra( EXTRAS_TIME ),
										 intent.getBooleanExtra(
												 EXTRAS_IF_ERROR,
												 false
										 )
			);
		}
	}
}
