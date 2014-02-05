package com.panguso.android.shijingshan.news;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Specific for store a {@link News}'s information.
 * 
 * @author Luo Yinzhuo
 */
public class NewsInfo {
	/** The news ID. */
	private final String mID;
	/** The title. */
	private final String mTitle;
	/** The image URL. */
	private final String mImageURL;
	/** The news URL. */
	private final String mNewsURL;
	/** The add time. */
	private final String mTime;

	/**
	 * Construct a new instance.
	 * 
	 * @param ID
	 *            The news ID.
	 * @param title
	 *            The title.
	 * @param imageURL
	 *            The image URL.
	 * @param URL
	 *            The article URL.
	 * @param time
	 *            The add time.
	 */
	private NewsInfo(String ID, String title, String imageURL, String URL,
			String time) {
		mID = ID;
		mTitle = title;
		mImageURL = imageURL;
		mNewsURL = URL;
		mTime = time;
	}

	/** The key to get news id. */
	private static final String KEY_NEWS_ID = "newsid";
	/** The key to get news title. */
	private static final String KEY_TITLE = "title";
	/** The key to get image URL. */
	private static final String KEY_IMAGE_URL = "imgurl";
	/** The key to get news URL. */
	private static final String KEY_URL = "filename";
	/** The key to get add time. */
	private static final String KEY_ADD_TIME = "addtime";

	/**
	 * Parse a news info from its JSON format.
	 * 
	 * @param json
	 *            The news info in JSON format.
	 * @return The news info.
	 * @throws JSONException
	 *             If the news info has error.
	 * @author Luo Yinzhuo
	 */
	public static NewsInfo parse(JSONObject json) throws JSONException {
		return new NewsInfo(json.getString(KEY_NEWS_ID),
				json.getString(KEY_TITLE), json.getString(KEY_IMAGE_URL),
				json.getString(KEY_URL), json.getString(KEY_ADD_TIME));
	}

	/**
	 * Get the news ID.
	 * 
	 * @return The news ID.
	 * @author Luo Yinzhuo
	 */
	public String getID() {
		return mID;
	}

	/**
	 * Get the {@link News} based on the {@link NewsInfo}.
	 * 
	 * @return The {@link News}.
	 * @author Luo Yinzhuo
	 */
	public News getNews() {
		return new News(mID, mTitle, mImageURL, mNewsURL, mTime);
	}

	@Override
    public String toString() {
	    return "NewsInfo [mID=" + mID + ", mTitle=" + mTitle + ", mImageURL=" + mImageURL
	            + ", mNewsURL=" + mNewsURL + ", mTime=" + mTime + "]";
    }
}
