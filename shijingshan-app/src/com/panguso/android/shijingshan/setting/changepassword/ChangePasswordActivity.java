package com.panguso.android.shijingshan.setting.changepassword;

import org.json.JSONException;

import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.account.AccountManager;
import com.panguso.android.shijingshan.column.ColumnPageActivity;
import com.panguso.android.shijingshan.dialog.MessageDialog;
import com.panguso.android.shijingshan.dialog.MessageDialog.OnMessageDialogListener;
import com.panguso.android.shijingshan.dialog.WaitingDialog;
import com.panguso.android.shijingshan.net.NetworkService;
import com.panguso.android.shijingshan.net.NetworkService.ChangePasswordRequestListener;
import com.panguso.android.shijingshan.register.RegisterCheckEditText;
import com.panguso.android.shijingshan.register.RegisterCheckEditText.OnRegisterCheckEditTextListener;
import com.panguso.android.shijingshan.widget.BlueTitleBar;
import com.panguso.android.shijingshan.widget.BlueTitleBar.OnBackListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * The change password activity.
 * 
 * @author Luo Yinzhuo
 */
public class ChangePasswordActivity extends Activity implements OnBackListener,
		OnRegisterCheckEditTextListener, OnClickListener,
		ChangePasswordRequestListener, OnMessageDialogListener {
	/** The waiting dialog ID. */
	private static final int DIALOG_WAITING = 0;
	/** The retry dialog ID. */
	private static final int DIALOG_RETRY = 1;
	/** The old password and new password same dialog ID. */
	private static final int DIALOG_OLD_PASSWORD_NEW_PASSWORD_SAME = 2;
	/** The server database error dialog ID. */
	private static final int DIALOG_DATABASE_ERROR = 3;

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
		case DIALOG_OLD_PASSWORD_NEW_PASSWORD_SAME:
			return new MessageDialog(this,
					DIALOG_OLD_PASSWORD_NEW_PASSWORD_SAME,
					getString(R.string.old_password_new_password_same_title),
					getString(R.string.old_password_new_password_same_text),
					getString(R.string.old_password_new_password_same_button),
					this);
		case DIALOG_DATABASE_ERROR:
			return new MessageDialog(this, DIALOG_DATABASE_ERROR,
					getString(R.string.database_error_title),
					getString(R.string.database_error_text),
					getString(R.string.database_error_button), this);
		default:
			return null;
		}
	}

	/** The title bar. */
	private BlueTitleBar mTitleBar;

	/** The old password. */
	private RegisterCheckEditText mOldPassword;
	/** The old password valid flag. */
	private boolean mOldPasswordValid = false;

	/** The new password. */
	private RegisterCheckEditText mNewPassword;
	/** The new password valid flag. */
	private boolean mNewPasswordValid = false;

	/** The confirm new password. */
	private RegisterCheckEditText mConfirmNewPassword;
	/** The confirm new password valid flag. */
	private boolean mConfirmNewPasswordValid = false;

	/** The change password button. */
	private Button mChangePassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_password_activity);

		mTitleBar = (BlueTitleBar) findViewById(R.id.title_bar);
		mTitleBar.setTitle(getString(R.string.change_password_title));
		mTitleBar.setOnBackListener(this);

		Resources resources = getResources();

		mOldPassword = (RegisterCheckEditText) findViewById(R.id.old_password);
		mOldPassword.setTextHint(R.string.old_password_hint);
		mOldPassword.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		mOldPassword.setMaxLength(resources
				.getInteger(R.integer.max_length_password));
		mOldPassword.setDigits(R.string.digits_password);
		mOldPassword.setOnRegisterCheckEditTextListenerListener(this);

		mNewPassword = (RegisterCheckEditText) findViewById(R.id.new_password);
		mNewPassword.setTextHint(R.string.new_password_hint);
		mNewPassword.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		mNewPassword.setMaxLength(resources
				.getInteger(R.integer.max_length_password));
		mNewPassword.setDigits(R.string.digits_password);
		mNewPassword.setOnRegisterCheckEditTextListenerListener(this);

		mConfirmNewPassword = (RegisterCheckEditText) findViewById(R.id.confirm_new_password);
		mConfirmNewPassword.setTextHint(R.string.confirm_new_password_hint);
		mConfirmNewPassword.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		mConfirmNewPassword.setMaxLength(resources
				.getInteger(R.integer.max_length_password));
		mConfirmNewPassword.setDigits(R.string.digits_password);
		mConfirmNewPassword.setOnRegisterCheckEditTextListenerListener(this);

		mChangePassword = (Button) findViewById(R.id.change_password);
		mChangePassword.setOnClickListener(this);
	}

	@Override
	public void onBack() {
		finish();
	}

	@Override
	public void onRegisterCheckEditTextChanged(int id, String text) {
		switch (id) {
		case R.id.old_password:
			mOldPasswordValid = AccountManager.getPassword().equals(text);
			break;
		case R.id.new_password:
			mNewPasswordValid = text.length() >= getResources().getInteger(
					R.integer.min_length_password);
			String confirmNewPassword = mConfirmNewPassword.getText();
			mConfirmNewPasswordValid = confirmNewPassword.equals(text);
			mConfirmNewPassword.setCheck(mConfirmNewPasswordValid);
			break;
		case R.id.confirm_new_password:
			mConfirmNewPasswordValid = text.length() >= getResources()
					.getInteger(R.integer.min_length_password)
					&& text.equals(mNewPassword.getText());
			break;
		}
		checkIfChangePasswordEnabled();
	}

	@Override
	public boolean onRegisterCheckEditTextLostFocus(int id) {
		switch (id) {
		case R.id.old_password:
			return mOldPasswordValid;
		case R.id.new_password:
			return mNewPasswordValid;
		case R.id.confirm_new_password:
			return mConfirmNewPasswordValid;
		}
		return false;
	}

	/**
	 * Check if the change password button could be enabled or not.
	 * 
	 * @author Luo Yinzhuo
	 */
	private void checkIfChangePasswordEnabled() {
		mChangePassword.setEnabled(mOldPasswordValid && mNewPasswordValid
				&& mConfirmNewPasswordValid);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		showDialog(DIALOG_WAITING);
		NetworkService.changePassword(getString(R.string.server_url),
				AccountManager.getAccount(), mOldPassword.getText(),
				mNewPassword.getText(), this);
	}

	@Override
	public void onChangePasswordRequestFailed() {
	}

	@Override
	public void onChangePasswordResponseSuccess() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AccountManager.changePassword(mNewPassword.getText());

				// TODO: Maybe there's a better way to do this by invoking
				// ColumnPageActivity's method.
				Editor editor = getSharedPreferences(
						"column.ColumnPageActivity", MODE_PRIVATE).edit();
				try {
					editor.putString(ColumnPageActivity.KEY_LAST_ACCOUNT,
							AccountManager.getJson());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				editor.commit();

				finish();
			}
		});
	}

	@Override
	public void onChangePasswordResponseFailed() {
		runOnUiThread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				dismissDialog(DIALOG_WAITING);
				showDialog(DIALOG_RETRY);
			}
		});
	}

	@Override
	public void onChangePasswordResponseOldPasswordIncorrect(String errorMessage) {
	}

	@Override
	public void onChangePasswordResponseOldPasswordNewPasswordSame(
			String errorMessage) {
		runOnUiThread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				dismissDialog(DIALOG_WAITING);
				showDialog(DIALOG_OLD_PASSWORD_NEW_PASSWORD_SAME);
			}
		});
	}

	@Override
	public void onChangePasswordResponseDatabaseError(String errorMessage) {
		runOnUiThread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				dismissDialog(DIALOG_WAITING);
				showDialog(DIALOG_DATABASE_ERROR);
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
			NetworkService.changePassword(getString(R.string.server_url),
					AccountManager.getAccount(), mOldPassword.getText(),
					mNewPassword.getText(), this);
			break;
		default:
			dismissDialog(id);
			break;
		}
	}

}
