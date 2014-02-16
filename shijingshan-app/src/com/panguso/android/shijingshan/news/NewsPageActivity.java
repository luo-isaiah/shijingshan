package com.panguso.android.shijingshan.news;

import java.util.ArrayList;
import java.util.List;

import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.column.ColumnInfo;
import com.panguso.android.shijingshan.column.Column;
import com.panguso.android.shijingshan.dialog.MessageDialog;
import com.panguso.android.shijingshan.dialog.MessageDialog.OnMessageDialogListener;
import com.panguso.android.shijingshan.dialog.WaitingDialog;
import com.panguso.android.shijingshan.net.NetworkService;
import com.panguso.android.shijingshan.net.NetworkService.NewsListRequestListener;
import com.panguso.android.shijingshan.news.NewsPageTitleBar.OnBackListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

/**
 * The news page activity.
 * 
 * @author Luo Yinzhuo
 */
public class NewsPageActivity extends Activity implements OnBackListener,
		NewsListRequestListener, OnMessageDialogListener {
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
	private NewsPageTitleBar mTitleBar;
	/** The news page view. */
	private NewsPageView mNewsPageView;
	/** The column id. */
	private int mColumnId;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showDialog(DIALOG_WAITING);

		setContentView(R.layout.news_page_activity);
		mTitleBar = (NewsPageTitleBar) findViewById(R.id.title_bar);
		mTitleBar.setOnBackListener(this);

		Intent intent = getIntent();
		mTitleBar.setTitle(intent.getStringExtra(Column.KEY_NAME));

		mNewsPageView = (NewsPageView) findViewById(R.id.news_page);

		mColumnId = intent.getIntExtra(Column.KEY_ID, 0);
		NetworkService.getNewsList(getResources()
				.getString(R.string.server_url), mColumnId, this);
	}

	@Override
	public void onTitleBarBack() {
		finish();
	}

	@Override
	public void onNewsListRequestFailed() {
	}

	@Override
	public void onNewsListResponseSuccess(List<NewsInfo> newsInfos,
			List<ColumnInfo> childColumnInfos) {
		final List<News> newses = new ArrayList<News>();
		for (NewsInfo newsInfo : newsInfos) {
			newses.add(newsInfo.getNews(getResources()));
		}

		runOnUiThread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				mNewsPageView.initialize(newses);
				dismissDialog(DIALOG_WAITING);
			}
		});
	}

	@Override
	public void onNewsListResponseFailed() {
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
	public void onMessageDialogBack(int id) {
		finish();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onMessageDialogConfirmed(int id) {
		switch (id) {
		case DIALOG_RETRY:
			dismissDialog(DIALOG_RETRY);
			showDialog(DIALOG_WAITING);
			NetworkService.getNewsList(
					getResources().getString(R.string.server_url), mColumnId,
					this);
			break;
		}
	}
}
