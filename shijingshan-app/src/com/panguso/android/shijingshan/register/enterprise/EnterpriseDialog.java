/**
 * 
 */
package com.panguso.android.shijingshan.register.enterprise;

import java.util.ArrayList;
import java.util.List;

import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.net.NetworkService;
import com.panguso.android.shijingshan.net.NetworkService.EnterpriseInfoListRequestListener;
import com.panguso.android.shijingshan.register.enterprise.EnterpriseButton.OnEnterpriseButtonListener;
import com.panguso.android.shijingshan.widget.BlueTitleBar;
import com.panguso.android.shijingshan.widget.BlueTitleBar.OnBackListener;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.SparseArray;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

/**
 * Specific for the enterprise select dialog.
 * 
 * @author Luo Yinzhuo
 * 
 */
public class EnterpriseDialog extends Dialog implements OnBackListener,
		EnterpriseInfoListRequestListener, OnEnterpriseButtonListener {

	/**
	 * Interface definition for a callback to be invoked during the request for
	 * {@link EnterpriseButton} list or when the back button is clicked or back
	 * key pressed or one of the enterprise button is clicked.
	 * 
	 * @author Luo Yinzhuo
	 */
	public interface OnEnterpriseDialogListener {

		/**
		 * Called when the dialog is initializing.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onEnterpriseDialogInitializing();

		/**
		 * Called when the dialog finishes initialization.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onEnterpriseDialogInitialized();

		/**
		 * Called when the back button is clicked or the back key pressed.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onEnterpriseDialogBack();

		/**
		 * Called when one of the {@link UserType} is selected.
		 * 
		 * @param id
		 *            The enterprise id.
		 * @param name
		 *            The enterprise name.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onEnterpriseSelected(int id, String name);
	}

	/** The array to store business enterprise info. */
	private final SparseArray<List<EnterpriseInfo>> mBusinessEnterpriseArray = new SparseArray<List<EnterpriseInfo>>();
	/** The enterprise button cache. */
	private final List<EnterpriseButton> mEnterpriseButtonCache = new ArrayList<EnterpriseButton>();

	/** The title bar. */
	private final BlueTitleBar mTitleBar;
	/** The enterprise layout. */
	private final LinearLayout mEnterprise;
	/** The business id. */
	private int mBusinessId;
	/** The listener. */
	private final OnEnterpriseDialogListener mListener;

	/**
	 * Construct a new instance.
	 * 
	 * @param context
	 *            The context.
	 */
	@SuppressWarnings("deprecation")
	public EnterpriseDialog(Context context, int businessId,
			OnEnterpriseDialogListener listener) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.enterprise_dialog);

		mTitleBar = (BlueTitleBar) findViewById(R.id.title_bar);
		mTitleBar.setTitle(context.getString(R.string.enterprise_title));
		mTitleBar.setOnBackListener(this);

		mEnterprise = (LinearLayout) findViewById(R.id.enterprise_layout);

		Window window = getWindow();
		window.setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

		mListener = listener;
		mBusinessId = businessId;
		NetworkService.getEnterpriseInfoList(
				context.getResources().getString(R.string.server_url),
				businessId, this);
	}

	/**
	 * Set business id to show the relevant enterprise list.
	 * 
	 * @param businessId
	 *            The business id.
	 * @author Luo Yinzhuo
	 */
	public void setBusinessId(int businessId) {
		mBusinessId = businessId;
		List<EnterpriseInfo> enterpriseInfos = mBusinessEnterpriseArray
				.get(mBusinessId);
		if (enterpriseInfos == null) {
			NetworkService.getEnterpriseInfoList(getContext().getResources()
					.getString(R.string.server_url), businessId, this);
		} else {
			mEnterprise.removeAllViews();

			int buttonSize = mEnterpriseButtonCache.size();

			for (int i = 0; i < enterpriseInfos.size(); i++) {
				EnterpriseInfo enterpriseInfo = enterpriseInfos.get(i);

				EnterpriseButton button;
				if (i < buttonSize) {
					button = mEnterpriseButtonCache.get(i);
					button = enterpriseInfo.getEnterpriseButton(button);
				} else {
					button = enterpriseInfo.getEnterpriseButton(getContext());
					button.setOnEnterpriseButtonListener(EnterpriseDialog.this);
					mEnterpriseButtonCache.add(button);
				}
				mEnterprise.addView(button);
			}
		}
	}

	@Override
	public void onBack() {
		if (mListener != null) {
			mListener.onEnterpriseDialogBack();
		}
	}

	@Override
	public void onBackPressed() {
		onBack();
	}

	@Override
	public void show() {
		super.show();
		if (mBusinessEnterpriseArray.get(mBusinessId) == null
				&& mListener != null) {
			mListener.onEnterpriseDialogInitializing();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.panguso.android.shijingshan.net.NetworkService.
	 * EnterpriseInfoListRequestListener#onEnterpriseInfoListRequestFailed()
	 */
	@Override
	public void onEnterpriseInfoListRequestFailed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEnterpriseInfoListResponseSuccess(final int businessId,
			final List<EnterpriseInfo> enterpriseInfos) {
		mBusinessEnterpriseArray.put(businessId, enterpriseInfos);

		getOwnerActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mBusinessId == businessId) {
					mEnterprise.removeAllViews();

					int buttonSize = mEnterpriseButtonCache.size();

					for (int i = 0; i < enterpriseInfos.size(); i++) {
						EnterpriseInfo enterpriseInfo = enterpriseInfos.get(i);

						EnterpriseButton button;
						if (i < buttonSize) {
							button = mEnterpriseButtonCache.get(i);
							button = enterpriseInfo.getEnterpriseButton(button);
						} else {
							button = enterpriseInfo
									.getEnterpriseButton(getContext());
							button.setOnEnterpriseButtonListener(EnterpriseDialog.this);
							mEnterpriseButtonCache.add(button);
						}
						mEnterprise.addView(button);
					}

					if (mListener != null) {
						mListener.onEnterpriseDialogInitialized();
					}
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.panguso.android.shijingshan.net.NetworkService.
	 * EnterpriseInfoListRequestListener#onEnterpriseInfoListResponseFailed()
	 */
	@Override
	public void onEnterpriseInfoListResponseFailed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClicked(int id, String name) {
		if (mListener != null) {
			mListener.onEnterpriseSelected(id, name);
		}
	}

}
