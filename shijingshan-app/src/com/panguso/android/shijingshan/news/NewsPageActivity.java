package com.panguso.android.shijingshan.news;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.column.Column;
import com.panguso.android.shijingshan.column.ColumnInfo;
import com.panguso.android.shijingshan.column.ColumnPage;
import com.panguso.android.shijingshan.net.NetworkService;
import com.panguso.android.shijingshan.net.NetworkService.NewsListRequestListener;
import com.panguso.android.shijingshan.news.NewsPageTitleBar.OnBackListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * The article page activity.
 * 
 * @author Luo Yinzhuo
 */
public class NewsPageActivity extends Activity implements OnBackListener, NewsListRequestListener {
	/** The title bar. */
	private NewsPageTitleBar mTitleBar;
	/** The news page view. */
	private NewsPageView mNewsPageView;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.news_page_activity);
	    mTitleBar = (NewsPageTitleBar) findViewById(R.id.title_bar);
	    mTitleBar.setOnBackListener(this);
	    
	    Intent intent = getIntent();
	    mTitleBar.setTitle(intent.getStringExtra(Column.KEY_NAME));
	    
	    mNewsPageView = (NewsPageView) findViewById(R.id.news_page);
	    
	    final String columnID = intent.getStringExtra(Column.KEY_ID);
	    NetworkService.getNewsList(getResources().getString(R.string.server_url), columnID, this);
    }

	@Override
    public void onBack() {
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
			}
		});
	}

	@Override
	public void onNewsListResponseFailed(String columnID) {
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
