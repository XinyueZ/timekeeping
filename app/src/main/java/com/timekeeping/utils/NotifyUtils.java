package com.timekeeping.utils;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.content.ContextCompat;

import com.timekeeping.R;
import com.timekeeping.app.activities.MainActivity;

public final class NotifyUtils {
	private static void ringWorks(Context cxt, Builder builder) {
		AudioManager audioManager = (AudioManager) cxt.getSystemService(Context.AUDIO_SERVICE);
		if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
			builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000, 1000,1000 });
		 }
		builder.setLights(ContextCompat.getColor(cxt, R.color.primary_color), 1000, 1000);
	}



	public static void notifyWithoutBigImage(Context cxt, int id, String title, String desc, @DrawableRes int icon,
			PendingIntent contentIntent) {
		NotificationManager mgr = (NotificationManager) cxt.getSystemService(Context.NOTIFICATION_SERVICE);
		Builder builder = new Builder(cxt).setWhen(id).setSmallIcon(icon).setTicker(title).setContentTitle(title)
				.setContentText(desc).addAction(R.drawable.ic_app_rating, cxt.getString(R.string.btn_app_rating),
						getAppPlayStore(cxt)).setStyle(new BigTextStyle().bigText(desc).setBigContentTitle(title))
				.setAutoCancel(true);
		builder.setContentIntent(contentIntent);
		ringWorks(cxt, builder);
		mgr.notify(id, builder.build());
	}


	public static PendingIntent getAppPlayStore(Context cxt) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
				"https://play.google.com/store/apps/details?id=" + cxt.getPackageName()));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return PendingIntent.getActivity(cxt, com.chopping.utils.Utils.randInt(1, 9999), intent,
				PendingIntent.FLAG_ONE_SHOT);
	}

	public static PendingIntent getAppHome(Context cxt) {
		Intent intent = new Intent(cxt, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return PendingIntent.getActivity(cxt, com.chopping.utils.Utils.randInt(1, 9999), intent, PendingIntent.FLAG_ONE_SHOT);
	}

}