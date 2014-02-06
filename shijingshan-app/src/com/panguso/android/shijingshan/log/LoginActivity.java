package com.panguso.android.shijingshan.log;

import org.json.JSONException;

import com.panguso.android.shijingshan.Application;
import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.account.Account;
import com.panguso.android.shijingshan.account.AccountManager;
import com.panguso.android.shijingshan.dialog.MessageDialog;
import com.panguso.android.shijingshan.dialog.MessageDialog.OnMessageDialogListener;
import com.panguso.android.shijingshan.dialog.WaitingDialog;
import com.panguso.android.shijingshan.net.NetworkService;
import com.panguso.android.shijingshan.net.NetworkService.LoginRequestListener;
import com.panguso.android.shijingshan.register.RegisterActivity;
import com.panguso.android.shijingshan.widget.BlueTitleBar;
import com.panguso.android.shijingshan.widget.BlueTitleBar.OnBackListener;
import com.panguso.android.shijingshan.widget.UnderlineButton;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * The login activity.
 * 
 * @author Luo Yinzhuo
 */
public class LoginActivity extends Activity implements OnBackListener,
		OnClickListener, TextWatcher, LoginRequestListener,
		OnEditorActionListener, OnMessageDialogListener {
	/** The waiting dialog ID. */
	private static final int DIALOG_WAITING = 0;
	/** The unsupported dialog ID. */
	private static final int DIALOG_UNSUPPORTED = 1;
	/** The retry dialog ID. */
	private static final int DIALOG_RETRY = 2;
	/** The account not exist dialog ID. */
	private static final int DIALOG_ACCOUNT_NOT_EXIST = 3;
	/** The account canceled dialog ID. */
	private static final int DIALOG_ACCOUNT_CANCELED = 4;
	/** The account frozen dialog ID. */
	private static final int DIALOG_ACCOUNT_FROZEN = 5;
	/** The account not activated dialog ID. */
	private static final int DIALOG_ACCOUNT_NOT_ACTIVATED = 6;
	/** The account password not match dialog ID. */
	private static final int DIALOG_ACCOUNT_PASSWORD_NOT_MATCH = 7;
	/** The server no data error dialog ID. */
	private static final int DIALOG_NO_DATA_ERROR = 8;
	/** The server database error dialog ID. */
	private static final int DIALOG_DATABASE_ERROR = 9;

	@Override
	protected Dialog onCreateDialog(int id) {
		Resources resources = getResources();
		switch (id) {
		case DIALOG_WAITING:
			return new WaitingDialog(this);
		case DIALOG_UNSUPPORTED:
			return new MessageDialog(this, DIALOG_UNSUPPORTED,
					resources.getString(R.string.unsupported_title),
					resources.getString(R.string.unsupported_text),
					resources.getString(R.string.unsupported_button), this);
		case DIALOG_RETRY:
			return new MessageDialog(this, DIALOG_RETRY,
					resources.getString(R.string.retry_title),
					resources.getString(R.string.retry_text),
					resources.getString(R.string.retry_button), this);
		case DIALOG_ACCOUNT_NOT_EXIST:
			return new MessageDialog(this, DIALOG_ACCOUNT_NOT_EXIST,
					resources.getString(R.string.account_not_exist_title),
					resources.getString(R.string.account_not_exist_text),
					resources.getString(R.string.account_not_exit_button), this);
		case DIALOG_ACCOUNT_CANCELED:
			return new MessageDialog(this, DIALOG_ACCOUNT_CANCELED,
					resources.getString(R.string.account_canceled_title),
					resources.getString(R.string.account_canceled_text),
					resources.getString(R.string.account_canceled_button), this);
		case DIALOG_ACCOUNT_FROZEN:
			return new MessageDialog(this, DIALOG_ACCOUNT_FROZEN,
					resources.getString(R.string.account_frozen_title),
					resources.getString(R.string.account_frozen_text),
					resources.getString(R.string.account_frozen_button), this);
		case DIALOG_ACCOUNT_NOT_ACTIVATED:
			return new MessageDialog(this, DIALOG_ACCOUNT_NOT_ACTIVATED,
					resources.getString(R.string.account_not_activated_title),
					resources.getString(R.string.account_not_activated_text),
					resources.getString(R.string.account_not_activated_button),
					this);
		case DIALOG_ACCOUNT_PASSWORD_NOT_MATCH:
			return new MessageDialog(
					this,
					DIALOG_ACCOUNT_PASSWORD_NOT_MATCH,
					resources
							.getString(R.string.account_password_not_match_title),
					resources
							.getString(R.string.account_password_not_match_text),
					resources
							.getString(R.string.account_password_not_match_button),
					this);
		case DIALOG_NO_DATA_ERROR:
			return new MessageDialog(this, DIALOG_NO_DATA_ERROR,
					resources.getString(R.string.no_data_error_title),
					resources.getString(R.string.no_data_error_text),
					resources.getString(R.string.no_data_error_button), this);
		case DIALOG_DATABASE_ERROR:
			return new MessageDialog(this, DIALOG_DATABASE_ERROR,
					resources.getString(R.string.database_error_title),
					resources.getString(R.string.database_error_text),
					resources.getString(R.string.database_error_button), this);
		default:
			return null;
		}
	}

	/** The key to get last login account data. */
	private static final String KEY_LAST_LOGIN_ACCOUNT = "last_login_account";

	/** The title bar. */
	private BlueTitleBar mTitleBar;
	/** The account name. */
	private EditText mAccount;
	/** The minimum length of account name. */
	private int mMinLengthAccount;
	/** The password. */
	private EditText mPassword;
	/** The minimum length of password. */
	private int mMinLengthPassword;
	/** The login button. */
	private Button mLogin;
	/** The register button. */
	private UnderlineButton mRegister;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);

		mTitleBar = (BlueTitleBar) findViewById(R.id.title_bar);
		mTitleBar.setTitle(getResources().getString(R.string.log_title));
		mTitleBar.setOnBackListener(this);

		mAccount = (EditText) findViewById(R.id.account);
		mAccount.addTextChangedListener(this);
		mMinLengthAccount = getResources().getInteger(
				R.integer.min_length_username);

		mPassword = (EditText) findViewById(R.id.password);
		mPassword.addTextChangedListener(this);
		mPassword.setOnEditorActionListener(this);
		mMinLengthPassword = getResources().getInteger(
				R.integer.min_length_password);

		mLogin = (Button) findViewById(R.id.login);
		mLogin.setOnClickListener(this);

		mRegister = (UnderlineButton) findViewById(R.id.register);
		mRegister.setOnClickListener(this);

		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
		String lastLoginAccount = sharedPreferences.getString(
				KEY_LAST_LOGIN_ACCOUNT, "");
		if (lastLoginAccount.length() > 0) {
			try {
				Account account = Account.parse(lastLoginAccount);
				mAccount.setText(account.getAccount());
				mPassword.setText(account.getPassword());
				mLogin.setEnabled(true);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onBack() {
		finish();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onMessageDialogBack(int id) {
		dismissDialog(id);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onMessageDialogConfirmed(int id) {
		switch (id) {
		case DIALOG_RETRY:
			login();
			break;
		default:
			dismissDialog(id);
			break;
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (mAccount.getText().length() >= mMinLengthAccount
				&& mPassword.getText().length() >= mMinLengthPassword) {
			mLogin.setEnabled(true);
		} else {
			mLogin.setEnabled(false);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	/**
	 * Login.
	 * 
	 * @author Luo Yinzhuo
	 */
	@SuppressWarnings("deprecation")
	private void login() {
		showDialog(DIALOG_WAITING);
		NetworkService.login(getResources().getString(R.string.server_url),
				mAccount.getText().toString(), mPassword.getText().toString(),
				((Application) getApplication()).getUUID(), Build.MODEL, this);
	}

	/** Register activity request code. */
	private static final int REQUEST_CODE_REGISTER = 1;
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login:
			login();
			break;
		case R.id.register:
			Intent intent = new Intent(this, RegisterActivity.class);
			startActivityForResult(intent, REQUEST_CODE_REGISTER);
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		case REQUEST_CODE_REGISTER:
			if (resultCode == RESULT_OK) {
				String account = data.getStringExtra(RegisterActivity.KEY_ACCOUNT);
				String password = data.getStringExtra(RegisterActivity.KEY_PASSWORD);
				mAccount.setText(account);
				mPassword.setText(password);
				mLogin.setEnabled(true);
			}
			break;
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (mLogin.isEnabled()) {
			login();
			return true;
		}
		return false;
	}

	@Override
	public void onLoginRequestFailed() {
		runOnUiThread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				showDialog(DIALOG_UNSUPPORTED);
				dismissDialog(DIALOG_WAITING);
			}
		});
	}

	/** The key to store account. */
	public static final String KEY_ACCOUNT = "account";
	/** The key to store password. */
	public static final String KEY_PASSWORD = "password";

	@Override
	public void onLoginResponseSuccess(final String account,
			final String password) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					String jsonAccount = AccountManager
							.login(account, password);
					SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
					Editor editor = sharedPreferences.edit();
					editor.putString(KEY_LAST_LOGIN_ACCOUNT, jsonAccount);
					editor.commit();
				} catch (JSONException e) {
					e.printStackTrace();
				}

				Intent intent = new Intent();
				intent.putExtra(KEY_ACCOUNT, account);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	@Override
	public void onLoginResponseFailed() {
		runOnUiThread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				showDialog(DIALOG_RETRY);
				dismissDialog(DIALOG_WAITING);
			}
		});
	}

	@Override
	public void onLoginResponseAccountNotExist(String errorMessage) {
		runOnUiThread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				showDialog(DIALOG_ACCOUNT_NOT_EXIST);
				dismissDialog(DIALOG_WAITING);
			}
		});
	}

	@Override
	public void onLoginResponseAccountCanceled(String errorMessage) {
		runOnUiThread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				showDialog(DIALOG_ACCOUNT_CANCELED);
				dismissDialog(DIALOG_WAITING);
			}
		});
	}

	@Override
	public void onLoginResponseAccountFrozen(String errorMessage) {
		runOnUiThread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				showDialog(DIALOG_ACCOUNT_FROZEN);
				dismissDialog(DIALOG_WAITING);
			}
		});
	}

	@Override
	public void onLoginResponseAccountNotActivated(String errorMessage) {
		runOnUiThread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				showDialog(DIALOG_ACCOUNT_NOT_ACTIVATED);
				dismissDialog(DIALOG_WAITING);
			}
		});
	}

	@Override
	public void onLoginResponseAccountPasswordNotMatch(String errorMessage) {
		runOnUiThread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				showDialog(DIALOG_ACCOUNT_PASSWORD_NOT_MATCH);
				dismissDialog(DIALOG_WAITING);
			}
		});
	}

	@Override
	public void onLoginResponseNoDataError(String errorMessage) {
		runOnUiThread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				showDialog(DIALOG_NO_DATA_ERROR);
				dismissDialog(DIALOG_WAITING);
			}
		});
	}

	@Override
	public void onLoginResponseDatabaseError(String errorMessage) {
		runOnUiThread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				showDialog(DIALOG_DATABASE_ERROR);
				dismissDialog(DIALOG_WAITING);
			}
		});
	}
}
