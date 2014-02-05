package com.panguso.android.shijingshan.column;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

/**
 * Specific for store a {@link Column}'s information.
 * 
 * @author Luo Yinzhuo
 */
public final class ColumnInfo {
	/** The column ID. */
	private final int mId;
	/** The column name. */
	private final String mName;
	/** Whether the column need subscribe or not. */
	private final boolean mSubscribe;

	/**
	 * Construct a new instance.
	 * 
	 * @param id
	 *            The column ID.
	 * @param name
	 *            The column name.
	 * @param open
	 *            The column is open or not.
	 */
	private ColumnInfo(int id, String name, boolean open) {
		mId = id;
		mName = name;
		mSubscribe = !open;
	}

	/**
	 * Get the {@link Column} based on the {@link ColumnInfo}.
	 * 
	 * @param context
	 *            The system context.
	 * @return The {@link Column}.
	 * @author Luo Yinzhuo
	 */
	public final Column getColumn(Context context) {
		return new Column(context, mId, mName, mSubscribe);
	}

	/** The key to get column ID. */
	private static final String KEY_COLUMN_ID = "columnId";
	/** The key to get column name. */
	private static final String KEY_COLUMN_NAME = "columnName";
	/** The key to get sfgk. */
	private static final String KEY_SFGK = "sfgk";
	/** The value of sfgk yes. */
	private static final String VALUE_SFGK_YES = "yes";

	/**
	 * Check if the JSON object is a column info JSON object.
	 * 
	 * @param json
	 *            The JSON object.
	 * @return True if the JSON object is a column info JSON object, otherwise
	 *         false.
	 * @author Luo Yinzhuo
	 */
	public static boolean isColumnInfo(JSONObject json) {
		return json != null && json.has(KEY_COLUMN_ID)
				&& json.has(KEY_COLUMN_NAME) && json.has(KEY_SFGK);
	}

	/**
	 * Parse a column info from its JSON object.
	 * 
	 * @param json
	 *            The column info JSON object.
	 * @return The column info.
	 * @throws JSONException
	 *             If the column info has error.
	 * @author Luo Yinzhuo
	 */
	public static ColumnInfo parse(JSONObject json) throws JSONException {
		return new ColumnInfo(json.getInt(KEY_COLUMN_ID),
				json.getString(KEY_COLUMN_NAME), json.getString(KEY_SFGK)
						.equals(VALUE_SFGK_YES));
	}

	@Override
	public String toString() {
		return "ColumnInfo [mID=" + mId + ", mName=" + mName + ", mSubscribe="
				+ mSubscribe + "]";
	}
}
