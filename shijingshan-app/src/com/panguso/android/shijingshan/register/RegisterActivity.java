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
import com.panguso.android.shijingshan.widget.BlueTitleBar;
import com.panguso.android.shijingshan.widget.BlueTitleBar.OnBackListener;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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
		OnEditorActionListener, OnBackListener {
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_activity);

		mTitleBar = (BlueTitleBar) findViewById(R.id.title_bar);
		mTitleBar.setTitle(getResources().getString(R.string.log_title));
		mTitleBar.setOnBackListener(this);

		mUserName = (EditText) findViewById(R.id.username);
		mPassword = (EditText) findViewById(R.id.password);
		mConfirmPassword = (EditText) findViewById(R.id.confirm_password);
		mMobileNumber = (EditText) findViewById(R.id.mobile_number);

		mUserName.setOnEditorActionListener(this);
	}

	@Override
	public void onBack() {
		finish();
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		Log.d("RegisterActivity",
				"KeyEvent "
						+ Boolean.valueOf(KeyEvent.KEYCODE_ENTER == event
								.getKeyCode()));
		return false;
	}

}
