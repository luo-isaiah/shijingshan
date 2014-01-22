/**
 * 
 */
package com.panguso.android.shijingshan.register.enterprise;


import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.Dialog;
import android.content.Context;
import android.util.SparseArray;

/**
 * Specific for the enterprise select dialog.
 * 
 * @author Luo Yinzhuo
 * 
 */
public class EnterpriseDialog extends Dialog {

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
	
	/**
	 * Construct a new instance.
	 * 
	 * @param context The context.
	 */
	public EnterpriseDialog(Context context, int businessId, OnEnterpriseDialogListener listener) {
		super(context);
		
		
	}
	
	public void setBusinessId(int businessId) {
		
	}

}
