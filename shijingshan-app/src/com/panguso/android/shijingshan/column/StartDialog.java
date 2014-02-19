package com.panguso.android.shijingshan.column;

import com.panguso.android.shijingshan.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;

/**
 * Specific for the application start dialog.
 * 
 * @author Luo Yinzhuo
 */
public class StartDialog extends Dialog {
	/**
	 * Construct a new instance.
	 * 
	 * @param context
	 *            The context.
	 * @param listener
	 *            The listener.
	 */
	@SuppressWarnings("deprecation")
	public StartDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.start_dialog);

		Window window = getWindow();
		window.setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
	}
	
	@Override
	public void onBackPressed() {
	}
}
