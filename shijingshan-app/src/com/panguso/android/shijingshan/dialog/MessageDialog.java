/**
 * 
 */
package com.panguso.android.shijingshan.dialog;

import com.panguso.android.shijingshan.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Specific dialog for display a message.
 * 
 * @author Luo Yinzhuo
 * 
 */
public class MessageDialog extends Dialog implements
		android.view.View.OnClickListener {

	/**
	 * Interface definition for a callback to be invoked when the back key is
	 * pressed or the confirm button is clicked.
	 * 
	 * @author Luo Yinzhuo
	 */
	public interface OnMessageDialogListener {

		/**
		 * Called when the back key is pressed.
		 * 
		 * @param id
		 *            The dialog id.
		 * @author Luo Yinzhuo
		 */
		public void onMessageDialogBack(int id);

		/**
		 * Called when the confirm button is clicked.
		 * 
		 * @param id
		 *            The dialog id.
		 * @author Luo Yinzhuo
		 */
		public void onMessageDialogConfirmed(int id);
	}

	/** The dialog id. */
	private final int mId;
	/** The title. */
	private final TextView mTitle;
	/** The message. */
	private final TextView mMessage;
	/** The confirm button. */
	private final Button mConfirm;
	/** The listener. */
	private OnMessageDialogListener mListener;

	/**
	 * Construct a new instance.
	 * 
	 * @param context
	 *            The context.
	 * @param id
	 *            The dialog id.
	 * @param title
	 *            The title.
	 * @param message
	 *            The message.
	 * @param confirm
	 *            The confirm button text.
	 * @param listener
	 *            The listener.
	 */
	public MessageDialog(Context context, int id, String title, String message,
			String confirm, OnMessageDialogListener listener) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.message_dialog);

		mId = id;
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText(title);
		mMessage = (TextView) findViewById(R.id.message);
		mMessage.setText(message);
		mConfirm = (Button) findViewById(R.id.confirm);
		mConfirm.setText(confirm);
		mConfirm.setOnClickListener(this);
		mListener = listener;
	}

	@Override
	public void onBackPressed() {
		if (mListener != null) {
			mListener.onMessageDialogBack(mId);
		}
	}

	@Override
	public void onClick(View v) {
		if (mListener != null) {
			mListener.onMessageDialogConfirmed(mId);
		}
	}

}
