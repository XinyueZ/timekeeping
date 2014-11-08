package com.timekeeping.app;

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
	 * Action when database has been updated.
	 */
	public static final String ACTION_UPDATE = "com.timekeeping.app.action.UPDATE";
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
	private IntentFilter mTickFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
	/**
	 * We wanna event to handle for every minute, this is the {@link android.content.BroadcastReceiver} for every minute
	 * from system.
	 */
	private BroadcastReceiver mTickReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context cxt, Intent intent) {
			speak();
		}
	};


	/**
	 * Database has been updated, we need refresh list of {@link com.timekeeping.data.Time}s.
	 */
	private IntentFilter mUpdateFilter = new IntentFilter(ACTION_UPDATE);
	/**
	 * Event for update the list of {@link com.timekeeping.data.Time}s.
	 */
	private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context cxt, Intent intent) {
			loadData();
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
		registerReceiver(mTickReceiver, mTickFilter);
		registerReceiver(mUpdateReceiver, mUpdateFilter);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		loadData();
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * Load all {@link com.timekeeping.data.Time}s from database.
	 */
	private void loadData() {
		new ParallelTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				mTimes = DB.getInstance(getApplication()).getTimes(Sort.DESC);
				mTextToSpeech = new TextToSpeech(getApplication(), TimekeepingService.this);
				return null;
			}
		}.executeParallel();
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mTickReceiver);
		unregisterReceiver(mUpdateReceiver);
		mTextToSpeech = null;
		mTimes = null;
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
