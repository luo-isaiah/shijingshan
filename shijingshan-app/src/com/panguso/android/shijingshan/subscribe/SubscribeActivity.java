package com.panguso.android.shijingshan.subscribe;

import java.util.ArrayList;
import java.util.List;

import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.account.AccountManager;
import com.panguso.android.shijingshan.dialog.MessageDialog;
import com.panguso.android.shijingshan.dialog.MessageDialog.OnMessageDialogListener;
import com.panguso.android.shijingshan.dialog.WaitingDialog;
import com.panguso.android.shijingshan.net.NetworkService;
import com.panguso.android.shijingshan.net.NetworkService.SaveSubscribeInfoListRequestListener;
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
		SearchSubscribeInfoListRequestListener, OnSubscribeButtonListener,
		OnMessageDialogListener, SaveSubscribeInfoListRequestListener {

	/** The waiting dialog ID. */
	private static final int DIALOG_WAITING = 0;
	/** The retry dialog ID. */
	private static final int DIALOG_RETRY = 1;

	/** The retry data. */
	private final Bundle mRetryData = new Bundle();
	/** The retry type key. */
	private static final String KEY_RETRY_TYPE = "retry_type";
	/** The retry type search. */
	private static final int RETRY_TYPE_SEARCH = 0;
	/** The retry type add. */
	private static final int RETRY_TYPE_SAVE = 1;
	/** The retry subscribe id key. */
	private static final String KEY_SUBSCRIBE_IDS = "subscribe_ids";

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_WAITING:
			return new WaitingDialog(this);
		case DIALOG_RETRY:
			return new MessageDialog(this, DIALOG_RETRY,
					getString(R.string.retry_title),
					getString(R.string.retry_text),
					getString(R.string.retry_button), this);
		default:
			return null;
		}
	}

	/** The title bar. */
	private BlueTitleBar mTitleBar;
	/** The subscribe layout. */
	private LinearLayout mSubscribe;
	/** The subscribe button cache. */
	private final List<SubscribeButton> mSubscribeButtonCache = new ArrayList<SubscribeButton>();

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
				int buttonSize = mSubscribeButtonCache.size();

				for (int i = 0; i < subscribeInfos.size(); i++) {
					SubscribeInfo subscribeInfo = subscribeInfos.get(i);

					SubscribeButton button;
					if (i < buttonSize) {
						button = mSubscribeButtonCache.get(i);
						button = subscribeInfo.getSubscribeButton(button);
					} else {
						button = subscribeInfo
								.getSubscribeButton(SubscribeActivity.this);
						button.setOnSubscribeButtonListener(SubscribeActivity.this);
						mSubscribeButtonCache.add(button);
					}
					mSubscribe.addView(button);
				}
				dismissDialog(DIALOG_WAITING);
			}
		});
	}

	@Override
	public void onSearchSubscribeInfoListResponseFailed() {
		runOnUiThread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				showDialog(DIALOG_RETRY);
				mRetryData.putInt(KEY_RETRY_TYPE, RETRY_TYPE_SEARCH);
				dismissDialog(DIALOG_WAITING);
			}
		});
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClicked(int id, boolean check) {
		List<Integer> subscribeIds = new ArrayList<Integer>();
		for (SubscribeButton button : mSubscribeButtonCache) {
			if (button.isChecked()) {
				subscribeIds.add(button.getSubscribeId());
			}
		}

		if (check) {
			subscribeIds.remove(Integer.valueOf(id));
		} else {
			subscribeIds.add(id);
		}

		NetworkService.saveSubscribeInfoList(getString(R.string.server_url),
				AccountManager.getAccount(), subscribeIds, this);
		showDialog(DIALOG_WAITING);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onMessageDialogBack(int id) {
		switch (id) {
		case DIALOG_RETRY:
			dismissDialog(DIALOG_RETRY);
			int retryType = mRetryData.getInt(KEY_RETRY_TYPE);
			if (retryType == RETRY_TYPE_SEARCH) {
				finish();
			}
			break;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onMessageDialogConfirmed(int id) {
		switch (id) {
		case DIALOG_RETRY:
			dismissDialog(DIALOG_RETRY);
			showDialog(DIALOG_WAITING);
			int retryType = mRetryData.getInt(KEY_RETRY_TYPE);
			switch (retryType) {
			case RETRY_TYPE_SEARCH:
				NetworkService.searchSubscribeInfoList(
						getString(R.string.server_url),
						AccountManager.getAccount(), this);
				break;
			case RETRY_TYPE_SAVE:
				NetworkService
						.saveSubscribeInfoList(
								getString(R.string.server_url),
								AccountManager.getAccount(),
								mRetryData
										.getIntegerArrayList(KEY_SUBSCRIBE_IDS),
								this);
				break;
			}
		}
	}

	@Override
	public void onSaveSubscribeInfoListRequestFailed() {
	}

	@Override
	public void onSaveSubscribeInfoListResponseSuccess(
			final List<Integer> subscribeIds) {
		runOnUiThread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				for (SubscribeButton button : mSubscribeButtonCache) {
					button.setCheckBox(subscribeIds.contains(button
							.getSubscribeId()));
				}
				dismissDialog(DIALOG_WAITING);
			}
		});
	}

	@Override
	public void onSaveSubscribeInfoListResponseFailed(
			final List<Integer> subscribeIds) {
		runOnUiThread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				showDialog(DIALOG_RETRY);
				mRetryData.putInt(KEY_RETRY_TYPE, RETRY_TYPE_SAVE);
				mRetryData.putIntegerArrayList(KEY_SUBSCRIBE_IDS,
						(ArrayList<Integer>) subscribeIds);
				dismissDialog(DIALOG_WAITING);
			}
		});
	}
}
