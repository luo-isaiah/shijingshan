/**
 * 
 */
package com.panguso.android.shijingshan.register;

import com.panguso.android.shijingshan.R;

import android.R.integer;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Specific for the {@link RegisterActivity}'s enterprise button and user type
 * button.
 * 
 * @author Luo Yinzhuo
 * 
 */
public class RegisterArrowButton extends RelativeLayout implements
		OnClickListener {

	/** The button. */
	private Button mButton;

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

	@Override
	public void onClick(View v) {
		Log.d("RegisterArrowButton", "Enterprise button clicked!");
	}
}
