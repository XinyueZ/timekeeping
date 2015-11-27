package com.timekeeping.app.fragments;

import java.io.Serializable;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.timekeeping.R;
import com.timekeeping.app.activities.MainActivity;
import com.timekeeping.data.Time;
import com.timekeeping.databinding.WakeUpBinding;

public final class WakeUpFragment extends Fragment {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_wake_up;

	private static final String EXTRAS_TIME = WakeUpFragment.class.getName() + ".EXTRAS.time";
	private static final String EXTRAS_IF_ERROR = WakeUpFragment.class.getName() + ".EXTRAS.if.error";
	private WakeUpBinding mBinding;

	public static WakeUpFragment newInstance(Context context, Time time, boolean ifError) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRAS_TIME, (Serializable) time);
		args.putBoolean(EXTRAS_IF_ERROR, ifError);
		return (WakeUpFragment) WakeUpFragment.instantiate(context, WakeUpFragment.class.getName(), args);
	}


	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}


	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Bundle args = getArguments();

		mBinding = DataBindingUtil.bind(view.findViewById(R.id.wake_up_ll));
		mBinding.setTime((Time) args.getSerializable(EXTRAS_TIME));
		mBinding.setIfError(args.getBoolean(EXTRAS_IF_ERROR));
		mBinding.wakeUpLl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.showInstance(getActivity());
				ActivityCompat.finishAfterTransition(getActivity());
			}
		});
	}


}
