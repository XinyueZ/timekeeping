package com.timekeeping.app.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.chopping.activities.BaseActivity;
import com.chopping.application.BasicPrefs;
import com.chopping.bus.CloseDrawerEvent;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment.OnDialogDismissListener;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment.OnTimeSetListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.timekeeping.R;
import com.timekeeping.app.App;
import com.timekeeping.app.adapters.TimeKeepingListAdapter;
import com.timekeeping.app.fragments.AboutDialogFragment;
import com.timekeeping.app.fragments.AboutDialogFragment.EulaConfirmationDialog;
import com.timekeeping.app.fragments.CommentDialogFragment;
import com.timekeeping.app.fragments.VolumeDialogFragment;
import com.timekeeping.bus.DeleteTimeEvent;
import com.timekeeping.bus.EULAConfirmedEvent;
import com.timekeeping.bus.EULARejectEvent;
import com.timekeeping.bus.EditTaskEvent;
import com.timekeeping.bus.EditTimeEvent;
import com.timekeeping.bus.SaveCommentEvent;
import com.timekeeping.bus.SavedWeekDaysEvent;
import com.timekeeping.bus.SelectItemEvent;
import com.timekeeping.bus.StartActionModeEvent;
import com.timekeeping.bus.SwitchOnOffTimeEvent;
import com.timekeeping.data.Time;
import com.timekeeping.databinding.ActivityMainBinding;
import com.timekeeping.utils.Prefs;
import com.timekeeping.utils.TypefaceSpan;
import com.timekeeping.utils.Utils;
import com.timekeeping.widget.FontTextView.Fonts;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * The {@link MainActivity}.
 *
 * @author Xinyue Zhao
 */
public class MainActivity extends BaseActivity implements OnClickListener, OnTimeSetListener, OnDialogDismissListener {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT           = R.layout.activity_main;
	/**
	 * Menu for the Action-Mode.
	 */
	private static final int ACTION_MODE_MENU = R.menu.action_mode;

	/**
	 * Main menu.
	 */
	private static final int MENU_MAIN = R.menu.menu_main;

	/**
	 * Use navigation-drawer for this fork.
	 */
	private ActionBarDrawerToggle mDrawerToggle;
	/**
	 * The {@link android.support.v7.view.ActionMode}.
	 */
	private ActionMode            mActionMode;
	/**
	 * Edit a item  or  not.
	 */
	private boolean               mEdit;
	/**
	 * {@link Time} to edit.
	 */
	private Time                  mEditedTime;
	/**
	 * The interstitial ad.
	 */
	private InterstitialAd        mInterstitialAd;
	/**
	 * Data-binding.
	 */
	private ActivityMainBinding   mBinding;

	private Realm          mRealm;
	private RealmAsyncTask mTransaction;
	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link  EULARejectEvent}.
	 *
	 * @param e
	 * 		Event {@link  EULARejectEvent}.
	 */
	public void onEvent( EULARejectEvent e ) {
		ActivityCompat.finishAfterTransition( this );
	}

	/**
	 * Handler for {@link EULAConfirmedEvent}
	 *
	 * @param e
	 * 		Event {@link  EULAConfirmedEvent}.
	 */
	public void onEvent( EULAConfirmedEvent e ) {

	}


	/**
	 * Handler for {@link DeleteTimeEvent}.
	 *
	 * @param e
	 * 		Event {@link DeleteTimeEvent}.
	 */
	public void onEvent( DeleteTimeEvent e ) {
		Time      time     = e.getTime();
		final int position = e.getPosition();
		if( time != null ) {
			mRealm.beginTransaction();
			mRealm.addChangeListener( new RealmChangeListener() {
				@Override
				public void onChange() {
					mBinding.getAdapter()
							.notifyItemRemoved( position );
					mRealm.removeChangeListener( this );
				}
			} );
			time.removeFromRealm();
			mRealm.commitTransaction();
		}
	}

	/**
	 * Handler for {@link EditTimeEvent}.
	 *
	 * @param e
	 * 		Event {@link EditTimeEvent}.
	 */
	public void onEvent( EditTimeEvent e ) {
		editTime(
				e.getPosition(),
				e.getTime()
		);
	}


	/**
	 * Handler for {@link SwitchOnOffTimeEvent}.
	 *
	 * @param e
	 * 		Event {@link SwitchOnOffTimeEvent}.
	 */
	public void onEvent( SwitchOnOffTimeEvent e ) {
		setTimeOnOff(
				e.getPosition(),
				e.getTime()
		);
	}


	/**
	 * Handler for {@link }.
	 *
	 * @param e
	 * 		Event {@link}.
	 */
	public void onEvent( CloseDrawerEvent e ) {
		mBinding.drawerLayout.closeDrawers();
	}


	/**
	 * Handler for {@link SelectItemEvent}.
	 *
	 * @param e
	 * 		Event {@link SelectItemEvent}.
	 */
	public void onEvent( SelectItemEvent e ) {
		toggleSelection( e.getPosition() );
	}

	/**
	 * Handler for {@link StartActionModeEvent}.
	 *
	 * @param e
	 * 		Event {@link  StartActionModeEvent}.
	 */
	public void onEvent( StartActionModeEvent e ) {
		//See more about action-mode.
		//http://databasefaq.com/index.php/answer/19065/android-android-fragments-recyclerview-android-actionmode-problems-with-implementing-contextual-action-mode-in-recyclerview-fragment
		mActionMode = startSupportActionMode( new Callback() {
			@Override
			public boolean onCreateActionMode( ActionMode mode, Menu menu ) {
				mode.getMenuInflater()
					.inflate(
							ACTION_MODE_MENU,
							menu
					);
				mBinding.toolbar.setVisibility( View.GONE );
				mBinding.errorContent.setStatusBarBackgroundColor( R.color.primary_dark_color );
				mBinding.getAdapter()
						.setActionMode( true );
				mBinding.getAdapter()
						.notifyDataSetChanged();
				mBinding.addNewTimeBtn.hide();
				return true;
			}

			@Override
			public boolean onPrepareActionMode( ActionMode mode, Menu menu ) {
				return false;
			}

			@Override
			public boolean onActionItemClicked( ActionMode mode, MenuItem item ) {
				final List<Integer> selectedItems = mBinding.getAdapter()
															.getSelectedItems();
				final List<Time> selectedTimes = new ArrayList<>();

				for( Integer pos : selectedItems ) {
					selectedTimes.add( mBinding.getAdapter()
											   .getData()
											   .get( pos ) );
				}

				mRealm.beginTransaction();
				for( Time delTime : selectedTimes ) {
					delTime.removeFromRealm();
				}
				mRealm.addChangeListener( new RealmChangeListener() {
					@Override
					public void onChange() {
						for( Integer pos : selectedItems ) {
							mBinding.getAdapter()
									.notifyItemRemoved( pos.intValue() );
							if( mActionMode != null ) {
								mActionMode.finish();
							}
						}
						mRealm.removeChangeListener( this );
					}
				} );
				mRealm.commitTransaction();


				return true;
			}

			@Override
			public void onDestroyActionMode( ActionMode mode ) {
				mActionMode = null;
				mBinding.toolbar.setVisibility( View.VISIBLE );

				mBinding.getAdapter()
						.clearSelection();
				mBinding.getAdapter()
						.setActionMode( false );
				mBinding.getAdapter()
						.notifyDataSetChanged();
				mBinding.addNewTimeBtn.show();
			}
		} );
	}

	/**
	 * Handler for {@link com.timekeeping.bus.EditTaskEvent}.
	 *
	 * @param e
	 * 		Event {@link com.timekeeping.bus.EditTaskEvent}.
	 */
	public void onEvent( EditTaskEvent e ) {
		showDialogFragment(
				CommentDialogFragment.newInstance(
						App.Instance,
						e.getPosition(),
						e.getTime()
				),
				null
		);
	}


	/**
	 * Handler for {@link SaveCommentEvent}.
	 *
	 * @param e
	 * 		Event {@link SaveCommentEvent}.
	 */
	public void onEvent( SaveCommentEvent e ) {
		mEdit = true;
		mRealm.beginTransaction();
		mEditedTime = e.getTime();
		mEditedTime.setTask( e.getComment() );
		updateOthers( e.getPosition() );
	}

	/**
	 * Handler for {@link com.timekeeping.bus.SavedWeekDaysEvent}.
	 *
	 * @param e
	 * 		Event {@link com.timekeeping.bus.SavedWeekDaysEvent}.
	 */
	public void onEvent( SavedWeekDaysEvent e ) {
		mEdit = true;
		mRealm.beginTransaction();
		mEditedTime = e.getTime();
		mEditedTime.setWeekDays( e.getWeekDays() );
		updateOthers( e.getPosition() );
	}
	//------------------------------------------------


	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		getMenuInflater().inflate(
				MENU_MAIN,
				menu
		);
		MenuItem menuShare = menu.findItem( R.id.action_share_app );
		//Getting the actionprovider associated with the menu item whose id is share.
		android.support.v7.widget.ShareActionProvider provider
				= (android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider( menuShare );
		//Setting a share intent.
		String subject = getString(
				R.string.lbl_share_app_title,
				getString( R.string.application_name )
		);
		String text = getString(
				R.string.lbl_share_app_content,
				getString( R.string.tray_info )
		);
		provider.setShareIntent( Utils.getDefaultShareIntent(
				provider,
				subject,
				text
		) );

		MenuItem volMi = menu.findItem( R.id.action_volume );
		int volume = Prefs.getInstance( getApplication() )
						  .getVolume();
		String[] labels = getResources().getStringArray( R.array.volumes );
		String   label;
		int      icon;
		switch( volume ) {
			case 0:
				label = labels[ 0 ];
				icon = R.drawable.ic_volume_vibration;
				break;
			case 2:
				label = labels[ 2 ];
				icon = R.drawable.ic_volume_sharp;
				break;
			default:
				label = labels[ 1 ];
				icon = R.drawable.ic_volume_medium;
				break;
		}
		volMi.setIcon( icon );
		volMi.setTitle( label );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		if( mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected( item ) ) {
			return true;
		}
		int id = item.getItemId();
		switch( id ) {
			case R.id.action_about:
				showDialogFragment(
						AboutDialogFragment.newInstance( this ),
						null
				);
				break;
			case R.id.action_volume:
				showDialogFragment(
						VolumeDialogFragment.newInstance( this ),
						null
				);
				break;
		}
		return super.onOptionsItemSelected( item );
	}


	@Override
	public void onClick( View v ) {
		switch( v.getId() ) {
			case R.id.add_new_time_btn:
				addNewTime();
				break;
		}
	}

	/**
	 * Added a new entry of {@link com.timekeeping.data.Time} to database.
	 */
	private void addNewTime() {
		mBinding.addNewTimeBtn.hide();
		RadialTimePickerDialogFragment timePickerDialog = RadialTimePickerDialogFragment.newInstance(
				this,
				0,
				0,
				DateFormat.is24HourFormat( this )
		);
		timePickerDialog.setOnDismissListener( this );
		timePickerDialog.show(
				getSupportFragmentManager(),
				null
		);
	}

	/**
	 * Edit a entry of {@link com.timekeeping.data.Time} to database.
	 */
	private void editTime( int position ) {
		mBinding.addNewTimeBtn.show();
		RadialTimePickerDialogFragment timePickerDialog = RadialTimePickerDialogFragment.newInstance(
				this,
				mEditedTime.getHour(),
				mEditedTime.getMinute(),
				DateFormat.is24HourFormat( this )
		);
		timePickerDialog.setOnDismissListener( this );
		timePickerDialog.show(
				getSupportFragmentManager(),
				position + ""
		);
	}

	/**
	 * Start to edit a {@link com.timekeeping.data.Time}
	 *
	 * @param position
	 * 		The position of {@link Time} to edit.
	 * @param timeToEdit
	 * 		The object to edit.
	 */
	private void editTime( int position, Time timeToEdit ) {
		mEdit = true;
		mEditedTime = timeToEdit;
		if( mEditedTime != null ) {
			editTime( position );
		}
	}

	/**
	 * Set on/off status of the time. It is toggled.
	 *
	 * @param position
	 * 		The position of {@link Time} to update.
	 * @param timeToSet
	 * 		The object to set.
	 */
	private void setTimeOnOff( int position, Time timeToSet ) {
		mEdit = true;
		mEditedTime = timeToSet;
		mRealm.beginTransaction();
		mEditedTime.setOnOff( !mEditedTime.isOnOff() );
		updateOthers( position );
	}

	private void refreshGrid() {
		final RealmResults<Time> result = mRealm.where( Time.class )
												.findAllSortedAsync(
														"editTime",
														RealmResults.SORT_ORDER_DESCENDING
												);
		result.addChangeListener( new RealmChangeListener() {
			@Override
			public void onChange() {
				mBinding.getAdapter()
						.setData( result );
				mBinding.getAdapter()
						.notifyDataSetChanged();
				mRealm.removeChangeListener( this );
			}
		} );
	}


	/**
	 * Insert a {@link com.timekeeping.data.Time} to database.
	 *
	 * @param hourOfDay
	 * 		Hour
	 * @param minute
	 * 		Minute.
	 */
	private void insertNewTime( int hourOfDay, int minute ) {
		final Time newTime = new Time(
				System.currentTimeMillis(),
				hourOfDay,
				minute,
				System.currentTimeMillis(),
				true
		);
		final RealmResults<Time> results = mRealm.where( Time.class )
												 .equalTo(
														 "hour",
														 newTime.getHour()
												 )
												 .equalTo(
														 "minute",
														 newTime.getMinute()
												 )
												 .findAllAsync();
		results.addChangeListener( new RealmChangeListener() {
			@Override
			public void onChange() {
				if( results.size() == 0 ) {
					mRealm.addChangeListener( new RealmChangeListener() {
						@Override
						public void onChange() {
							mBinding.getAdapter()
									.notifyItemInserted( 0 );
							showStatusMessage( newTime );
							mBinding.scheduleGv.getLayoutManager()
											   .scrollToPosition( 0 );
							mRealm.removeChangeListener( this );
						}
					} );
					mRealm.beginTransaction();
					mRealm.copyToRealm( newTime );
					mRealm.commitTransaction();
				} else {
					showStatusMessage( getString( R.string.msg_duplicated_setting ) );
				}
				results.removeChangeListener( this );
			}
		} );
	}


	/**
	 * Edited and update a {@link com.timekeeping.data.Time} to database.
	 */
	private void updateTime( final int position, final int hourOfDay, final int minute ) {
		final RealmQuery<Time> query = mRealm.where( Time.class )
											 .equalTo(
													 "hour",
													 hourOfDay
											 )
											 .equalTo(
													 "minute",
													 minute
											 );
		final RealmResults<Time> results = query.findAllAsync();
		results.addChangeListener( new RealmChangeListener() {
			@Override
			public void onChange() {
				if( query.count() == 0 ) {
					mRealm.beginTransaction();
					mRealm.addChangeListener( new RealmChangeListener() {
						@Override
						public void onChange() {
							mBinding.getAdapter()
									.notifyItemChanged( position );
							mRealm.removeChangeListener( this );
						}
					} );
					mEditedTime.setHour( hourOfDay );
					mEditedTime.setMinute( minute );
					mRealm.copyToRealmOrUpdate( mEditedTime );
					mRealm.commitTransaction();
					mEdit = false;
					results.removeChangeListener( this );
				} else {
					showStatusMessage( getString( R.string.msg_duplicated_setting ) );
				}
			}
		} );
	}


	/**
	 * Edited and update a {@link com.timekeeping.data.Time}'s comment/task to database.
	 *
	 * @param position
	 * 		The position of {@link Time} to update.
	 */
	private void updateOthers( final int position ) {
		mRealm.addChangeListener( new RealmChangeListener() {
			@Override
			public void onChange() {
				mBinding.getAdapter()
						.notifyItemChanged( position );
				mRealm.removeChangeListener( this );
			}
		} );
		mRealm.copyToRealmOrUpdate( mEditedTime );
		mRealm.commitTransaction();
		mEdit = false;
	}


	/**
	 * Show a message after changing item on database.
	 *
	 * @param time
	 * 		The item that has been changed.
	 */
	private void showStatusMessage( Time time ) {
		String fmt = getString( time.isOnOff() ? R.string.on_status : R.string.off_status );
		String message = String.format(
				fmt,
				Utils.formatTime( time )
		);
		Snackbar.make(
				findViewById( R.id.error_content ),
				message,
				Snackbar.LENGTH_LONG
		)
				.show();
	}

	/**
	 * Show a message.
	 *
	 * @param msg
	 * 		Some text to show
	 */
	private void showStatusMessage( String msg ) {
		Snackbar.make(
				findViewById( R.id.error_content ),
				msg,
				Snackbar.LENGTH_LONG
		)
				.show();
	}

	@Override
	public void onTimeSet( RadialTimePickerDialogFragment dialog, int hourOfDay, int minute ) {
		if( mEdit && dialog.getTag() != null ) {
			int pos = Integer.parseInt( dialog.getTag()
											  .toString() );
			updateTime(
					pos,
					hourOfDay,
					minute
			);
		} else {
			insertNewTime(
					hourOfDay,
					minute
			);
		}
	}


	@Override
	protected void onAppConfigLoaded() {
		super.onAppConfigLoaded();
		addDrawerHeader();
		refreshGrid();
	}

	@Override
	protected void onAppConfigIgnored() {
		super.onAppConfigIgnored();
		addDrawerHeader();
		refreshGrid();
	}

	private void addDrawerHeader() {
		if( mBinding.navView.getHeaderCount() == 0 ) {
			mBinding.navView.addHeaderView( getLayoutInflater().inflate(
					R.layout.nav_header,
					mBinding.navView,
					false
			) );
		}
	}


	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance( getApplication() );
	}

	@Override
	protected void onPostCreate( Bundle savedInstanceState ) {
		super.onPostCreate( savedInstanceState );
		setErrorHandlerAvailable( false );
	}

	/**
	 * Initialize the navigation drawer.
	 */
	private void initDrawer() {
		ActionBar actionBar = getSupportActionBar();
		if( actionBar != null ) {
			actionBar.setHomeButtonEnabled( true );
			actionBar.setDisplayHomeAsUpEnabled( true );
			mBinding.drawerLayout.setDrawerListener( mDrawerToggle = new ActionBarDrawerToggle(
					this,
					mBinding.drawerLayout,
					R.string.application_name,
					R.string.application_name
			) );
		}
	}

	/**
	 * To confirm whether the validation of the Play-service of Google Inc.
	 */
	private void checkPlayService() {
		final int isFound = GooglePlayServicesUtil.isGooglePlayServicesAvailable( this );
		if( isFound == ConnectionResult.SUCCESS ) {//Ignore update.
			//The "End User License Agreement" must be confirmed before you use this application.
			if( !Prefs.getInstance( getApplication() )
					  .isEULAOnceConfirmed() ) {
				showDialogFragment(
						new EulaConfirmationDialog(),
						null
				);
			}
		} else {
			new Builder( this ).setTitle( R.string.application_name )
							   .setMessage( R.string.lbl_play_service )
							   .setCancelable( false )
							   .setPositiveButton(
									   R.string.btn_ok,
									   new DialogInterface.OnClickListener() {
										   public void onClick( DialogInterface dialog, int whichButton ) {
											   dialog.dismiss();
											   Intent intent = new Intent( Intent.ACTION_VIEW );
											   intent.setData( Uri.parse( getString( R.string.play_service_url ) ) );
											   try {
												   startActivity( intent );
											   } catch( ActivityNotFoundException e0 ) {
												   intent.setData( Uri.parse( getString( R.string.play_service_web ) ) );
												   try {
													   startActivity( intent );
												   } catch( Exception e1 ) {
													   //Ignore now.
												   }
											   } finally {
												   finish();
											   }
										   }
									   }
							   )
							   .create()
							   .show();
		}
	}

	/**
	 * Show  {@link android.support.v4.app.DialogFragment}.
	 *
	 * @param _dlgFrg
	 * 		An instance of {@link android.support.v4.app.DialogFragment}.
	 * @param _tagName
	 * 		Tag name for dialog, default is "dlg". To grantee that only one instance of {@link android.support.v4.app.DialogFragment} can been seen.
	 */
	protected void showDialogFragment( DialogFragment _dlgFrg, String _tagName ) {
		try {
			if( _dlgFrg != null ) {
				DialogFragment      dialogFragment = _dlgFrg;
				FragmentTransaction ft             = getSupportFragmentManager().beginTransaction();
				// Ensure that there's only one dialog to the user.
				Fragment prev = getSupportFragmentManager().findFragmentByTag( "dlg" );
				if( prev != null ) {
					ft.remove( prev );
				}
				try {
					if( TextUtils.isEmpty( _tagName ) ) {
						dialogFragment.show(
								ft,
								"dlg"
						);
					} else {
						dialogFragment.show(
								ft,
								_tagName
						);
					}
				} catch( Exception _e ) {
				}
			}
		} catch( Exception _e ) {
		}
	}


	@Override
	public void onDialogDismiss( DialogInterface dialoginterface ) {
		mBinding.addNewTimeBtn.show();
	}

	/**
	 * Invoke displayInterstitial() when you are ready to display an interstitial.
	 */
	public void displayInterstitial() {
		if( mInterstitialAd.isLoaded() ) {
			mInterstitialAd.show();
		}
	}


	/**
	 * Select items on view when opened action-mode.
	 *
	 * @param position
	 * 		The select position.
	 */
	private void toggleSelection( int position ) {
		mBinding.getAdapter()
				.toggleSelection( position );
		int count = mBinding.getAdapter()
							.getSelectedItemCount();

		if( count == 0 ) {
			mActionMode.finish();
		} else {
			mActionMode.setTitle( String.valueOf( count ) );
			mActionMode.invalidate();
		}
	}

	private void initGrid() {
		mBinding.scheduleGv.setLayoutManager( new GridLayoutManager(
				this,
				getResources().getInteger( R.integer.card_count )
		) );
		mBinding.scheduleGv.addOnScrollListener( new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled( RecyclerView recyclerView, int dx, int dy ) {
				float y = ViewCompat.getY( recyclerView );
				if( y < dy ) {
					if( mBinding.addNewTimeBtn.isShown() ) {
						mBinding.addNewTimeBtn.hide();
					}
				} else {
					if( !mBinding.addNewTimeBtn.isShown() ) {
						if( mBinding.getAdapter() != null && mBinding.getAdapter()
																	 .isActionMode() ) {
							return;
						}
						mBinding.addNewTimeBtn.show();
					}
				}
			}

		} );
		mBinding.setAdapter( new TimeKeepingListAdapter() );
	}

	private void initBar() {
		SpannableString s = new SpannableString( getString( R.string.application_name ) );
		s.setSpan(
				new TypefaceSpan(
						this,
						Fonts.FONT_LIGHT
				),
				0,
				s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
		);
		setSupportActionBar( mBinding.toolbar );
		mBinding.toolbar.setTitle( s );
	}

	/**
	 * Show single instance of {@link MainActivity}
	 *
	 * @param cxt
	 * 		{@link Context}.
	 */
	public static void showInstance( Activity cxt ) {
		Intent intent = new Intent(
				cxt,
				MainActivity.class
		);
		intent.setFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP );
		ActivityCompat.startActivity(
				cxt,
				intent,
				null
		);
	}

	@Override
	public void onBackPressed() {
		if( mBinding.drawerLayout.isDrawerOpen( GravityCompat.START ) || mBinding.drawerLayout.isDrawerOpen( GravityCompat.END ) ) {
			mBinding.drawerLayout.closeDrawers();
		} else {
			super.onBackPressed();
		}
	}


	private void initAds() {
		Prefs prefs    = Prefs.getInstance( getApplication() );
		int   curTime  = prefs.getShownDetailsTimes();
		int   adsTimes = 10;
		if( curTime % adsTimes == 0 ) {
			// Create an ad.
			mInterstitialAd = new InterstitialAd( this );
			mInterstitialAd.setAdUnitId( getString( R.string.ad_unit_id ) );
			// Create ad request.
			AdRequest adRequest = new AdRequest.Builder().build();
			// Begin loading your interstitial.
			mInterstitialAd.setAdListener( new AdListener() {
				@Override
				public void onAdLoaded() {
					super.onAdLoaded();
					displayInterstitial();
				}
			} );
			mInterstitialAd.loadAd( adRequest );
		}
		curTime++;
		prefs.setShownDetailsTimes( curTime );
	}


	private void initComponents() {
		mBinding = DataBindingUtil.setContentView(
				this,
				LAYOUT
		);
		setUpErrorHandling( (ViewGroup) findViewById( R.id.error_content ) );
		//FAB
		mBinding.addNewTimeBtn.setOnClickListener( this );
	}

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		mRealm = Realm.getInstance( App.Instance );
		super.onCreate( savedInstanceState );
		initComponents();
		initBar();
		initDrawer();
		initGrid();
		initAds();
	}

	@Override
	protected void onDestroy() {
		if( mTransaction != null && !mTransaction.isCancelled() ) {
			mTransaction.cancel();
		}
		if( mRealm != null ) {
			mRealm.removeAllChangeListeners();
			mRealm.close();
		}
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		if( mDrawerToggle != null ) {
			mDrawerToggle.syncState();
		}

		checkPlayService();
	}


}
