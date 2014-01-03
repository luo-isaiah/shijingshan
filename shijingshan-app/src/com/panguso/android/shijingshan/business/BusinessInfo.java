package com.panguso.android.shijingshan.business;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Specific for store a {@link Business}'s information.
 * 
 * @author Luo Yinzhuo
 */
public class BusinessInfo {
	/** The business id. */
	private final String mId;
	/** The business name. */
	private final String mName;

	/**
	 * Construct a new instance.
	 * 
	 * @param id The business id.
	 * @param name The business name.
	 */
	private BusinessInfo(String id, String name) {
		mId = id;
		mName = name;
	}

	/** The key to get business id. */
	private static final String KEY_BUSINESS_ID = "code_id";
	/** The key to get business name. */
	private static final String KEY_BUSINESS_NAME = "code_name";

	/**
	 * Check if the JSON object is a business info JSON object.
	 * 
	 * @param json The JSON object.
	 * @return True if the JSON object is a business info JSON object, otherwise
	 *         false.
	 * @author Luo Yinzhuo
	 */
	public static boolean isBusinessInfo(JSONObject json) {
		return json != null && json.has(KEY_BUSINESS_ID) && json.has(KEY_BUSINESS_NAME);
	}

	/**
	 * Parse a business info from its JSON object.
	 * 
	 * @param json The business info JSON object.
	 * @return The business info.
	 * @throws JSONException If the business info has error.
	 * @author Luo Yinzhuo
	 */
	public static BusinessInfo parse(JSONObject json) throws JSONException {
		return new BusinessInfo(json.getString(KEY_BUSINESS_ID), json.getString(KEY_BUSINESS_NAME));
	}

	@Override
    public String toString() {
	    return "BusinessInfo [mId=" + mId + ", mName=" + mName + "]";
    }
}
