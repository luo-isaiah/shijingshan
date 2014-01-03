package com.panguso.android.shijingshan.business;

import java.util.List;

import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.net.NetworkService;
import com.panguso.android.shijingshan.net.NetworkService.BusinessInfoListRequestListener;
import com.panguso.android.shijingshan.widget.BlueTitleBar;
import com.panguso.android.shijingshan.widget.BlueTitleBar.OnBackListener;

import android.app.Activity;
import android.os.Bundle;

public class BusinessActivity extends Activity implements BusinessInfoListRequestListener, OnBackListener {
	/** The title bar. */
	private BlueTitleBar mTitleBar;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.business_activity);
		mTitleBar = (BlueTitleBar) findViewById(R.id.title_bar);
		
		mTitleBar.setTitle(getResources().getString(R.string.business_title));
		mTitleBar.setOnBackListener(this);
	    
	    NetworkService.getBusinessInfoList(getResources().getString(R.string.server_url), this);
    }

	@Override
    public void onBusinessInfoListRequestFailed() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void onBusinessInfoListResponseSuccess(List<BusinessInfo> businessInfos) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void onBusinessInfoListResponseFailed() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void onBack() {
		finish();
    }

}
