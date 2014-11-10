package com.timekeeping.app.fragments;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.chopping.application.BasicPrefs;
import com.chopping.fragments.BaseFragment;
import com.timekeeping.R;
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

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setErrorHandlerAvailable(false);

		SwitchCompat cb = (SwitchCompat) view.findViewById(R.id.pause_resume_cb);
		cb.setShowText(true);
		cb.setChecked(Prefs.getInstance(getActivity().getApplication()).areAllPaused());
		cb.setOnCheckedChangeListener(this);
	}

	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance(getActivity().getApplication());
	}


	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Prefs.getInstance(getActivity().getApplication()).setPauseAll(isChecked);
	}
}
