package com.panguso.android.shijingshan.account;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represent an account.
 * 
 * @author Luo Yinzhuo
 */
public class Account {
	/** The account name. */
	private final String mName;
	/** The password. */
	private final String mPassword;

	/**
	 * Construct a new instance.
	 * 
	 * @param name The account name.
	 * @param password The password.
	 */
	public Account(String name, String password) {
		mName = name;
		mPassword = password;
	}

	/**
	 * Get the account's name.
	 * 
	 * @return The account's name.
	 * @author Luo Yinzhuo
	 */
	public final String getAccount() {
		return mName;
	}

	/**
	 * Get the account's password.
	 * 
	 * @return The account's password.
	 * @author Luo Yinzhuo
	 */
	public final String getPassword() {
		return mPassword;
	}

	/** The key to record account's name. */
	private static final String KEY_NAME = "name";
	/** The key to record account's password. */
	private static final String KEY_PASSWORD = "password";

	/**
	 * Get the account's data in JSON format.
	 * 
	 * @return The account's data in JSON format.
	 * @throws JSONException If the format is failed.
	 * @author Luo Yinzhuo
	 */
	public final String getJson() throws JSONException {
		JSONObject account = new JSONObject();
		account.put(KEY_NAME, mName);
		account.put(KEY_PASSWORD, mPassword);
		return account.toString();
	}

	/**
	 * Construct a {@link Account} from its data in JSON format.
	 * 
	 * @param json The account's data in JSON format.
	 * @return The {@link Account}.
	 * @throws JSONException If there are error in JSON data.
	 * @author Luo Yinzhuo
	 */
	public static Account parse(String json) throws JSONException {
		JSONObject account = new JSONObject(json);
		return new Account(account.getString(KEY_NAME), account.getString(KEY_PASSWORD));
	}

	@Override
    public String toString() {
	    return "User [mName=" + mName + ", mPassword=" + mPassword + "]";
    }
}
