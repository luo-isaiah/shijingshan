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

import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.dialog.WaitingDialog;
import com.panguso.android.shijingshan.dialog.WaitingDialog.OnWaitingDialogListener;
import com.panguso.android.shijingshan.register.RegisterArrowButton.OnRegisterArrowButtonListener;
import com.panguso.android.shijingshan.register.usertype.UserTypeDialog;
import com.panguso.android.shijingshan.register.usertype.UserTypeDialog.OnUserTypeDialogListener;
import com.panguso.android.shijingshan.widget.BlueTitleBar;
import com.panguso.android.shijingshan.widget.BlueTitleBar.OnBackListener;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * Specified for register new user.
 * 
 * @author Luo Yinzhuo
 * @date 2013-10-21
 */
public class RegisterActivity extends Activity implements
		OnEditorActionListener, OnBackListener, OnClickListener,
		OnWaitingDialogListener, OnRegisterArrowButtonListener,
		OnUserTypeDialogListener {
	
	/** The waiting dialog ID. */
	private static final int DIALOG_WAITING = 0;
	/** The user type dialog ID. */
	private static final int DIALOG_USER_TYPE = 1;

	/** The {@link WaitingDialog} visibility flag. */
	private boolean mWaitingDialogVisible = false;
	/** The {@link UserTypeDialog} visibility flag. */
	private boolean mUserTypeDialogVisible = false;

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_WAITING:
			return new WaitingDialog(this, this);
		case DIALOG_USER_TYPE:
			return new UserTypeDialog(this, this);
		default:
			return null;
		}
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
	}

	/** The title bar. */
	private BlueTitleBar mTitleBar;

	/** The user name. */
	private EditText mUserName;
	/** The password. */
	private EditText mPassword;
	/** The confirm password. */
	private EditText mConfirmPassword;
	/** The mobile number. */
	private EditText mMobileNumber;
	/** The enterprise. */
	private RegisterArrowButton mEnterprise;
	/** The user type. */
	private RegisterArrowButton mUserType;
	/** The user type id. */
	private String mUserTypeId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_activity);

		mTitleBar = (BlueTitleBar) findViewById(R.id.title_bar);
		mTitleBar.setTitle(getResources().getString(R.string.register_title));
		mTitleBar.setOnBackListener(this);

		mUserName = (EditText) findViewById(R.id.username);
		mPassword = (EditText) findViewById(R.id.password);
		mConfirmPassword = (EditText) findViewById(R.id.confirm_password);
		mMobileNumber = (EditText) findViewById(R.id.mobile_number);

		mEnterprise = (RegisterArrowButton) findViewById(R.id.enterprise);
		mEnterprise.setTextHint(R.string.register_enterprise_hint);
		mEnterprise.setOnRegisterArrowButtonListener(this);

		mUserType = (RegisterArrowButton) findViewById(R.id.user_type);
		mUserType.setTextHint(R.string.register_user_type_hint);
		mUserType.setOnRegisterArrowButtonListener(this);
	}

	@Override
	public void onBack() {
		finish();
	}

	@Override
	public void onWaitingDialogBack() {

	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		Log.d("RegisterActivity",
				"KeyEvent "
						+ Boolean.valueOf(KeyEvent.KEYCODE_ENTER == event
								.getKeyCode()));
		return false;
	}

	@Override
	public void onClick(View v) {
		Log.d("RegisterActivity", "Enterprise button clicked!");
	}

	@Override
	public void onRegisterArrowButtonClicked(RegisterArrowButton button) {
		switch (button.getId()) {
		case R.id.user_type:
			showDialog(DIALOG_USER_TYPE);
			mUserTypeDialogVisible = true;
			break;
		}
	}

	@Override
	public void onUserTypeDialogBack() {
		if (mWaitingDialogVisible) {
			dismissDialog(DIALOG_WAITING);
			mWaitingDialogVisible = false;
		}
		dismissDialog(DIALOG_USER_TYPE);
		mUserTypeDialogVisible = false;
	}

	@Override
	public void onUserTypeDialogInitializing() {
		showDialog(DIALOG_WAITING);
		mWaitingDialogVisible = true;
	}

	@Override
	public void onUserTypeDialogInitialized() {
		dismissDialog(DIALOG_WAITING);
		mWaitingDialogVisible = false;
	}

	@Override
	public void onUserTypeSelected(String id, String name) {
		mUserTypeId = id;
		mUserType.setText(name);
		dismissDialog(DIALOG_USER_TYPE);
		mUserTypeDialogVisible = false;
	}
	
}
