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
		OnRegisterCheckEditTextListener, RegisterRequestListener {

	/** The waiting dialog ID. */
	private static final int DIALOG_WAITING = -1;
	/** The retry dialog ID. */
	private static final int DIALOG_RETRY = -2;
	/** The unsupported dialog ID. */
	private static final int DIALOG_UNSUPPORTED = -3;

	/** The business dialog ID. */
	private static final int DIALOG_BUSINESS = 1;
	/** The enterprise dialog ID. */
	private static final int DIALOG_ENTERPRISE = 2;
	/** The new enterprise dialog ID. */
	private static final int DIALOG_NEW_ENTERPRISE = 3;
	/** The user type dialog ID. */
	private static final int DIALOG_USER_TYPE = 4;

	/** The waiting dialog. */
	private WaitingDialog mWaitingDialog;
	/** The business dialog. */
	private BusinessDialog mBusinessDialog;
	/** The {@link UserTypeDialog} visibility flag. */
	private boolean mUserTypeDialogVisible = false;

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_WAITING:
			mWaitingDialog = new WaitingDialog(this);
			return mWaitingDialog;
		case DIALOG_BUSINESS:
			mBusinessDialog = new BusinessDialog(this, this);
			return mBusinessDialog;
		case DIALOG_NEW_ENTERPRISE:
			return new NewEnterpriseDialog(this, this);
		case DIALOG_ENTERPRISE:
			return new EnterpriseDialog(this, mBusinessId, this);
		case DIALOG_USER_TYPE:
			return new UserTypeDialog(this, this);
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
	/** The register request running flag. */
	private boolean mRegistering = false;

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
			mUserTypeDialogVisible = true;
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
		if (mWaitingDialog.isShowing()) {
			dismissDialog(DIALOG_WAITING);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onBusinessDialogBack() {
		if (mWaitingDialog.isShowing()) {
			dismissDialog(DIALOG_WAITING);
		}
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
		if (mWaitingDialog.isShowing()) {
			dismissDialog(DIALOG_WAITING);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onEnterpriseDialogBack() {
		if (mWaitingDialog.isShowing()) {
			dismissDialog(DIALOG_WAITING);
		}
		dismissDialog(DIALOG_ENTERPRISE);
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
		if (mWaitingDialog.isShowing()) {
			dismissDialog(DIALOG_WAITING);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onUserTypeDialogBack() {
		if (mWaitingDialog.isShowing()) {
			dismissDialog(DIALOG_WAITING);
		}
		dismissDialog(DIALOG_USER_TYPE);
		mUserTypeDialogVisible = false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onUserTypeSelected(int id, String name) {
		mUserTypeId = id;
		mUserType.setText(name);
		dismissDialog(DIALOG_USER_TYPE);
		mUserTypeDialogVisible = false;
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
		mRegistering = true;

		showDialog(DIALOG_WAITING);

		NetworkService.register(getResources().getString(R.string.server_url),
				mUsername.getText(), mPassword.getText(),
				mMobileNumber.getText(), mEnterpriseId, mEnterpriseName,
				((Application) getApplication()).getUUID(), Build.MODEL,
				mUserTypeId, this);
	}

	@Override
	public void onRegisterRequestFailed() {
		mRegistering = false;
	}

	@Override
	public void onRegisterResponseSuccess(String account, String password) {
		mRegistering = false;
	}

	@Override
	public void onRegisterResponseFailed() {
		mRegistering = false;
	}

	/** The mark to seperate two user name. */
	private final String USERNAME_DELIMITER = "|";

	@SuppressWarnings("deprecation")
	@Override
	public void onRegisterResponseAccountExist(String account,
			String errorMessage) {
		if (mUsernameExist.length() > 0) {
			mUsernameExist.append(USERNAME_DELIMITER);
		}
		mUsernameExist.append(account);
		mUsername.setCheck(false);

		dismissDialog(DIALOG_WAITING);
		mRegistering = false;
	}

	@Override
	public void onRegisterResponseDatabaseError(String errorMessage) {
		mRegistering = false;
	}
}
