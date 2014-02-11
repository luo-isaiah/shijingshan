package com.panguso.android.shijingshan.notification;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.account.AccountManager;
import com.panguso.android.shijingshan.net.NetworkService;
import com.panguso.android.shijingshan.net.RequestFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			String content = getNotification(context
					.getString(R.string.server_url));
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static String getNotification(String serverURL) throws JSONException,
			ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = RequestFactory.createNotificationRequest(serverURL,
				AccountManager.getAccount());
		HttpResponse response = httpClient.execute(post);

		String content = NetworkService.getContent(response);
		Log.d("NotificationBroadcastReceiver", content);
		return content;
	}

}
