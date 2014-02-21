package com.panguso.android.shijingshan.notification;

import com.panguso.android.shijingshan.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * To display the title.
 * 
 * @author Luo Yinzhuo
 */
public class NotificationTitleButton extends Button {

	/** The text color. */
	private final int mTextColor;
	/** The title margin vertical. */
	private final float mMarginVertical;
	/** The title offset vertical. */
	private final float mOffsetVertical;

	/** The time color. */
	private final int mTimeColor;
	/** The time text size. */
	private final float mTimeTextSize;
	/** The time offset vertical. */
	private final float mTimeOffsetVertical;

	/**
	 * Construct a new instance.
	 * 
	 * @param context
	 *            The context.
	 * @param attrs
	 *            The attributes.
	 */
	public NotificationTitleButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		Resources resources = context.getResources();
		mTextColor = resources.getColor(R.color.notification_button_title);
		mMarginVertical = resources
				.getDimension(R.dimen.notification_title_margin_vertical);
		mOffsetVertical = resources
				.getDimension(R.dimen.notification_title_offset_vertical);

		mTimeColor = resources.getColor(R.color.notification_button_time);
		mTimeTextSize = resources
				.getDimension(R.dimen.notification_button_time);
		mTimeOffsetVertical = resources
				.getDimension(R.dimen.notification_time_offset_vertical);
	}

	/** The title. */
	private String mTitle;

	/**
	 * Set the title.
	 * 
	 * @param title
	 *            The title.
	 * @author Luo Yinzhuo
	 */
	public void setTitle(String title) {
		mTitle = title;
	}

	/** The time. */
	private String mTime;

	/**
	 * Set the time.
	 * 
	 * @param time
	 *            The time.
	 * @author Luo Yinzhuo
	 */
	public void setTime(String time) {
		mTime = time;
	}

	/** The shared {@link Paint} for reuse. */
	private static final Paint PAINT = new Paint(Paint.ANTI_ALIAS_FLAG
			| Paint.DITHER_FLAG);
	/** The ellipsis. */
	protected static final String ELLIPSIS = "...";

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		PAINT.setColor(mTimeColor);
		PAINT.setTextSize(mTimeTextSize);
		final float timeWidth = PAINT.measureText(mTime);
		canvas.drawText(mTime, getWidth() - getPaddingRight() - timeWidth,
				getHeight() - mTimeOffsetVertical, PAINT);

		PAINT.setColor(mTextColor);
		PAINT.setTextSize(getTextSize());

		int maxWidth = getWidth() - getPaddingLeft() - getPaddingRight();

		if (PAINT.measureText(mTitle) > maxWidth) {
			// Draw first line.
			int start = 0;
			int end = 1;
			float width = PAINT.measureText(mTitle, start, end);
			while (width < maxWidth && end < mTitle.length()) {
				end++;
				width = PAINT.measureText(mTitle, start, end);
			}

			canvas.drawText(mTitle.substring(start, end - 1), getPaddingLeft(),
					(getHeight() - mMarginVertical) / 2 - mOffsetVertical,
					PAINT);

			// Draw second line.
			start = end - 1;
			if (end < mTitle.length()) {
				maxWidth -= timeWidth;
				end = mTitle.length();
				width = PAINT.measureText(mTitle, start, end);
				if (width > maxWidth) {
					float ellipsisWidth = PAINT.measureText(ELLIPSIS);
					while (width + ellipsisWidth > maxWidth) {
						end--;
						width = PAINT.measureText(mTitle, start, end);
					}
					canvas.drawText(mTitle.substring(start, end) + ELLIPSIS,
							getPaddingLeft(), (getHeight() + mMarginVertical)
									/ 2 + getTextSize() - mOffsetVertical,
							PAINT);
				} else {
					canvas.drawText(mTitle.substring(start, end),
							getPaddingLeft(), (getHeight() + mMarginVertical)
									/ 2 + getTextSize() - mOffsetVertical,
							PAINT);
				}
			}
		} else {
			canvas.drawText(mTitle, getPaddingLeft(),
					(getHeight() + getTextSize()) / 2 - mOffsetVertical, PAINT);
		}
	}
}
