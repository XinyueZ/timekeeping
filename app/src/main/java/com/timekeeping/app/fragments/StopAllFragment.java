package com.timekeeping.app.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.chopping.application.BasicPrefs;
import com.chopping.fragments.BaseFragment;
import com.timekeeping.R;
import com.timekeeping.app.App;
import com.timekeeping.databinding.StopAllBinding;
import com.timekeeping.utils.Prefs;

/**
 * {@link StopAllFragment} contains a {@link android.support.v7.widget.SwitchCompat} to stop or restart all
 * timekeepings.
 *
 * @author Xinyue Zhao
 */
public final class StopAllFragment extends BaseFragment implements OnCheckedChangeListener {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_stop_all;

	private StopAllBinding mBinding;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setErrorHandlerAvailable(false);

		boolean paused = Prefs.getInstance(App.Instance).areAllPaused();
		mBinding = DataBindingUtil.bind(view.findViewById(R.id.stop_all_rl));
		mBinding.setIsChecked(paused);
		mBinding.pauseResumeCb.setChecked(paused);
		mBinding.pauseResumeCb.setOnCheckedChangeListener(this);
	}

	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance(getActivity().getApplication());
	}


	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Prefs.getInstance(getActivity().getApplication()).setPauseAll(isChecked);
		mBinding.setIsChecked(isChecked);
		Snackbar.make(mBinding.stopAllRl, isChecked ? R.string.msg_pause_all : R.string.msg_play_all, Snackbar.LENGTH_LONG).show();
		if(isChecked) {
			App.stopAppGuardService();
		} else {
			App.startAppGuardService();
		}
	}
}
