package com.panguso.android.shijingshan.notification;

import java.util.List;

import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.notification.NotificationButton.OnNotificationButtonListener;
import com.panguso.android.shijingshan.widget.BlueTitleBar;
import com.panguso.android.shijingshan.widget.BlueTitleBar.OnBackListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.widget.LinearLayout;

/**
 * The notification list activity.
 * 
 * @author Luo Yinzhuo
 */
public class NotificationListActivity extends Activity implements
		OnBackListener, OnNotificationButtonListener,
		OnSharedPreferenceChangeListener {

	/** The title bar. */
	private BlueTitleBar mTitleBar;
	/** The notification layout. */
	private LinearLayout mNotification;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_list_activity);

		mTitleBar = (BlueTitleBar) findViewById(R.id.title_bar);
		mTitleBar.setTitle(getString(R.string.notification_list_title));
		mTitleBar.setOnBackListener(this);

		mNotification = (LinearLayout) findViewById(R.id.notification_layout);
		NotificationInfoManager.registerListener(this, this);
		refreshNotification();
	}

	@Override
	protected void onDestroy() {
		NotificationInfoManager.unregisterListener(this, this);
		super.onDestroy();
	}

	@Override
	public void onBack() {
		finish();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		refreshNotification();
	}

	/**
	 * Refresh notification list.
	 * 
	 * @author Luo Yinzhuo
	 */
	private void refreshNotification() {
		mNotification.removeAllViews();

		List<NotificationInfo> notificationInfos = NotificationInfoManager
				.getNotificationInfos(this);
		for (NotificationInfo notificationInfo : notificationInfos) {
			NotificationButton button = notificationInfo
					.getNotificationButton(this);
			button.setOnNotificationButtonListener(this);
			mNotification.addView(button);
		}
	}

	@Override
	public void onClicked(String id, String url) {
		Intent intent = new Intent(this, NotificationActivity.class);
		intent.putExtra(NotificationInfo.KEY_ID, id);
		intent.putExtra(NotificationInfo.KEY_URL, url);
		startActivity(intent);
	}
}
