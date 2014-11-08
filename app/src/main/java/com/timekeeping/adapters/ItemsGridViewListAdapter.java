package com.timekeeping.adapters;

import java.util.LinkedList;
import java.util.List;

import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFloat;
import com.timekeeping.R;
import com.timekeeping.data.Time;
import com.timekeeping.utils.Utils;


/**
 * The adapter for main {@link android.widget.ListView}.
 *
 * @author Xinyue Zhao
 */
public final class ItemsGridViewListAdapter extends BaseActionModeListAdapter<Time> {
	/**
	 * Data source.
	 */
	private List<Time> mItemList;


	/**
	 * Get data source, list of {@link Time}.
	 *
	 * @return The list of {@link Time}.
	 */
	public List<Time> getItemList() {
		return mItemList;
	}

	/**
	 * Set data source, list of {@link Time}.
	 * <p/>
	 * It's better to pass a  {@link java.util.LinkedList}.
	 *
	 * @param itemList
	 * 		The list of {@link Time}.
	 */
	public void setItemList(List<Time> itemList) {
		mItemList = itemList;
	}


	@Override
	public int getCount() {
		return mItemList == null ? 0 : mItemList.size();
	}

	@Override
	public Object getItem(int position) {
		return mItemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(getLayoutId(), parent, false);
			vh = createViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		Time time = mItemList.get(position);
		vh.mTimeTv.setText(Utils.formatTime(time));

		Resources resources = parent.getContext().getResources();
		vh.mOnOffBtn.setDrawableIcon(resources.getDrawable(time.isOnOff() ? R.drawable.ic_off : R.drawable.ic_on));

		super.getView(position, convertView, parent);
		return convertView;
	}

	/**
	 * Get {@link android.support.annotation.LayoutRes} of list.
	 *
	 * @return {@link android.support.annotation.LayoutRes}
	 */
	private int getLayoutId() {
		return R.layout.item_grid;
	}

	/**
	 * Create a  {@link ViewHolder} object variant.
	 *
	 * @param convertView
	 * 		The root {@link android.view.View} of item.
	 *
	 * @return A {@link  ViewHolder} object.
	 */
	private ViewHolder createViewHolder(View convertView) {
		return new ViewHolder(convertView);
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
		if (mItemList == null) {
			return -1;
		}
		int index = -1;
		int pos = 0;
		for (Time i : mItemList) {
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
		if (mItemList == null) {
			return null;
		}
		Time ret = null;
		for (Time i : mItemList) {
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
		if (mItemList == null) {
			mItemList = new LinkedList<Time>();
		}
		mItemList.add(item);
		notifyDataSetChanged();
	}

	/**
	 * Edit a found item which has been cached by this {@link android.widget.Adapter}.
	 * <p/>
	 * It calls <b>{@link #notifyDataSetChanged()}</b> internally.
	 *
	 * @param itemFound
	 * 		The item that has been cached.
	 * @param newItem
	 * 		The item to edit.
	 */
	public void editItem(Time itemFound, Time newItem) {
		itemFound.setId(newItem.getId());
		itemFound.setHour(newItem.getHour());
		itemFound.setMinute(newItem.getMinute());
		itemFound.setEditTime(newItem.getEditTime());
		notifyDataSetChanged();
	}

	/**
	 * Remove a found item which has been cached by this {@link android.widget.Adapter}.
	 * <p/>
	 * When {@code itemFound} is null, nothing happens.
	 * <p/>
	 * It calls <b>{@link #notifyDataSetChanged()}</b> internally.
	 *
	 * @param itemFound
	 * 		The item that has been cached.
	 * @param itemFound
	 * 		The item to remove.
	 */
	public void removeItem(Time itemFound) {
		if (itemFound != null) {
			for (Time i : mItemList) {
				if (i.getId() == itemFound.getId()) {
					mItemList.remove(i);
					notifyDataSetChanged();
					break;
				}
			}
		}
	}


	@Override
	protected List<Time> getDataSource() {
		return mItemList;
	}

	@Override
	protected long getItemKey(Time item) {
		return item.getId();
	}

	/**
	 * ViewHolder patter for {@link com.timekeeping.R.layout#item_grid}.
	 *
	 * @author Xinyue Zhao
	 */
	protected static class ViewHolder extends ViewHolderActionMode {
		CardView mGv;
		TextView mTimeTv;
		ButtonFloat mEditBtn;
		ButtonFloat mOnOffBtn;
		ButtonFloat mDeleteBtn;

		protected ViewHolder(View convertView) {
			super(convertView);
			mGv = (CardView) convertView.findViewById(R.id.cv);
			mTimeTv = (TextView) convertView.findViewById(R.id.time_tv);
			mEditBtn = (ButtonFloat) convertView.findViewById(R.id.edit_btn);
			mOnOffBtn = (ButtonFloat) convertView.findViewById(R.id.on_off_btn);
			mDeleteBtn = (ButtonFloat) convertView.findViewById(R.id.delete_btn);
		}
	}
}
