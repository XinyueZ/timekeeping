package com.timekeeping.app.adapters;

import java.util.List;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnDismissListener;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;

import com.timekeeping.BR;
import com.timekeeping.R;
import com.timekeeping.bus.DeleteTimeEvent;
import com.timekeeping.bus.EditTaskEvent;
import com.timekeeping.bus.EditTimeEvent;
import com.timekeeping.bus.SavedWeekDaysEvent;
import com.timekeeping.bus.SelectItemEvent;
import com.timekeeping.bus.StartActionModeEvent;
import com.timekeeping.bus.SwitchOnOffTimeEvent;
import com.timekeeping.data.Time;

import de.greenrobot.event.EventBus;


public final class TimeKeepingListAdapter extends SelectableAdapter<TimeKeepingListAdapter.ViewHolder> {
	/**
	 * Main layout for this component.
	 */
	private static final int ITEM_LAYOUT = R.layout.item_grid;
	/**
	 * Data-source.
	 */
	private List<Time> mVisibleData;


	public TimeKeepingListAdapter(List<Time> data) {
		setData(data);
	}

	/**
	 * Set data-source for list-view.
	 *
	 * @param data
	 * 		Data-source.
	 */
	public void setData(List<Time> data) {
		mVisibleData = data;
	}

	/**
	 * Get current used data-source.
	 *
	 * @return The data-source.
	 */
	public List<Time> getData() {
		return mVisibleData;
	}

	@Override
	public int getItemCount() {
		return mVisibleData == null ? 0 : mVisibleData.size();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		Context cxt = parent.getContext();
		LayoutInflater inflater = LayoutInflater.from(cxt);
		ViewDataBinding binding = DataBindingUtil.inflate(inflater, ITEM_LAYOUT, parent, false);
		return new TimeKeepingListAdapter.ViewHolder(binding);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {
		final Time entry = mVisibleData.get(position);
		holder.mOnOffV.setBackgroundResource(entry.isOnOff() ? R.drawable.ic_on:R.drawable.ic_off);
		holder.mCb.setVisibility(
				!isActionMode() ? View.INVISIBLE : isSelected(position) ? View.VISIBLE : View.INVISIBLE);
		holder.mBinding.setVariable(BR.time, entry);
		holder.mBinding.setVariable(BR.adapter, this);
		holder.mBinding.setVariable(BR.handler, new GridItemHandler(this, position, entry));
		holder.mBinding.executePendingBindings();
	}


	/**
	 * Get the object of {@link Time} whose "id" equals to {@code item} in the data-list.
	 *
	 * @param item
	 * 		The item to search.
	 *
	 * @return The object of the item. If not found <b>return null</b>.
	 */
	public Time findItem(Time item) {
		if (mVisibleData == null) {
			return null;
		}
		Time ret = null;
		for (Time i : mVisibleData) {
			if (i.getId() == item.getId()) {
				ret = i;
				break;
			}
		}
		return ret;
	}


	/**
	 * Edit a found item which has been cached by this {@link android.widget.Adapter}.
	 * <p/>
	 * It calls <b>{@link #notifyDataSetChanged()}</b> internally.
	 *
	 * @param oldEntry
	 * 		The item that has been cached.
	 * @param newEntry
	 * 		The item to edit.
	 */
	public void editItem(Time oldEntry, Time newEntry) {
		oldEntry.setId(newEntry.getId());
		oldEntry.setHour(newEntry.getHour());
		oldEntry.setMinute(newEntry.getMinute());
		oldEntry.setOnOff(newEntry.isOnOff());
		oldEntry.setTask(newEntry.getTask());
		oldEntry.setEditTime(newEntry.getEditTime());
		notifyDataSetChanged();
	}


	/**
	 * Remove a found item which has been cached by this {@link android.widget.Adapter}.
	 * <p/>
	 * When {@code itemFound} is null, nothing happens.
	 * <p/>
	 * It calls <b>{@link #notifyDataSetChanged()}</b> internally.
	 *
	 * @param item
	 * 		The item that has been cached.
	 * @param item
	 * 		The item to remove.
	 */
	public void removeItem(Time item) {
		if (item != null) {
			for (Time i : mVisibleData) {
				if (i.getId() == item.getId()) {
					mVisibleData.remove(i);
					notifyDataSetChanged();
					break;
				}
			}
		}
	}


	/**
	 * ViewHolder for the list.
	 */
	static class ViewHolder extends RecyclerView.ViewHolder {
		private ViewDataBinding mBinding;
		private View mOnOffV;
		private View mCb;

		ViewHolder(ViewDataBinding binding) {
			super(binding.getRoot());
			mOnOffV = binding.getRoot().findViewById(R.id.on_off_btn);
			mCb = binding.getRoot().findViewById(R.id.item_iv);
			View menuHolder = binding.getRoot().findViewById(R.id.week_days_btn);
			PopupMenu menuPopup = new PopupMenu(binding.getRoot().getContext(), menuHolder);
			menuPopup.inflate(R.menu.week_days);
			menuHolder.setTag(menuPopup);
			mBinding = binding;
		}
	}


	public static final class GridItemHandler {
		private TimeKeepingListAdapter mAdapter;
		private int mPosition;
		private Time mTime;

		public GridItemHandler(TimeKeepingListAdapter adapter, int position, Time time) {
			mAdapter = adapter;
			mPosition = position;
			mTime = time;
		}

		public boolean startActionModeEvent(View view) {
			if (!mAdapter.isActionMode()) {
				EventBus.getDefault().post(new StartActionModeEvent());
			}

			return true;
		}

		public void editTaskEvent(View view) {
			EventBus.getDefault().post(new EditTaskEvent(mAdapter.getData().get(mPosition)));
		}

		public void selectItemEvent(View view) {
			EventBus.getDefault().post(new SelectItemEvent(mPosition));
		}

		public void editTimeEvent(View view) {
			EventBus.getDefault().post(new EditTimeEvent(mTime));
		}

		public void switchOnOffTimeEvent(View view) {
			EventBus.getDefault().post(new SwitchOnOffTimeEvent(mTime));
		}

		public void deleteTimeEvent(View view) {
			EventBus.getDefault().post(new DeleteTimeEvent(mTime));
		}

		public void showMenu(View view) {
			OnMenuItemClickListener onMenuItemClickListener = new OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					item.setChecked(!item.isChecked());
					return true;
				}
			};
			PopupMenu menuPopup = (PopupMenu) view.getTag();
			Time time = mAdapter.getData().get(mPosition);
			menuPopup.show();
			Menu menu = menuPopup.getMenu();
			MenuItem day0 = menu.findItem(R.id.action_week_day_0);
			day0.setOnMenuItemClickListener(onMenuItemClickListener);
			MenuItem day1 = menu.findItem(R.id.action_week_day_1);
			day1.setOnMenuItemClickListener(onMenuItemClickListener);
			MenuItem day2 = menu.findItem(R.id.action_week_day_2);
			day2.setOnMenuItemClickListener(onMenuItemClickListener);
			MenuItem day3 = menu.findItem(R.id.action_week_day_3);
			day3.setOnMenuItemClickListener(onMenuItemClickListener);
			MenuItem day4 = menu.findItem(R.id.action_week_day_4);
			day4.setOnMenuItemClickListener(onMenuItemClickListener);
			MenuItem day5 = menu.findItem(R.id.action_week_day_5);
			day5.setOnMenuItemClickListener(onMenuItemClickListener);
			MenuItem day6 = menu.findItem(R.id.action_week_day_6);
			day6.setOnMenuItemClickListener(onMenuItemClickListener);

			day0.setChecked(false);
			day1.setChecked(false);
			day2.setChecked(false);
			day3.setChecked(false);
			day4.setChecked(false);
			day5.setChecked(false);
			day6.setChecked(false);
			String[] days = time.getWeekDays().split(",");
			for (String day : days) {
				switch (day) {
				case "0":
					day0.setChecked(true);
					break;
				case "1":
					day1.setChecked(true);
					break;
				case "2":
					day2.setChecked(true);
					break;
				case "3":
					day3.setChecked(true);
					break;
				case "4":
					day4.setChecked(true);
					break;
				case "5":
					day5.setChecked(true);
					break;
				case "6":
					day6.setChecked(true);
					break;
				}
			}

			menuPopup.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(PopupMenu menuPopup) {
					StringBuilder weekDays = new StringBuilder();
					Menu menu = menuPopup.getMenu();
					MenuItem day0 = menu.findItem(R.id.action_week_day_0);
					MenuItem day1 = menu.findItem(R.id.action_week_day_1);
					MenuItem day2 = menu.findItem(R.id.action_week_day_2);
					MenuItem day3 = menu.findItem(R.id.action_week_day_3);
					MenuItem day4 = menu.findItem(R.id.action_week_day_4);
					MenuItem day5 = menu.findItem(R.id.action_week_day_5);
					MenuItem day6 = menu.findItem(R.id.action_week_day_6);
					if (day0.isChecked()) {
						weekDays.append("0").append(',');
					}
					if (day1.isChecked()) {
						weekDays.append("1").append(',');
					}
					if (day2.isChecked()) {
						weekDays.append("2").append(',');
					}
					if (day3.isChecked()) {
						weekDays.append("3").append(',');
					}
					if (day4.isChecked()) {
						weekDays.append("4").append(',');
					}
					if (day5.isChecked()) {
						weekDays.append("5").append(',');
					}
					if (day6.isChecked()) {
						weekDays.append("6").append(',');
					}
					Time time = mAdapter.getData().get(mPosition);
					time.setWeekDays(weekDays.toString());
					EventBus.getDefault().post(new SavedWeekDaysEvent(time));
				}
			});
		}
	}
}
