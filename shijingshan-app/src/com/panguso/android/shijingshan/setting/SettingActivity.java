package com.panguso.android.shijingshan.setting;

import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.account.AccountManager;
import com.panguso.android.shijingshan.net.NetworkService;
import com.panguso.android.shijingshan.setting.SettingButton.OnSettingButtonListener;
import com.panguso.android.shijingshan.setting.about.AboutActivity;
import com.panguso.android.shijingshan.setting.changepassword.ChangePasswordActivity;
import com.panguso.android.shijingshan.setting.suggestion.SuggestionActivity;
import com.panguso.android.shijingshan.widget.BlueTitleBar;
import com.panguso.android.shijingshan.widget.BlueTitleBar.OnBackListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * The setting activity.
 * 
 * @author Luo Yinzhuo
 */
public class SettingActivity extends Activity implements OnBackListener,
		OnSettingButtonListener {
	/** The title bar. */
	private BlueTitleBar mTitleBar;
	/** The change password button. */
	private SettingButton mChangePassword;
	/** The clear cache button. */
	private SettingButton mClearCache;
	/** The clear cache text. */
	private TextView mClearCacheText;
	/** The suggestion button. */
	private SettingButton mSuggestion;
	/** The about button. */
	private SettingButton mAbout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_activity);

		mTitleBar = (BlueTitleBar) findViewById(R.id.title_bar);
		mTitleBar.setTitle(getResources().getString(R.string.setting_title));
		mTitleBar.setOnBackListener(this);

		mChangePassword = (SettingButton) findViewById(R.id.change_password);
		mChangePassword.setText(getString(R.string.change_password));
		mChangePassword.setOnSettingButtonListener(this);

		mClearCache = (SettingButton) findViewById(R.id.clear_cache);
		mClearCache.setText(getString(R.string.clear_cache));
		mClearCache.setOnSettingButtonListener(this);

		mClearCacheText = (TextView) findViewById(R.id.clear_cache_text);
		updateClearCacheText();

		mSuggestion = (SettingButton) findViewById(R.id.suggestion);
		mSuggestion.setText(getString(R.string.suggestion));
		mSuggestion.setOnSettingButtonListener(this);

		mAbout = (SettingButton) findViewById(R.id.about);
		mAbout.setText(getString(R.string.about));
		mAbout.setOnSettingButtonListener(this);

		if (!AccountManager.isLogin()) {
			mChangePassword.setVisibility(View.GONE);
			mSuggestion.setVisibility(View.GONE);
		}
	}

	@Override
	public void onBack() {
		finish();
	}

	@Override
	public void onClicked(int id) {
		switch (id) {
		case R.id.change_password:
			startActivity(new Intent(this, ChangePasswordActivity.class));
			break;
		case R.id.clear_cache:
			NetworkService.clearExternalStorage();
			updateClearCacheText();
			break;
		case R.id.suggestion:
			startActivity(new Intent(this, SuggestionActivity.class));
			break;
		case R.id.about:
			startActivity(new Intent(this, AboutActivity.class));
			break;
		}
	}

	/**
	 * Update the clear cache text.
	 *
	 * @author Luo Yinzhuo
	 */
	private void updateClearCacheText() {
		long externalStorageUsedSpace = NetworkService
				.getExternalStorageUsedSpace();
		if (externalStorageUsedSpace == -1) {
			mClearCacheText.setVisibility(View.GONE);
		} else {
			String[] units = { "B", "KB", "MB", "GB" };
			float result = externalStorageUsedSpace / 8.0f;
			int unit = 0;
			while (result > 1024 && unit < units.length) {
				result /= 1024;
				unit++;
			}
			mClearCacheText.setText(String.format("%.2f %s", result, units[unit]));
		}
	}
}
