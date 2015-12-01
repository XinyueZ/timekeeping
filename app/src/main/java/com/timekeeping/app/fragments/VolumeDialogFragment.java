package com.timekeeping.app.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.timekeeping.R;
import com.timekeeping.app.App;
import com.timekeeping.utils.Prefs;

public final class VolumeDialogFragment extends DialogFragment {

	public static DialogFragment newInstance( Context context ) {
		return (DialogFragment) VolumeDialogFragment.instantiate( context, VolumeDialogFragment.class.getName() );
	}

	@Override
	public Dialog onCreateDialog( Bundle savedInstanceState ) {
		int  vol    = Prefs.getInstance( App.Instance ).getVolume();
		View layout = View.inflate( App.Instance, R.layout.fragment_dialog_volume, null );

		final SeekBar seekbar = (SeekBar) layout.findViewById( R.id.vol_sb );
		seekbar.setProgress( vol );

		final TextView decTv = (TextView) layout.findViewById( R.id.vol_tv );
		decTv.setText( App.Instance.getResources().getStringArray( R.array.volumes )[ vol ] );

		seekbar.setOnSeekBarChangeListener( new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser ) {
				decTv.setText( App.Instance.getResources().getStringArray( R.array.volumes )[ progress ] );
			}

			@Override
			public void onStartTrackingTouch( SeekBar seekBar ) {

			}

			@Override
			public void onStopTrackingTouch( SeekBar seekBar ) {

			}
		} );
		return new AlertDialog.Builder( getActivity() ).setTitle( R.string.lbl_vol ).setView( layout ).setCancelable( true ).setPositiveButton(
				R.string.save_label, new DialogInterface.OnClickListener() {
					public void onClick( DialogInterface dialog, int whichButton ) {
						int vol = seekbar.getProgress();
						Prefs.getInstance( App.Instance ).setVolume( vol );
						ActivityCompat.invalidateOptionsMenu( getActivity() );
						dismiss();
					}
				} ).setNegativeButton( R.string.btn_cancel, null ).create();
	}
}
