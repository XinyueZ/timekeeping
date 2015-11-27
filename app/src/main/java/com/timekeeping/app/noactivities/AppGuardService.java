package com.timekeeping.app.noactivities;


import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.Engine;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.timekeeping.R;
import com.timekeeping.data.Time;
import com.timekeeping.database.DB;
import com.timekeeping.database.DB.Sort;
import com.timekeeping.utils.NotifyUtils;
import com.timekeeping.utils.Prefs;
import com.timekeeping.utils.Utils;

public final class AppGuardService extends GcmTaskService {
	private static final String TAG = "AppGuardService";
	private static final int NOTIFY_ID = 0x07;
	private static int sLastHour = -1;
	private static int sLastMin = -1;
	/**
	 * Speak text.
	 */
	private TextToSpeech mTextToSpeech;

	private WakeLock mWakeLock;

	@Override
	public int onRunTask(TaskParams taskParams) {
		synchronized (AppGuardService.TAG) {
			Prefs prefs = Prefs.getInstance(getApplication());
			Calendar calendar = Calendar.getInstance();
			if (!prefs.areAllPaused() && prefs.isEULAOnceConfirmed()) {
				int hour = calendar.get(Calendar.HOUR_OF_DAY);
				int min = calendar.get(Calendar.MINUTE);
				if (hour == sLastHour && min == sLastMin) {
					return GcmNetworkManager.RESULT_SUCCESS;
				}
				sLastHour = hour;
				sLastMin = min;
				speak(hour, min);
			}
		}
		return GcmNetworkManager.RESULT_SUCCESS;
	}

	private static void notify(Context cxt, String time) {
		NotifyUtils.notifyWithoutBigImage(cxt, NOTIFY_ID, cxt.getString(R.string.application_name),
				cxt.getString(R.string.msg_voice_clock, time), R.drawable.ic_voice_clock_notify,
				NotifyUtils.getAppHome(cxt));
	}


	private void speak(final int hour, final int minute) {
		List<Time> times = DB.getInstance(getApplication()).getTimes(Sort.DESC);
		Prefs prefs = Prefs.getInstance(getApplication());
		if (!prefs.areAllPaused() && prefs.isEULAOnceConfirmed()) {
			for (final Time time : times) {
				if (time.getHour() == hour && time.getMinute() == minute &&
						time.isOnOff()) {
					prepareSpeak();

					//Speak time.
					mTextToSpeech = new TextToSpeech(getApplication(), new OnInitListener() {
						@Override
						public void onInit(int status) {
							Intent wakeUpIntent = new Intent(WakeUpReceiver.ACTION_WAKE_UP);
							wakeUpIntent.putExtra(WakeUpReceiver.EXTRAS_TIME, time);
							if (status == TextToSpeech.SUCCESS) {
								wakeUpIntent.putExtra(WakeUpReceiver.EXTRAS_IF_ERROR, false);
								sendBroadcast(wakeUpIntent);
								String timeText = Utils.formatTime(hour, minute, true);
								String timeToSpeak = getString(R.string.lbl_prefix, timeText);
								String taskToSpeak = time.getTask();
								//noinspection unchecked
								if (mTextToSpeech != null && time.isOnOff()) {
									AppGuardService.this.doSpeak(!TextUtils.isEmpty(taskToSpeak) ?
											String.format("%s,%s", timeToSpeak, taskToSpeak) : timeToSpeak);

									AppGuardService.notify(getApplication(), !TextUtils.isEmpty(taskToSpeak) ?
											String.format("%s: %s", timeText, taskToSpeak) : timeText);
								}
							} else {
								wakeUpIntent.putExtra(WakeUpReceiver.EXTRAS_IF_ERROR, true);
								sendBroadcast(wakeUpIntent);
							}
						}
					});
					if (Build.VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
						mTextToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
							@Override
							public void onStart(String utteranceId) {

							}

							@Override
							public void onDone(String utteranceId) {
								doneSpeak();
							}

							@Override
							public void onError(String utteranceId) {
								doneSpeak();
							}
						});
					} else {
						mTextToSpeech.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener() {
							@Override
							public void onUtteranceCompleted(String utteranceId) {
								doneSpeak();
							}
						});
					}
				}
			}
		}
	}

	private void prepareSpeak() {
		if (mWakeLock == null) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		}
		mWakeLock.acquire();
		try {
			TimeUnit.SECONDS.sleep(15);
		} catch (InterruptedException e) {
			//Ignore...
		}
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int amStreamMusicMaxVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		am.setStreamVolume(AudioManager.STREAM_MUSIC, amStreamMusicMaxVol, 0);
	}

	private void doneSpeak() {
		if (mWakeLock != null && mWakeLock.isHeld()) {
			mWakeLock.release();
		}
		mTextToSpeech.shutdown();
		stopSelf();
	}

	private void doSpeak(String timeToSpeak) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Bundle args = new Bundle();
			args.putString(Engine.KEY_PARAM_UTTERANCE_ID, "com.svox.pico");
			mTextToSpeech.speak(timeToSpeak, TextToSpeech.QUEUE_FLUSH, args, null);
		} else {
			mTextToSpeech.speak(timeToSpeak, TextToSpeech.QUEUE_FLUSH, null);
		}
	}
}
