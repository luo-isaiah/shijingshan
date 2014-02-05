package com.panguso.android.shijingshan.news;

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
public class NewsPageTitleBar extends RelativeLayout implements OnClickListener {

	/**
	 * Interface definition for a callback to be invoked when a
	 * {@link NewsPageTitleBar} 's back button is clicked.
	 * 
	 * @author Luo Yinzhuo
	 */
	public interface OnBackListener {

		/**
		 * Called when a {@link NewsPageTitleBar}'s back button has been
		 * clicked.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onTitleBarBack();
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
	public NewsPageTitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.news_page_title_bar_widget, this);
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
		mListener.onTitleBarBack();
	}
}
