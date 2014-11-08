package com.timekeeping.utils;

import android.app.Application;
import android.content.Context;

import com.chopping.application.BasicPrefs;

/**
 * The preference of the application.
 */
public final class Prefs extends BasicPrefs {
	/**
	 * Impl singleton pattern.
	 */
	private static Prefs sInstance;


	/**
	 * Created a DeviceData storage.
	 *
	 * @param context
	 * 		A context object.
	 */
	private Prefs(Context context) {
		super(context);
	}

	/**
	 * Get instance of  {@link  Prefs} singleton.
	 *
	 * @param cxt
	 * 		{@link android.app.Application}.
	 *
	 * @return The {@link Prefs} singleton.
	 */
	public static Prefs getInstance(Application cxt) {
		if (sInstance == null) {
			sInstance = new Prefs(cxt);
		}
		return sInstance;
	}


}
