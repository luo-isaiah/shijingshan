package com.panguso.android.shijingshan.register.usertype;

import com.panguso.android.shijingshan.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * Represent a user type button.
 * 
 * @author Luo Yinzhuo
 */
public final class UserTypeButton extends RelativeLayout implements OnClickListener {

	/**
	 * Interface definition for a callback when the {@link UserTypeButton} is
	 * clicked.
	 * 
	 * @author luoyinzhuo
	 */
	public interface OnUserTypeButtonListener {

		/**
		 * Called when the {@link UserTypeButton} is clicked.
		 * 
		 * @param id
		 *            The user type id.
		 * @param name
		 *            The user type name.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onClicked(String id, String name);
	}

	/** The user type id. */
	private final String mId;
	/** The button. */
	private final Button mButton;
	/** The listener. */
	private OnUserTypeButtonListener mListener;

	/**
	 * Construct a new instance.
	 * 
	 * @param context
	 *            The context.
	 * @param attrs
	 *            The attributes.
	 */
	UserTypeButton(Context context, String id, String name) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.user_type_button_widget, this);

		mId = id;

		mButton = (Button) findViewById(R.id.button);
		mButton.setOnClickListener(this);
		mButton.setText(name);
	}

	/**
	 * Set the {@link OnUserTypeButtonListener}.
	 * 
	 * @param listener
	 *            The {@link OnUserTypeButtonListener}.
	 * 
	 * @author Luo Yinzhuo
	 */
	public void setOnUserTypeButtonListener(OnUserTypeButtonListener listener) {
		mListener = listener;
	}

	@Override
	public void onClick(View v) {
		if (mListener != null) {
			mListener.onClicked(mId, mButton.getText().toString());
		}
	}

}
