package com.panguso.android.shijingshan.dialog;

import com.panguso.android.shijingshan.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

/**
 * Specific for the waiting dialog.
 * 
 * @author Luo Yinzhuo
 */
public class WaitingDialog extends Dialog {

	/**
	 * Interface definition for a callback to be invoked when the back key is
	 * pressed.
	 * 
	 * @author Luo Yinzhuo
	 */
	public interface OnWaitingDialogListener {
		
		/**
		 * Called when the back key is pressed.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onWaitingDialogBack();
	}
	
	/** The listener. */
	private final OnWaitingDialogListener mListener;

	/**
	 * Construct a new instance.
	 * 
	 * @param context
	 */
	public WaitingDialog(Context context, OnWaitingDialogListener listener) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.waiting_dialog);
		
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
		mListener = listener;
	}

	@Override
	public void onBackPressed() {
		mListener.onWaitingDialogBack();
	}
}
