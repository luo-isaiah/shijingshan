package com.panguso.android.shijingshan.news;

import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.net.NetworkService;
import com.panguso.android.shijingshan.net.NetworkService.NewsImageRequestListener;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Represent a piece of news.
 * 
 * @author Luo Yinzhuo
 */
abstract class News {
	/** The key to store news URL. */
	public static final String KEY_NEWS_URL = "news_url";

	/** The news URL. */
	private final String mNewsURL;

	/** The horizontal margin. */
	protected final float mMarginHorizontal;
	/** The vertical margin. */
	protected final float mMarginVertical;

	/**
	 * Construct a new instance.
	 * 
	 * @param newsURL
	 *            The news URL.
	 */
	protected News(String newsURL, float marginHorizontal, float marginVertical) {
		mNewsURL = newsURL;
		mMarginHorizontal = marginHorizontal;
		mMarginVertical = marginVertical;
	}

	/**
	 * The news status enumeration.
	 * 
	 * @author Luo Yinzhuo
	 */
	public enum Status {
		NORMAL, PRESS
	}

	/** The news status. */
	protected Status mStatus = Status.NORMAL;

	/**
	 * Set the news status.
	 * 
	 * @param status
	 *            The news status.
	 * 
	 * @author Luo Yinzhuo
	 */
	public final void setStatus(Status status) {
		mStatus = status;
	}

	/**
	 * Invoked when a single tap occurs on the {@link News}.
	 * 
	 * @param context
	 *            The system context.
	 * 
	 * @author Luo Yinzhuo
	 */
	public final void onSingleTapUp(Context context) {
		Intent intent = new Intent(context, NewsActivity.class);
		intent.putExtra(KEY_NEWS_URL, mNewsURL);
		context.startActivity(intent);
	}

	/**
	 * Check if the {@link News} has an image or not.
	 * 
	 * @return True if the {@link News} has an image, otherwise false.
	 * @author Luo Yinzhuo
	 */
	abstract boolean hasImage();

	/** The shared {@link Paint} for reuse. */
	protected static final Paint PAINT = new Paint(Paint.ANTI_ALIAS_FLAG
			| Paint.DITHER_FLAG);
	/** The ellipsis. */
	protected static final String ELLIPSIS = "...";

	/**
	 * Invoked by {@link NewsPage} to draw the news on it.
	 * 
	 * @param canvas
	 *            The {@link NewsPageView}'s canvas.
	 * @param rect
	 *            The news rectangle.
	 * @param page
	 *            The {@link NewsPage} index.
	 * @param listener
	 *            The image request listener.
	 * @author Luo Yinzhuo
	 */
	abstract void draw(Canvas canvas, Rect rect, int page,
			NewsImageRequestListener listener);
}

/**
 * Base class for text news.
 * 
 * @author Luo Yinzhuo
 */
abstract class TextNews extends News {
	/** The title. */
	private final String mTitle;
	/** The title color. */
	private final int mTitleColor;
	/** The title text size. */
	private final float mTitleTextSize;

	/** The time. */
	private final String mTime;
	/** The time color. */
	private final int mTimeColor;
	/** The time text size. */
	private final float mTimeTextSize;

	/** The background color when pressing. */
	private final int mBackgroundPress;

	/**
	 * Construct a new instance.
	 * 
	 * @param resources
	 *            The {@link Resources}.
	 * @param title
	 *            The news title.
	 * @param time
	 *            The news time.
	 * @param newsURL
	 *            The news URL.
	 * @param titleTextSize
	 *            The title text size.
	 */
	protected TextNews(Resources resources, String title, String time,
			String newsURL, float titleTextSize) {
		super(newsURL, resources.getDimension(R.dimen.news_margin_horizontal),
				resources.getDimension(R.dimen.news_margin_vertical));
		mTitle = title;
		mTime = time.substring(0, time.indexOf(" "));
		mTitleTextSize = titleTextSize;

		mTitleColor = resources.getColor(R.color.text_news_title);

		mTimeColor = resources.getColor(R.color.text_news_time);
		mTimeTextSize = resources.getDimension(R.dimen.text_news_time);

		mBackgroundPress = resources
				.getColor(R.color.text_news_background_press);
	}

	@Override
	final boolean hasImage() {
		return false;
	}

	@Override
	final void draw(Canvas canvas, Rect rect, int page,
			NewsImageRequestListener listener) {
		if (mStatus == Status.PRESS) {
			PAINT.setColor(mBackgroundPress);
			canvas.drawRect(rect, PAINT);
		}

		PAINT.setColor(mTimeColor);
		PAINT.setTextSize(mTimeTextSize);
		canvas.drawText(mTime,
				rect.right - mMarginHorizontal - PAINT.measureText(mTime),
				rect.bottom - mMarginVertical, PAINT);

		PAINT.setColor(mTitleColor);
		PAINT.setTextSize(mTitleTextSize);

		int start = 0;
		int end = 1;
		final float maxWidth = rect.width() - 2 * mMarginHorizontal;
		int line = 0;
		final int maxLine = 2;
		while (end <= mTitle.length() && line < maxLine) {
			float width = PAINT.measureText(mTitle, start, end);
			while (width < maxWidth && end < mTitle.length()) {
				end++;
				width = PAINT.measureText(mTitle, start, end);
			}

			if (width > maxWidth) {
				canvas.drawText(mTitle.substring(start, end - 1), rect.left
						+ mMarginHorizontal, rect.top
						+ (mMarginVertical + mTitleTextSize) * (line + 1),
						PAINT);
				start = end - 1;
			} else {
				canvas.drawText(mTitle.substring(start, end), rect.left
						+ mMarginHorizontal, rect.top
						+ (mMarginVertical + mTitleTextSize) * (line + 1),
						PAINT);
				start = end;
			}
			
			if (start == mTitle.length()) {
				break;
			}
			line++;
		}

		if (start != mTitle.length() && end <= mTitle.length()) {
			end = mTitle.length();
			float width = PAINT.measureText(mTitle, start, end);
			if (width > maxWidth) {
				float ellipsisWidth = PAINT.measureText(ELLIPSIS);
				while (width + ellipsisWidth > maxWidth) {
					end--;
					width = PAINT.measureText(mTitle, start, end);
				}
				canvas.drawText(mTitle.substring(start, end) + ELLIPSIS,
						rect.left + mMarginHorizontal, rect.top
								+ (mMarginVertical + mTitleTextSize) * 3, PAINT);
			} else {
				canvas.drawText(mTitle.substring(start, end), rect.left
						+ mMarginHorizontal, rect.top
						+ (mMarginVertical + mTitleTextSize) * 3, PAINT);
			}
		}
	}
}

/**
 * The 1x1 cell text news.
 * 
 * @author Luo Yinzhuo
 */
final class OneCellTextNews extends TextNews {

	/**
	 * Construct a new instance.
	 * 
	 * @param resources
	 *            The resources.
	 * @param title
	 *            The news title.
	 * @param time
	 *            The news time.
	 * @param newsURL
	 *            The news URL.
	 */
	OneCellTextNews(Resources resources, String title, String time,
			String newsURL) {
		super(resources, title, time, newsURL, resources
				.getDimension(R.dimen.one_cell_text_news_title));
	}
}

/**
 * The 1 line text news.
 * 
 * @author Luo Yinzhuo
 */
final class OneLineTextNews extends TextNews {

	/**
	 * Construct a new instance.
	 * 
	 * @param resources
	 *            The resources.
	 * @param title
	 *            The news title.
	 * @param time
	 *            The news time.
	 * @param newsURL
	 *            The news URL.
	 */
	OneLineTextNews(Resources resources, String title, String time,
			String newsURL) {
		super(resources, title, time, newsURL, resources
				.getDimension(R.dimen.one_line_text_news_title));
	}
}

/**
 * The 2x2 cells image news.
 * 
 * @author Luo Yinzhuo
 */
final class ImageNews extends News {
	/** The news title. */
	private final String mTitle;
	/** The title color. */
	private final int mTitleColor;
	/** The title text size. */
	private final float mTitleTextSize;
	/** The image URL. */
	private final String mImageURL;

	/**
	 * Construct a new instance.
	 * 
	 * @param resources
	 *            The resources.
	 * @param title
	 *            The news title.
	 * @param imageURL
	 *            The image URL.
	 * @param newsURL
	 *            The news URL.
	 */
	ImageNews(Resources resources, String title, String imageURL, String newsURL) {
		super(newsURL, resources.getDimension(R.dimen.news_margin_horizontal),
				resources.getDimension(R.dimen.news_margin_vertical));
		mTitle = title;
		mImageURL = imageURL;

		mTitleColor = resources.getColor(R.color.image_news_title);
		mTitleTextSize = resources.getDimension(R.dimen.image_news_title);
	}

	@Override
	boolean hasImage() {
		return true;
	}

	@Override
	void draw(Canvas canvas, Rect rect, int page,
			NewsImageRequestListener listener) {
		Bitmap bitmap = NetworkService.getNewsImage(page, mImageURL, listener);
		if (bitmap != null) {
			synchronized (bitmap) {
				if (!bitmap.isRecycled()) {
					canvas.drawBitmap(bitmap, null, rect, null);
				}
			}
		}

		PAINT.setColor(mTitleColor);
		PAINT.setTextSize(mTitleTextSize);

		final float maxWidth = rect.width() - 2 * mMarginHorizontal;
		if (PAINT.measureText(mTitle) > maxWidth) {
			int end = mTitle.length() - 2;
			float ellipsisWidth = PAINT.measureText(ELLIPSIS);
			while (PAINT.measureText(mTitle.substring(0, end)) + ellipsisWidth > maxWidth) {
				end--;
			}
			canvas.drawText(mTitle.substring(0, end).concat(ELLIPSIS),
					rect.left + mMarginHorizontal, rect.bottom
							- mMarginVertical, PAINT);
		} else {
			canvas.drawText(mTitle, rect.left + mMarginHorizontal, rect.bottom
					- mMarginVertical, PAINT);
		}
	}

}