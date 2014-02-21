package com.panguso.android.shijingshan.notification;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

/**
 * Specific for store a notification's information.
 * 
 * @author Luo Yinzhuo
 */
public final class NotificationInfo {
	/** The notification id. */
	private final String mId;
	/** The notification title. */
	private final String mTitle;
	/** The notification add time. */
	private final String mAddTime;
	/** The notification summary. */
	private final String mSummary;
	/** The notification URL. */
	private final String mURL;

	/**
	 * Construct a new instance.
	 * 
	 * @param id
	 *            The notification id.
	 * @param title
	 *            The notification title.
	 * @param addTime
	 *            The notification add time.
	 * @param summary
	 *            The notification summary.
	 * @param url
	 *            The notification url.
	 */
	private NotificationInfo(String id, String title, String addTime,
			String summary, String url) {
		mId = id;
		mTitle = title;
		mAddTime = addTime;
		mSummary = summary;
		mURL = url;
	}

	/**
	 * Get the notification id.
	 * 
	 * @return The notification id.
	 * @author Luo Yinzhuo
	 */
	public String getId() {
		return mId;
	}

	/**
	 * Get the notification title.
	 * 
	 * @return The notification title.
	 * @author Luo Yinzhuo
	 */
	public String getTitle() {
		return mTitle;
	}

	/**
	 * Get the notification summary.
	 * 
	 * @return The notification summary.
	 * @author Luo Yinzhuo
	 */
	public String getSummary() {
		return mSummary;
	}

	/**
	 * Get the URL.
	 * 
	 * @return The URL.
	 * @author Luo Yinzhuo
	 */
	public String getURL() {
		return mURL;
	}

	/**
	 * Get the {@link NotificationButton} based on itself.
	 * 
	 * @param context
	 *            The context.
	 * @return The {@link NotificationButton}.
	 * @author Luo Yinzhuo
	 */
	public NotificationButton getNotificationButton(Context context) {
		return new NotificationButton(context, mId, mTitle, mAddTime, mURL);
	}

	/**
	 * Get the notification info in JSON format.
	 * 
	 * @return The notification info in JSON format.
	 * @throws JSONException
	 *             If the notification info has error.
	 * @author Luo Yinzhuo
	 */
	public String getJson() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(KEY_ID, mId);
		json.put(KEY_TITLE, mTitle);
		json.put(KEY_ADD_TIME, mAddTime);
		json.put(KEY_SUMMARY, mSummary);
		json.put(KEY_URL, mURL);
		return json.toString();
	}

	/** The key to get notification id. */
	public static final String KEY_ID = "newsid";
	/** The key to get notification title. */
	private static final String KEY_TITLE = "title";
	/** The key to get notification add time. */
	private static final String KEY_ADD_TIME = "addtime";
	/** The key to get notification summary. */
	private static final String KEY_SUMMARY = "summary";
	/** The key to get notification URL. */
	public static final String KEY_URL = "Filename";

	/**
	 * Check if the JSON object is a notification info JSON object.
	 * 
	 * @param json
	 *            The JSON object.
	 * @return True if the JSON object is a notification info JSON object,
	 *         otherwise false.
	 * @author Luo Yinzhuo
	 */
	public static boolean isNotificationInfo(JSONObject json) {
		return json != null && json.has(KEY_ID) && json.has(KEY_TITLE)
				&& json.has(KEY_ADD_TIME) && json.has(KEY_SUMMARY)
				&& json.has(KEY_URL);
	}

	/**
	 * Parse a notification info from its JSON format.
	 * 
	 * @param json
	 *            The notification info in JSON format.
	 * @return The notification info.
	 * @throws JSONException
	 *             If the notification info has error.
	 * @author Luo Yinzhuo
	 */
	public static NotificationInfo parse(JSONObject json) throws JSONException {
		return new NotificationInfo(json.getString(KEY_ID),
				json.getString(KEY_TITLE), json.getString(KEY_ADD_TIME),
				json.getString(KEY_SUMMARY), json.getString(KEY_URL));
	}

	@Override
	public String toString() {
		return "NotificationInfo [mId=" + mId + ", mTitle=" + mTitle
				+ ", mAddTime=" + mAddTime + ", mSummary=" + mSummary + "]";
	}
}
