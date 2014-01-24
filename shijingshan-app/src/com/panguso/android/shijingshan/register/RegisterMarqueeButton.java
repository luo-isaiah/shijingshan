/**
 * 
 */
package com.panguso.android.shijingshan.register;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Specific button for display marquee effect.
 * 
 * @author Luo Yinzhuo
 */
public class RegisterMarqueeButton extends Button {

	/**
	 * Construct a new instance.
	 * 
	 * @param context
	 *            The context.
	 * @param attrs
	 *            The attributes.
	 */
	public RegisterMarqueeButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (widthMode == MeasureSpec.EXACTLY) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		} else {
			super.onMeasure(MeasureSpec.EXACTLY, heightMeasureSpec);
		}
	}

	@Override
	public boolean isFocused() {
		return true;
	}
}
