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
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.os.AsyncTaskCompat;
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
import com.timekeeping.app.fragments.CommentFragment;
import com.timekeeping.bus.DeleteTimeEvent;
import com.timekeeping.bus.EULAConfirmedEvent;
import com.timekeeping.bus.EULARejectEvent;
import com.timekeeping.bus.EditTaskEvent;
import com.timekeeping.bus.EditTimeEvent;
import com.timekeeping.bus.SavedTaskEvent;
import com.timekeeping.bus.SelectItemEvent;
import com.timekeeping.bus.StartActionModeEvent;
import com.timekeeping.bus.SwitchOnOffTimeEvent;
import com.timekeeping.data.Time;
import com.timekeeping.database.DB;
import com.timekeeping.database.DB.Sort;
import com.timekeeping.databinding.MainBinding;
import com.timekeeping.utils.Prefs;
import com.timekeeping.utils.TypefaceSpan;
import com.timekeeping.utils.Utils;
import com.timekeeping.widget.FontTextView.Fonts;

/**
 * The {@link MainActivity}.
 *
 * @author Xinyue Zhao
 */
public class MainActivity extends BaseActivity implements OnInitListener, OnClickListener, OnTimeSetListener,
		OnDialogDismissListener {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_main;
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
	private ActionMode mActionMode;
	/**
	 * Edit a item  or  not.
	 */
	private boolean mEdit;
	/**
	 * {@link Time} to edit.
	 */
	private Time mEditedTime;
	/**
	 * The interstitial ad.
	 */
	private InterstitialAd mInterstitialAd;
	/**
	 * Data-binding.
	 */
	private MainBinding mBinding;

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link  EULARejectEvent}.
	 *
	 * @param e
	 * 		Event {@link  EULARejectEvent}.
	 */
	public void onEvent(EULARejectEvent e) {
		ActivityCompat.finishAfterTransition(this);
	}

	/**
	 * Handler for {@link EULAConfirmedEvent}
	 *
	 * @param e
	 * 		Event {@link  EULAConfirmedEvent}.
	 */
	public void onEvent(EULAConfirmedEvent e) {

	}


	/**
	 * Handler for {@link DeleteTimeEvent}.
	 *
	 * @param e
	 * 		Event {@link DeleteTimeEvent}.
	 */
	public void onEvent(DeleteTimeEvent e) {
		AsyncTaskCompat.executeParallel(new AsyncTask<Time, Time, Time>() {
			@Override
			protected Time doInBackground(Time... params) {
				Time time = params[0];
				if (time != null) {
					DB.getInstance(getApplication()).removeTime(time);
				}
				return time;
			}

			@Override
			protected void onPostExecute(Time time) {
				super.onPostExecute(time);
				mBinding.getAdapter().removeItem(time);
			}
		}, e.getTime());
	}

	/**
	 * Handler for {@link EditTimeEvent}.
	 *
	 * @param e
	 * 		Event {@link EditTimeEvent}.
	 */
	public void onEvent(EditTimeEvent e) {
		editTime(e.getTime());
	}


	/**
	 * Handler for {@link SwitchOnOffTimeEvent}.
	 *
	 * @param e
	 * 		Event {@link SwitchOnOffTimeEvent}.
	 */
	public void onEvent(SwitchOnOffTimeEvent e) {
		setTimeOnOff(e.getTime());
	}


	/**
	 * Handler for {@link }.
	 *
	 * @param e
	 * 		Event {@link}.
	 */
	public void onEvent(CloseDrawerEvent e) {
		mBinding.drawerLayout.closeDrawers();
	}


	/**
	 * Handler for {@link SelectItemEvent}.
	 *
	 * @param e
	 * 		Event {@link SelectItemEvent}.
	 */
	public void onEvent(SelectItemEvent e) {
		toggleSelection(e.getPosition());
	}

	/**
	 * Handler for {@link StartActionModeEvent}.
	 *
	 * @param e
	 * 		Event {@link  StartActionModeEvent}.
	 */
	public void onEvent(StartActionModeEvent e) {
		//See more about action-mode.
		//http://databasefaq.com/index.php/answer/19065/android-android-fragments-recyclerview-android-actionmode-problems-with-implementing-contextual-action-mode-in-recyclerview-fragment
		mActionMode = startSupportActionMode(new Callback() {
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				mode.getMenuInflater().inflate(ACTION_MODE_MENU, menu);
				mBinding.toolbar.setVisibility(View.GONE);
				mBinding.errorContent.setStatusBarBackgroundColor(R.color.primary_dark_color);
				mBinding.getAdapter().setActionMode(true);
				mBinding.getAdapter().notifyDataSetChanged();
				return true;
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				AsyncTaskCompat.executeParallel(new AsyncTask<List<Integer>, Void, Void>() {
					@Override
					protected Void doInBackground(List<Integer>... params) {
						List<Integer> selectedItems = params[0];
						List<Time> selectedTimes = new ArrayList<>();
						for (Integer pos : selectedItems) {
							selectedTimes.add(mBinding.getAdapter().getData().get(pos));
						}
						DB db = DB.getInstance(getApplication());
						for (Time delTime : selectedTimes) {
							db.removeTime(delTime);
						}

						for (Time delTime : selectedTimes) {
							mBinding.getAdapter().getData().remove(delTime);
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						super.onPostExecute(result);
						mBinding.getAdapter().notifyDataSetChanged();
						mActionMode.finish();
					}
				}, mBinding.getAdapter().getSelectedItems());
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				mActionMode = null;
				mBinding.toolbar.setVisibility(View.VISIBLE);

				mBinding.getAdapter().clearSelection();
				mBinding.getAdapter().setActionMode(false);
				mBinding.getAdapter().notifyDataSetChanged();
			}
		});
	}

	/**
	 * Handler for {@link com.timekeeping.bus.EditTaskEvent}.
	 *
	 * @param e
	 * 		Event {@link com.timekeeping.bus.EditTaskEvent}.
	 */
	public void onEvent(EditTaskEvent e) {
		showDialogFragment(CommentFragment.newInstance(App.Instance, e.getTime()), null);
	}


	/**
	 * Handler for {@link com.timekeeping.bus.SavedTaskEvent}.
	 *
	 * @param e
	 * 		Event {@link com.timekeeping.bus.SavedTaskEvent}.
	 */
	public void onEvent(SavedTaskEvent e) {
		mEditedTime = e.getTime();
		updateTask();
	}
	//------------------------------------------------


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(MENU_MAIN, menu);
		MenuItem menuShare = menu.findItem(R.id.action_share_app);
		//Getting the actionprovider associated with the menu item whose id is share.
		android.support.v7.widget.ShareActionProvider provider =
				(android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(menuShare);
		//Setting a share intent.
		String subject = getString(R.string.lbl_share_app_title, getString(R.string.application_name));
		String text = getString(R.string.lbl_share_app_content, getString(R.string.tray_info));
		provider.setShareIntent(Utils.getDefaultShareIntent(provider, subject, text));

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		int id = item.getItemId();
		switch (id) {
		case R.id.action_about:
			showDialogFragment(AboutDialogFragment.newInstance(this), null);
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onInit(int status) {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
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
		RadialTimePickerDialogFragment timePickerDialog = RadialTimePickerDialogFragment.newInstance(this, 0, 0,
				DateFormat.is24HourFormat(this));
		timePickerDialog.setOnDismissListener(this);
		timePickerDialog.show(getSupportFragmentManager(), null);
	}

	/**
	 * Edit a entry of {@link com.timekeeping.data.Time} to database.
	 */
	private void editTime() {
		mBinding.addNewTimeBtn.show();
		RadialTimePickerDialogFragment timePickerDialog = RadialTimePickerDialogFragment.newInstance(this,
				mEditedTime.getHour(), mEditedTime.getMinute(), DateFormat.is24HourFormat(this));
		timePickerDialog.setOnDismissListener(this);
		timePickerDialog.show(getSupportFragmentManager(), null);
	}

	/**
	 * Start to edit a {@link com.timekeeping.data.Time}
	 *
	 * @param timeToEdit
	 * 		The object to edit.
	 */
	private void editTime(Time timeToEdit) {
		mEdit = true;
		mEditedTime = timeToEdit;
		if (mEditedTime != null) {
			editTime();
		}
	}

	/**
	 * Set on/off status of the time. It is toggled.
	 *
	 * @param timeToSet
	 * 		The object to set.
	 */
	private void setTimeOnOff(Time timeToSet) {
		mEdit = true;
		mEditedTime = timeToSet;
		mEditedTime.setOnOff(!mEditedTime.isOnOff());
		switchTimeOnOff();
	}

	private void refreshGrid() {
		AsyncTaskCompat.executeParallel(new AsyncTask<Void, List<Time>, List<Time>>() {
			@Override
			protected List<Time> doInBackground(Void... params) {
				return DB.getInstance(getApplication()).getTimes(Sort.DESC);
			}

			@Override
			protected void onPostExecute(List<Time> times) {
				super.onPostExecute(times);
				mBinding.getAdapter().setData(times);
				mBinding.getAdapter().notifyDataSetChanged();
			}
		});
	}


	/**
	 * Insert a {@link com.timekeeping.data.Time} to database.
	 *
	 * @param hourOfDay
	 * 		Hour
	 * @param minute
	 * 		Minute.
	 */
	private void insertNewTime(int hourOfDay, int minute) {
		AsyncTaskCompat.executeParallel(new AsyncTask<Time, Time, Time>() {
			@Override
			protected Time doInBackground(Time... params) {
				Time newTime = params[0];
				DB db = DB.getInstance(getApplication());
				boolean find = db.findTime(newTime);
				if (!find && db.addTime(newTime)) {
					return newTime;
				} else {
					return null;
				}
			}

			@Override
			protected void onPostExecute(Time time) {
				super.onPostExecute(time);
				if (time != null) {
					refreshGrid();
					showStatusMessage(time);
					mBinding.scheduleGv.getLayoutManager().scrollToPosition(0);
				}
			}
		}, new Time(-1, hourOfDay, minute, -1, true));
	}


	/**
	 * Edited and update a {@link com.timekeeping.data.Time} to database.
	 */
	private void updateTime() {
		AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Time>() {
			@Override
			protected Time doInBackground(Void... params) {
				DB db = DB.getInstance(getApplication());
				boolean find = db.findTime(mEditedTime);
				if (!find && db.updateTime(mEditedTime)) {
					return mBinding.getAdapter().findItem(mEditedTime);
				} else {
					return null;
				}
			}

			@Override
			protected void onPostExecute(Time oldEntry) {
				super.onPostExecute(oldEntry);
				if (oldEntry != null) {
					mBinding.getAdapter().editItem(oldEntry, mEditedTime);
					mEdit = false;
					showStatusMessage(mEditedTime);
				}
			}
		});
	}



	/**
	 * Edited and update a {@link com.timekeeping.data.Time}'s comment/task to database.
	 */
	private void updateTask() {
		AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Time>() {
			@Override
			protected Time doInBackground(Void... params) {
				DB db = DB.getInstance(getApplication());
				boolean find = db.findTime(mEditedTime);
				if (find && db.updateTime(mEditedTime)) {
					return mBinding.getAdapter().findItem(mEditedTime);
				} else {
					return null;
				}
			}

			@Override
			protected void onPostExecute(Time oldEntry) {
				super.onPostExecute(oldEntry);
				if (oldEntry != null) {
					mBinding.getAdapter().editItem(oldEntry, mEditedTime);
					mEdit = false;
					showStatusMessage(mEditedTime);
				}
			}
		});
	}




	/**
	 * Edited and update a {@link com.timekeeping.data.Time} to database.
	 */
	private void switchTimeOnOff() {
		AsyncTaskCompat.executeParallel(new AsyncTask<Void, Time, Time>() {
			@Override
			protected Time doInBackground(Void... params) {
				if (DB.getInstance(getApplication()).updateTime(mEditedTime)) {
					return mBinding.getAdapter().findItem(mEditedTime);
				} else {
					return null;
				}
			}

			@Override
			protected void onPostExecute(Time oldEntry) {
				super.onPostExecute(oldEntry);
				if (oldEntry != null) {
					mBinding.getAdapter().editItem(oldEntry, mEditedTime);
					mEdit = false;

					showStatusMessage(mEditedTime);
				}
			}
		});
	}

	/**
	 * Show a message after changing item on database.
	 *
	 * @param time
	 * 		The item that has been changed.
	 */
	private void showStatusMessage(Time time) {
		String fmt = getString(time.isOnOff() ? R.string.on_status : R.string.off_status);
		String message = String.format(fmt, Utils.formatTime(time));
		Snackbar.make(findViewById(R.id.error_content), message, Snackbar.LENGTH_LONG).show();
	}

	@Override
	public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
		if (mEdit) {
			mEditedTime.setHour(hourOfDay);
			mEditedTime.setMinute(minute);
			updateTime();
		} else {
			insertNewTime(hourOfDay, minute);
		}
	}


	@Override
	protected void onAppConfigLoaded() {
		super.onAppConfigLoaded();
		addDrawerHeader();
	}

	@Override
	protected void onAppConfigIgnored() {
		super.onAppConfigIgnored();
		addDrawerHeader();
	}

	private void addDrawerHeader() {
		if (mBinding.navView.getHeaderCount() == 0) {
			mBinding.navView.addHeaderView(getLayoutInflater().inflate(R.layout.nav_header, mBinding.navView, false));
		}
	}


	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance(getApplication());
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		setErrorHandlerAvailable(false);
	}

	/**
	 * Initialize the navigation drawer.
	 */
	private void initDrawer() {
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
			mBinding.drawerLayout.setDrawerListener(mDrawerToggle =
					new ActionBarDrawerToggle(this, mBinding.drawerLayout, R.string.application_name,
							R.string.application_name));
		}
	}

	/**
	 * To confirm whether the validation of the Play-service of Google Inc.
	 */
	private void checkPlayService() {
		final int isFound = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (isFound == ConnectionResult.SUCCESS) {//Ignore update.
			//The "End User License Agreement" must be confirmed before you use this application.
			if (!Prefs.getInstance(getApplication()).isEULAOnceConfirmed()) {
				showDialogFragment(new EulaConfirmationDialog(), null);
			}
		} else {
			new Builder(this).setTitle(R.string.application_name).setMessage(R.string.lbl_play_service).setCancelable(
					false).setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(getString(R.string.play_service_url)));
					try {
						startActivity(intent);
					} catch (ActivityNotFoundException e0) {
						intent.setData(Uri.parse(getString(R.string.play_service_web)));
						try {
							startActivity(intent);
						} catch (Exception e1) {
							//Ignore now.
						}
					} finally {
						finish();
					}
				}
			}).create().show();
		}
	}

	/**
	 * Show  {@link android.support.v4.app.DialogFragment}.
	 *
	 * @param _dlgFrg
	 * 		An instance of {@link android.support.v4.app.DialogFragment}.
	 * @param _tagName
	 * 		Tag name for dialog, default is "dlg". To grantee that only one instance of {@link
	 * 		android.support.v4.app.DialogFragment} can been seen.
	 */
	protected void showDialogFragment(DialogFragment _dlgFrg, String _tagName) {
		try {
			if (_dlgFrg != null) {
				DialogFragment dialogFragment = _dlgFrg;
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				// Ensure that there's only one dialog to the user.
				Fragment prev = getSupportFragmentManager().findFragmentByTag("dlg");
				if (prev != null) {
					ft.remove(prev);
				}
				try {
					if (TextUtils.isEmpty(_tagName)) {
						dialogFragment.show(ft, "dlg");
					} else {
						dialogFragment.show(ft, _tagName);
					}
				} catch (Exception _e) {
				}
			}
		} catch (Exception _e) {
		}
	}


	@Override
	public void onDialogDismiss(DialogInterface dialoginterface) {
		mBinding.addNewTimeBtn.show();
	}

	/**
	 * Invoke displayInterstitial() when you are ready to display an interstitial.
	 */
	public void displayInterstitial() {
		if (mInterstitialAd.isLoaded()) {
			mInterstitialAd.show();
		}
	}


	/**
	 * Select items on view when opened action-mode.
	 *
	 * @param position
	 * 		The select position.
	 */
	private void toggleSelection(int position) {
		mBinding.getAdapter().toggleSelection(position);
		int count = mBinding.getAdapter().getSelectedItemCount();

		if (count == 0) {
			mActionMode.finish();
		} else {
			mActionMode.setTitle(String.valueOf(count));
			mActionMode.invalidate();
		}
	}

	private void initGrid() {
		mBinding.scheduleGv.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.card_count)));
		mBinding.scheduleGv.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				float y = ViewCompat.getY(recyclerView);
				if (y < dy) {
					if (mBinding.addNewTimeBtn.isShown()) {
						mBinding.addNewTimeBtn.hide();
					}
				} else {
					if (!mBinding.addNewTimeBtn.isShown()) {
						mBinding.addNewTimeBtn.show();
					}
				}
			}

		});
		mBinding.setAdapter(new TimeKeepingListAdapter(null));
		refreshGrid();
	}

	private void initBar() {
		SpannableString s = new SpannableString(getString(R.string.application_name));
		s.setSpan(new TypefaceSpan(this, Fonts.FONT_LIGHT), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		setSupportActionBar(mBinding.toolbar);
		mBinding.toolbar.setTitle(s);
	}

	/**
	 * Show single instance of {@link MainActivity}
	 *
	 * @param cxt
	 * 		{@link Context}.
	 */
	public static void showInstance(Activity cxt) {
		Intent intent = new Intent(cxt, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		ActivityCompat.startActivity(cxt, intent, null);
	}

	@Override
	public void onBackPressed() {
		if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START) || mBinding.drawerLayout.isDrawerOpen(
				GravityCompat.END)) {
			mBinding.drawerLayout.closeDrawers();
		} else {
			super.onBackPressed();
		}
	}


	private void initAds() {
		Prefs prefs = Prefs.getInstance(getApplication());
		int curTime = prefs.getShownDetailsTimes();
		int adsTimes = 10;
		if (curTime % adsTimes == 0) {
			// Create an ad.
			mInterstitialAd = new InterstitialAd(this);
			mInterstitialAd.setAdUnitId(getString(R.string.ad_unit_id));
			// Create ad request.
			AdRequest adRequest = new AdRequest.Builder().build();
			// Begin loading your interstitial.
			mInterstitialAd.setAdListener(new AdListener() {
				@Override
				public void onAdLoaded() {
					super.onAdLoaded();
					displayInterstitial();
				}
			});
			mInterstitialAd.loadAd(adRequest);
		}
		curTime++;
		prefs.setShownDetailsTimes(curTime);
	}


	private void initComponents() {
		mBinding = DataBindingUtil.setContentView(this, LAYOUT);
		setUpErrorHandling((ViewGroup) findViewById(R.id.error_content));
		//FAB
		mBinding.addNewTimeBtn.setOnClickListener(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initComponents();
		initBar();
		initDrawer();
		initGrid();
		initAds();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mDrawerToggle != null) {
			mDrawerToggle.syncState();
		}

		checkPlayService();
	}


}
