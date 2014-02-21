package com.panguso.android.shijingshan.notification;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.account.AccountManager;

/**
 * Manage received {@link NotificationInfo}.
 * 
 * @author Luo Yinzhuo
 */
public class NotificationInfoManager {
	/** The shared preferences name. */
	private static final String SHARED_PREFERENCES_NAME = "NotificationInfoManager";

	/** The key to get notifications data. */
	private static final String KEY_NOTIFICATIONS = "_notifications";
	/** The key to get notification data. */
	private static final String KEY_NOTIFICATION = "_notification";

	/**
	 * Called when {@link NotificationBroadcastReceiver} execute.
	 * 
	 * @param context
	 *            The context.
	 * @param notificationInfos
	 *            The {@link NotificationInfo} list.
	 * @author Luo Yinzhuo
	 */
	public static void onNotification(Context context,
			List<NotificationInfo> notificationInfos) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

		// First check if there's new notification and get one of the newest to
		// build a {@link Notification}.
		final String oldNotificationIds = sharedPreferences.getString(
				AccountManager.getAccount() + KEY_NOTIFICATIONS, "");
		List<NotificationInfo> newNotificationInfos = new ArrayList<NotificationInfo>();
		boolean newNotification = false;
		for (NotificationInfo notificationInfo : notificationInfos) {
			final String notificationId = notificationInfo.getId();
			if (!oldNotificationIds.contains(notificationId)) {
				newNotificationInfos.add(notificationInfo);

				if (!newNotification) {
					newNotification = true;
					buildNotification(context, notificationId,
							notificationInfo.getTitle(),
							notificationInfo.getSummary(),
							notificationInfo.getURL());
				}
			}
		}

		// Second save the new notifications into the {@link SharedPrefereces}.
		try {
			JSONArray notificationIds;
			if (oldNotificationIds.length() > 0) {
				notificationIds = new JSONArray(oldNotificationIds);
			} else {
				notificationIds = new JSONArray();
			}

			Editor editor = sharedPreferences.edit();
			for (NotificationInfo notificationInfo : newNotificationInfos) {
				final String notificationId = notificationInfo.getId();
				notificationIds.put(notificationId);
				editor.putString(notificationId + KEY_NOTIFICATION,
						notificationInfo.getJson());
			}
			editor.putString(AccountManager.getAccount() + KEY_NOTIFICATIONS,
					notificationIds.toString());
			editor.commit();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Build a {@link Notification}.
	 * 
	 * @param context
	 *            The context.
	 * @param id
	 *            The notification id.
	 * @param title
	 *            The notification title.
	 * @param summary
	 *            The notification summary.
	 * @param url
	 *            The notification url.
	 * @author Luo Yinzhuo
	 */
	@SuppressWarnings("deprecation")
	private static void buildNotification(Context context, String id,
			String title, String summary, String url) {
		Notification notification = new Notification(R.drawable.icon, title,
				System.currentTimeMillis());

		Intent intent = new Intent(context, NotificationActivity.class);
		intent.putExtra(NotificationInfo.KEY_ID, id);
		intent.putExtra(NotificationInfo.KEY_URL, url);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_ONE_SHOT);
		notification.setLatestEventInfo(context, title, summary, pendingIntent);
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(id.hashCode(), notification);
	}

	/**
	 * Register a listener to detect the {@link NotificationInfo} changes.
	 * 
	 * @param context
	 *            The context.
	 * @param listener
	 *            The listener.
	 * @author Luo Yinzhuo
	 */
	public static void registerListener(Context context,
			OnSharedPreferenceChangeListener listener) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
	}

	/**
	 * Unregister the listener from detecting the [@link NotificationInfo}
	 * changes.
	 * 
	 * @param context
	 *            The context.
	 * @param listener
	 *            The listener.
	 * @author Luo Yinzhuo
	 */
	public static void unregisterListener(Context context,
			OnSharedPreferenceChangeListener listener) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
	}

	/**
	 * Get current account's {@link NotificationInfo} list.
	 * 
	 * @param context
	 *            The context.
	 * @return The current account's {@link NotificationInfo} list.
	 * @author Luo Yinzhuo
	 */
	public static List<NotificationInfo> getNotificationInfos(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();

		try {
			JSONArray notificationIds = new JSONArray(
					sharedPreferences.getString(AccountManager.getAccount()
							+ KEY_NOTIFICATIONS, ""));
			for (int i = 0; i < notificationIds.length(); i++) {
				String notificationId = notificationIds.getString(i);
				JSONObject notification = new JSONObject(
						sharedPreferences.getString(notificationId
								+ KEY_NOTIFICATION, ""));
				if (NotificationInfo.isNotificationInfo(notification)) {
					notificationInfos.add(NotificationInfo.parse(notification));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return notificationInfos;
	}
}
