/**
 *  Copyright (c)  2011-2020 Panguso, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Panguso, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Panguso.
 */
package com.panguso.android.shijingshan.register;

import com.panguso.android.shijingshan.Application;
import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.dialog.MessageDialog;
import com.panguso.android.shijingshan.dialog.MessageDialog.OnMessageDialogListener;
import com.panguso.android.shijingshan.dialog.WaitingDialog;
import com.panguso.android.shijingshan.net.NetworkService;
import com.panguso.android.shijingshan.net.NetworkService.RegisterRequestListener;
import com.panguso.android.shijingshan.register.RegisterArrowButton.OnRegisterArrowButtonListener;
import com.panguso.android.shijingshan.register.RegisterCheckEditText.OnRegisterCheckEditTextListener;
import com.panguso.android.shijingshan.register.business.BusinessDialog;
import com.panguso.android.shijingshan.register.business.BusinessDialog.OnBusinessDialogListener;
import com.panguso.android.shijingshan.register.business.NewEnterpriseDialog;
import com.panguso.android.shijingshan.register.business.NewEnterpriseDialog.OnNewEnterpriseDialogListener;
import com.panguso.android.shijingshan.register.enterprise.EnterpriseDialog;
import com.panguso.android.shijingshan.register.enterprise.EnterpriseDialog.OnEnterpriseDialogListener;
import com.panguso.android.shijingshan.register.usertype.UserTypeDialog;
import com.panguso.android.shijingshan.register.usertype.UserTypeDialog.OnUserTypeDialogListener;
import com.panguso.android.shijingshan.widget.BlueTitleBar;
import com.panguso.android.shijingshan.widget.BlueTitleBar.OnBackListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Specified for register new user.
 * 
 * @author Luo Yinzhuo
 * @date 2013-10-21
 */
public class RegisterActivity extends Activity implements OnBackListener,
		OnClickListener, OnRegisterArrowButtonListener,
		OnUserTypeDialogListener, OnBusinessDialogListener,
		OnEnterpriseDialogListener, OnNewEnterpriseDialogListener,
		OnRegisterCheckEditTextListener, RegisterRequestListener,
		OnMessageDialogListener {

	/** The waiting dialog ID. */
	private static final int DIALOG_WAITING = -1;
	/** The retry dialog ID. */
	private static final int DIALOG_RETRY = -2;
	/** The account exist dialog ID. */
	private static final int DIALOG_ACCOUNT_EXIST = -3;
	/** The server database error dialog ID. */
	private static final int DIALOG_DATABASE_ERROR = -4;

	/** The business dialog ID. */
	private static final int DIALOG_BUSINESS = 1;
	/** The enterprise dialog ID. */
	private static final int DIALOG_ENTERPRISE = 2;
	/** The new enterprise dialog ID. */
	private static final int DIALOG_NEW_ENTERPRISE = 3;
	/** The user type dialog ID. */
	private static final int DIALOG_USER_TYPE = 4;

	/** The retry data. */
	private final Bundle mRetryData = new Bundle();
	/** The retry type key. */
	private static final String KEY_RETRY_TYPE = "retry_type";
	/** The retry type register. */
	private static final int RETRY_TYPE_REGISTER = 0;
	/** The retry type business. */
	private static final int RETRY_TYPE_BUSINESS = 1;
	/** The retry type enterprise. */
	private static final int RETRY_TYPE_ENTERPRISE = 2;
	/** The retry type user type. */
	private static final int RETRY_TYPE_USER_TYPE = 3;

	/** The business dialog. */
	private BusinessDialog mBusinessDialog;
	/** The enterprise dialog. */
	private EnterpriseDialog mEnterpriseDialog;
	/** The user type dialog. */
	private UserTypeDialog mUserTypeDialog;

	@Override
	protected Dialog onCreateDialog(int id) {
		Resources resources = getResources();
		switch (id) {
		case DIALOG_WAITING:
			return new WaitingDialog(this);
		case DIALOG_RETRY:
			return new MessageDialog(this, DIALOG_RETRY,
					resources.getString(R.string.retry_title),
					resources.getString(R.string.retry_text),
					resources.getString(R.string.retry_button), this);
		case DIALOG_ACCOUNT_EXIST:
			return new MessageDialog(this, DIALOG_ACCOUNT_EXIST,
					resources.getString(R.string.account_exist_title),
					resources.getString(R.string.account_exist_text),
					resources.getString(R.string.account_exist_button), this);
		case DIALOG_DATABASE_ERROR:
			return new MessageDialog(this, DIALOG_DATABASE_ERROR,
					resources.getString(R.string.database_error_title),
					resources.getString(R.string.database_error_text),
					resources.getString(R.string.database_error_button), this);
		case DIALOG_BUSINESS:
			mBusinessDialog = new BusinessDialog(this, this);
			return mBusinessDialog;
		case DIALOG_NEW_ENTERPRISE:
			return new NewEnterpriseDialog(this, this);
		case DIALOG_ENTERPRISE:
			mEnterpriseDialog = new EnterpriseDialog(this, mBusinessId, this);
			return mEnterpriseDialog;
		case DIALOG_USER_TYPE:
			mUserTypeDialog = new UserTypeDialog(this, this);
			return mUserTypeDialog;
		default:
			return null;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (id == DIALOG_ENTERPRISE) {
			((EnterpriseDialog) dialog).setBusinessId(mBusinessId);
		}
		super.onPrepareDialog(id, dialog);
	}

	/** The title bar. */
	private BlueTitleBar mTitleBar;

	/** The user name. */
	private RegisterCheckEditText mUsername;
	/** The user name already exist. */
	private final StringBuilder mUsernameExist = new StringBuilder();
	/** The user name valid flag. */
	private boolean mUsernameValid = false;

	/** The password. */
	private RegisterCheckEditText mPassword;
	/** The password valid flag. */
	private boolean mPasswordValid = false;

	/** The confirm password. */
	private RegisterCheckEditText mConfirmPassword;
	/** The confirm valid flag. */
	private boolean mConfirmPasswordValid = false;

	/** The mobile number. */
	private RegisterCheckEditText mMobileNumber;
	/** The confirm valid flag. */
	private boolean mMobileNumberValid = false;

	/** The enterprise. */
	private RegisterArrowButton mEnterprise;
	/** The business id. */
	private int mBusinessId = 0;
	/** The enterprise id. */
	private Integer mEnterpriseId;
	/** The new enterprise id. */
	private static final int NEW_ENTERPRISE_ID = -1;
	/** The enterprise name. */
	private String mEnterpriseName = "";
	/** The user type. */
	private RegisterArrowButton mUserType;
	/** The user type id. */
	private Integer mUserTypeId;
	/** The register button. */
	private Button mRegister;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_activity);

		mTitleBar = (BlueTitleBar) findViewById(R.id.title_bar);
		mTitleBar.setTitle(getResources().getString(R.string.register_title));
		mTitleBar.setOnBackListener(this);

		Resources resources = getResources();

		mUsername = (RegisterCheckEditText) findViewById(R.id.username);
		mUsername.setTextHint(R.string.register_username_hint);
		mUsername.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_NORMAL);
		mUsername.setMaxLength(resources
				.getInteger(R.integer.max_length_username));
		mUsername.setDigits(R.string.digits_username);
		mUsername.setOnRegisterCheckEditTextListenerListener(this);

		mPassword = (RegisterCheckEditText) findViewById(R.id.password);
		mPassword.setTextHint(R.string.register_password_hint);
		mPassword.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		mPassword.setMaxLength(resources
				.getInteger(R.integer.max_length_password));
		mPassword.setDigits(R.string.digits_password);
		mPassword.setOnRegisterCheckEditTextListenerListener(this);

		mConfirmPassword = (RegisterCheckEditText) findViewById(R.id.confirm_password);
		mConfirmPassword.setTextHint(R.string.register_confirm_password_hint);
		mConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		mConfirmPassword.setMaxLength(resources
				.getInteger(R.integer.max_length_password));
		mConfirmPassword.setDigits(R.string.digits_password);
		mConfirmPassword.setOnRegisterCheckEditTextListenerListener(this);

		mMobileNumber = (RegisterCheckEditText) findViewById(R.id.mobile_number);
		mMobileNumber.setTextHint(R.string.register_mobile_number_hint);
		mMobileNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
		mMobileNumber.setMaxLength(resources
				.getInteger(R.integer.length_mobile_number));
		mMobileNumber.setDigits(R.string.digits_mobile_number);
		mMobileNumber.setOnRegisterCheckEditTextListenerListener(this);

		mEnterprise = (RegisterArrowButton) findViewById(R.id.enterprise);
		mEnterprise.setTextHint(R.string.register_enterprise_hint);
		mEnterprise.setOnRegisterArrowButtonListener(this);

		mUserType = (RegisterArrowButton) findViewById(R.id.user_type);
		mUserType.setTextHint(R.string.register_user_type_hint);
		mUserType.setOnRegisterArrowButtonListener(this);

		mRegister = (Button) findViewById(R.id.register);
		mRegister.setOnClickListener(this);
	}

	@Override
	public void onBack() {
		finish();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onMessageDialogBack(int id) {
		switch (id) {
		case DIALOG_RETRY:
			dismissDialog(DIALOG_RETRY);
			int retryType = mRetryData.getInt(KEY_RETRY_TYPE);
			switch (retryType) {
			case RETRY_TYPE_BUSINESS:
				dismissDialog(DIALOG_BUSINESS);
				break;
			case RETRY_TYPE_ENTERPRISE:
				dismissDialog(DIALOG_ENTERPRISE);
				break;
			case RETRY_TYPE_USER_TYPE:
				dismissDialog(DIALOG_USER_TYPE);
				break;
			}
			break;
		default:
			dismissDialog(id);
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
			case RETRY_TYPE_REGISTER:
				NetworkService.register(
						getResources().getString(R.string.server_url),
						mUsername.getText(), mPassword.getText(),
						mMobileNumber.getText(), mEnterpriseId,
						mEnterpriseName,
						((Application) getApplication()).getUUID(),
						Build.MODEL, mUserTypeId, this);
				break;
			case RETRY_TYPE_BUSINESS:
				mBusinessDialog.retry();
				break;
			case RETRY_TYPE_ENTERPRISE:
				mEnterpriseDialog.retry();
				break;
			case RETRY_TYPE_USER_TYPE:
				mUserTypeDialog.retry();
				break;
			}
			break;
		default:
			dismissDialog(id);
			break;
		}
	}

	@Override
	public void onRegisterCheckEditTextChanged(int id, String text) {
		switch (id) {
		case R.id.username:
			mUsernameValid = text.length() >= getResources().getInteger(
					R.integer.min_length_username)
					&& mUsernameExist.indexOf(text) == -1;
			break;
		case R.id.password:
			mPasswordValid = text.length() >= getResources().getInteger(
					R.integer.min_length_password);
			String confirmPassword = mConfirmPassword.getText();
			mConfirmPasswordValid = confirmPassword.equals(text);
			mConfirmPassword.setCheck(mConfirmPasswordValid);
			break;
		case R.id.confirm_password:
			mConfirmPasswordValid = text.length() >= getResources().getInteger(
					R.integer.min_length_password)
					&& text.equals(mPassword.getText());
			break;
		case R.id.mobile_number:
			mMobileNumberValid = text.length() == getResources().getInteger(
					R.integer.length_mobile_number);
			break;
		}
		checkIfRegisterEnabled();
	}

	@Override
	public boolean onRegisterCheckEditTextLostFocus(int id) {
		switch (id) {
		case R.id.username:
			return mUsernameValid;
		case R.id.password:
			return mPasswordValid;
		case R.id.confirm_password:
			return mConfirmPasswordValid;
		case R.id.mobile_number:
			return mMobileNumberValid;
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRegisterArrowButtonClicked(RegisterArrowButton button) {
		switch (button.getId()) {
		case R.id.enterprise:
			if (mEnterpriseId == null) {
				showDialog(DIALOG_BUSINESS);
			} else if (mEnterpriseId == NEW_ENTERPRISE_ID) {
				showDialog(DIALOG_BUSINESS);
				showDialog(DIALOG_NEW_ENTERPRISE);
			} else {
				showDialog(DIALOG_ENTERPRISE);
			}
			break;
		case R.id.user_type:
			showDialog(DIALOG_USER_TYPE);
			break;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onBusinessDialogInitializing() {
		showDialog(DIALOG_WAITING);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onBusinessDialogInitialized() {
		dismissDialog(DIALOG_WAITING);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onBusinessDialogInitializeFailed() {
		showDialog(DIALOG_RETRY);
		mRetryData.putInt(KEY_RETRY_TYPE, RETRY_TYPE_BUSINESS);
		dismissDialog(DIALOG_WAITING);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onBusinessDialogBack() {
		dismissDialog(DIALOG_WAITING);
		dismissDialog(DIALOG_BUSINESS);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onBusinessSelected(int id, String name) {
		mBusinessId = id;
		showDialog(DIALOG_ENTERPRISE);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onNewEnterpriseClicked() {
		showDialog(DIALOG_NEW_ENTERPRISE);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onNewEnterpriseDialogBack() {
		dismissDialog(DIALOG_NEW_ENTERPRISE);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onNewEnterpriseCreated(String newEnterprise) {
		mEnterpriseId = NEW_ENTERPRISE_ID;
		mEnterpriseName = newEnterprise;
		mEnterprise.setText(newEnterprise);
		dismissDialog(DIALOG_NEW_ENTERPRISE);
		dismissDialog(DIALOG_BUSINESS);
		checkIfRegisterEnabled();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onEnterpriseDialogInitializing() {
		showDialog(DIALOG_WAITING);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onEnterpriseDialogInitialized() {
		dismissDialog(DIALOG_WAITING);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onEnterpriseDialogInitializeFailed() {
		showDialog(DIALOG_RETRY);
		mRetryData.putInt(KEY_RETRY_TYPE, RETRY_TYPE_ENTERPRISE);
		dismissDialog(DIALOG_WAITING);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onEnterpriseDialogBack() {
		dismissDialog(DIALOG_WAITING);
		dismissDialog(DIALOG_ENTERPRISE);
		if (!mBusinessDialog.isShowing()) {
			showDialog(DIALOG_BUSINESS);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onEnterpriseSelected(int id, String name) {
		mEnterpriseId = id;
		mEnterpriseName = name;

		mEnterprise.setText(name);
		dismissDialog(DIALOG_ENTERPRISE);

		if (mBusinessDialog.isShowing()) {
			dismissDialog(DIALOG_BUSINESS);
		}
		checkIfRegisterEnabled();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onUserTypeDialogInitializing() {
		showDialog(DIALOG_WAITING);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onUserTypeDialogInitialized() {
		dismissDialog(DIALOG_WAITING);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onUserTypeDialogInitializeFailed() {
		dismissDialog(DIALOG_WAITING);
		mRetryData.putInt(KEY_RETRY_TYPE, RETRY_TYPE_USER_TYPE);
		showDialog(DIALOG_RETRY);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onUserTypeDialogBack() {
		dismissDialog(DIALOG_WAITING);
		dismissDialog(DIALOG_USER_TYPE);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onUserTypeSelected(int id, String name) {
		mUserTypeId = id;
		mUserType.setText(name);
		dismissDialog(DIALOG_USER_TYPE);
		checkIfRegisterEnabled();
	}

	/**
	 * Check if the register button could be enabled or not.
	 * 
	 * @author Luo Yinzhuo
	 */
	private void checkIfRegisterEnabled() {
		mRegister.setEnabled(mUsernameValid && mPasswordValid
				&& mConfirmPasswordValid && mMobileNumberValid
				&& mEnterpriseId != null && mUserTypeId != null);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		showDialog(DIALOG_WAITING);
		NetworkService.register(getResources().getString(R.string.server_url),
				mUsername.getText(), mPassword.getText(),
				mMobileNumber.getText(), mEnterpriseId, mEnterpriseName,
				((Application) getApplication()).getUUID(), Build.MODEL,
				mUserTypeId, this);
	}

	@Override
	public void onRegisterRequestFailed() {
	}

	/** The key to store account. */
	public static final String KEY_ACCOUNT = "account";
	/** The key to store password. */
	public static final String KEY_PASSWORD = "password";

	@Override
	public void onRegisterResponseSuccess(final String account,
			final String password) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent();
				intent.putExtra(KEY_ACCOUNT, account);
				intent.putExtra(KEY_PASSWORD, password);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	@Override
	public void onRegisterResponseFailed() {
		runOnUiThread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				showDialog(DIALOG_RETRY);
				mRetryData.putInt(KEY_RETRY_TYPE, RETRY_TYPE_REGISTER);
				dismissDialog(DIALOG_WAITING);
			}
		});
	}

	/** The mark to separate two user name. */
	private final String USERNAME_DELIMITER = "|";

	@Override
	public void onRegisterResponseAccountExist(final String account,
			String errorMessage) {
		runOnUiThread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				if (mUsernameExist.length() > 0) {
					mUsernameExist.append(USERNAME_DELIMITER);
				}
				mUsernameExist.append(account);
				mUsername.setCheck(false);

				showDialog(DIALOG_ACCOUNT_EXIST);
				dismissDialog(DIALOG_WAITING);
			}
		});
	}

	@Override
	public void onRegisterResponseDatabaseError(String errorMessage) {
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
