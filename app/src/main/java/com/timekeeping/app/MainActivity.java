package com.timekeeping.app;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;

import com.chopping.utils.DeviceUtils;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog.OnTimeSetListener;
import com.timekeeping.R;
import com.timekeeping.adapters.ItemsGridViewListAdapter;
import com.timekeeping.data.Time;
import com.timekeeping.database.DB;
import com.timekeeping.database.DB.Sort;
import com.timekeeping.utils.ParallelTask;

/**
 * The {@link com.timekeeping.app.MainActivity}.
 *
 * @author Xinyue Zhao
 */
public class MainActivity extends ActionBarActivity implements OnInitListener, OnClickListener, OnTimeSetListener, AbsListView.OnScrollListener {

	/**
	 * Holding all saved {@link  com.timekeeping.data.Time}s.
	 */
	private GridView mGridView;
	/**
	 * {@link android.widget.Adapter} for {@link #mGridView}.
	 */
	private ItemsGridViewListAdapter mAdp;


	/**
	 * Helper value to detect scroll direction of {@link android.widget.ListView} {@link #mGridView}.
	 */
	private int mLastFirstVisibleItem;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Let all columns to equal.
		mGridView = (GridView) findViewById(R.id.schedule_gv);
		int screenWidth = DeviceUtils.getScreenSize(this, 0).Width;
		mGridView.setColumnWidth(screenWidth / 3);
		mGridView.setOnScrollListener(this);

		//Init speech-framework.
		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, 0x1);

		findViewById(R.id.add_new_time_btn).setOnClickListener(this);
		//Adapter for grid and dummy data.
		mAdp = new ItemsGridViewListAdapter();
		mGridView.setAdapter(mAdp);
		refreshGrid();

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
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
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
		RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog.newInstance(this, 0, 0,
				DateFormat.is24HourFormat(this));
		timePickerDialog.show(getSupportFragmentManager(), null);
	}

	/**
	 * Refresh the data on the {@link #mGridView}.
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
				if(time != null) {
					sendBroadcast(new Intent(TimekeepingService.ACTION_UPDATE));
					refreshGrid();
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
		if (view.getId() == mGridView.getId()) {
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				if (!getSupportActionBar().isShowing()) {
					getSupportActionBar().show();
				}
			} else  if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
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
}
