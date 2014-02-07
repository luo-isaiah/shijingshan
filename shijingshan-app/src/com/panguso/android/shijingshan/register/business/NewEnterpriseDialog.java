/**
 * 
 */
package com.panguso.android.shijingshan.register.business;

import com.panguso.android.shijingshan.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * Specific for the new enterprise dialog.
 * 
 * @author Luo Yinzhuo
 * 
 */
public class NewEnterpriseDialog extends Dialog implements
		android.view.View.OnClickListener, TextWatcher, OnEditorActionListener {

	/**
	 * Interface definition for a callback to be invoked when the back key is
	 * pressed or the enter key is pressed or the confirm button is clicked.
	 * 
	 * @author Luo Yinzhuo
	 */
	public interface OnNewEnterpriseDialogListener {

		/**
		 * Called when the back key is pressed.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onNewEnterpriseDialogBack();

		/**
		 * Called when the new enterprise name is created.
		 * 
		 * @param newEnterprise
		 *            The new enterprise name.
		 * @author Luo Yinzhuo
		 */
		public void onNewEnterpriseCreated(String newEnterprise);
	}

	/** The new enterprise edit text. */
	private final EditText mNewEnterprise;
	/** The confirm button. */
	private final Button mConfirm;
	/** The listener. */
	private final OnNewEnterpriseDialogListener mListener;

	/**
	 * Construct a new instance.
	 * 
	 * @param context
	 *            The context.
	 * @param listener
	 *            The listener.
	 */
	public NewEnterpriseDialog(Context context,
			OnNewEnterpriseDialogListener listener) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.new_enterprise_dialog);

		mNewEnterprise = (EditText) findViewById(R.id.new_enterprise);
		mNewEnterprise.addTextChangedListener(this);
		mNewEnterprise.setOnEditorActionListener(this);

		mConfirm = (Button) findViewById(R.id.confirm);
		mConfirm.setOnClickListener(this);

		getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		setCanceledOnTouchOutside(false);

		mListener = listener;
	}

	@Override
	public void show() {
		mNewEnterprise.selectAll();
		super.show();
	}

	@Override
	public void onBackPressed() {
		mListener.onNewEnterpriseDialogBack();
	}
	
	@Override
	public void onClick(View v) {
		mListener.onNewEnterpriseCreated(mNewEnterprise.getText().toString());
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (s.length() > 0) {
			mConfirm.setEnabled(true);
		} else {
			mConfirm.setEnabled(false);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (mConfirm.isEnabled()
				&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			mListener.onNewEnterpriseCreated(mNewEnterprise.getText()
					.toString());
			return true;
		}
		return false;
	}
}
