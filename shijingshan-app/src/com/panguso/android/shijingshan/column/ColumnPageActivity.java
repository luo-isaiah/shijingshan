package com.panguso.android.shijingshan.column;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.account.AccountManager;
import com.panguso.android.shijingshan.column.StartDialog.OnStartDialogListener;
import com.panguso.android.shijingshan.dialog.MessageDialog;
import com.panguso.android.shijingshan.dialog.MessageDialog.OnMessageDialogListener;
import com.panguso.android.shijingshan.dialog.WaitingDialog;
import com.panguso.android.shijingshan.dialog.WaitingDialog.OnWaitingDialogListener;
import com.panguso.android.shijingshan.log.LogActivity;
import com.panguso.android.shijingshan.net.NetworkService;
import com.panguso.android.shijingshan.net.NetworkService.ColumnInfoListRequestListener;
import com.panguso.android.shijingshan.setting.SettingActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
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
		ColumnInfoListRequestListener, OnClickListener, OnStartDialogListener,
		OnWaitingDialogListener, OnMessageDialogListener {
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

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_START:
			return new StartDialog(this, this);
		case DIALOG_WAITING:
			mWaitingDialog = new WaitingDialog(this, this);
			return mWaitingDialog;
		case DIALOG_RETRY:
			Resources resources = getResources();
			mRetryDialog = new MessageDialog(this, DIALOG_RETRY,
					resources.getString(R.string.retry_title),
					resources.getString(R.string.retry_text),
					resources.getString(R.string.retry_button), this);
			return mRetryDialog;
		case DIALOG_UNSUPPORTED:
			resources = getResources();
			mUnsupportedDialog = new MessageDialog(this, DIALOG_UNSUPPORTED,
					resources.getString(R.string.unsupported_title),
					resources.getString(R.string.unsupported_text),
					resources.getString(R.string.unsupported_button), this);
			return mUnsupportedDialog;
		default:
			return null;
		}
	}

	/** The key to get last logged account in {@link SharedPreferences} data. */
	private static final String KEY_LAST_ACCOUNT = "last_account";
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

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showDialog(DIALOG_START);

		setContentView(R.layout.column_page_activity);
		mLog = (ImageButton) findViewById(R.id.log);
		mLog.setOnClickListener(this);
		mSetting = (ImageButton) findViewById(R.id.setting);
		mSetting.setOnClickListener(this);
		mSubscribe = (ImageButton) findViewById(R.id.subscribe);
		mNotice = (ImageButton) findViewById(R.id.notice);
		mColumnPageView = (ColumnPageView) findViewById(R.id.column_page);

		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
		String lastAccount = sharedPreferences.getString(KEY_LAST_ACCOUNT,
				"");
		Log.d("ColumnPageActivity", "onCreate last account:" + lastAccount);
		if (lastAccount.length() > 0) {

		} else {
			String columnPages = sharedPreferences.getString(KEY_COLUMN_PAGES,
					"");
			Log.d("ColumnPageActivity", "No account, column pages:" + columnPages);
			if (columnPages.length() > 0) {

			} else {
				NetworkService.getColumnInfoList(
						getResources().getString(R.string.server_url),
						AccountManager.getAccount(), this);
			}
		}
	}

	@Override
	protected void onDestroy() {
		Log.d("ColumnPageActivity", "onDestroy last account:" + AccountManager.getAccount() + " Initialized:" + mInitialized);
		if (mInitialized) {
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			try {
				mColumnPageView.save(sharedPreferences,
						AccountManager.getAccount() + KEY_COLUMN_PAGES);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		super.onDestroy();
	}

	/**
	 * Display column pages from {@link SharedPreferences}.
	 * 
	 * @author Luo Yinzhuo
	 */
	private void displayColumnPages() {
		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
		String columnPages = sharedPreferences.getString(
				AccountManager.getAccount() + KEY_COLUMN_PAGES, "");
		if (columnPages.length() > 0) {
			try {
				mColumnPageView.initialize(columnPages);
				onInitialized();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			// if (mColumnInfos.isEmpty()) {
			// NetworkService.getColumnInfoList(
			// getResources().getString(R.string.server_url),
			// AccountManager.getUserName(), this);
			// } else {
			// mColumnPageView.initialize(createColumnPages(mColumnInfos), 0);
			// onInitialized();
			// }
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

	@SuppressWarnings("deprecation")
	@Override
	public void onColumnInfoListRequestFailed() {
		showDialog(DIALOG_UNSUPPORTED);
	}

	@Override
	public void onColumnInfoListResponseSuccess(
			final List<ColumnInfo> columnInfos) {
		Log.d("ColumnPageActivity", "onColumnInfoListResponseSuccess:" + columnInfos.toString());
		
		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				mColumnPageView.initialize(createColumnPages(columnInfos), 0);
				onInitialized();
			}
		});
	}

	@Override
	public void onColumnInfoListResponseFailed() {
		Log.d("ColumnPageActivity", "onColumnInfoListResponseFailed is finishing:" + isFinishing());
		if (isFinishing()) {
			return;
		}
		runOnUiThread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				showDialog(DIALOG_RETRY);
			}
		});
	}

	/**
	 * Create the list of {@link ColumnPage} from list of {@link ColumnInfo}.
	 * 
	 * @param columnInfos
	 *            The list of {@link ColumnInfo}.
	 * @return The list of {@link ColumnPage} with current user state.
	 * @author Luo Yinzhuo
	 */
	private List<ColumnPage> createColumnPages(List<ColumnInfo> columnInfos) {
		// final boolean login = UserManager.isLogin();
		final boolean login = true;
		List<ColumnPage> columnPages = new ArrayList<ColumnPage>();
		ColumnPage page = new ColumnPage();
		columnPages.add(page);

		for (int i = 0; i < columnInfos.size(); i++) {
			ColumnInfo columnInfo = columnInfos.get(i);
			if (columnInfo.isOpen() || login) {
				if (page.isFull()) {
					page = new ColumnPage();
					columnPages.add(page);
				}
				page.addColumn(columnInfo.getColumn(this));
			}
		}

		if (page.isFull()) {
			page = new ColumnPage();
			columnPages.add(page);
		}
		page.addColumn(AddColumn.getInstance(this));
		return columnPages;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.log:
			Intent intent = new Intent(this, LogActivity.class);
			startActivity(intent);
			break;
		case R.id.setting:
			intent = new Intent(this, SettingActivity.class);
			startActivity(intent);
		default:
			break;
		}
	}

	@Override
	public void onStartDialogBack() {
		finish();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onTimeout() {
		dismissDialog(DIALOG_START);

		if ((mRetryDialog == null || !mRetryDialog.isShowing())
				&& (mUnsupportedDialog == null || !mUnsupportedDialog
						.isShowing()) && !mInitialized) {
			showDialog(DIALOG_WAITING);
		}
	}

	@Override
	public void onWaitingDialogBack() {
		finish();
	}

	@Override
	public void onMessageDialogBack(int id) {
		finish();
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
		case DIALOG_UNSUPPORTED:
			finish();
			break;
		}
	}
}
