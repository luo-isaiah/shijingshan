package com.panguso.android.shijingshan.notification;

import com.panguso.android.shijingshan.net.NetworkService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * The notification broadcast receiver.
 * 
 * @author Luo Yinzhuo
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		NetworkService.getNotification(context);
	}
}
