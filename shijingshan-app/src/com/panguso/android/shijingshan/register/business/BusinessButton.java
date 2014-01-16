package com.panguso.android.shijingshan.register.business;

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
 * Represent a business button.
 * 
 * @author Luo Yinzhuo
 * 
 */
public final class BusinessButton extends RelativeLayout implements
		OnClickListener, OnTouchListener {

	/**
	 * Interface definition for a callback when the {@link BusinessButton} is
	 * clicked.
	 * 
	 * @author luoyinzhuo
	 */
	public interface OnBusinessButtonListener {

		/**
		 * Called when the {@link BusinessButton} is clicked.
		 * 
		 * @param id
		 *            The business id.
		 * @param name
		 *            The business name.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onClicked(int id, String name);
	}

	/** The business id. */
	private final int mId;
	/** The button. */
	private final Button mButton;
	/** The arrow. */
	private ImageView mArrow;
	/** The listener. */
	private OnBusinessButtonListener mListener;

	/**
	 * @param context
	 */
	BusinessButton(Context context, int id, String name) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.business_button_widget, this);

		mId = id;

		mButton = (Button) findViewById(R.id.button);
		mButton.setOnTouchListener(this);
		mButton.setOnClickListener(this);
		mButton.setText(name);

		mArrow = (ImageView) findViewById(R.id.arrow);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mArrow.setBackgroundResource(R.drawable.arrow_press);
			break;
		case MotionEvent.ACTION_UP:
			mArrow.setBackgroundResource(R.drawable.arrow_normal);
		default:
			break;
		}
		return false;
	}

	/**
	 * Set the {@link OnBusinessButtonListener}.
	 * 
	 * @param listener
	 *            The {@link OnBusinessButtonListener}.
	 * 
	 * @author Luo Yinzhuo
	 */
	public void setOnBusinessButtonListener(OnBusinessButtonListener listener) {
		mListener = listener;
	}

	@Override
	public void onClick(View v) {
		if (mListener != null) {
			mListener.onClicked(mId, mButton.getText().toString());
		}
	}
}
