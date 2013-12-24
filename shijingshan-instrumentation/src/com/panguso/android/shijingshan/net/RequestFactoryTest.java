package com.panguso.android.shijingshan.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import android.test.AndroidTestCase;

/**
 * To test original web request.
 * 
 * @author Luo Yinzhuo
 * 
 */
public class RequestFactoryTest extends AndroidTestCase {
	/** The server url. */
	private static final String SERVER_URL = "http://tsinghuacims.oicp.net:45476/sjs/JsonAction";
	/** The UUID. */
	private static final String UUID = "ffffffff-aa13-3f0f-ffff-ffffd0fe3dcb";

	/** The HTTP client. */
	private HttpClient mHttpClient;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mHttpClient = new DefaultHttpClient();
	}

	private HttpResponse execute(HttpPost request) {
		HttpResponse response = null;
		try {
			response = mHttpClient.execute(request);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	private String getContent(HttpResponse response) {
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity()
			        .getContent(), "UTF-8"));
			for (String temp = reader.readLine(); temp != null; temp = reader.readLine()) {
				sb.append(temp);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * Test {@link RequestFactory#createColumnListRequest(String, String)}.
	 * 
	 * @author Luo Yinzhuo
	 */
	public void testColumnListRequest() {
		try {
			HttpPost request = RequestFactory.createColumnListRequest(SERVER_URL, UUID);
			assertNotNull(request);
			HttpResponse response = execute(request);
			assertNotNull(response);
			assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
			String content = getContent(response);
			assertFalse(content.isEmpty());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
	        e.printStackTrace();
        }
	}

	/** The column ID. */
	private static final String COLUMN_ID = "100";
	
	/**
	 * Test {@link RequestFactory#createArticalListRequest(String, String)}.
	 * 
	 * @author Luo Yinzhuo
	 */
	public void testArticleListRequest() {
		try {
			HttpPost request = RequestFactory.createNewsListRequest(SERVER_URL, COLUMN_ID);
			assertNotNull(request);
			HttpResponse response = execute(request);
			assertNotNull(response);
			assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
			String content = getContent(response);
			assertFalse(content.isEmpty());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
	        e.printStackTrace();
        }
	}

}
