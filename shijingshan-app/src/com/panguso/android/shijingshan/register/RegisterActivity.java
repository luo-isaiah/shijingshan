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
import com.panguso.android.shijingshan.register.business.BusinessDialog;
import com.panguso.android.shijingshan.register.business.BusinessDialog.OnBusinessDialogListener;
import com.panguso.android.shijingshan.register.enterprise.EnterpriseDialog;
import com.panguso.android.shijingshan.register.enterprise.EnterpriseDialog.OnEnterpriseDialogListener;
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
import android.widget.EditText;
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
		OnUserTypeDialogListener, OnBusinessDialogListener, OnEnterpriseDialogListener {

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

	/** The {@link WaitingDialog} visibility flag. */
	private boolean mWaitingDialogVisible = false;
	/** The {@link UserTypeDialog} visibility flag. */
	private boolean mUserTypeDialogVisible = false;
	/** The {@link BusinessDialog} visibility flag. */
	private boolean mBusinessDialogVisible = false;

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_WAITING:
			return new WaitingDialog(this, this);
		case DIALOG_BUSINESS:
			return new BusinessDialog(this, this);
		case DIALOG_ENTERPRISE:
			return new EnterpriseDialog(this, mBusinessId, this);
		case DIALOG_USER_TYPE:
			return new UserTypeDialog(this, this);
		default:
			return null;
		}
	}

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
	private EditText mUserName;
	/** The password. */
	private EditText mPassword;
	/** The confirm password. */
	private EditText mConfirmPassword;
	/** The mobile number. */
	private EditText mMobileNumber;
	/** The enterprise. */
	private RegisterArrowButton mEnterprise;
	/** The business id. */
	private int mBusinessId = 0;
	/** The enterprise id. */
	private int mEnterpriseId = 0;
	/** The new enterprise id. */
	private static final int NEW_ENTERPRISE_ID = -1;
	/** The enterprise name. */
	private String mEnterpriseName = "";
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
		case R.id.enterprise:
			if (mEnterpriseName.length() == 0
					|| mEnterpriseId == NEW_ENTERPRISE_ID) {
				showDialog(DIALOG_BUSINESS);
				mBusinessDialogVisible = true;
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
	
	@Override
	public void onBusinessDialogInitializing() {
		showDialog(DIALOG_WAITING);
		mWaitingDialogVisible = true;		
	}

	@Override
	public void onBusinessDialogInitialized() {
		dismissDialog(DIALOG_WAITING);
		mWaitingDialogVisible = false;		
	}

	@Override
	public void onBusinessDialogBack() {
		if (mWaitingDialogVisible) {
			dismissDialog(DIALOG_WAITING);
			mWaitingDialogVisible = false;
		}
		dismissDialog(DIALOG_BUSINESS);
		mBusinessDialogVisible = false;		
	}

	@Override
	public void onBusinessSelected(int id, String name) {
		mBusinessId = id;
		showDialog(DIALOG_ENTERPRISE);
	}

	/* (non-Javadoc)
	 * @see com.panguso.android.shijingshan.register.enterprise.EnterpriseDialog.OnEnterpriseDialogListener#onEnterpriseDialogInitializing()
	 */
	@Override
	public void onEnterpriseDialogInitializing() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.panguso.android.shijingshan.register.enterprise.EnterpriseDialog.OnEnterpriseDialogListener#onEnterpriseDialogInitialized()
	 */
	@Override
	public void onEnterpriseDialogInitialized() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.panguso.android.shijingshan.register.enterprise.EnterpriseDialog.OnEnterpriseDialogListener#onEnterpriseDialogBack()
	 */
	@Override
	public void onEnterpriseDialogBack() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.panguso.android.shijingshan.register.enterprise.EnterpriseDialog.OnEnterpriseDialogListener#onEnterpriseSelected(int, java.lang.String)
	 */
	@Override
	public void onEnterpriseSelected(int id, String name) {
		// TODO Auto-generated method stub
		
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
	public void onUserTypeDialogBack() {
		if (mWaitingDialogVisible) {
			dismissDialog(DIALOG_WAITING);
			mWaitingDialogVisible = false;
		}
		dismissDialog(DIALOG_USER_TYPE);
		mUserTypeDialogVisible = false;
	}
	
	@Override
	public void onUserTypeSelected(String id, String name) {
		mUserTypeId = id;
		mUserType.setText(name);
		dismissDialog(DIALOG_USER_TYPE);
		mUserTypeDialogVisible = false;
	}
}
