package com.panguso.android.shijingshan.news;

import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.column.Column;
import com.panguso.android.shijingshan.column.ColumnPage;
import com.panguso.android.shijingshan.news.NewsPageTitleBar.OnBackListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * The article page activity.
 * 
 * @author Luo Yinzhuo
 */
public class NewsPageActivity extends Activity implements OnBackListener {
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
	    
    }

	@Override
    public void onBack() {
		finish();
    }

}
