package com.panguso.android.shijingshan.news;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.panguso.android.shijingshan.R.id;
import com.panguso.android.shijingshan.column.Column;
import com.panguso.android.shijingshan.net.NetworkService;
import com.panguso.android.shijingshan.net.NetworkService.ImageRequestListener;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.util.LruCache;

/**
 * Represent a piece of news.
 * 
 * @author Luo Yinzhuo
 */
public class News {
	/** The paint shared by all the news. */
	private static final Paint PAINT = new Paint(Paint.ANTI_ALIAS_FLAG
			| Paint.DITHER_FLAG);
	/** The horizontal margin. */
	private static float MARGIN_HORIZONTAL;
	/** The vertical margin. */
	private static float MARGIN_VERTICAL;
	/** The news title font size which has image. */
	private static float IMAGE_FONT_SIZE;
	/** The news title font size which doesn't have image. */
	private static float NO_IMAGE_FONT_SIZE;
	/** The news title color which has image. */
	private static int IMAGE_FONT_COLOR;
	/** The news title color which doesn't have image. */
	private static int NO_IMAGE_FONT_COLOR;

	/**
	 * Initialize the horizontal margin, vertical margin, news title font size,
	 * color.
	 * 
	 * @param marginHorizontal
	 *            The horizontal margin.
	 * @param marginVertical
	 *            The vertical margin.
	 * @param imageFontSize
	 *            The news title font size which has image.
	 * @param noImageFontSize
	 *            The news title font size which doesn't have image.
	 * @param imageFontColor
	 *            The news title font color which has image.
	 * @param noImageFontColor
	 *            The news title font color which doesn't have image.
	 * @author Luo Yinzhuo
	 */
	public static void initialize(float marginHorizontal, float marginVertical,
			float imageFontSize, float noImageFontSize, int imageFontColor,
			int noImageFontColor) {
		MARGIN_HORIZONTAL = marginHorizontal;
		MARGIN_VERTICAL = marginVertical;
		IMAGE_FONT_SIZE = imageFontSize;
		NO_IMAGE_FONT_SIZE = noImageFontSize;
		IMAGE_FONT_COLOR = imageFontColor;
		NO_IMAGE_FONT_COLOR = noImageFontColor;
	}

	/** The news id. */
	private final String mID;
	/** The news title. */
	private final String mTitle;
	/** The image URL. */
	private final String mImageURL;
	/** The news URL. */
	private final String mNewsURL;
	/** The news time. */
	private final String mTime;
	
	/** The key to store news URL. */
	public static final String KEY_NEWS_URL = "news_url";

	/**
	 * Construct a new instance.
	 * 
	 * @param id
	 *            The news id.
	 * @param title
	 *            The news title.
	 * @param imageURL
	 *            The image URL.
	 * @param newsURL
	 *            The news URL.
	 * @param time
	 *            The news time.
	 */
	News(String id, String title, String imageURL, String newsURL, String time) {
		mID = id;
		mTitle = title;
		mImageURL = imageURL;
		mNewsURL = newsURL;
		mTime = time;
	}

	/**
	 * Check if the {@link News} has an image or not.
	 * 
	 * @return True if the {@link News} has an image, otherwise false.
	 * @author Luo Yinzhuo
	 */
	public boolean hasImage() {
		return mImageURL.length() > 0;
	}

	/**
	 * Invoked by {@link NewsPage} to draw the news on it.
	 * 
	 * @param canvas
	 *            The {@link NewsPageView}'s canvas.
	 * @param rect
	 *            The news rectangle.
	 * @param listener
	 *            The image request listener.
	 * @author Luo Yinzhuo
	 */
	public void draw(Canvas canvas, Rect rect, ImageRequestListener listener) {
		Log.d("News", "draw image URL length:" + mImageURL.length());
		if (mImageURL.length() > 0) {
			Bitmap bitmap = NetworkService.getImage(mImageURL, listener);
			if (bitmap != null) {
				synchronized (bitmap) {
					if (!bitmap.isRecycled()) {
						canvas.drawBitmap(bitmap, null, rect, null);
						Log.d("News", "draw bitmap exist.");
					}
				}
			}

			PAINT.setColor(IMAGE_FONT_COLOR);
			PAINT.setTextSize(IMAGE_FONT_SIZE);
			canvas.drawText(mTitle, rect.left + MARGIN_HORIZONTAL, rect.bottom
					- MARGIN_VERTICAL, PAINT);
		} else {
			// Draw background first.
			PAINT.setColor(NO_IMAGE_FONT_COLOR);
			PAINT.setTextSize(NO_IMAGE_FONT_SIZE);

			int start = 0;
			int end = 1;
			final float maxWidth = rect.width() - 2 * MARGIN_HORIZONTAL;
			int line = 0;
			final int maxLine = 3;
			while (end < mTitle.length() && line < maxLine) {
				float width = PAINT.measureText(mTitle, start, end);
				while (width < maxWidth && end < mTitle.length()) {
					end++;
					width = PAINT.measureText(mTitle, start, end);
				}

				canvas.drawText(mTitle.substring(start, end - 1), rect.left
						+ MARGIN_HORIZONTAL, rect.top
						+ (MARGIN_VERTICAL + NO_IMAGE_FONT_SIZE) * (line + 1),
						PAINT);
				start = end - 1;
				line++;
			}
		}
	}

	/**
	 * Invoked when a single tap occurs on the {@link News}.
	 * 
	 * @param context
	 *            The system context.
	 * 
	 * @author Luo Yinzhuo
	 */
	public void onSingleTapUp(Context context) {
		Intent intent = new Intent(context, NewsActivity.class);
		intent.putExtra(KEY_NEWS_URL, mNewsURL);
		context.startActivity(intent);
	}
}
