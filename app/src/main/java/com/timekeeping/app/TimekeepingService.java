package com.timekeeping.app;

import java.lang.ref.WeakReference;
import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

import com.timekeeping.data.Time;
import com.timekeeping.database.DB;
import com.timekeeping.database.DB.Sort;
import com.timekeeping.utils.ParallelTask;
import com.timekeeping.utils.Utils;

import org.joda.time.DateTime;

/**
 * Main service for the application.
 *
 * @author Xinyue Zhao
 */
public final class TimekeepingService extends Service implements OnInitListener {
	/**
	 * Retrieved data list from {@link com.timekeeping.database.DB}.
	 */
	private List<Time> mTimes;
	/**
	 * Speak text.
	 */
	private TextToSpeech mTextToSpeech;

	/**
	 * We wanna every-minute-event, this is the {@link android.content.IntentFilter} for every minute from system.
	 */
	private IntentFilter mIntentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
	/**
	 * We wanna event to handle for every minute, this is the {@link android.content.BroadcastReceiver} for every minute
	 * from system.
	 */
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context cxt, Intent intent) {
			speak();
		}
	};


	/**
	 * Init this service.
	 * <p/>
	 * No used.
	 */
	public TimekeepingService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}


	@Override
	public void onCreate() {
		super.onCreate();
		registerReceiver(mReceiver, mIntentFilter);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//noinspection unchecked
		new ParallelTask<WeakReference<TimekeepingService>, Void, Void>() {
			@Override
			protected Void doInBackground(WeakReference<TimekeepingService>... params) {
				WeakReference<TimekeepingService> cxtRef = params[0];
				if (cxtRef.get() != null) {
					TimekeepingService service = cxtRef.get();
					mTimes = DB.getInstance(service.getApplication()).getTimes(Sort.DESC);
					mTextToSpeech = new TextToSpeech(service.getApplication(), TimekeepingService.this);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				super.onPostExecute(aVoid);
			}
		}.executeParallel(new WeakReference<TimekeepingService>(this));


		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mReceiver);
		mTextToSpeech = null;
		super.onDestroy();
	}

	@Override
	public void onInit(int status) {
		//First do nothing.
	}


	/**
	 * Speak now for the right time.
	 */
	private void speak() {
		DateTime now = DateTime.now();
		for (Time time : mTimes) {
			if (time.getHour() == now.getHourOfDay() && time.getMinute() == now.getMinuteOfHour()) {
				//Speak time.
				if (mTextToSpeech != null) {
					String timeToSpeak = Utils.formatTime(time.getHour(), time.getMinute(), false);
					//noinspection unchecked
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						//No checked, need emulator or device to test here.
						mTextToSpeech.speak(timeToSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
					} else {
						mTextToSpeech.speak(timeToSpeak, TextToSpeech.QUEUE_FLUSH, null);
					}
				}
			}
		}
	}

}
