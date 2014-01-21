package com.panguso.android.shijingshan.register.enterprise;

import com.panguso.android.shijingshan.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * Represent an enterprise type button.
 * 
 * @author Luo Yinzhuo
 */
public final class EnterpriseButton extends RelativeLayout implements OnClickListener {

	/**
	 * Interface definition for a callback when the {@link EnterpriseButton} is
	 * clicked.
	 * 
	 * @author luoyinzhuo
	 */
	public interface OnEnterpriseButtonListener {

		/**
		 * Called when the {@link EnterpriseButton} is clicked.
		 * 
		 * @param id
		 *            The enterprise id.
		 * @param name
		 *            The enterprise name.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onClicked(int id, String name);
	}

	/** The enterprise id. */
	private final int mId;
	/** The button. */
	private final Button mButton;
	/** The listener. */
	private OnEnterpriseButtonListener mListener;

	/**
	 * Construct a new instance.
	 * 
	 * @param context
	 *            The context.
	 * @param attrs
	 *            The attributes.
	 */
	EnterpriseButton(Context context, int id, String name) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.enterprise_button_widget, this);

		mId = id;

		mButton = (Button) findViewById(R.id.button);
		mButton.setOnClickListener(this);
		mButton.setText(name);
	}

	/**
	 * Set the {@link OnEnterpriseButtonListener}.
	 * 
	 * @param listener
	 *            The {@link OnEnterpriseButtonListener}.
	 * 
	 * @author Luo Yinzhuo
	 */
	public void setOnEnterpriseButtonListener(OnEnterpriseButtonListener listener) {
		mListener = listener;
	}

	@Override
	public void onClick(View v) {
		if (mListener != null) {
			mListener.onClicked(mId, mButton.getText().toString());
		}
	}

}
