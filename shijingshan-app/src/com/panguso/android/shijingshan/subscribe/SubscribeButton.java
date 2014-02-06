package com.panguso.android.shijingshan.subscribe;

import com.panguso.android.shijingshan.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Represent a subscribe button.
 * 
 * @author Luo Yinzhuo
 */
public class SubscribeButton extends RelativeLayout implements OnTouchListener,
		OnClickListener {

	/**
	 * Interface definition for a callback when the {@link SubscribeButton} is
	 * clicked.
	 * 
	 * @author Luo Yinzhuo
	 */
	public interface OnSubscribeButtonListener {

		/**
		 * Called when the {@link SubscribeButton} is clicked.
		 * 
		 * @param id
		 *            The subscribe id.
		 * @param check
		 *            True if checked, otherwise false.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onClicked(int id, boolean check);
	}

	/** The subscribe id. */
	private final int mId;
	/** The button. */
	private final Button mButton;
	/** The check box. */
	private ImageView mCheckBox;
	/** The check flag. */
	private boolean mCheck;
	/** The listener. */
	private OnSubscribeButtonListener mListener;
	
	/**
	 * Construct a new instance.
	 * 
	 * @param context
	 *            The context.
	 * @param id
	 *            The subscribe column id.
	 * @param name
	 *            The subscribe column name.
	 * @param check
	 *            True if the column is subscribed, otherwise false.
	 */
	SubscribeButton(Context context, int id, String name, boolean check) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.subscribe_button_widget, this);

		mId = id;

		mButton = (Button) findViewById(R.id.button);
		mButton.setOnTouchListener(this);
		mButton.setOnClickListener(this);
		mButton.setText(name);

		mCheckBox = (ImageView) findViewById(R.id.check_box);
		setCheckBox(check);
	}

	/**
	 * Set the check box's image.
	 * 
	 * @param check
	 *            True if checked, otherwise false.
	 * 
	 * @author Luo Yinzhuo
	 */
	public void setCheckBox(boolean check) {
		mCheck = check;
		if (check) {
			mCheckBox.setBackgroundResource(R.drawable.check_box_check);
		} else {
			mCheckBox.setBackgroundResource(R.drawable.check_box_uncheck);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mCheckBox.setBackgroundResource(R.drawable.check_box_press);
			break;
		}
		return false;
	}

	/**
	 * Set the {@link OnSubscribeButtonListener}.
	 * 
	 * @param listener
	 *            The {@link OnSubscribeButtonListener}.
	 * 
	 * @author Luo Yinzhuo
	 */
	public void setOnSubscribeButtonListener(OnSubscribeButtonListener listener) {
		mListener = listener;
	}

	@Override
	public void onClick(View v) {
		if (mListener != null) {
			mListener.onClicked(mId, mCheck);
		}
	}

}
