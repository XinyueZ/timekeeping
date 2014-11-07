package timekeeping.de.timekeeping.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import timekeeping.de.timekeeping.data.Time;

/**
 * Define util-methods.
 *
 * @author Xinyue Zhao
 */
public final class Utils {
	/**
	 * Convert time in {@link java.lang.String} from a {@link timekeeping.de.timekeeping.data.Time}.
	 * <p/>
	 * For example:
	 * <p/>
	 * value := 1 return 01
	 * <p/>
	 * value := 12 return 12
	 *
	 * @param item
	 * 		{@link timekeeping.de.timekeeping.data.Time}.
	 */
	public static String formatTime(Time item) {
		String fmt = "%s:%s";
		NumberFormat fmtNum = new DecimalFormat("##00");
		String ret = String.format(fmt, fmtNum.format(item.getHour()), fmtNum.format(item.getMinute()));
		return ret;
	}
}
