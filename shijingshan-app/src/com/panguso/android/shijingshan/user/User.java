package com.panguso.android.shijingshan.user;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represent a user.
 * 
 * @author Luo Yinzhuo
 */
public class User {
	/** The user name. */
	private final String mName;
	/** The password. */
	private final String mPassword;

	/**
	 * Construct a new instance.
	 * 
	 * @param name The user name.
	 * @param password The password.
	 */
	public User(String name, String password) {
		mName = name;
		mPassword = password;
	}

	/**
	 * Get the user's name.
	 * 
	 * @return The user's name.
	 * @author Luo Yinzhuo
	 */
	public final String getName() {
		return mName;
	}

	/**
	 * Get the user's password.
	 * 
	 * @return The user's password.
	 * @author Luo Yinzhuo
	 */
	public final String getPassword() {
		return mPassword;
	}

	/** The key to record user's name. */
	private static final String KEY_NAME = "name";
	/** The key to record user's password. */
	private static final String KEY_PASSWORD = "password";

	/**
	 * Get the user's data in JSON format.
	 * 
	 * @return The user's data in JSON format.
	 * @throws JSONException If the format is failed.
	 * @author Luo Yinzhuo
	 */
	public final String getJson() throws JSONException {
		JSONObject user = new JSONObject();
		user.put(KEY_NAME, mName);
		user.put(KEY_PASSWORD, mPassword);
		return user.toString();
	}

	/**
	 * Construct a {@link User} from its data in JSON format.
	 * 
	 * @param json The user's data in JSON format.
	 * @return The user.
	 * @throws JSONException If there are error in JSON data.
	 * @author Luo Yinzhuo
	 */
	public static User parse(String json) throws JSONException {
		JSONObject user = new JSONObject(json);
		return new User(user.getString(KEY_NAME), user.getString(KEY_PASSWORD));
	}

	@Override
    public String toString() {
	    return "User [mName=" + mName + ", mPassword=" + mPassword + "]";
    }
}
