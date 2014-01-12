package com.panguso.android.shijingshan.log;

import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.register.RegisterActivity;
import com.panguso.android.shijingshan.widget.BlueTitleBar;
import com.panguso.android.shijingshan.widget.BlueTitleBar.OnBackListener;
import com.panguso.android.shijingshan.widget.UnderlineButton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * The log activity.
 * 
 * @author Luo Yinzhuo
 */
public class LogActivity extends Activity implements OnBackListener, OnClickListener {
	/** The title bar. */
	private BlueTitleBar mTitleBar;
	/** The register button. */
	private UnderlineButton mRegister;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_activity);
		
		mTitleBar = (BlueTitleBar) findViewById(R.id.title_bar);
		mTitleBar.setTitle(getResources().getString(R.string.log_title));
		mTitleBar.setOnBackListener(this);
		
		mRegister = (UnderlineButton) findViewById(R.id.register);
		mRegister.setOnClickListener(this);
	}

	@Override
	public void onBack() {
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register:
			Intent intent = new Intent(this, RegisterActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
}
