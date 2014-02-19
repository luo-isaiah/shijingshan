package com.panguso.android.shijingshan.notification;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.Button;

/**
 * 
 * @author Luo Yinzhuo
 */
public class NotificationButton extends Button {

	/** The notification id. */
	private final String mId;
	/** The notification title. */
	private final String mTitle;
	/** The notification add time. */
	private final String mAddTime;

	/**
	 * @param context
	 */
	public NotificationButton(Context context, String id, String title,
			String addTime) {
		super(context);
		mId = id;
		mTitle = title;
		mAddTime = addTime.substring(0, addTime.indexOf(" "));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

	}
}
