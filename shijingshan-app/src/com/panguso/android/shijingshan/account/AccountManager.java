package com.panguso.android.shijingshan.account;

import org.json.JSONException;
import org.json.JSONObject;

import com.panguso.android.shijingshan.notification.NotificationBroadcastReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

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

	/** The request code for {@link NotificationBroadcastReceiver}. */
	private static final int REQUEST_CODE_NOTIFICATION_BROADCAST_RECEIVER = 0;

	/**
	 * A new account login.
	 * 
	 * @param account
	 *            The account name.
	 * @param password
	 *            The password.
	 * @return The account data in JSON format.
	 * @throws JSONException
	 *             If there's error in JSON data.
	 * 
	 * @author Luo Yinzhuo
	 */
	public static String login(Context context, String account, String password)
			throws JSONException {
		ACCOUNT = new Account(account, password);
		LOGIN_TIME = System.currentTimeMillis();

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
				REQUEST_CODE_NOTIFICATION_BROADCAST_RECEIVER, new Intent(
						context, NotificationBroadcastReceiver.class),
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis(),
				AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
		return ACCOUNT.getJson();
	}

	/**
	 * The account logout.
	 * 
	 * @author Luo Yinzhuo
	 */
	public static void logout(Context context) {
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
				REQUEST_CODE_NOTIFICATION_BROADCAST_RECEIVER, new Intent(
						context, NotificationBroadcastReceiver.class),
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
		ACCOUNT = NoAccount.getInstance();
	}

	/**
	 * Check if the user has logged in.
	 * 
	 * @return True if the user has logged in, otherwise false.
	 * @author Luo Yinzhuo
	 */
	public static boolean isLogin() {
		return ACCOUNT != NoAccount.getInstance();
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

	/**
	 * Get the current account's password.
	 * 
	 * @return The current account's password.
	 * 
	 * @author Luo Yinzhuo
	 */
	public static String getPassword() {
		return ACCOUNT.getPassword();
	}

	/**
	 * Change the current account's password.
	 * 
	 * @param password
	 *            The current account's password.
	 * @author Luo Yinzhuo
	 */
	public static void changePassword(String password) {
		ACCOUNT = new Account(ACCOUNT.getAccount(), password);
	}

	/** The key to store last login user's name. */
	private static final String KEY_ACCOUNT = "name";
	/** The key to store last login user's password. */
	private static final String KEY_PASSWORD = "password";
	/** The key to store last login user's time. */
	private static final String KEY_LOGIN_TIME = "time";

	/**
	 * Update manager from JSON format data.
	 * 
	 * @param json
	 *            The manager data in JSON format.
	 * @throws JSONException
	 *             If there are JSON format error in the manager data.
	 * 
	 * @author Luo Yinzhuo
	 */
	public static void parse(String json) throws JSONException {
		JSONObject accountManager = new JSONObject(json);
		final String account = accountManager.getString(KEY_ACCOUNT);
		if (account.length() > 0) {
			ACCOUNT = new Account(account,
					accountManager.getString(KEY_PASSWORD));
			LOGIN_TIME = accountManager.getLong(KEY_LOGIN_TIME);
		}
	}

	/**
	 * Get current manager data in JSON format.
	 * 
	 * @return The Current manager data in JSON format.
	 * @throws JSONException
	 *             If there are JSON format error occurs.
	 * 
	 * @author Luo Yinzhuo
	 */
	public static String getJson() throws JSONException {
		JSONObject accountManager = new JSONObject();
		accountManager.put(KEY_ACCOUNT, ACCOUNT.getAccount());
		accountManager.put(KEY_PASSWORD, ACCOUNT.getPassword());
		accountManager.put(KEY_LOGIN_TIME, LOGIN_TIME);
		return accountManager.toString();
	}

	/** The login time out. */
	private static final long LOGIN_TIME_OUT = 24 * 60 * 60 * 1000;

	/**
	 * Check if it needs to re-login.
	 * 
	 * @return True if it needs, otherwise false.
	 * 
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
