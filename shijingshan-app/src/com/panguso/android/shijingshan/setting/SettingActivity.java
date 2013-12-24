package com.panguso.android.shijingshan.setting;

import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.widget.ListButton;
import com.panguso.android.shijingshan.widget.ListButton.OnListButtonListener;
import com.panguso.android.shijingshan.widget.BlueTitleBar;
import com.panguso.android.shijingshan.widget.BlueTitleBar.OnBackListener;

import android.app.Activity;
import android.os.Bundle;

/**
 * The setting activity.
 * 
 * @author Luo Yinzhuo
 */
public class SettingActivity extends Activity implements OnBackListener, OnListButtonListener {
	/** The title bar. */
	private BlueTitleBar mTitleBar;
	/** The change password button. */
	private ListButton mChangePassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_activity);
		mTitleBar = (BlueTitleBar) findViewById(R.id.title_bar);
		mTitleBar.setTitle(getResources().getString(R.string.setting_title));
		mTitleBar.setOnBackListener(this);
		mChangePassword = (ListButton) findViewById(R.id.change_password);
		mChangePassword.setText(getResources().getString(R.string.change_password));
		mChangePassword.setOnListButtonListener(this);
	}

	@Override
	public void onBack() {
		finish();
	}

	@Override
    public void onListButton(ListButton button) {
	    switch (button.getId()) {
			case R.id.change_password:
				
				break;

			default:
				break;
		}
    }
}
