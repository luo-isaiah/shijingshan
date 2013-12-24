package com.panguso.android.shijingshan.user;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Manage current {@link User} state.
 * 
 * @author Luo Yinzhuo
 */
public final class UserManager {
	/** Initialized user, because no user has logged in. */
	private static User USER = NoUser.getInstance();
	/** The login time. */
	private static long LOGIN_TIME = 0;
	/** The login state. */
	private static boolean LOGIN = false;

	/**
	 * Check if the user has logged in.
	 * 
	 * @return True if the user has logged in, otherwise false.
	 * @author Luo Yinzhuo
	 */
	public static boolean isLogin() {
		return LOGIN;
	}

	/**
	 * Get the current user's name.
	 * 
	 * @return The current user's name.
	 * @author Luo Yinzhuo
	 */
	public static String getUserName() {
		return USER.getName();
	}

	/** The key to store last login user's name. */
	private static final String KEY_NAME = "name";
	/** The key to store last login user's password. */
	private static final String KEY_PASSWORD = "password";
	/** The key to store last login user's time. */
	private static final String KEY_TIME = "time";

	/**
	 * Parse the last login user data to update the manager.
	 * 
	 * @param json The last login user data in JSON format.
	 * @throws JSONException If there are JSON format error in the last login
	 *         user data.
	 * @author Luo Yinzhuo
	 */
	public static void parse(String json) throws JSONException {
		JSONObject login = new JSONObject(json);
		final String name = login.getString(KEY_NAME);
		if (name.length() != 0) {
			USER = new User(name, login.getString(KEY_PASSWORD));
			LOGIN_TIME = login.getLong(KEY_TIME);
			LOGIN = !needReLogin();
		}
	}

	/** The login time out. */
	private static final long LOGIN_TIME_OUT = 24 * 60 * 60 * 1000;

	/**
	 * Check if it needs to re-login.
	 * 
	 * @return True if it needs, otherwise false.
	 * @author Luo Yinzhuo
	 */
	public static boolean needReLogin() {
		return System.currentTimeMillis() - LOGIN_TIME > LOGIN_TIME_OUT;
	}
}

/**
 * Specific for no user login state.
 * 
 * @author Luo Yinzhuo
 */
final class NoUser extends User {

	/** The single instance. */
	private static final NoUser SINGLE_INSTANCE = new NoUser();

	/**
	 * Construct a new instance.
	 */
	private NoUser() {
		super("", "");
	}

	/**
	 * Get the single {@link NoUser} instance.
	 * 
	 * @return The single {@link NoUser} instance.
	 * @author Luo Yinzhuo
	 */
	static final NoUser getInstance() {
		return SINGLE_INSTANCE;
	}
}
