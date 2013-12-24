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
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.news_page_activity);
	    mTitleBar = (NewsPageTitleBar) findViewById(R.id.title_bar);
	    mTitleBar.setOnBackListener(this);
	    
	    Intent intent = getIntent();
	    mTitleBar.setTitle(intent.getStringExtra(Column.KEY_NAME));
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
	public void onNewsListResponseSuccess(List<NewsInfo> newsInfos, List<ColumnInfo> childColumnInfos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNewsListResponseFailed() {
		// TODO Auto-generated method stub
		
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
	private List<NewsPage> createNewsPage(List<NewsInfo> newsInfos) {
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
			
			
		}
		return newsPages;
	}
}
