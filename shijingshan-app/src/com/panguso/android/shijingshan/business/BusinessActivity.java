package com.panguso.android.shijingshan.business;

import java.util.List;

import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.dialog.WaitingDialog;
import com.panguso.android.shijingshan.dialog.WaitingDialog.OnWaitingDialogListener;
import com.panguso.android.shijingshan.net.NetworkService;
import com.panguso.android.shijingshan.net.NetworkService.BusinessInfoListRequestListener;
import com.panguso.android.shijingshan.register.business.BusinessInfo;
import com.panguso.android.shijingshan.widget.BlueTitleBar;
import com.panguso.android.shijingshan.widget.BlueTitleBar.OnBackListener;

import android.R.integer;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BusinessActivity extends Activity implements BusinessInfoListRequestListener,
        OnBackListener, OnWaitingDialogListener {
	/** The waiting dialog ID. */
	private static final int DIALOG_WAITING = 0;
	
	@Override
    protected Dialog onCreateDialog(int id) {
		switch (id) {
			case DIALOG_WAITING:
				return new WaitingDialog(this, this);
			default:
				return null;
		}
    }

	/** The title bar. */
	private BlueTitleBar mTitleBar;
	/** The business layout. */
	private LinearLayout mBusiness;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    showDialog(DIALOG_WAITING);
	    
	    setContentView(R.layout.business_dialog);
		mTitleBar = (BlueTitleBar) findViewById(R.id.title_bar);
		
		mTitleBar.setTitle(getResources().getString(R.string.business_title));
		mTitleBar.setOnBackListener(this);
	    
		mBusiness = (LinearLayout) findViewById(R.id.business_layout);
		
		for (int i = 0; i < 20; i++) {
			Button text = new Button(this);
			text.setText("TESTTEST" + i);
			text.setTextSize(20);
			mBusiness.addView(text);
		}
		
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

	@Override
	public void onWaitingDialogBack() {
		// TODO Auto-generated method stub
		
	}

}
