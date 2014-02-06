package com.panguso.android.shijingshan.register.usertype;

import java.util.List;

import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.net.NetworkService;
import com.panguso.android.shijingshan.net.NetworkService.UserTypeInfoListRequestListener;
import com.panguso.android.shijingshan.register.usertype.UserTypeButton.OnUserTypeButtonListener;
import com.panguso.android.shijingshan.widget.BlueTitleBar;
import com.panguso.android.shijingshan.widget.BlueTitleBar.OnBackListener;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

/**
 * Specific for the user type select dialog.
 * 
 * @author luoyinzhuo
 * 
 */
public final class UserTypeDialog extends Dialog implements OnBackListener,
		UserTypeInfoListRequestListener, OnUserTypeButtonListener {

	/**
	 * Interface definition for a callback to be invoked during the request for
	 * {@link UserTypeButton} list or when the back button is clicked or back
	 * key pressed or one of the user type button is clicked.
	 * 
	 * @author Luo Yinzhuo
	 */
	public interface OnUserTypeDialogListener {

		/**
		 * Called when the dialog is initializing.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onUserTypeDialogInitializing();

		/**
		 * Called when the dialog finishes initialization.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onUserTypeDialogInitialized();

		/**
		 * Called when the dialog failed initialization.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onUserTypeDialogInitializeFailed();

		/**
		 * Called when the back button is clicked or the back key pressed.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onUserTypeDialogBack();

		/**
		 * Called when one of the {@link UserType} is selected.
		 * 
		 * @param id
		 *            The user type id.
		 * @param name
		 *            The user type name.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onUserTypeSelected(int id, String name);
	}

	/** The title bar. */
	private BlueTitleBar mTitleBar;
	/** The user type layout. */
	private LinearLayout mUserType;
	/** The initialize flag. */
	private boolean mInitialized = false;
	/** The listener. */
	private final OnUserTypeDialogListener mListener;

	/**
	 * Construct a new instance.
	 * 
	 * @param context
	 *            The context.
	 */
	@SuppressWarnings("deprecation")
	public UserTypeDialog(Context context, OnUserTypeDialogListener listener) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.user_type_dialog);

		mTitleBar = (BlueTitleBar) findViewById(R.id.title_bar);
		mTitleBar.setTitle(context.getString(R.string.user_type_title));
		mTitleBar.setOnBackListener(this);

		mUserType = (LinearLayout) findViewById(R.id.user_type_layout);

		Window window = getWindow();
		window.setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

		mListener = listener;
		NetworkService.getUserTypeInfoList(
				context.getString(R.string.server_url), this);
	}

	@Override
	public void onBack() {
		if (mListener != null) {
			mListener.onUserTypeDialogBack();
		}
	}

	@Override
	public void onBackPressed() {
		onBack();
	}

	@Override
	public void show() {
		super.show();
		if (!mInitialized && mListener != null) {
			mListener.onUserTypeDialogInitializing();
		}
	}

	@Override
	public void onUserTypeInfoListRequestFailed() {
	}

	@Override
	public void onUserTypeInfoListResponseSuccess(
			final List<UserTypeInfo> userTypeInfos) {
		getOwnerActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				for (UserTypeInfo userTypeInfo : userTypeInfos) {
					UserTypeButton button = userTypeInfo
							.getUserTypeButton(getContext());
					button.setOnUserTypeButtonListener(UserTypeDialog.this);
					mUserType.addView(button);
				}

				if (mListener != null) {
					mListener.onUserTypeDialogInitialized();
				}

				mInitialized = true;
			}
		});
	}

	/**
	 * Retry initialization.
	 * 
	 * @author Luo Yinzhuo
	 */
	public void retry() {
		NetworkService.getUserTypeInfoList(
				getContext().getString(R.string.server_url), this);
	}

	@Override
	public void onUserTypeInfoListResponseFailed() {
		getOwnerActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mListener != null) {
					mListener.onUserTypeDialogInitializeFailed();
				}
			}
		});
	}

	@Override
	public void onClicked(int id, String name) {
		if (mListener != null) {
			mListener.onUserTypeSelected(id, name);
		}
	}

}
