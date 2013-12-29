/**
 *  Copyright (c)  2011-2020 Panguso, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Panguso, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Panguso.
 */
package com.panguso.android.shijingshan.column;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.panguso.android.shijingshan.Application;
import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.log.LogActivity;
import com.panguso.android.shijingshan.net.NetworkService;
import com.panguso.android.shijingshan.net.NetworkService.ColumnInfoListRequestListener;
import com.panguso.android.shijingshan.setting.SettingActivity;
import com.panguso.android.shijingshan.user.UserManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

/**
 * The column page activity.
 * 
 * @author Luo Yinzhuo
 * @date 2013-8-7
 */
public class ColumnPageActivity extends Activity implements ColumnInfoListRequestListener,
        OnClickListener {
	/** The key to get last user's logged in data. */
	private static final String KEY_LAST_USER = "last_user";
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.column_page_activity);
		mLog = (ImageButton) findViewById(R.id.log);
		mLog.setOnClickListener(this);
		mSetting = (ImageButton) findViewById(R.id.setting);
		mSetting.setOnClickListener(this);
		mSubscribe = (ImageButton) findViewById(R.id.subscribe);
		mNotice = (ImageButton) findViewById(R.id.notice);

		mColumnPageView = (ColumnPageView) findViewById(R.id.column_page);

		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
		// First, get the last logged in user's info.
		String lastUserLogin = sharedPreferences.getString(KEY_LAST_USER, "");
		if (lastUserLogin.length() > 0) {
			// There's a last logged in user.
			try {
				UserManager.parse(lastUserLogin);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			// Check whether the user needs to re-login.
			if (UserManager.needReLogin()) {
				// TODO: Make the user to login.
				return;
			}
		}

		displayColumnPages();
	}

	@Override
	protected void onDestroy() {
		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
		try {
			mColumnPageView.save(sharedPreferences, UserManager.getUserName() + KEY_COLUMN_PAGES);
		} catch (JSONException e) {
			e.printStackTrace();
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
		String columnPages = sharedPreferences.getString(UserManager.getUserName()
		        + KEY_COLUMN_PAGES, "");
		if (columnPages.length() > 0) {
			try {
				mColumnPageView.initialize(columnPages);
				// TODO: dismiss the waiting dialog.
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			if (mColumnInfos.isEmpty()) {
				NetworkService.getColumnInfoList(getResources().getString(R.string.server_url),
				        ((Application) getApplication()).getUUID(), this);
			} else {
				mColumnPageView.initialize(createColumnPages(mColumnInfos), 0);
			}
		}
	}

	/** The list of column info. */
	private final List<ColumnInfo> mColumnInfos = new ArrayList<ColumnInfo>();

	@Override
	public void onColumnInfoListRequestFailed() {

	}

	@Override
	public void onColumnInfoListResponseSuccess(final List<ColumnInfo> columnInfos) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mColumnInfos.clear();
				mColumnInfos.addAll(columnInfos);
				mColumnPageView.initialize(createColumnPages(mColumnInfos), 0);
			}
		});
	}

	@Override
	public void onColumnInfoListResponseFailed() {
		// TODO change the dialog to let the user to retry.
		NetworkService.getColumnInfoList(getResources().getString(R.string.server_url),
		        ((Application) getApplication()).getUUID(), this);
	}

	/**
	 * Create the list of {@link ColumnPage} from list of {@link ColumnInfo}.
	 * 
	 * @param columnInfos The list of {@link ColumnInfo}.
	 * @return The list of {@link ColumnPage} with current user state.
	 * @author Luo Yinzhuo
	 */
	private List<ColumnPage> createColumnPages(List<ColumnInfo> columnInfos) {
//		final boolean login = UserManager.isLogin();
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

}
