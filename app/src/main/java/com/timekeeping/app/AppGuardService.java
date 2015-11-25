package com.timekeeping.app;


import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.Engine;
import android.speech.tts.TextToSpeech.OnInitListener;

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


	@Override
	public int onRunTask(TaskParams taskParams) {
		synchronized (AppGuardService.TAG) {
			Calendar calendar = Calendar.getInstance();
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int min = calendar.get(Calendar.MINUTE);
			if (hour == sLastHour && min == sLastMin) {
				return GcmNetworkManager.RESULT_SUCCESS;
			}
			sLastHour = hour;
			sLastMin = min;
			speak(hour, min);
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
					//Speak time.
					mTextToSpeech = new TextToSpeech(getApplication(), new OnInitListener() {
						@Override
						public void onInit(int status) {
							String timeText = Utils.formatTime(hour, minute, true);
							String timeToSpeak = getString(R.string.lbl_prefix, timeText);
							//noinspection unchecked
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
								Bundle args = new Bundle();
								args.putString(Engine.KEY_PARAM_UTTERANCE_ID, "com.svox.pico");
								mTextToSpeech.speak(timeToSpeak, TextToSpeech.QUEUE_FLUSH, args, null);
							} else {
								mTextToSpeech.speak(timeToSpeak, TextToSpeech.QUEUE_FLUSH, null);
							}
							AppGuardService.notify(getApplication(), timeText);
						}
					});
				}
			}
		}
	}
}
