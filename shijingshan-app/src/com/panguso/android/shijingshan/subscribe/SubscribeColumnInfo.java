package com.panguso.android.shijingshan.subscribe;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Specific for store a subscribe column's information.
 * 
 * @author Luo Yinzhuo
 */
public final class SubscribeColumnInfo {

	/** The column ID. */
	private final String mID;
	/** The column name. */
	private final String mName;
	/** The subscribe flag. */
	private final boolean mSubscribe;

	/**
	 * Construct a new instance.
	 * 
	 * @param id
	 *            The column ID.
	 * @param name
	 *            The column name.
	 * @param subscribe
	 *            The column is subscribed or not.
	 */
	private SubscribeColumnInfo(String id, String name, boolean subscribe) {
		mID = id;
		mName = name;
		mSubscribe = subscribe;
	}

	/** The key to get column ID. */
	private static final String KEY_COLUMN_ID = "columnId";
	/** The key to get column name. */
	private static final String KEY_COLUMN_NAME = "columnName";
	/** The key to get subscribe flag. */
	private static final String KEY_COLUMN_DINGYUE = "dingyue";
	/** The value of subscribe yes. */
	private static final String VALUE_DINGYUE_YES = "yes";

	/**
	 * Check if the JSON object is a subscribe column info JSON object.
	 * 
	 * @param json
	 *            The JSON object.
	 * @return True if the JSON object is a subscribe column info JSON object,
	 *         otherwise false.
	 * @author Luo Yinzhuo
	 */
	public static boolean isSubscribeColumnInfo(JSONObject json) {
		return json != null && json.has(KEY_COLUMN_ID)
				&& json.has(KEY_COLUMN_NAME) && json.has(KEY_COLUMN_DINGYUE);
	}

	/**
	 * Parse a subscribe column info from its JSON object.
	 * 
	 * @param json
	 *            The subscribe column info JSON object.
	 * @return The subscribe column info.
	 * @throws JSONException
	 *             If the column info has error.
	 * @author Luo Yinzhuo
	 */
	public static SubscribeColumnInfo parse(JSONObject json)
			throws JSONException {
		return new SubscribeColumnInfo(json.getString(KEY_COLUMN_ID),
				json.getString(KEY_COLUMN_NAME), json.getString(
						KEY_COLUMN_DINGYUE).equals(VALUE_DINGYUE_YES));
	}

	@Override
	public String toString() {
		return "SubscribeColumnInfo [mID=" + mID + ", mName=" + mName
				+ ", mSubscribe=" + mSubscribe + "]";
	}
}
