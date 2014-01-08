/**
 * 
 */
package com.panguso.android.shijingshan.news;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * The news activity.
 * 
 * @author Luo Yinzhuo
 * 
 */
public class NewsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		String newsURL = intent.getStringExtra(News.KEY_NEWS_URL);
		
		WebView view = new WebView(this);
		setContentView(view);
		
		view.getSettings().setJavaScriptEnabled(true);
		view.loadUrl(newsURL);
	}

}
