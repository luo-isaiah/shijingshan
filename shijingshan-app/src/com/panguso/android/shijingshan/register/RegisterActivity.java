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
public class RegisterActivity extends Activity implements OnEditorActionListener {
	/** The user name. */
	private EditText mUserName;
	/** The password. */
	private EditText mPassword;
	/** The mobile number. */
	private EditText mMobileNumber;
	/** The enterprise id. */
	private EditText mEnterpriseId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_activity);
		
		mUserName = (EditText) findViewById(R.id.register_username);
		mPassword = (EditText) findViewById(R.id.register_password);
		mMobileNumber = (EditText) findViewById(R.id.register_mobile_number);
		mEnterpriseId = (EditText) findViewById(R.id.register_enterprise_id);
		
		mUserName.setOnEditorActionListener(this);
	}

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    	Log.d("RegisterActivity", "KeyEvent " + Boolean.valueOf(KeyEvent.KEYCODE_ENTER == event.getKeyCode()));
	    return false;
    }
}
