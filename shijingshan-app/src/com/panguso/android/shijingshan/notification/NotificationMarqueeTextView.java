package com.panguso.android.shijingshan.notification;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Specific text view for display marquee effect.
 * 
 * @author Luo Yinzhuo
 */
public class NotificationMarqueeTextView extends TextView {

	/**
	 * Construct a new instance.
	 * 
	 * @param context
	 *            The context.
	 * @param attrs
	 *            The attributes.
	 */
	public NotificationMarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean isFocused() {
		return true;
	}
}
