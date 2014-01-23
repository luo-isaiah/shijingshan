/**
 * 
 */
package com.panguso.android.shijingshan.register.usertype;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

/**
 * Specific for store a {@link UserTypeButton}'s information.
 * 
 * @author Luo Yinzhuo
 * 
 */
public final class UserTypeInfo {
	/** The user type id. */
	private final int mId;
	/** The user type name. */
	private final String mName;

	/**
	 * Construct a new instance.
	 * 
	 * @param id
	 *            The user type id.
	 * @param name
	 *            The user type name.
	 */
	private UserTypeInfo(int id, String name) {
		mId = id;
		mName = name;
	}

	/**
	 * Get the {@link UserTypeButton} based on the {@link UserTypeInfo}.
	 * 
	 * @param context
	 *            The context.
	 * @return The {@link UserTypeButton}.
	 * 
	 * @author Luo Yinzhuo
	 */
	public UserTypeButton getUserTypeButton(Context context) {
		return new UserTypeButton(context, mId, mName);
	}

	/** The key to get user type id. */
	private static final String KEY_USER_TYPE_ID = "code_id";
	/** The key to get user type name. */
	private static final String KEY_USER_TYPE_NAME = "code_name";

	/**
	 * Check if the JSON object is a user type info JSON object.
	 * 
	 * @param json
	 *            The JSON object.
	 * @return True if the JSON object is a user type info JSON object,
	 *         otherwise false.
	 * @author Luo Yinzhuo
	 */
	public static boolean isUserTypeInfo(JSONObject json) {
		return json != null && json.has(KEY_USER_TYPE_ID)
				&& json.has(KEY_USER_TYPE_NAME);
	}

	/**
	 * Parse a user type info from its JSON object.
	 * 
	 * @param json
	 *            The user type info JSON object.
	 * @return The user type info.
	 * @throws JSONException
	 *             If the user type info has error.
	 * @author Luo Yinzhuo
	 */
	public static UserTypeInfo parse(JSONObject json) throws JSONException {
		return new UserTypeInfo(json.getInt(KEY_USER_TYPE_ID),
				json.getString(KEY_USER_TYPE_NAME));
	}

	@Override
	public String toString() {
		return "UserTypeInfo [mId=" + mId + ", mName=" + mName + "]";
	}
}
