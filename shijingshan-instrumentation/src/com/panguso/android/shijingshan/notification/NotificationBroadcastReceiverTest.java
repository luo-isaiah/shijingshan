package com.panguso.android.shijingshan.notification;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.panguso.android.shijingshan.account.AccountManager;

import android.test.AndroidTestCase;

public class NotificationBroadcastReceiverTest extends AndroidTestCase {

	/** The server url. */
	private static final String SERVER_URL = "http://s-94379.gotocdn.com/sjs/JsonAction";
	/** The account. */
	private static final String ACCOUNT = "panguso";
	/** The password. */
	private static final String PASSWORD = "123456";

	/**
	 * Test {@link NotificationBroadcastReceiver#getNotification(String)}
	 * 
	 * @author Luo Yinzhuo
	 */
	public void testGetNotification() {
		try {
			AccountManager.login(ACCOUNT, PASSWORD);
			String content = NotificationBroadcastReceiver
					.getNotification(SERVER_URL);
			return;
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue("Get notification failed!", false);
	}

}
