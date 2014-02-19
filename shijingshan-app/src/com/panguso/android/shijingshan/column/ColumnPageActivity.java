package com.panguso.android.shijingshan.column;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.account.AccountManager;
import com.panguso.android.shijingshan.dialog.MessageDialog;
import com.panguso.android.shijingshan.dialog.MessageDialog.OnMessageDialogListener;
import com.panguso.android.shijingshan.dialog.WaitingDialog;
import com.panguso.android.shijingshan.login.LoginActivity;
import com.panguso.android.shijingshan.net.NetworkService;
import com.panguso.android.shijingshan.net.NetworkService.ColumnInfoListRequestListener;
import com.panguso.android.shijingshan.setting.SettingActivity;
import com.panguso.android.shijingshan.subscribe.SubscribeActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

/**
 * The column page activity.
 * 
 * @author Luo Yinzhuo
 * @date 2013-8-7
 */
public class ColumnPageActivity extends Activity implements
		ColumnInfoListRequestListener, OnClickListener, OnMessageDialogListener {
	/** The initialize flag. */
	private boolean mInitialized = false;
	/** The waiting dialog. */
	private WaitingDialog mWaitingDialog;
	/** The retry dialog. */
	private MessageDialog mRetryDialog;
	/** The unsupported dialog. */
	private MessageDialog mUnsupportedDialog;

	/** The start dialog ID. */
	private static final int DIALOG_START = 0;
	/** The waiting dialog ID. */
	private static final int DIALOG_WAITING = 1;
	/** The retry dialog ID. */
	private static final int DIALOG_RETRY = 2;
	/** The unsupported dialog ID. */
	private static final int DIALOG_UNSUPPORTED = 3;
	/** The logout dialog ID. */
	private static final int DIALOG_LOGOUT = 4;

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_START:
			return new StartDialog(this);
		case DIALOG_WAITING:
			mWaitingDialog = new WaitingDialog(this);
			return mWaitingDialog;
		case DIALOG_RETRY:
			mRetryDialog = new MessageDialog(this, DIALOG_RETRY,
					getString(R.string.retry_title),
					getString(R.string.retry_text),
					getString(R.string.retry_button), this);
			return mRetryDialog;
		case DIALOG_UNSUPPORTED:
			mUnsupportedDialog = new MessageDialog(this, DIALOG_UNSUPPORTED,
					getString(R.string.unsupported_title),
					getString(R.string.unsupported_text),
					getString(R.string.unsupported_button), this);
			return mUnsupportedDialog;
		case DIALOG_LOGOUT:
			return new MessageDialog(this, DIALOG_LOGOUT,
					getString(R.string.logout_title),
					getString(R.string.logout_text),
					getString(R.string.logout_button), this);
		default:
			return null;
		}
	}

	/** The key to get last account data. */
	public static final String KEY_LAST_ACCOUNT = "last_account";
	/** The key to get the last displayed {@link ColumnPage}'s data. */
	private static final String KEY_COLUMN_PAGES = "_column_page";

	/** The column page view. */
	private ColumnPageView mColumnPageView;
	/** The log button. */
	private ImageButton mLog;
	/** The setting button. */
	private ImageButton mSetting;
	/** The subscribe button. */
	private ImageButton mSubscribe;
	/** The notice button. */
	private ImageButton mNotice;

	private static final int MESSAGE_START_DIALOG_TIMEOUT = 0;
	/** The start dialog timeout. */
	private static final int START_DIALOG_TIMEOUT = 5000;

	/** The start dialog handler. */
	private Handler mStartDialogHandler;
	/** The start dialog timeout flag. */
	private boolean mStartDialogTimeout = false;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showDialog(DIALOG_START);
		mStartDialogHandler = new Handler(getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MESSAGE_START_DIALOG_TIMEOUT:
					Log.d("ColumnPageActivity", "Handle time out message!");
					mStartDialogTimeout = true;
					onStartDialogTimeout();
					break;
				}
			}
		};

		mStartDialogHandler.sendEmptyMessageDelayed(
				MESSAGE_START_DIALOG_TIMEOUT, START_DIALOG_TIMEOUT);

		setContentView(R.layout.column_page_activity);
		mLog = (ImageButton) findViewById(R.id.log);
		mLog.setOnClickListener(this);

		mSetting = (ImageButton) findViewById(R.id.setting);
		mSetting.setOnClickListener(this);

		mSubscribe = (ImageButton) findViewById(R.id.subscribe);
		mSubscribe.setOnClickListener(this);
		mSubscribe.setVisibility(View.INVISIBLE);

		mNotice = (ImageButton) findViewById(R.id.notice);
		mColumnPageView = (ColumnPageView) findViewById(R.id.column_page);

		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
		String lastAccount = sharedPreferences.getString(KEY_LAST_ACCOUNT, "");

		if (lastAccount.length() > 0) {
			try {
				AccountManager.parse(lastAccount);
				if (AccountManager.needReLogin()) {
					Intent intent = new Intent(this, LoginActivity.class);
					intent.putExtra(LoginActivity.KEY_ACCOUNT,
							AccountManager.getAccount());
					intent.putExtra(LoginActivity.KEY_PASSWORD,
							AccountManager.getPassword());
					AccountManager.logout(this);
					startActivityForResult(intent, REQUEST_CODE_LOGIN);
					return;
				} else {
					mSubscribe.setVisibility(View.VISIBLE);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		NetworkService.getColumnInfoList(getString(R.string.server_url),
				AccountManager.getAccount(), this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mStartDialogTimeout) {
			onStartDialogTimeout();
		}
	}

	@Override
	protected void onDestroy() {
		if (mInitialized) {
			try {
				String columnPage = mColumnPageView.getJson();
				Editor editor = getPreferences(MODE_PRIVATE).edit();
				editor.putString(
						AccountManager.getAccount() + KEY_COLUMN_PAGES,
						columnPage);
				editor.commit();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		super.onDestroy();
	}

	/**
	 * Display column pages.
	 * 
	 * @param columns
	 *            The columns.
	 * @author Luo Yinzhuo
	 */
	private void displayColumnPages(List<Column> columns) {
		if (AccountManager.isLogin()) {
			columns.add(AddColumn.getInstance(this));
			mLog.setImageResource(R.drawable.login);
			mSubscribe.setVisibility(View.VISIBLE);
		} else {
			mLog.setImageResource(R.drawable.logout);
			mSubscribe.setVisibility(View.INVISIBLE);
		}

		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
		String columnPages = sharedPreferences.getString(
				AccountManager.getAccount() + KEY_COLUMN_PAGES, "");
		if (columnPages.length() > 0) {
			try {
				mColumnPageView.initialize(columnPages);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		mColumnPageView.filter(columns);
		onInitialized();
	}

	/**
	 * Save column pages.
	 * 
	 * @author Luo Yinzhuo
	 */
	private void saveColumnPages() {
		if (mInitialized) {
			try {
				String columnPage = mColumnPageView.getJson();
				Editor editor = getPreferences(MODE_PRIVATE).edit();
				editor.putString(
						AccountManager.getAccount() + KEY_COLUMN_PAGES,
						columnPage);
				editor.commit();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Called when the {@link ColumnPageView} complete the initialization.
	 * 
	 * @author Luo Yinzhuo
	 */
	@SuppressWarnings("deprecation")
	private void onInitialized() {
		if (mWaitingDialog != null && mWaitingDialog.isShowing()) {
			dismissDialog(DIALOG_WAITING);
		}
		mInitialized = true;
	}

	@Override
	public void onColumnInfoListRequestFailed() {
		runOnUiThread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				showDialog(DIALOG_UNSUPPORTED);
			}
		});
	}

	@Override
	public void onColumnInfoListResponseSuccess(
			final List<ColumnInfo> columnInfos) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				List<Column> columns = new ArrayList<Column>();
				for (ColumnInfo columnInfo : columnInfos) {
					columns.add(columnInfo.getColumn(ColumnPageActivity.this));
				}
				displayColumnPages(columns);
			}
		});
	}

	@Override
	public void onColumnInfoListResponseFailed() {
		runOnUiThread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				showDialog(DIALOG_RETRY);
			}
		});
	}

	/** Login activity request code. */
	private static final int REQUEST_CODE_LOGIN = 1;
	/** Subscribe activity request code. */
	static final int REQUEST_CODE_SUBSCRIBE = 2;

	/**
	 * Subscribe.
	 */
	void subscribe() {
		saveColumnPages();
		startActivityForResult(new Intent(this, SubscribeActivity.class),
				REQUEST_CODE_SUBSCRIBE);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		mColumnPageView.explore();

		switch (v.getId()) {
		case R.id.log:
			if (AccountManager.isLogin()) {
				showDialog(DIALOG_LOGOUT);
			} else {
				startActivityForResult(new Intent(this, LoginActivity.class),
						REQUEST_CODE_LOGIN);
			}
			break;
		case R.id.subscribe:
			subscribe();
			break;
		case R.id.setting:
			startActivity(new Intent(this, SettingActivity.class));
		default:
			break;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_LOGIN:
			if (resultCode == RESULT_OK) {
				saveColumnPages();

				Editor editor = getPreferences(MODE_PRIVATE).edit();
				try {
					editor.putString(KEY_LAST_ACCOUNT, AccountManager.getJson());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				editor.commit();

				mInitialized = false;
			}

			if (!mInitialized) {
				showDialog(DIALOG_WAITING);
				NetworkService.getColumnInfoList(
						getString(R.string.server_url),
						AccountManager.getAccount(), this);
			}
			break;
		case REQUEST_CODE_SUBSCRIBE:
			showDialog(DIALOG_WAITING);
			mInitialized = false;
			NetworkService.getColumnInfoList(
					getResources().getString(R.string.server_url),
					AccountManager.getAccount(), this);
			break;
		}
	}

	@SuppressWarnings("deprecation")
	private void onStartDialogTimeout() {
		if (mUnsupportedDialog != null) {
			return;
		} else {
			Log.d("ColumnPageActivity", "Activity dismiss dialog!");
			dismissDialog(DIALOG_START);
			if (!mInitialized
					&& (mRetryDialog == null || !mRetryDialog.isShowing())) {
				showDialog(DIALOG_WAITING);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onMessageDialogBack(int id) {
		switch (id) {
		case DIALOG_LOGOUT:
			dismissDialog(DIALOG_LOGOUT);
			break;
		default:
			finish();
			break;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onMessageDialogConfirmed(int id) {
		switch (id) {
		case DIALOG_RETRY:
			NetworkService.getColumnInfoList(
					getResources().getString(R.string.server_url),
					AccountManager.getAccount(), this);
			showDialog(DIALOG_WAITING);
			dismissDialog(DIALOG_RETRY);
			break;
		case DIALOG_LOGOUT:
			saveColumnPages();
			AccountManager.logout(this);

			Editor editor = getPreferences(MODE_PRIVATE).edit();
			try {
				editor.putString(KEY_LAST_ACCOUNT, AccountManager.getJson());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			editor.commit();

			NetworkService.getColumnInfoList(
					getResources().getString(R.string.server_url),
					AccountManager.getAccount(), this);
			showDialog(DIALOG_WAITING);
			dismissDialog(DIALOG_LOGOUT);
			break;
		case DIALOG_UNSUPPORTED:
			finish();
			break;
		}
	}
}
