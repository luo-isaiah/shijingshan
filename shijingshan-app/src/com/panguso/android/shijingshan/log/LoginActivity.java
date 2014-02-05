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

	/** The unsupported dialog. */
	private MessageDialog mUnsupportedDialog;

	@Override
	protected Dialog onCreateDialog(int id) {
		Resources resources = getResources();
		switch (id) {
		case DIALOG_WAITING:
			return new WaitingDialog(this);
		case DIALOG_UNSUPPORTED:
			mUnsupportedDialog = new MessageDialog(this, DIALOG_UNSUPPORTED,
					resources.getString(R.string.unsupported_title),
					resources.getString(R.string.unsupported_text),
					resources.getString(R.string.unsupported_button), this);
			return mUnsupportedDialog;
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
		String lastLoginAccount = sharedPreferences.getString(KEY_LAST_LOGIN_ACCOUNT, "");
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

	@Override
	public void onMessageDialogBack(int id) {
		finish();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onMessageDialogConfirmed(int id) {
		switch (id) {
		
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login:
			login();
			break;
		case R.id.register:
			Intent intent = new Intent(this, RegisterActivity.class);
			startActivity(intent);
			break;

		default:
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
	public void onLoginResponseSuccess(final String account, final String password) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					String jsonAccount = AccountManager.login(account, password);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.panguso.android.shijingshan.net.NetworkService.LoginRequestListener
	 * #onLoginResponseFailed()
	 */
	@Override
	public void onLoginResponseFailed() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.panguso.android.shijingshan.net.NetworkService.LoginRequestListener
	 * #onLoginResponseAccountNotExist(java.lang.String)
	 */
	@Override
	public void onLoginResponseAccountNotExist(String errorMessage) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.panguso.android.shijingshan.net.NetworkService.LoginRequestListener
	 * #onLoginResponseAccountCanceled(java.lang.String)
	 */
	@Override
	public void onLoginResponseAccountCanceled(String errorMessage) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.panguso.android.shijingshan.net.NetworkService.LoginRequestListener
	 * #onLoginResponseAccountFrozen(java.lang.String)
	 */
	@Override
	public void onLoginResponseAccountFrozen(String errorMessage) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.panguso.android.shijingshan.net.NetworkService.LoginRequestListener
	 * #onLoginResponseAccountNotActivated(java.lang.String)
	 */
	@Override
	public void onLoginResponseAccountNotActivated(String errorMessage) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.panguso.android.shijingshan.net.NetworkService.LoginRequestListener
	 * #onLoginResponseAccountPasswordNotMatch(java.lang.String)
	 */
	@Override
	public void onLoginResponseAccountPasswordNotMatch(String errorMessage) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.panguso.android.shijingshan.net.NetworkService.LoginRequestListener
	 * #onLoginResponseDatabaseError(java.lang.String)
	 */
	@Override
	public void onLoginResponseDatabaseError(String errorMessage) {
		// TODO Auto-generated method stub

	}
}
