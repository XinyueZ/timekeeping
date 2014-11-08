package com.timekeeping.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.content.Intent;

import com.timekeeping.data.Time;

/**
 * Define util-methods.
 *
 * @author Xinyue Zhao
 */
public final class Utils {
	/**
	 * Convert time in {@link java.lang.String} from a {@link  com.timekeeping.data.Time}.
	 * <p/>
	 * For example:
	 * <p/>
	 * value := 1 return 01
	 * <p/>
	 * value := 12 return 12
	 *
	 * @param item
	 * 		{@link  com.timekeeping.data.Time}.
	 */
	public static String formatTime(Time item) {
		String fmt = "%s:%s";
		NumberFormat fmtNum = new DecimalFormat("##00");
		String ret = String.format(fmt, fmtNum.format(item.getHour()), fmtNum.format(item.getMinute()));
		return ret;
	}

	/**
	 * Convert hour and minute compressed in {@link java.lang.String}.
	 * <p/>
	 *
	 * @param hour
	 * 		The hour.
	 * @param minute
	 * 		The minute.
	 * @param isTwoDigits Two digits ({@code true} or not.
	 * 		For example:
	 * 		<p/>
	 * 		value := 1 return 01
	 * 		<p/>
	 * 		value := 12 return 12
	 */
	public static String formatTime(int hour, int minute, boolean isTwoDigits) {
		String fmt = "%s:%s";
		if (!isTwoDigits) {
			return String.format(fmt, hour, minute);
		} else {
			NumberFormat fmtNum = new DecimalFormat("##00");
			return String.format(fmt, fmtNum.format(hour), fmtNum.format(minute));
		}
	}


	/**
	 * Standard sharing app for sharing on actionbar.
	 */
	public static Intent getDefaultShareIntent(android.support.v7.widget.ShareActionProvider provider, String subject,
			String body) {
		if (provider != null) {
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("text/plain");
			i.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
			i.putExtra(android.content.Intent.EXTRA_TEXT, body);
			provider.setShareIntent(i);
			return i;
		}
		return null;
	}
}
