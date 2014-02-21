package com.panguso.android.shijingshan.notification;

import com.panguso.android.shijingshan.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

/**
 * Represent a notification button.
 * 
 * @author Luo Yinzhuo
 */
public class NotificationButton extends RelativeLayout implements
		OnClickListener {

	/**
	 * Interface definition for a callback when the {@link NotificationButton}
	 * is clicked.
	 * 
	 * @author Luo Yinzhuo
	 */
	public interface OnNotificationButtonListener {

		/**
		 * Called when the {@link NotificationButton} is clicked.
		 * 
		 * @param id
		 *            The notification id.
		 * @param url
		 *            The notification url.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onClicked(String id, String url);
	}

	/** The notification id. */
	private String mId;
	/** The notification url. */
	private String mURL;
	/** The title. */
	private final NotificationTitleButton mButton;
	/** The listener. */
	private OnNotificationButtonListener mListener;

	/**
	 * Construct a new instance.
	 * 
	 * @param context
	 *            The context.
	 * @param id
	 *            The notification id.
	 * @param title
	 *            The notification title.
	 * @param addTime
	 *            The notification add time.
	 * @param url
	 *            The notification URL.
	 */
	NotificationButton(Context context, String id, String title,
			String addTime, String url) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.notification_button_widget, this);

		mId = id;
		mURL = url;

		mButton = (NotificationTitleButton) findViewById(R.id.button);
		mButton.setTitle(title);
		mButton.setTime(addTime.substring(0, addTime.indexOf(" ")));
		mButton.setOnClickListener(this);

	}

	/**
	 * Set the {@link OnNotificationButtonListener}.
	 * 
	 * @param listener
	 *            The {@link OnNotificationButtonListener}.
	 * @author Luo Yinzhuo
	 */
	public void setOnNotificationButtonListener(
			OnNotificationButtonListener listener) {
		mListener = listener;
	}

	@Override
	public void onClick(View v) {
		if (mListener != null) {
			mListener.onClicked(mId, mURL);
		}
	}
}
