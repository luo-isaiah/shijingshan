package com.panguso.android.shijingshan.setting.about;

import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.widget.BlueTitleBar;
import com.panguso.android.shijingshan.widget.BlueTitleBar.OnBackListener;

import android.app.Activity;
import android.os.Bundle;

/**
 * The about activity.
 * 
 * @author Luo Yinzhuo
 */
public class AboutActivity extends Activity implements OnBackListener {
	/** The title bar. */
	private BlueTitleBar mTitleBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);

		mTitleBar = (BlueTitleBar) findViewById(R.id.title_bar);
		mTitleBar.setTitle(getResources().getString(R.string.about_title));
		mTitleBar.setOnBackListener(this);
	}

	@Override
	public void onBack() {
		finish();
	}

}
