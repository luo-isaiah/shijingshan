package com.panguso.android.shijingshan.news;

public class News {
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
}
