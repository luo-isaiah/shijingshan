/**
 * 
 */
package com.panguso.android.shijingshan.notification;

import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.widget.BlueTitleBar;
import com.panguso.android.shijingshan.widget.BlueTitleBar.OnBackListener;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

/**
 * The notification list activity.
 * 
 * @author Luo Yinzhuo
 */
public class NotificationListActivity extends Activity implements
		OnBackListener {

	/** The title bar. */
	private BlueTitleBar mTitleBar;
	/** The notification layout. */
	private LinearLayout mNotification;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_list_activity);

		mTitleBar = (BlueTitleBar) findViewById(R.id.title_bar);
		mTitleBar.setTitle(getString(R.string.notification_title));
		mTitleBar.setOnBackListener(this);

		mNotification = (LinearLayout) findViewById(R.id.notification_layout);
	}

	@Override
	public void onBack() {
		finish();
	}

}
