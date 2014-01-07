package com.panguso.android.shijingshan.log;

import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.widget.BlueTitleBar;
import com.panguso.android.shijingshan.widget.BlueTitleBar.OnBackListener;

import android.app.Activity;
import android.os.Bundle;

/**
 * The log activity.
 * 
 * @author Luo Yinzhuo
 */
public class LogActivity extends Activity implements OnBackListener {
	/** The title bar. */
	private BlueTitleBar mTitleBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_activity);
		mTitleBar = (BlueTitleBar) findViewById(R.id.title_bar);

		mTitleBar.setTitle(getResources().getString(R.string.log_title));
		mTitleBar.setOnBackListener(this);
	}

	@Override
	public void onBack() {
		finish();
	}
}
