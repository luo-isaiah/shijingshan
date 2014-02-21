/**
 * 
 */
package com.panguso.android.shijingshan.notification;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represent a notification.
 * 
 * @author Luo Yinzhuo
 */
public class Notification {
	/** The notification title. */
	private final String mTitle;
	/** The notification add time. */
	private final String mAddTime;

	/**
	 * Construct a new instance.
	 * 
	 * @param title
	 *            The notification title.
	 * @param addTime
	 *            The notification add time.
	 */
	private Notification(String title, String addTime) {
		mTitle = title;
		mAddTime = addTime;
	}

	/** The notification attachment. */
	private String mAttachment;

	/**
	 * Set attachment.
	 * 
	 * @param attachment
	 *            The attachment.
	 * @author Luo Yinzhuo
	 */
	public void setAttachment(String attachment) {
		mAttachment = attachment;
	}

	/**
	 * Get the title.
	 * 
	 * @return The title.
	 * @author Luo Yinzhuo
	 */
	public String getTitle() {
		return mTitle;
	}

	/**
	 * Get the add time.
	 * 
	 * @return The add time.
	 * @author Luo Yinzhuo
	 */
	public String getAddTime() {
		return mAddTime;
	}

	/** The key to get notification title. */
	private static final String KEY_TITLE = "title";
	/** The key to get notification add time. */
	private static final String KEY_ADD_TIME = "addtime";
	/** The key to get notification URL. */
	/** The key to get notification msgcontent. */
	private static final String KEY_MSG_CONTENT = "msgcontent";
	/** The key to get notification attachment. */
	private static final String KEY_ATTACHMENT = "attachment";

	/**
	 * Check if the JSON object is a notification JSON object.
	 * 
	 * @param json
	 *            The JSON object.
	 * @return True if the JSON object is a notification JSON object, otherwise
	 *         false.
	 * @author Luo Yinzhuo
	 */
	public static boolean isNotification(JSONObject json) {
		return json != null && json.has(KEY_TITLE) && json.has(KEY_ADD_TIME)
				&& json.has(KEY_MSG_CONTENT);
	}

	/**
	 * Parse a notification from its JSON format.
	 * 
	 * @param json
	 *            The notification in JSON format.
	 * @return The notification info.
	 * @throws JSONException
	 *             If the notification has error.
	 * @author Luo Yinzhuo
	 */
	public static Notification parse(JSONObject json) throws JSONException {
		Notification notification = new Notification(json.getString(KEY_TITLE),
				json.getString(KEY_ADD_TIME));
		if (json.has(KEY_ATTACHMENT)) {
			notification.setAttachment(json.getString(KEY_ATTACHMENT));
		}
		return notification;
	}

	@Override
	public String toString() {
		return "Notification [mTitle=" + mTitle + ", mAddTime=" + mAddTime
				+ ", mAttachment=" + mAttachment + "]";
	}
}
