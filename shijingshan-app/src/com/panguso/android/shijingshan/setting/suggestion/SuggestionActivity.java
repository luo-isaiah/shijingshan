package com.panguso.android.shijingshan.setting.suggestion;

import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.account.AccountManager;
import com.panguso.android.shijingshan.dialog.MessageDialog;
import com.panguso.android.shijingshan.dialog.MessageDialog.OnMessageDialogListener;
import com.panguso.android.shijingshan.dialog.WaitingDialog;
import com.panguso.android.shijingshan.net.NetworkService;
import com.panguso.android.shijingshan.net.NetworkService.SuggestionRequestListener;
import com.panguso.android.shijingshan.widget.BlueTitleBar;
import com.panguso.android.shijingshan.widget.BlueTitleBar.OnBackListener;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * The suggestion activity.
 * 
 * @author Luo Yinzhuo
 */
public class SuggestionActivity extends Activity implements OnBackListener,
		TextWatcher, OnClickListener, SuggestionRequestListener,
		OnMessageDialogListener {
	/** The waiting dialog ID. */
	private static final int DIALOG_WAITING = 0;
	/** The retry dialog ID. */
	private static final int DIALOG_RETRY = 1;

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_WAITING:
			return new WaitingDialog(this);
		case DIALOG_RETRY:
			return new MessageDialog(this, DIALOG_RETRY,
					getString(R.string.retry_title),
					getString(R.string.retry_text),
					getString(R.string.retry_button), this);
		default:
			return null;
		}
	}

	/** The title bar. */
	private BlueTitleBar mTitleBar;
	/** The content. */
	private EditText mContent;
	/** The contact. */
	private EditText mContact;
	/** The suggestion button. */
	private Button mSuggestion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suggestion_activity);

		mTitleBar = (BlueTitleBar) findViewById(R.id.title_bar);
		mTitleBar.setTitle(getResources().getString(R.string.suggestion_title));
		mTitleBar.setOnBackListener(this);

		mContent = (EditText) findViewById(R.id.content);
		mContent.addTextChangedListener(this);

		mContact = (EditText) findViewById(R.id.contact);

		mSuggestion = (Button) findViewById(R.id.suggestion);
		mSuggestion.setOnClickListener(this);
	}

	@Override
	public void onBack() {
		finish();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		mSuggestion.setEnabled(s.length() > 0);
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		showDialog(DIALOG_WAITING);
		NetworkService.suggestion(getString(R.string.server_url),
				AccountManager.getAccount(), mContact.getText().toString(),
				mContent.getText().toString(), this);
	}

	@Override
	public void onSuggestionRequestFailed() {
	}

	@Override
	public void onSuggestionResponseSuccess() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				finish();
			}
		});
	}

	@Override
	public void onSuggestionResponseFailed() {
		runOnUiThread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				dismissDialog(DIALOG_WAITING);
				showDialog(DIALOG_RETRY);
			}
		});
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onMessageDialogBack(int id) {
		dismissDialog(id);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onMessageDialogConfirmed(int id) {
		switch (id) {
		case DIALOG_RETRY:
			dismissDialog(DIALOG_RETRY);
			showDialog(DIALOG_WAITING);
			NetworkService.suggestion(getString(R.string.server_url),
					AccountManager.getAccount(), mContact.getText().toString(),
					mContent.getText().toString(), this);
			break;
		default:
			dismissDialog(id);
			break;
		}
	}
}
