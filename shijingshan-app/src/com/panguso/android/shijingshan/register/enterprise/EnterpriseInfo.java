/**
 * 
 */
package com.panguso.android.shijingshan.register.enterprise;

import org.json.JSONException;
import org.json.JSONObject;

import com.panguso.android.shijingshan.register.business.BusinessInfo;

/**
 * Specific for store a enterprise information.
 * 
 * @author Luo Yinzhuo
 */
public class EnterpriseInfo {
	/** The enterprise id. */
	private final int mId;
	/** The enterprise name. */
	private final String mName;

	/**
	 * Construct a new instance.
	 * 
	 * @param id
	 *            The enterprise id.
	 * @param name
	 *            The enterprise name.
	 */
	private EnterpriseInfo(int id, String name) {
		mId = id;
		mName = name;
	}

	/** The key to get enterprise id. */
	private static final String KEY_ENTERPRISE_ID = "enterpriseid";
	/** The key to get enterprise name. */
	private static final String KEY_ENTERPRISE_NAME = "enterprisename";
	
	/**
	 * Check if the JSON object is a enterprise info JSON object.
	 * 
	 * @param json The JSON object.
	 * @return True if the JSON object is a enterprise info JSON object, otherwise
	 *         false.
	 * @author Luo Yinzhuo
	 */
	public static boolean isEnterpriseInfo(JSONObject json) {
		return json != null && json.has(KEY_ENTERPRISE_ID) && json.has(KEY_ENTERPRISE_NAME);
	}

	/**
	 * Parse a enterprise info from its JSON object.
	 * 
	 * @param json The enterprise info JSON object.
	 * @return The enterprise info.
	 * @throws JSONException If the enterprise info has error.
	 * @author Luo Yinzhuo
	 */
	public static EnterpriseInfo parse(JSONObject json) throws JSONException {
		return new EnterpriseInfo(json.getInt(KEY_ENTERPRISE_ID), json.getString(KEY_ENTERPRISE_NAME));
	}
}
