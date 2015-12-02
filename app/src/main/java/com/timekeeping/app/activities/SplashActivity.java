package com.timekeeping.app.activities;

import java.util.ArrayList;
import java.util.List;

import android.Manifest.permission;
import android.content.Intent;
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
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.chopping.utils.Utils;
import com.timekeeping.R;
import com.timekeeping.app.App;
import com.timekeeping.bus.MigratedEvent;
import com.timekeeping.data.Time;
import com.timekeeping.database.DB;
import com.timekeeping.utils.Prefs;
import com.timekeeping.utils.TextToSpeechUtils;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
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

	private Realm          mRealm;
	private RealmAsyncTask mTransaction;
	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link com.timekeeping.bus.MigratedEvent}.
	 *
	 * @param e
	 * 		Event {@link com.timekeeping.bus.MigratedEvent}.
	 */
	public void onEvent( MigratedEvent e ) {
		initSpeech();
	}

	//------------------------------------------------


	@Override
	public void onRequestPermissionsResult( int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults ) {
		// delegate the permission handling to generated method
		SplashActivityPermissionsDispatcher.onRequestPermissionsResult(
				this,
				requestCode,
				grantResults
		);
	}


	@NeedsPermission(permission.WRITE_EXTERNAL_STORAGE)
	void getReadPhoneStatePermission() {
		insertDefaults();
	}


	@DeniedPermission(permission.WRITE_EXTERNAL_STORAGE)
	void noReadPhoneStatePermission() {
		Snackbar.make(
				findViewById( R.id.splash_v ),
				R.string.msg_permission_prompt,
				Snackbar.LENGTH_INDEFINITE
		)
				.setAction(
						R.string.btn_agree,
						new OnClickListener() {
							@Override
							public void onClick( View v ) {
								ActivityCompat.finishAffinity( SplashActivity.this );
							}
						}
				)
				.show();

	}

	/**
	 * Insert default items first time.
	 */
	public void insertDefaults() {
		Prefs prefs = Prefs.getInstance( getApplication() );
		if( !prefs.hasInitData() ) {
			mTransaction = mRealm.executeTransaction(
					new Realm.Transaction() {
						@Override
						public void execute( Realm bgRealm ) {
							List<Time> defaultTimes = new ArrayList<>();
							defaultTimes.add( new Time(
									System.currentTimeMillis(),
									9,
									0,
									System.currentTimeMillis(),
									true
							) );
							defaultTimes.add( new Time(
									System.currentTimeMillis() + 1,
									12,
									0,
									System.currentTimeMillis(),
									true
							) );
							defaultTimes.add( new Time(
									System.currentTimeMillis() + 2,
									18,
									0,
									System.currentTimeMillis(),
									true
							) );
							defaultTimes.add( new Time(
									System.currentTimeMillis() + 3,
									20,
									0,
									System.currentTimeMillis(),
									true
							) );
							defaultTimes.add( new Time(
									System.currentTimeMillis() + 4,
									22,
									30,
									System.currentTimeMillis(),
									true
							) );
							bgRealm.copyToRealm( defaultTimes );
						}
					},
					new Realm.Transaction.Callback() {
						@Override
						public void onSuccess() {
							Prefs.getInstance( getApplication() )
								 .setInitData( true );
							initSpeech();
						}

						@Override
						public void onError( Exception e ) {
							Prefs.getInstance( getApplication() )
								 .setInitData( true );
							initSpeech();
						}
					}
			);
		} else {
			if( prefs.isMigrated() ) {
				initSpeech();
			} else {
				com.timekeeping.utils.Utils.migrateToRealm(
						DB.getInstance( App.Instance ),
						mRealm,
						new Runnable() {
							@Override
							public void run() {
								Prefs.getInstance( App.Instance )
									 .setMigrated( true );
							}
						}
				);
			}
		}
	}

	private void initSpeech() {
		Intent checkIntent = new Intent();
		checkIntent.setAction( TextToSpeech.Engine.ACTION_CHECK_TTS_DATA );
		startActivityForResult(
				checkIntent,
				SPEECH_REQ
		);
	}


	private void doneSpeak( boolean isError ) {
		TextToSpeechUtils.doneSpeak( mTextToSpeech );
		if( !isError ) {
			goToMain();
		} else {
			Snackbar.make(
					findViewById( R.id.splash_v ),
					R.string.msg_app_cant_be_used,
					Snackbar.LENGTH_INDEFINITE
			)
					.setAction(
							R.string.btn_close,
							new OnClickListener() {
								@Override
								public void onClick( View v ) {
									ActivityCompat.finishAffinity( SplashActivity.this );
								}
							}
					)
					.show();

		}
	}

	private void goToMain() {
		MainActivity.showInstance( SplashActivity.this );
		ActivityCompat.finishAfterTransition( SplashActivity.this );
	}


	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
		if( requestCode == SPEECH_REQ ) {
			if( resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS ) {
				Prefs prefs = Prefs.getInstance( getApplication() );
				if( !prefs.isWelcomed() ) {
					prefs.setWelcomed( true );
					TextToSpeechUtils.prepareSpeak(
							getApplication(),
							Prefs.getInstance( getApplication() )
								 .getVolume()
					);
					mTextToSpeech = new TextToSpeech(
							getApplication(),
							new OnInitListener() {
								@Override
								public void onInit( int status ) {
									if( status == TextToSpeech.SUCCESS ) {
										TextToSpeechUtils.doSpeak(
												mTextToSpeech,
												getString( R.string.welcome )
										);
									} else {
										doneSpeak( true );
									}
								}
							}
					);
					if( Build.VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1 ) {
						mTextToSpeech.setOnUtteranceProgressListener( new UtteranceProgressListener() {
							@Override
							public void onStart( String utteranceId ) {

							}

							@Override
							public void onDone( String utteranceId ) {
								doneSpeak( false );
							}

							@Override
							public void onError( String utteranceId ) {
								doneSpeak( true );
							}
						} );
					} else {
						mTextToSpeech.setOnUtteranceCompletedListener( new OnUtteranceCompletedListener() {
							@Override
							public void onUtteranceCompleted( String utteranceId ) {
								doneSpeak( false );
							}
						} );
					}
				} else {
					Utils.showLongToast(
							getApplication(),
							R.string.welcome
					);
					goToMain();
				}

			} else {
				// missing data, install it
				Intent installIntent = new Intent();
				installIntent.setAction( TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA );
				startActivity( installIntent );
				ActivityCompat.finishAffinity( SplashActivity.this );
			}
		}
	}


	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		mRealm = Realm.getInstance( App.Instance );
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN
		);
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_splash );
		SplashActivityPermissionsDispatcher.getReadPhoneStatePermissionWithCheck( this );
		EventBus.getDefault()
				.register( this );
	}

	@Override
	protected void onDestroy() {
		if( mTransaction != null && !mTransaction.isCancelled() ) {
			mTransaction.cancel();
		}
		if( mRealm != null ) {
			mRealm.close();
		}
		EventBus.getDefault()
				.unregister( this );
		super.onDestroy();
	}


}
