package com.timekeeping.app.noactivities;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.timekeeping.R;
import com.timekeeping.app.App;
import com.timekeeping.database.DB;
import com.timekeeping.utils.NotifyUtils;
import com.timekeeping.utils.Prefs;

import io.realm.Realm;

public class TickerService extends Service {
	private static final int          ONGOING_NOTIFICATION_ID = 0x57;
	private static final String       TAG                     = "TickerService";
	private              boolean      mReg                    = false;
	private              IntentFilter mTickerFilter           = new IntentFilter( Intent.ACTION_TIME_TICK );
	private Realm mRealm;

	private BroadcastReceiver mTickerReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive( Context context, Intent intent
		) {
			startService( new Intent(
					context,
					AppGuardService.class
			) );
		}
	};

	public TickerService() {

	}

	@Override
	public IBinder onBind( Intent intent ) {
		return null;
	}

	@Override
	public int onStartCommand( Intent intent, int flags, int startId ) {
		Prefs prefs = Prefs.getInstance( getApplication() );
		if( prefs.hasInitData() && !prefs.isMigrated() ) {
			mRealm = Realm.getInstance( App.Instance );
			com.timekeeping.utils.Utils.migrateToRealm(
					DB.getInstance( App.Instance ),
					mRealm,
					new Runnable() {
						@Override
						public void run() {
							registerHandler();
						}
					}
			);
		} else {
			registerHandler();
		}
		return START_STICKY;
	}

	private void registerHandler() {
		if( !mReg ) {
			Notification notification = NotifyUtils.buildNotifyWithoutBigImage(
					this,
					ONGOING_NOTIFICATION_ID,
					getString( R.string.application_name ),
					getString( R.string.tray_info ),
					R.drawable.ic_tray,
					NotifyUtils.getAppHome( this ),
					false
			);
			startForeground(
					ONGOING_NOTIFICATION_ID,
					notification
			);
			registerReceiver(
					mTickerReceiver,
					mTickerFilter
			);
			mReg = true;
		}
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		if( mRealm != null ) {
			mRealm.close();
		}
		if( mTickerReceiver != null ) {
			unregisterReceiver( mTickerReceiver );
			mTickerReceiver = null;
			mReg = false;
		}
	}
}
