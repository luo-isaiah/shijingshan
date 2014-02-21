package com.panguso.android.shijingshan.notification;

import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.account.AccountManager;
import com.panguso.android.shijingshan.net.NetworkService;
import com.panguso.android.shijingshan.widget.BlueTitleBar;
import com.panguso.android.shijingshan.widget.BlueTitleBar.OnBackListener;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * The notification activity.
 * 
 * @author Luo Yinzhuo
 */
public class NotificationActivity extends Activity implements OnBackListener {

	/** The title bar. */
	private BlueTitleBar mTitleBar;

	/** The content. */
	private WebView mContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_activity);

		mTitleBar = (BlueTitleBar) findViewById(R.id.title_bar);
		mTitleBar.setTitle(getString(R.string.notification_title));
		mTitleBar.setOnBackListener(this);

		mContent = (WebView) findViewById(R.id.notification_content);

		Intent intent = getIntent();
		String id = intent.getStringExtra(NotificationInfo.KEY_ID);
		String url = intent.getStringExtra(NotificationInfo.KEY_URL);

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(id.hashCode());

		NetworkService.acknowledgeNotification(getString(R.string.server_url),
				AccountManager.getAccount(), id);
		mContent.loadUrl(url);
	}

	@Override
	public void onBack() {
		finish();
	}
}
