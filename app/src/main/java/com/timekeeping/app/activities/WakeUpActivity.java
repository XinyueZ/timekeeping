package com.timekeeping.app.activities;

import java.io.Serializable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.timekeeping.R;
import com.timekeeping.app.fragments.WakeUpFragment;
import com.timekeeping.data.Time;

public class WakeUpActivity extends AppCompatActivity {
	/**
	 * Main layout for this component.
	 */
	private static final int    LAYOUT          = R.layout.activity_wake_up;
	private static final String EXTRAS_TIME     = WakeUpFragment.class.getName() + ".EXTRAS.time";
	private static final String EXTRAS_IF_ERROR = WakeUpFragment.class.getName() + ".EXTRAS.if.error";


	/**
	 * Show single instance of {@link WakeUpActivity}
	 *
	 * @param cxt
	 * 		{@link Activity}.
	 */
	public static void showInstance( Context cxt, Time time, boolean ifError ) {
		Intent intent = new Intent( cxt, WakeUpActivity.class );
		intent.putExtra( EXTRAS_TIME, (Serializable) time );
		intent.putExtra( EXTRAS_IF_ERROR, ifError );
		intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP );
		cxt.startActivity( intent );
	}

	private void handleIntent( Intent intent ) {
		FragmentManager frgMgr = getSupportFragmentManager();
		WakeUpFragment frg = WakeUpFragment.newInstance( this, (Time) intent.getSerializableExtra( EXTRAS_TIME ),
														 intent.getBooleanExtra( EXTRAS_IF_ERROR, false )
		);
		frgMgr.beginTransaction().replace( R.id.wake_up_fl, frg, frg.getClass().getSimpleName() ).commit();
		frgMgr.executePendingTransactions();
	}

	@Override
	protected void onNewIntent( Intent intent ) {
		super.onNewIntent( intent );
		setIntent( intent );
		handleIntent( intent );
	}

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		boolean largerThan17 = Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1;
		if( largerThan17 ) {
			getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN|
								  WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
								  WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
								  WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON, WindowManager.LayoutParams.FLAG_FULLSCREEN|
																				  WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
																				  WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
																				  WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON );
		}

		super.onCreate( savedInstanceState );
		setContentView( LAYOUT );
		setTitle( "" );
		handleIntent( getIntent() );
	}

}
