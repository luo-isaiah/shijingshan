package com.panguso.android.shijingshan.setting;

import com.panguso.android.shijingshan.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * Specific for the {@link SettingActivity}'s button.
 * 
 * @author Luo Yinzhuo
 */
public class SettingButton extends RelativeLayout implements OnClickListener {

	/**
	 * Interface definition for a callback when the {@link SettingButton} is
	 * clicked.
	 * 
	 * @author Luo Yinzhuo
	 */
	public interface OnSettingButtonListener {

		/**
		 * Called when the {@link SettingButton} is clicked.
		 * 
		 * @param id
		 *            The id.
		 * @author Luo Yinzhuo
		 */
		public void onClicked(int id);
	}

	/** The button. */
	private final Button mButton;
	/** The listener. */
	private OnSettingButtonListener mListener;

	public SettingButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.setting_button_widget, this);

		mButton = (Button) findViewById(R.id.button);
		mButton.setOnClickListener(this);
	}

	/**
	 * Set the text.
	 * 
	 * @param text
	 *            The text.
	 * @author Luo Yinzhuo
	 */
	public void setText(String text) {
		mButton.setText(text);
	}

	/**
	 * Set the {@link OnSettingButtonListener}.
	 * 
	 * @param listener
	 *            The {@link OnSettingButtonListener}.
	 * @author Luo Yinzhuo
	 */
	public void setOnSettingButtonListener(OnSettingButtonListener listener) {
		mListener = listener;
	}

	@Override
	public void onClick(View v) {
		if (mListener != null) {
			mListener.onClicked(getId());
		}
	}

}
