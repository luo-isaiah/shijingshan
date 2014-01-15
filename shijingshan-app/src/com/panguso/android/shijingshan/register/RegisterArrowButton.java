/**
 * 
 */
package com.panguso.android.shijingshan.register;

import com.panguso.android.shijingshan.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Specific for the {@link RegisterActivity}'s enterprise button and user type
 * button.
 * 
 * @author Luo Yinzhuo
 * 
 */
public class RegisterArrowButton extends RelativeLayout implements
		OnClickListener {
	/**
	 * Interface definition for a callback to be invoked when the button is
	 * clicked.
	 * 
	 * @author Luo Yinzhuo
	 */
	public interface OnRegisterArrowButtonListener {

		/**
		 * Called when the button is clicked.
		 * 
		 * @param button
		 */
		public void onRegisterArrowButtonClicked(RegisterArrowButton button);
	}

	/** The button. */
	private Button mButton;
	/** The arrow. */
	private ImageView mArrow;
	/** The button listener. */
	private OnRegisterArrowButtonListener mListener;

	/**
	 * Construct a new instance.
	 * 
	 * @param context
	 *            The system context.
	 * @param attrs
	 *            The attributes.
	 */
	public RegisterArrowButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.register_arrow_button_widget, this);

		mButton = (Button) findViewById(R.id.button);
		mButton.setOnClickListener(this);

		mArrow = (ImageView) findViewById(R.id.arrow);
	}

	/**
	 * Set the text hint.
	 * 
	 * @param resid
	 *            The string resource's id.
	 * 
	 * @author Luo Yinzhuo
	 */
	public void setTextHint(int resid) {
		mButton.setHint(resid);
	}

	/**
	 * Set the text.
	 * 
	 * @param text
	 *            The text.
	 * 
	 * @author Luo Yinzhuo
	 */
	public void setText(String text) {
		mButton.setText(text);
		if (mArrow.getVisibility() == View.VISIBLE) {
			mArrow.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * Set the {@link OnRegisterArrowButtonListener}.
	 * 
	 * @param listener
	 *            The {@link OnRegisterArrowButtonListener}.
	 * 
	 * @author Luo Yinzhuo
	 */
	public void setOnRegisterArrowButtonListener(
			OnRegisterArrowButtonListener listener) {
		mListener = listener;
	}

	@Override
	public void onClick(View v) {
		if (mListener != null) {
			mListener.onRegisterArrowButtonClicked(this);
		}
	}
}
