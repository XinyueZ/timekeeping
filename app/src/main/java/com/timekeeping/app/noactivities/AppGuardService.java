package com.timekeeping.app.noactivities;


import java.util.Calendar;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;

import com.timekeeping.R;
import com.timekeeping.app.App;
import com.timekeeping.data.Time;
import com.timekeeping.utils.NotifyUtils;
import com.timekeeping.utils.Prefs;
import com.timekeeping.utils.TextToSpeechUtils;
import com.timekeeping.utils.Utils;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public final class AppGuardService extends IntentService {
	private static final String TAG       = "AppGuardService";
	private static final int    NOTIFY_ID = 0x07;
	/**
	 * Speak text.
	 */
	private TextToSpeech mTextToSpeech;

	private WakeLock mWakeLock;

	private Realm mRealm;

	public AppGuardService() {
		super( TAG );
	}


	@Override
	protected void onHandleIntent( Intent intent ) {
		mRealm = Realm.getInstance( App.Instance );
		Prefs    prefs    = Prefs.getInstance( getApplication() );
		Calendar calendar = Calendar.getInstance();
		if( !prefs.areAllPaused() && prefs.isEULAOnceConfirmed() ) {
			int hour      = calendar.get( Calendar.HOUR_OF_DAY );
			int min       = calendar.get( Calendar.MINUTE );
			int dayOfWeek = calendar.get( Calendar.DAY_OF_WEEK ) - 1; //Android: sunday == 1, this app: sunday==0;

			speak(
					hour,
					min,
					dayOfWeek
			);
		}
		if( mRealm != null ) {
			mRealm.close();
		}
	}


	private static void notify( Context cxt, String time ) {
		NotifyUtils.notifyWithoutBigImage(
				cxt,
				NOTIFY_ID,
				cxt.getString( R.string.application_name ),
				cxt.getString(
						R.string.msg_voice_clock,
						time
				),
				R.drawable.ic_voice_clock_notify,
				NotifyUtils.getAppHome( cxt ),
				Prefs.getInstance( App.Instance )
					 .getVolume() == 0
		);
	}


	private void speak( final int hour, final int minute, final int dayOfWeek ) {
		Prefs prefs = Prefs.getInstance( getApplication() );
		if( !prefs.areAllPaused() && prefs.isEULAOnceConfirmed() ) {
			RealmQuery<Time> query = mRealm.where( Time.class )
										   .equalTo(
												   "hour",
												   hour
										   )
										   .equalTo(
												   "minute",
												   minute
										   )
										   .contains(
												   "weekDays",
												   dayOfWeek + ""
										   )
										   .equalTo(
												   "onOff",
												   true
										   )
										   .or()
										   .equalTo(
												   "hour",
												   hour
										   )
										   .equalTo(
												   "minute",
												   minute
										   )
										   .isEmpty( "weekDays" )
										   .equalTo(
												   "onOff",
												   true
										   )
										   .or()
										   .equalTo(
												   "hour",
												   hour
										   )
										   .equalTo(
												   "minute",
												   minute
										   )
										   .isNull( "weekDays" )
										   .equalTo(
												   "onOff",
												   true
										   );
			RealmResults<Time> times = query.findAll();
			if( times.size() > 0 ) {
				final Time time = times.first();
				prepareSpeak();
				//Speak time.
				mTextToSpeech = new TextToSpeech(
						getApplication(),
						new OnInitListener() {
							@Override
							public void onInit( int status ) {
								Intent wakeUpIntent = new Intent( WakeUpReceiver.ACTION_WAKE_UP );
								wakeUpIntent.putExtra(
										WakeUpReceiver.EXTRAS_TIME,
										time
								);
								if( status == TextToSpeech.SUCCESS ) {
									wakeUpIntent.putExtra(
											WakeUpReceiver.EXTRAS_IF_ERROR,
											false
									);
									sendBroadcast( wakeUpIntent );
									String timeText = Utils.formatTime(
											hour,
											minute,
											true
									);
									String timeToSpeak = getString(
											R.string.lbl_prefix,
											timeText
									);
									String taskToSpeak = time.getTask();
									//noinspection unchecked
									if( mTextToSpeech != null ) {
										TextToSpeechUtils.doSpeak(
												mTextToSpeech,
												!TextUtils.isEmpty( taskToSpeak ) ? String.format(
														"%s,%s",
														timeToSpeak,
														taskToSpeak
												)                                 : timeToSpeak
										);

										AppGuardService.notify(
												getApplication(),
												!TextUtils.isEmpty( taskToSpeak ) ? String.format(
														"%s: %s",
														timeText,
														taskToSpeak
												)                                 : timeText
										);
									} else {
										doneSpeak();
									}
								} else {
									wakeUpIntent.putExtra(
											WakeUpReceiver.EXTRAS_IF_ERROR,
											true
									);
									sendBroadcast( wakeUpIntent );
									doneSpeak();
								}
							}
						}
				);
				if( Build.VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1 ) {
					mTextToSpeech.setOnUtteranceProgressListener( new UtteranceProgressListener() {
						@Override
						public void onStart( String utteranceId ) {

						}

						@Override
						public void onDone( String utteranceId ) {
							doneSpeak();
						}

						@Override
						public void onError( String utteranceId ) {
							doneSpeak();
						}
					} );
				} else {
					mTextToSpeech.setOnUtteranceCompletedListener( new OnUtteranceCompletedListener() {
						@Override
						public void onUtteranceCompleted( String utteranceId ) {
							doneSpeak();
						}
					} );
				}

			}
		}
	}

	private void prepareSpeak() {
		if( mWakeLock == null ) {
			PowerManager pm           = (PowerManager) getSystemService( Context.POWER_SERVICE );
			boolean      largerThan17 = Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1;
			mWakeLock = pm.newWakeLock(
					largerThan17 ? PowerManager.PARTIAL_WAKE_LOCK : PowerManager.FULL_WAKE_LOCK,
					TAG
			);
		}
		mWakeLock.acquire();
		TextToSpeechUtils.prepareSpeak(
				getApplication(),
				Prefs.getInstance( getApplication() )
					 .getVolume()
		);
	}

	private void doneSpeak() {
		TextToSpeechUtils.doneSpeak( mTextToSpeech );

		if( mWakeLock != null ) {
			mWakeLock.release();
			mWakeLock = null;
		}
	}
}
