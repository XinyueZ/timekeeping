package com.timekeeping.app.adapters;

import java.util.LinkedList;
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
	private List<Time> mVisibleData = new LinkedList<>(  );

	/**
	 * Get current used data-source.
	 *
	 * @return The data-source.
	 */
	public List<Time> getData() {
		return mVisibleData;
	}

	/**
	 * Set data-source for list-view.
	 *
	 * @param data
	 * 		Data-source.
	 */
	public void setData( List<Time> data ) {
		mVisibleData = data;
	}

	@Override
	public int getItemCount() {
		return mVisibleData == null ? 0 : mVisibleData.size();
	}

	@Override
	public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
		Context        cxt      = parent.getContext();
		LayoutInflater inflater = LayoutInflater.from( cxt );
		ViewDataBinding binding = DataBindingUtil.inflate(
				inflater,
				ITEM_LAYOUT,
				parent,
				false
		);
		return new TimeKeepingListAdapter.ViewHolder( binding );
	}

	@Override
	public void onBindViewHolder( final ViewHolder holder, final int position ) {
		final Time entry = mVisibleData.get( position );
		holder.mCb.setVisibility( !isActionMode() ? View.INVISIBLE : isSelected( position ) ? View.VISIBLE : View.INVISIBLE );
		holder.mBinding.setVariable(
				BR.time,
				entry
		);
		holder.mBinding.setVariable(
				BR.adapter,
				this
		);
		holder.mBinding.setVariable(
				BR.handler,
				new GridItemHandler(
						holder,
						this
				)
		);
		holder.mBinding.executePendingBindings();
	}


	/**
	 * ViewHolder for the list.
	 */
	static class ViewHolder extends RecyclerView.ViewHolder {
		private ViewDataBinding mBinding;
		private View            mCb;

		ViewHolder( ViewDataBinding binding ) {
			super( binding.getRoot() );
			mCb = binding.getRoot()
						 .findViewById( R.id.item_iv );
			View menuHolder = binding.getRoot()
									 .findViewById( R.id.week_days_btn );
			PopupMenu menuPopup = new PopupMenu(
					binding.getRoot()
						   .getContext(),
					menuHolder
			);
			menuPopup.inflate( R.menu.week_days );
			menuHolder.setTag( menuPopup );
			mBinding = binding;
		}
	}


	public static final class GridItemHandler {
		private ViewHolder             mViewHolder;
		private TimeKeepingListAdapter mAdapter;

		public GridItemHandler( ViewHolder viewHolder, TimeKeepingListAdapter adapter ) {
			mViewHolder = viewHolder;
			mAdapter = adapter;
		}

		public boolean startActionModeEvent( View view ) {
			if( !mAdapter.isActionMode() ) {
				EventBus.getDefault()
						.post( new StartActionModeEvent() );
			}

			return true;
		}

		public void editTaskEvent( View view ) {
			int pos = mViewHolder.getAdapterPosition();
			if( pos != RecyclerView.NO_POSITION ) {
				EventBus.getDefault()
						.post( new EditTaskEvent(
								pos,
								mAdapter.getData()
										.get( pos )
						) );
			}
		}

		public void selectItemEvent( View view ) {
			int pos = mViewHolder.getAdapterPosition();
			if( pos != RecyclerView.NO_POSITION ) {
				EventBus.getDefault()
						.post( new SelectItemEvent( pos ) );
			}
		}

		public void editTimeEvent( View view ) {
			int pos = mViewHolder.getAdapterPosition();
			if( pos != RecyclerView.NO_POSITION ) {
				EventBus.getDefault()
						.post( new EditTimeEvent(
								pos,
								mAdapter.getData()
										.get( pos )
						) );
			}
		}

		public void switchOnOffTimeEvent( View view ) {
			int pos = mViewHolder.getAdapterPosition();
			if( pos != RecyclerView.NO_POSITION ) {
				EventBus.getDefault()
						.post( new SwitchOnOffTimeEvent(
								pos,
								mAdapter.getData()
										.get( pos )
						) );
			}
		}

		public void deleteTimeEvent( View view ) {
			int pos = mViewHolder.getAdapterPosition();
			if( pos != RecyclerView.NO_POSITION ) {
				EventBus.getDefault()
						.post( new DeleteTimeEvent(
								pos,
								mAdapter.getData()
										.get( pos )
						) );
			}
		}

		public void showMenu( View view ) {

			int pos = mViewHolder.getAdapterPosition();
			if( pos != RecyclerView.NO_POSITION ) {
				OnMenuItemClickListener onMenuItemClickListener = new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick( MenuItem item ) {
						item.setChecked( !item.isChecked() );
						return true;
					}
				};
				PopupMenu menuPopup = (PopupMenu) view.getTag();
				Time time = mAdapter.getData()
									.get( pos );
				menuPopup.show();
				Menu     menu = menuPopup.getMenu();
				MenuItem day0 = menu.findItem( R.id.action_week_day_0 );
				day0.setOnMenuItemClickListener( onMenuItemClickListener );
				MenuItem day1 = menu.findItem( R.id.action_week_day_1 );
				day1.setOnMenuItemClickListener( onMenuItemClickListener );
				MenuItem day2 = menu.findItem( R.id.action_week_day_2 );
				day2.setOnMenuItemClickListener( onMenuItemClickListener );
				MenuItem day3 = menu.findItem( R.id.action_week_day_3 );
				day3.setOnMenuItemClickListener( onMenuItemClickListener );
				MenuItem day4 = menu.findItem( R.id.action_week_day_4 );
				day4.setOnMenuItemClickListener( onMenuItemClickListener );
				MenuItem day5 = menu.findItem( R.id.action_week_day_5 );
				day5.setOnMenuItemClickListener( onMenuItemClickListener );
				MenuItem day6 = menu.findItem( R.id.action_week_day_6 );
				day6.setOnMenuItemClickListener( onMenuItemClickListener );

				day0.setChecked( false );
				day1.setChecked( false );
				day2.setChecked( false );
				day3.setChecked( false );
				day4.setChecked( false );
				day5.setChecked( false );
				day6.setChecked( false );
				String[] days = time.getWeekDays()
									.split( "," );
				for( String day : days ) {
					switch( day ) {
						case "0":
							day0.setChecked( true );
							break;
						case "1":
							day1.setChecked( true );
							break;
						case "2":
							day2.setChecked( true );
							break;
						case "3":
							day3.setChecked( true );
							break;
						case "4":
							day4.setChecked( true );
							break;
						case "5":
							day5.setChecked( true );
							break;
						case "6":
							day6.setChecked( true );
							break;
					}
				}

				menuPopup.setOnDismissListener( new OnDismissListener() {
					@Override
					public void onDismiss( PopupMenu menuPopup ) {
						StringBuilder weekDays = new StringBuilder();
						Menu          menu     = menuPopup.getMenu();
						MenuItem      day0     = menu.findItem( R.id.action_week_day_0 );
						MenuItem      day1     = menu.findItem( R.id.action_week_day_1 );
						MenuItem      day2     = menu.findItem( R.id.action_week_day_2 );
						MenuItem      day3     = menu.findItem( R.id.action_week_day_3 );
						MenuItem      day4     = menu.findItem( R.id.action_week_day_4 );
						MenuItem      day5     = menu.findItem( R.id.action_week_day_5 );
						MenuItem      day6     = menu.findItem( R.id.action_week_day_6 );
						if( day0.isChecked() ) {
							weekDays.append( "0" )
									.append( ',' );
						}
						if( day1.isChecked() ) {
							weekDays.append( "1" )
									.append( ',' );
						}
						if( day2.isChecked() ) {
							weekDays.append( "2" )
									.append( ',' );
						}
						if( day3.isChecked() ) {
							weekDays.append( "3" )
									.append( ',' );
						}
						if( day4.isChecked() ) {
							weekDays.append( "4" )
									.append( ',' );
						}
						if( day5.isChecked() ) {
							weekDays.append( "5" )
									.append( ',' );
						}
						if( day6.isChecked() ) {
							weekDays.append( "6" )
									.append( ',' );
						}

						int pos = mViewHolder.getAdapterPosition();
						if( pos != RecyclerView.NO_POSITION ) {
							Time time = mAdapter.getData()
												.get( pos );
							EventBus.getDefault()
									.post( new SavedWeekDaysEvent(
											pos,
											time,
											weekDays.toString()
									) );
						}
					}
				} );
			}
		}
	}
}
