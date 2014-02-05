package com.panguso.android.shijingshan.news;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.column.ColumnInfo;
import com.panguso.android.shijingshan.column.Column;
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
		NewsListRequestListener {
	/** The waiting dialog ID. */
	private static final int DIALOG_WAITING = 0;
	/** The retry dialog ID. */
	private static final int DIALOG_RETRY = 1;

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case DIALOG_WAITING:
				return new WaitingDialog(this);
			default:
				return null;
		}
	}
	
	/** The title bar. */
	private NewsPageTitleBar mTitleBar;
	/** The news page view. */
	private NewsPageView mNewsPageView;
	
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
	    
	    final int columnID = intent.getIntExtra(Column.KEY_ID, 0);
	    NetworkService.getNewsList(getResources().getString(R.string.server_url), columnID, this);
    }

	@Override
    public void onTitleBarBack() {
		finish();
    }
	
	@Override
	public void onNewsListRequestFailed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNewsListResponseSuccess(final List<NewsInfo> newsInfos, List<ColumnInfo> childColumnInfos) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mNewsPageView.initialize(createNewsPages(newsInfos), 0);
				dismissDialog(DIALOG_WAITING);
			}
		});
	}

	@Override
	public void onNewsListResponseFailed(int columnID) {
		NetworkService.getNewsList(getResources().getString(R.string.server_url), columnID, this);		
	}
	
	/** The news map. */
	private final Map<String, News> mNewsMap = new HashMap<String, News>();
	
	/**
	 * Create the list of {@link NewsPage} from list of {@link NewsInfo}.
	 * 
	 * @param newsInfos The list of {@link NewsInfo}.
	 * @return The list of {@link NewsPage} with current column.
	 * @author Luo Yinzhuo
	 */
	private List<NewsPage> createNewsPages(List<NewsInfo> newsInfos) {
		List<NewsPage> newsPages = new ArrayList<NewsPage>();
		NewsPage page = new NewsPage();
		newsPages.add(page);
		
		for (int i = 0; i < newsInfos.size(); i++) {
			NewsInfo newsInfo = newsInfos.get(i);
			String id = newsInfo.getID();
			News news;
			if (mNewsMap.containsKey(id)) {
				news = mNewsMap.get(id);
			} else {
				news = newsInfo.getNews();
				mNewsMap.put(id, news);
			}
			
			if (!page.addNews(news)) {
				page = new NewsPage();
				newsPages.add(page);
				page.addNews(news);
			}
		}
		return newsPages;
	}
}
