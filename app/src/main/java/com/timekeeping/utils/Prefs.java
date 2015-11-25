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
	 * Storage. Whether the "End User License Agreement" has been shown and agreed at application's first start.
	 * <p/>
	 * {@code true} if EULA has been shown and agreed.
	 */
	private static final String KEY_EULA_SHOWN = "key_eula_shown";
	/**
	 * Storage. Pause all items or not.
	 */
	private static final String KEY_PAUSE_ALL = "key_pause_all";
	/**
	 * Storage. Whether data has been initialized.
	 */
	private static final String KEY_INIT_DATA = "key_init_data";
	private static final String KEY_SHOWN_DETAILS_TIMES = "key.details.shown.times";

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

	/**
	 * Whether the "End User License Agreement" has been shown and agreed at application's first start.
	 * <p/>
	 *
	 * @return {@code true} if EULA has been shown and agreed.
	 */
	public boolean isEULAOnceConfirmed() {
		return getBoolean(KEY_EULA_SHOWN, false);
	}

	/**
	 * Set whether the "End User License Agreement" has been shown and agreed at application's first start.
	 * <p/>
	 *
	 * @param isConfirmed
	 * 		{@code true} if EULA has been shown and agreed.
	 */
	public void setEULAOnceConfirmed(boolean isConfirmed) {
		setBoolean(KEY_EULA_SHOWN, isConfirmed);
	}


	/**
	 * Set storage for the status of "pause all" or not.
	 *
	 * @param pause
	 * 		{@code true} if all items are paused.
	 */
	public void setPauseAll(boolean pause) {
		setBoolean(KEY_PAUSE_ALL, pause);
	}

	/**
	 * To know whether all items have been paused or not.
	 *
	 * @return {@code true} if all items are paused.
	 */
	public boolean areAllPaused() {
		return getBoolean(KEY_PAUSE_ALL, false);
	}



	/**
	 * Set whether data has been initialized.
	 */
	public void setInitData(boolean init) {
		  setBoolean(KEY_INIT_DATA, init);
	}
	/**
	 * Whether data has been initialized.
	 */
	public boolean hasInitData() {
		return getBoolean(KEY_INIT_DATA, false);
	}

	public void setShownDetailsTimes(int times) {
		setInt(KEY_SHOWN_DETAILS_TIMES, times);
	}
	public int getShownDetailsTimes() {
		return getInt(KEY_SHOWN_DETAILS_TIMES, 1);
	}
}
