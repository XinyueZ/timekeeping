package com.timekeeping.app.activities;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.LongSparseArray;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

import com.chopping.activities.BaseActivity;
import com.chopping.application.BasicPrefs;
import com.chopping.utils.DeviceUtils;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog.OnTimeSetListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.timekeeping.R;
import com.timekeeping.adapters.ItemsGridViewListAdapter;
import com.timekeeping.app.fragments.AboutDialogFragment;
import com.timekeeping.app.fragments.AppListImpFragment;
import com.timekeeping.app.services.TimekeepingService;
import com.timekeeping.data.Time;
import com.timekeeping.database.DB;
import com.timekeeping.database.DB.Sort;
import com.timekeeping.utils.ParallelTask;
import com.timekeeping.utils.Prefs;
import com.timekeeping.utils.Utils;

/**
 * The {@link MainActivity}.
 *
 * @author Xinyue Zhao
 */
public class MainActivity extends BaseActivity implements OnInitListener, OnClickListener, OnTimeSetListener,
		OnScrollListener, OnItemLongClickListener, Callback {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_main;
	/**
	 * Menu for the Action-Mode.
	 */
	private static final int ACTION_MODE_MENU = R.menu.action_mode;
	/**
	 * Holding all saved {@link  com.timekeeping.data.Time}s.
	 */
	private GridView mGv;
	/**
	 * {@link android.widget.Adapter} for {@link #mGv}.
	 */
	private ItemsGridViewListAdapter mAdp;
	/**
	 * Height of {@link android.support.v7.app.ActionBar}.
	 */
	private int mActionBarHeight;

	/**
	 * Navigation drawer.
	 */
	private DrawerLayout mDrawerLayout;

	/**
	 * Use navigation-drawer for this fork.
	 */
	private ActionBarDrawerToggle mDrawerToggle;
	/**
	 * Helper value to detect scroll direction of {@link android.widget.ListView} {@link #mGv}.
	 */
	private int mLastFirstVisibleItem;
	/**
	 * UI that is clicked to add new item.
	 */
	private View mAddNewV;
	/**
	 * The {@link android.support.v7.view.ActionMode}.
	 */
	private ActionMode mActionMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);
		getActionBarHeight();
		initDrawer();

		//Let all columns to equal.
		mGv = (GridView) findViewById(R.id.schedule_gv);
		int screenWidth = DeviceUtils.getScreenSize(this, 0).Width;
		mGv.setColumnWidth(screenWidth / 3);
		mGv.setOnScrollListener(this);

		//Init speech-framework.
		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, 0x1);

		//Add new item.
		mAddNewV = findViewById(R.id.add_new_time_btn);
		mAddNewV.setOnClickListener(this);

		//Adapter for grid and dummy data.
		mAdp = new ItemsGridViewListAdapter();
		mGv.setAdapter(mAdp);
		refreshGrid();

		mGv.setOnItemLongClickListener(this);
	}



	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (mAdp != null) {
			mAdp.actionModeBegin();
		}
		//The ActionMode is starting, add-button should not work.
		mAddNewV.setVisibility(View.GONE);
		//Start the ActionMode.
		if (!getSupportActionBar().isShowing()) {
			getSupportActionBar().show();
		}
		startSupportActionMode(this);
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0x1) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				startService(new Intent(getApplication(), TimekeepingService.class));
			} else {
				// missing data, install it
				Intent installIntent = new Intent();
				installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);

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
	public void onResume() {
		super.onResume();
		if (mDrawerToggle != null) {
			mDrawerToggle.syncState();
		}

		checkPlayService();
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
		mAddNewV.setVisibility(View.INVISIBLE);
		RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog.newInstance(this, 0, 0,
				DateFormat.is24HourFormat(this));
		timePickerDialog.show(getSupportFragmentManager(), null);
	}

	/**
	 * Refresh the data on the {@link #mGv}.
	 */
	private void refreshGrid() {
		new ParallelTask<Void, List<Time>, List<Time>>() {
			@Override
			protected List<Time> doInBackground(Void... params) {
				return DB.getInstance(getApplication()).getTimes(Sort.DESC);
			}

			@Override
			protected void onPostExecute(List<Time> times) {
				super.onPostExecute(times);
				mAdp.setItemList(times);
				mAdp.notifyDataSetChanged();
			}
		}.executeParallel();
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
		new ParallelTask<Time, Time, Time>() {
			@Override
			protected Time doInBackground(Time... params) {
				Time newTime = params[0];
				if (DB.getInstance(getApplication()).addTime(newTime)) {
					return newTime;
				} else {
					return null;
				}
			}

			@Override
			protected void onPostExecute(Time time) {
				super.onPostExecute(time);
				if (time != null) {
					sendBroadcast(new Intent(TimekeepingService.ACTION_UPDATE));
					refreshGrid();
					mAddNewV.setVisibility(View.VISIBLE);
				}
			}
		}.executeParallel(new Time(-1, hourOfDay, minute, -1, true));
	}

	@Override
	public void onTimeSet(RadialTimePickerDialog dialog, int hourOfDay, int minute) {
		insertNewTime(hourOfDay, minute);
	}


	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (view.getId() == mGv.getId()) {
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				if (!getSupportActionBar().isShowing()) {
					getSupportActionBar().show();
				}
			} else if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
				if (getSupportActionBar().isShowing()) {
					getSupportActionBar().hide();
				}
			}

			final int currentFirstVisibleItem = view.getFirstVisiblePosition();
			if (currentFirstVisibleItem > mLastFirstVisibleItem) {
				if (getSupportActionBar().isShowing()) {
					getSupportActionBar().hide();
				}
			} else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
				if (!getSupportActionBar().isShowing()) {
					getSupportActionBar().show();
				}
			}
			mLastFirstVisibleItem = currentFirstVisibleItem;
		}
	}


	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	}


	@Override
	protected void onAppConfigLoaded() {
		super.onAppConfigLoaded();
		showAppList();
	}

	@Override
	protected void onAppConfigIgnored() {
		super.onAppConfigIgnored();
		showAppList();
	}

	/**
	 * Show all external applications links.
	 */
	private void showAppList() {
		getSupportFragmentManager().beginTransaction().replace(R.id.app_list_fl, AppListImpFragment.newInstance(this))
				.commit();
	}

	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance(getApplication());
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		setErrorHandlerAvailable(true);
	}

	/**
	 * Initialize the navigation drawer.
	 */
	private void initDrawer() {
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
			mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.application_name,
					R.string.app_name) {
				@Override
				public void onDrawerSlide(View drawerView, float slideOffset) {
					super.onDrawerSlide(drawerView, slideOffset);
					if (!getSupportActionBar().isShowing()) {
						getSupportActionBar().show();
					}
				}
			};
			mDrawerLayout.setDrawerListener(mDrawerToggle);
			findViewById(R.id.drawer_header_v).getLayoutParams().height = mActionBarHeight;
		}
	}


	/**
	 * Calculate height of actionbar.
	 */
	private void getActionBarHeight() {
		int[] abSzAttr;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			abSzAttr = new int[] { android.R.attr.actionBarSize };
		} else {
			abSzAttr = new int[] { R.attr.actionBarSize };
		}
		TypedArray a = obtainStyledAttributes(abSzAttr);
		mActionBarHeight = a.getDimensionPixelSize(0, -1);
	}



	/**
	 * To confirm whether the validation of the Play-service of Google Inc.
	 */
	private void checkPlayService() {
		final int isFound = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (isFound == ConnectionResult.SUCCESS ||
				isFound == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {//Ignore update.
			//The "End User License Agreement" must be confirmed before you use this application.
			if (!Prefs.getInstance(getApplication()).isEULAOnceConfirmed()) {
				showDialogFragment(AboutDialogFragment.EulaConfirmationDialog.newInstance(this), null);
			}
		} else {
			new AlertDialog.Builder(this).setTitle(R.string.application_name).setMessage(R.string.lbl_play_service)
					.setCancelable(false).setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(getString(R.string.play_service_url)));
					startActivity(intent);
					finish();
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
	public boolean onCreateActionMode(android.support.v7.view.ActionMode actionMode, Menu menu) {
		actionMode.getMenuInflater().inflate(ACTION_MODE_MENU, menu);
		mActionMode = actionMode;
		mGv.setOnItemLongClickListener(null);
		return true;
	}

	@Override
	public boolean onPrepareActionMode(android.support.v7.view.ActionMode actionMode, Menu menu) {
		return false;
	}

	@Override
	public boolean onActionItemClicked(final android.support.v7.view.ActionMode actionMode, MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case R.id.action_delete: {
			new ParallelTask<Void, Void, LongSparseArray<Time>>() {
				@Override
				protected LongSparseArray<Time> doInBackground(Void... params) {
					DB db = DB.getInstance(getApplication());
					long key;
					Time item;
					LongSparseArray<Time> removedItems = mAdp.removeItems();
					for (int i = 0; removedItems != null && i < removedItems.size(); i++) {
						key = removedItems.keyAt(i);
						item = removedItems.get(key);
						db.removeTime(item);
					}
					return removedItems;
				}

				@Override
				protected void onPostExecute(LongSparseArray<Time> result) {
					super.onPostExecute(result);
					if (result == null) {
						if (mAdp != null) {
							mAdp.notifyDataSetChanged();
						}
					}
					mActionMode.finish();
					mActionMode = null;
				}
			}.executeParallel();
			break;
		}
		default:
			return false;
		}
		return true;
	}

	@Override
	public void onDestroyActionMode(android.support.v7.view.ActionMode actionMode) {
		mActionMode = null;
		if (mAdp != null) {
			mAdp.actionModeEnd();
		}
		mGv.setOnItemLongClickListener(this);
		mAddNewV.setVisibility(View.VISIBLE);
	}
}
