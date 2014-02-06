package com.panguso.android.shijingshan.subscribe;

import java.util.List;

import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.account.AccountManager;
import com.panguso.android.shijingshan.dialog.WaitingDialog;
import com.panguso.android.shijingshan.net.NetworkService;
import com.panguso.android.shijingshan.net.NetworkService.SearchSubscribeInfoListRequestListener;
import com.panguso.android.shijingshan.subscribe.SubscribeButton.OnSubscribeButtonListener;
import com.panguso.android.shijingshan.widget.BlueTitleBar;
import com.panguso.android.shijingshan.widget.BlueTitleBar.OnBackListener;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.LinearLayout;

/**
 * The subscribe activity.
 * 
 * @author Luo Yinzhuo
 */
public class SubscribeActivity extends Activity implements OnBackListener,
		SearchSubscribeInfoListRequestListener, OnSubscribeButtonListener {

	/** The waiting dialog ID. */
	private static final int DIALOG_WAITING = 0;
	/** The retry dialog ID. */
	private static final int DIALOG_RETRY = 1;

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_WAITING:
			return new WaitingDialog(this);
		default:
			return null;
		}
	}

	/** The title bar. */
	private BlueTitleBar mTitleBar;
	/** The subscribe layout. */
	private LinearLayout mSubscribe;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showDialog(DIALOG_WAITING);

		setContentView(R.layout.subscribe_activity);

		mTitleBar = (BlueTitleBar) findViewById(R.id.title_bar);
		mTitleBar.setTitle(getString(R.string.subscribe_title));
		mTitleBar.setOnBackListener(this);

		mSubscribe = (LinearLayout) findViewById(R.id.subscribe_layout);

		NetworkService.searchSubscribeInfoList(getString(R.string.server_url),
				AccountManager.getAccount(), this);
	}

	@Override
	public void onBack() {
		finish();
	}

	@Override
	public void onSearchSubscribeInfoListRequestFailed() {
	}

	@Override
	public void onSearchSubscribeInfoListResponseSuccess(
			final List<SubscribeInfo> subscribeInfos) {
		runOnUiThread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				mSubscribe.removeAllViews();

				for (SubscribeInfo subscribeInfo : subscribeInfos) {
					SubscribeButton button = subscribeInfo
							.getSubscribeButton(SubscribeActivity.this);
					button.setOnSubscribeButtonListener(SubscribeActivity.this);
					mSubscribe.addView(button);
				}
				dismissDialog(DIALOG_WAITING);
			}
		});
	}

	@Override
	public void onSearchSubscribeInfoListResponseFailed() {

	}

	@Override
	public void onClicked(int id, boolean check) {
		// TODO Auto-generated method stub
		
	}
}
