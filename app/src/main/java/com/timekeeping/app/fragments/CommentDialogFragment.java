package com.timekeeping.app.fragments;

import java.io.Serializable;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;

import com.timekeeping.R;
import com.timekeeping.app.App;
import com.timekeeping.bus.SaveCommentEvent;
import com.timekeeping.data.Time;

import de.greenrobot.event.EventBus;

public final class CommentDialogFragment extends DialogFragment {
	private static final String EXTRAS_TIME = CommentDialogFragment.class.getName() + ".EXTRAS.time";
	private static final String EXTRAS_POSITION = CommentDialogFragment.class.getName() + ".EXTRAS.position";

	public static DialogFragment newInstance( Context context,int position, Time time ) {
		Bundle args = new Bundle();
		args.putInt(
				EXTRAS_POSITION,
				position
		);
		args.putSerializable(
				EXTRAS_TIME,
				(Serializable) time
		);
		return (DialogFragment) CommentDialogFragment.instantiate(
				context,
				CommentDialogFragment.class.getName(),
				args
		);
	}

	@Override
	public Dialog onCreateDialog( Bundle savedInstanceState ) {
		final EditText content = new EditText( App.Instance );
		Resources      resources = App.Instance.getResources();
		int            rightleft = resources.getDimensionPixelSize( R.dimen.activity_edit_content );
		int            topbottom = resources.getDimensionPixelSize( R.dimen.activity_vertical_margin );
		content.setTextColor( ContextCompat.getColor(
				App.Instance,
				R.color.common_black
		) );
		content.setInputType( InputType.TYPE_TEXT_FLAG_MULTI_LINE );
		content.setPadding(
				rightleft,
				topbottom,
				rightleft,
				topbottom
		);
		Time editedTime = (Time) getArguments().getSerializable( EXTRAS_TIME );
		if( editedTime != null ) {
			content.setText( editedTime.getTask() );
		}
		return new AlertDialog.Builder( getActivity() ).setTitle( R.string.lbl_comment )
													   .setView( content )
													   .setCancelable( true )
													   .setPositiveButton(
															   R.string.save_label,
															   new DialogInterface.OnClickListener() {
																   public void onClick( DialogInterface dialog, int whichButton ) {
																	   Time editedTime = (Time) getArguments().getSerializable( EXTRAS_TIME );
																	   if( editedTime != null ) {
																		   EventBus.getDefault()
																				   .post( new SaveCommentEvent(
																						   getArguments().getInt( EXTRAS_POSITION ),
																						   editedTime,
																						   content.getText().toString() ) );
																	   }
																	   dismiss();
																   }
															   }
													   )
													   .setNegativeButton(
															   R.string.btn_cancel,
															   null
													   )
													   .create();
	}
}
