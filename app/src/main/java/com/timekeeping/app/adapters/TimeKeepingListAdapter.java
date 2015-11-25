package com.timekeeping.app.adapters;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

import com.timekeeping.BR;
import com.timekeeping.R;
import com.timekeeping.bus.DeleteTimeEvent;
import com.timekeeping.bus.EditTimeEvent;
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


	public TimeKeepingListAdapter(List<Time> data ) {
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
		holder.mBinding.setVariable(BR.time, entry);
		holder.mBinding.setVariable(BR.handler, new GridItemHandler(position, entry));
		holder.mBinding.executePendingBindings();
		holder.mCardView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (!isActionMode()) {
					EventBus.getDefault().post(new StartActionModeEvent());
				}
				return true;
			}
		});
	}



	/**
	 * Get the index of the item whose "id" equals to {@code item} in the data-list.
	 *
	 * @param item
	 * 		The item to search.
	 *
	 * @return The index(position) of the item. If not found <b>return -1</b>.
	 */
	public int getItemPosition(Time item) {
		if (mVisibleData == null) {
			return -1;
		}
		int index = -1;
		int pos = 0;
		for (Time i : mVisibleData) {
			if (i.getId() == item.getId()) {
				index = pos;
				break;
			}
			pos++;
		}
		return index;
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
	 * Add item into cached data of this {@link android.widget.Adapter}.
	 * <p/>
	 * It calls <b>{@link #notifyDataSetChanged()}</b> internally.
	 * <p/>
	 * It will also create an internal {@link java.util.LinkedList} when there's no cache {@link java.util.List}
	 * initialized.
	 *
	 * @param item
	 * 		The item to add.
	 */
	public void addItem(Time item) {
		if (mVisibleData == null) {
			mVisibleData = new LinkedList<>();
		}
		mVisibleData.add(item);
		notifyDataSetChanged();
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
		private View mCardView;

		ViewHolder(ViewDataBinding binding) {
			super(binding.getRoot());
			mCardView = binding.getRoot().findViewById(R.id.item_cv);
			mBinding = binding;
		}
	}


	public static final class GridItemHandler  {
		private int mPosition;
		private Time mTime;

		public GridItemHandler(int position, Time time) {
			mPosition = position;
			mTime = time;
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
	}
}
