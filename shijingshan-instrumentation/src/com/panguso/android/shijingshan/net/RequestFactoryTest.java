package com.panguso.android.shijingshan.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
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
	private static final String SERVER_URL = "http://s-94379.gotocdn.com/sjs/JsonAction";
	/** The UUID. */
	private static final String UUID = "ffffffff-aa13-3f0f-ffff-ffffd0fe3dcb";
	/** The no user. */
	private static final String NO_USER = "";
	
	/**
	 * Read content.
	 * @param content The {@link HttpEntity}'s content.
	 * @return The content string.
	 * @throws IOException
	 */
	private static String readContent(InputStream content) throws IOException {
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(content, "UTF-8"));
		for (String temp = reader.readLine(); temp != null; temp = reader.readLine()) {
			sb.append(temp);
		}
		reader.close();
		return URLDecoder.decode(sb.toString(), "utf-8");
	}
	
	/**
	 * Test {@link RequestFactory#createBusinessInfoListRequest(String)}.
	 * 
	 * @author Luo Yinzhuo
	 */
	public void testBusinessListRequest() {
		final String EXPECT_CONTENT = "transCode=101";
        try {
        	HttpPost request = RequestFactory.createBusinessInfoListRequest(SERVER_URL);
        	assertNotNull(request);
    		String content = readContent(request.getEntity().getContent());
    		assertNotNull(content);
    		assertEquals(EXPECT_CONTENT, content);
    		return;
        } catch (IOException e) {
	        e.printStackTrace();
        }
        assertTrue("Create business list Request with no user failed!", false);
	}

	/**
	 * Test {@link RequestFactory#createColumnInfoListRequest(String, String)}.
	 * 
	 * @author Luo Yinzhuo
	 */
	public void testColumnListRequestWithNoUser() {
		final String EXPECT_CONTENT = "transCode=201&param={\"account\":\"\"}";
		try {
			HttpPost request = RequestFactory.createColumnInfoListRequest(SERVER_URL, NO_USER);
			assertNotNull(request);
			String content = readContent(request.getEntity().getContent());
			assertNotNull(content);
			assertEquals(EXPECT_CONTENT, content);
			return;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
	        e.printStackTrace();
        }
		assertTrue("Create column list Request with no user failed!", false);
	}

	/** The column ID. */
	private static final String COLUMN_ID = "100";
	
	/**
	 * Test {@link RequestFactory#createArticalListRequest(String, String)}.
	 * 
	 * @author Luo Yinzhuo
	 */
	public void testArticleListRequest() {
		final String EXPECT_CONTENT = "transCode=202&param={\"columnId\":\"100\"}";
		try {
			HttpPost request = RequestFactory.createNewsInfoListRequest(SERVER_URL, COLUMN_ID);
			assertNotNull(request);
			String content = readContent(request.getEntity().getContent());
			assertNotNull(content);
			assertEquals(EXPECT_CONTENT, content);
			return;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
	        e.printStackTrace();
        }
		assertTrue("Create article list Request failed!", false);
	}

}
