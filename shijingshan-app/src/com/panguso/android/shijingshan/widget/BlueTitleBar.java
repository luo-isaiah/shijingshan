package com.panguso.android.shijingshan.widget;

import com.panguso.android.shijingshan.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Specific for the blue title bar widget.
 * 
 * @author Luo Yinzhuo
 */
public class BlueTitleBar extends RelativeLayout implements OnClickListener {

	/**
	 * Interface definition for a callback to be invoked when a {@link BlueTitleBar}
	 * 's back button is clicked.
	 * 
	 * @author Luo Yinzhuo
	 */
	public interface OnBackListener {

		/**
		 * Called when a {@link BlueTitleBar}'s back button has been clicked.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onBack();
	}

	/** The back button. */
	private ImageButton mBack;
	/** The title text. */
	private TextView mTitle;
	/** The back button listener. */
	private OnBackListener mListener;

	/**
	 * Construct a new instance.
	 * 
	 * @param context The system context.
	 * @param attrs The attributes.
	 */
	public BlueTitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.blue_title_bar_widget, this);
		mBack = (ImageButton) findViewById(R.id.back);
		mTitle = (TextView) findViewById(R.id.title);

		mBack.setOnClickListener(this);
	}

	/**
	 * Set the title.
	 * 
	 * @param title The title.
	 * @author Luo Yinzhuo
	 */
	public void setTitle(String title) {
		mTitle.setText(title);
	}

	/**
	 * Set the {@link OnBackListener}.
	 * 
	 * @param listener The {@link OnBackListener}.
	 * @author Luo Yinzhuo
	 */
	public void setOnBackListener(OnBackListener listener) {
		mListener = listener;
	}

	@Override
	public void onClick(View v) {
		mListener.onBack();
	}
}
