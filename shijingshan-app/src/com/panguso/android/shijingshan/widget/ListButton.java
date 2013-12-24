package com.panguso.android.shijingshan.widget;

import com.panguso.android.shijingshan.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Specific for the list button widget.
 * 
 * @author Luo Yinzhuo
 */
public class ListButton extends RelativeLayout implements OnTouchListener, OnClickListener {

	/**
	 * Interface definition for a callback to be invoked when a
	 * {@link ListButton} is clicked.
	 * 
	 * @author Luo Yinzhuo
	 */
	public interface OnListButtonListener {

		/**
		 * Called when a {@link ListButton} has been clicked.
		 * 
		 * @param button The button that was clicked.
		 * @author Luo Yinzhuo
		 */
		public void onListButton(ListButton button);
	}

	/** The background button. */
	private ImageButton mBackground;
	/** The text. */
	private TextView mText;
	/** The arrow. */
	private ImageView mArrow;
	/** The {@link ListButton} listener. */
	private OnListButtonListener mListener;

	/**
	 * Construct a new instance.
	 * 
	 * @param context The system context.
	 * @param attrs The attributes.
	 */
	public ListButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.list_button_widget, this);
		mBackground = (ImageButton) findViewById(R.id.background);
		mBackground.setOnTouchListener(this);
		mBackground.setOnClickListener(this);
		mText = (TextView) findViewById(R.id.text);
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

	@Override
	public void onClick(View v) {
		mListener.onListButton(this);
	}

	/**
	 * Set the text.
	 * 
	 * @param text The text.
	 * @author Luo Yinzhuo
	 */
	public void setText(String text) {
		mText.setText(text);
	}

	/**
	 * Register a callback to be invoked when this {@link ListButton} is
	 * clicked.
	 * 
	 * @param listener The callback that will run.
	 * @author Luo Yinzhuo
	 */
	public void setOnListButtonListener(OnListButtonListener listener) {
		mListener = listener;
	}

}
