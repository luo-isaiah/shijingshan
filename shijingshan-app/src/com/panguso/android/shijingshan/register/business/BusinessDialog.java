package com.panguso.android.shijingshan.register.business;

import java.util.List;

import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.net.NetworkService;
import com.panguso.android.shijingshan.net.NetworkService.BusinessInfoListRequestListener;
import com.panguso.android.shijingshan.register.business.BusinessButton.OnBusinessButtonListener;
import com.panguso.android.shijingshan.widget.BlueTitleBar;
import com.panguso.android.shijingshan.widget.UnderlineButton;
import com.panguso.android.shijingshan.widget.BlueTitleBar.OnBackListener;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

/**
 * Specific for the business select dialog.
 * 
 * @author Luo Yinzhuo
 * 
 */
public class BusinessDialog extends Dialog implements OnBackListener,
		BusinessInfoListRequestListener, OnBusinessButtonListener,
		OnClickListener {

	/**
	 * Interface definition for a callback to be invoked during the request for
	 * {@link BusinessButton} list or when the back button is clicked or back
	 * key pressed or one of the business button is clicked.
	 * 
	 * @author Luo Yinzhuo
	 */
	public interface OnBusinessDialogListener {

		/**
		 * Called when the dialog is initializing.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onBusinessDialogInitializing();

		/**
		 * Called when the dialog finishes initialization.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onBusinessDialogInitialized();

		/**
		 * Called when the back button is clicked or the back key pressed.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onBusinessDialogBack();

		/**
		 * Called when one of the {@link Business} is selected.
		 * 
		 * @param id
		 *            The business id.
		 * @param name
		 *            The business name.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onBusinessSelected(int id, String name);

		/**
		 * Called when the new enterprise button is clicked.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onNewEnterpriseClicked();
	}

	/** The title bar. */
	private final BlueTitleBar mTitleBar;
	/** The business layout. */
	private final LinearLayout mBusiness;
	/** The new enterprise button. */
	private final UnderlineButton mNewEnterprise;
	/** The initialize flag. */
	private boolean mInitialized = false;
	/** The listener. */
	private final OnBusinessDialogListener mListener;

	/**
	 * Construct a new instance.
	 * 
	 * @param context
	 *            The context.
	 */
	@SuppressWarnings("deprecation")
	public BusinessDialog(Context context, OnBusinessDialogListener listener) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.business_dialog);

		mTitleBar = (BlueTitleBar) findViewById(R.id.title_bar);
		mTitleBar.setTitle(context.getResources().getString(
				R.string.business_title));
		mTitleBar.setOnBackListener(this);

		mBusiness = (LinearLayout) findViewById(R.id.business_layout);

		mNewEnterprise = (UnderlineButton) findViewById(R.id.new_enterprise);
		mNewEnterprise.setOnClickListener(this);

		Window window = getWindow();
		window.setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

		mListener = listener;
		NetworkService.getBusinessInfoList(
				context.getResources().getString(R.string.server_url), this);
	}

	@Override
	public void onBack() {
		if (mListener != null) {
			mListener.onBusinessDialogBack();
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
			mListener.onBusinessDialogInitializing();
		}
	}

	@Override
	public void onBusinessInfoListRequestFailed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBusinessInfoListResponseSuccess(
			final List<BusinessInfo> businessInfos) {
		getOwnerActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				for (BusinessInfo businessInfo : businessInfos) {
					BusinessButton button = businessInfo
							.getBusinessButton(getContext());
					button.setOnBusinessButtonListener(BusinessDialog.this);
					mBusiness.addView(button);
				}

				if (mListener != null) {
					mListener.onBusinessDialogInitialized();
				}

				mInitialized = true;
			}
		});
	}

	@Override
	public void onBusinessInfoListResponseFailed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClicked(int id, String name) {
		if (mListener != null) {
			mListener.onBusinessSelected(id, name);
		}
	}

	@Override
	public void onClick(View v) {
		if (mListener != null) {
			mListener.onNewEnterpriseClicked();
		}
	}

}
