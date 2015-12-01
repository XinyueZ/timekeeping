package com.timekeeping.app.noactivities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.timekeeping.app.App;

/**
 * Handling device boot by {@link BroadcastReceiver}.
 *
 * @author Xinyue Zhao
 */
public final class BootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive( Context context, Intent intent ) {
		App.startAppGuardService();
	}
}

