package com.panguso.android.shijingshan.widget;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Specific for the under line text button.
 * 
 * @author Luo Yinzhuo
 */
public class UnderlineButton extends Button {

	/**
	 * Construct a new instance.
	 * 
	 * @param context The system context.
	 * @param attrs The attributes.
	 */
	public UnderlineButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
	}

}
