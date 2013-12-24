package com.panguso.android.shijingshan.news;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

/**
 * The article page view.
 * 
 * @author Luo Yinzhuo
 */
public class NewsPageView extends View {

	/**
	 * Construct a new instance.
	 * 
	 * @param context The context.
	 * @param attrs The attributes.
	 */
	public NewsPageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		NewsPage.initialize(w, h);
    }

	@Override
    protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.WHITE);
    }

}
