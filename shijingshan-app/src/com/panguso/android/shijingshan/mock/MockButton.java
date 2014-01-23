/**
 * 
 */
package com.panguso.android.shijingshan.mock;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

/**
 * @author luoyinzhuo
 *
 */
public class MockButton extends Button {

	/**
	 * @param context
	 * @param attrs
	 */
	public MockButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		// TODO Auto-generated method stub
		super.setText(text, type);
	}
}
