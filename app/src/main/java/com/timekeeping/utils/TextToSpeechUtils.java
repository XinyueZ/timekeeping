package com.timekeeping.utils;


import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.Engine;

public final class TextToSpeechUtils {

	public static void prepareSpeak(Context cxt, int volume) {
		AudioManager am = (AudioManager) cxt.getSystemService(Context.AUDIO_SERVICE);
		int v = 2;
		switch (volume) {
		case 0:
			v = 0;
			break;
		case 1:
			v = 2;
			break;
		case 2:
			v = 1;
			break;
		}
		int vol = v != 0 ? am.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / v : 0;
		am.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0);
	}


	public static void doSpeak(TextToSpeech tts, String timeToSpeak) {
		String id = java.lang.System.currentTimeMillis() + "";
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Bundle args = new Bundle();
			args.putString(Engine.KEY_PARAM_UTTERANCE_ID, "com.svox.pico");
			tts.speak(timeToSpeak, TextToSpeech.QUEUE_ADD, args, id);
		} else {
			HashMap<String, String> map = new HashMap<>();
			map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, id);
			tts.speak(timeToSpeak, TextToSpeech.QUEUE_ADD, map);
		}
	}


	public static void doneSpeak(TextToSpeech tts) {
		if (tts != null) {
			tts.shutdown();
		}
	}
}
