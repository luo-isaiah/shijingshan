package com.panguso.android.shijingshan.column;

import com.panguso.android.shijingshan.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;

/**
 * Specific for the application start dialog.
 * 
 * @author Luo Yinzhuo
 */
public class StartDialog extends Dialog {

	/**
	 * Interface definition for a callback to be invoked when it is timed out or
	 * the back key is pressed.
	 * 
	 * @author Luo Yinzhuo
	 */
	public interface OnStartDialogListener {
		/**
		 * Called when the dialog is timed out.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onTimeout();
	}

	/** The listener. */
	private final OnStartDialogListener mListener;

	/** The start dialog timeout. */
	private static final int TIMEOUT = 5000;

	/** The timeout message ID. */
	private static final int MESSAGE_TIMEOUT = 0;
	/** The handler. */
	private final Handler mHandler = new StartDialogHandler(this);

	/**
	 * Specific {@link Handler} for this dialog to provide timeout function.
	 * 
	 * @author Luo Yinzhuo
	 */
	private static class StartDialogHandler extends Handler {
		/** The dialog. */
		private final StartDialog mDialog;

		/**
		 * Construct a new instance.
		 * 
		 * @param dialog
		 *            The dialog.
		 */
		public StartDialogHandler(StartDialog dialog) {
			mDialog = dialog;
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_TIMEOUT:
				mDialog.onTimeout();
				break;
			}
		}
	}

	/**
	 * Construct a new instance.
	 * 
	 * @param context
	 *            The context.
	 * @param listener
	 *            The listener.
	 */
	@SuppressWarnings("deprecation")
	public StartDialog(Context context, OnStartDialogListener listener) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.start_dialog);

		Window window = getWindow();
		window.setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		mListener = listener;
	}

	@Override
	public void show() {
		super.show();
		mHandler.sendEmptyMessageDelayed(MESSAGE_TIMEOUT, TIMEOUT);
	}

	@Override
	public void dismiss() {
		mHandler.removeMessages(MESSAGE_TIMEOUT);
		super.dismiss();
	}

	/**
	 * Invoked when the dialog is time out.
	 * 
	 * @author Luo Yinzhuo
	 */
	private void onTimeout() {
		mListener.onTimeout();
	}

	@Override
	public void onBackPressed() {
	}
}
