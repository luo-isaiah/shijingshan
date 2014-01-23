/**
 * 
 */
package com.panguso.android.shijingshan.mock;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TableLayout;

/**
 * @author luoyinzhuo
 *
 */
public class MockTableLayout extends TableLayout {

	/**
	 * @param context
	 * @param attrs
	 */
	public MockTableLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void measureChildren(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.measureChildren(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void measureChild(View child, int parentWidthMeasureSpec,
			int parentHeightMeasureSpec) {
		// TODO Auto-generated method stub
		super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec);
	}

	@Override
	protected void measureChildWithMargins(View child,
			int parentWidthMeasureSpec, int widthUsed,
			int parentHeightMeasureSpec, int heightUsed) {
		// TODO Auto-generated method stub
		super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed,
				parentHeightMeasureSpec, heightUsed);
	}
	
	

}
