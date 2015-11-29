package com.timekeeping.app.activities;

import android.Manifest.permission;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.chopping.utils.Utils;
import com.timekeeping.R;
import com.timekeeping.data.Time;
import com.timekeeping.database.DB;
import com.timekeeping.utils.Prefs;
import com.timekeeping.utils.TextToSpeechUtils;

import permissions.dispatcher.DeniedPermission;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class SplashActivity extends AppCompatActivity {

	private static final int SPEECH_REQ = 0x1;
	/**
	 * Speak text.
	 */
	private TextToSpeech mTextToSpeech;

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
			@NonNull int[] grantResults) {
		// delegate the permission handling to generated method
		SplashActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
	}


	@NeedsPermission(permission.WRITE_EXTERNAL_STORAGE)
	void getReadPhoneStatePermission() {
		insertDefaults();
	}


	@DeniedPermission(permission.WRITE_EXTERNAL_STORAGE)
	void noReadPhoneStatePermission() {
		Snackbar.make(findViewById(R.id.splash_v), R.string.msg_permission_prompt, Snackbar.LENGTH_INDEFINITE)
				.setAction(R.string.btn_agree, new OnClickListener() {
					@Override
					public void onClick(View v) {
						ActivityCompat.finishAffinity(SplashActivity.this);
					}
				}).show();

	}

	/**
	 * Insert default items first time.
	 */
	public void insertDefaults() {
		if (!Prefs.getInstance(getApplication()).hasInitData()) {
			AsyncTaskCompat.executeParallel(new AsyncTask<Time, Time, Time>() {
				@Override
				protected Time doInBackground(Time... params) {
					DB db = DB.getInstance(getApplication());
					Time t = new Time(-1, 9, 0, -1, true);
					db.addTime(t);
					t = new Time(-1, 12, 0, -1, true);
					db.addTime(t);
					t = new Time(-1, 18, 0, -1, true);
					db.addTime(t);
					t = new Time(-1, 20, 0, -1, false);
					db.addTime(t);
					t = new Time(-1, 22, 30, -1, false);
					db.addTime(t);

					Prefs.getInstance(getApplication()).setInitData(true);
					return null;
				}

				@Override
				protected void onPostExecute(Time time) {
					super.onPostExecute(time);
					initSpeech();
				}
			});
		} else {
			initSpeech();
		}
	}

	private void initSpeech() {
		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, SPEECH_REQ);
	}


	private void doneSpeak(boolean isError) {
		TextToSpeechUtils.doneSpeak(mTextToSpeech);
		if (!isError) {
			goToMain();
		} else {
			Snackbar.make(findViewById(R.id.splash_v), R.string.msg_app_cant_be_used, Snackbar.LENGTH_INDEFINITE)
					.setAction(R.string.btn_close, new OnClickListener() {
						@Override
						public void onClick(View v) {
							ActivityCompat.finishAffinity(SplashActivity.this);
						}
					}).show();

		}
	}

	private void goToMain() {
		MainActivity.showInstance(SplashActivity.this);
		ActivityCompat.finishAfterTransition(SplashActivity.this);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SPEECH_REQ) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				Prefs prefs = Prefs.getInstance(getApplication());
				if (!prefs.isWelcomed()) {
					prefs.setWelcomed(true);
					TextToSpeechUtils.prepareSpeak(getApplication(), Prefs.getInstance(getApplication()).getVolume());
					mTextToSpeech = new TextToSpeech(getApplication(), new OnInitListener() {
						@Override
						public void onInit(int status) {
							if (status == TextToSpeech.SUCCESS) {
								TextToSpeechUtils.doSpeak(mTextToSpeech, getString(R.string.welcome));
							} else {
								doneSpeak(true);
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
								doneSpeak(false);
							}

							@Override
							public void onError(String utteranceId) {
								doneSpeak(true);
							}
						});
					} else {
						mTextToSpeech.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener() {
							@Override
							public void onUtteranceCompleted(String utteranceId) {
								doneSpeak(false);
							}
						});
					}
				} else {
					Utils.showLongToast(getApplication(), R.string.welcome);
					goToMain();
				}

			} else {
				// missing data, install it
				Intent installIntent = new Intent();
				installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
				ActivityCompat.finishAffinity(SplashActivity.this);
			}
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		SplashActivityPermissionsDispatcher.getReadPhoneStatePermissionWithCheck(this);
	}
}
