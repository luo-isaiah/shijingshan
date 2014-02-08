package com.panguso.android.shijingshan.account;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Manage current {@link Account} state.
 * 
 * @author Luo Yinzhuo
 */
public final class AccountManager {
	/** Initialized account, because no account has logged in. */
	private static Account ACCOUNT = NoAccount.getInstance();
	/** The login time. */
	private static long LOGIN_TIME = 0;
	/** The login state. */
	private static boolean LOGIN = false;

	/**
	 * The account has logged in.
	 * 
	 * @param account
	 *            The account name.
	 * @param password
	 *            The password.
	 * @return The account data in JSON format.
	 * @throws JSONException
	 *             If there's error in JSON data.
	 * @author Luo Yinzhuo
	 */
	public static String login(String account, String password)
			throws JSONException {
		ACCOUNT = new Account(account, password);
		LOGIN_TIME = System.currentTimeMillis();
		return ACCOUNT.getJson();
	}
	
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
	 * Get the current account's name.
	 * 
	 * @return The current account's name.
	 * @author Luo Yinzhuo
	 */
	public static String getAccount() {
		return ACCOUNT.getAccount();
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
	 * @param json
	 *            The last login user data in JSON format.
	 * @throws JSONException
	 *             If there are JSON format error in the last login user data.
	 * @author Luo Yinzhuo
	 */
	public static void parse(String json) throws JSONException {
		JSONObject login = new JSONObject(json);
		final String name = login.getString(KEY_NAME);
		if (name.length() != 0) {
			ACCOUNT = new Account(name, login.getString(KEY_PASSWORD));
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
 * Specific for no account login state.
 * 
 * @author Luo Yinzhuo
 */
final class NoAccount extends Account {

	/** The single instance. */
	private static final NoAccount SINGLE_INSTANCE = new NoAccount();

	/**
	 * Construct a new instance.
	 */
	private NoAccount() {
		super("", "");
	}

	/**
	 * Get the single {@link NoAccount} instance.
	 * 
	 * @return The single {@link NoAccount} instance.
	 * @author Luo Yinzhuo
	 */
	static final NoAccount getInstance() {
		return SINGLE_INSTANCE;
	}
}
