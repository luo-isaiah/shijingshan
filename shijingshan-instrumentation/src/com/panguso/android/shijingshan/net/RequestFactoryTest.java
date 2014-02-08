package com.panguso.android.shijingshan.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

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
	/** The no user. */
	private static final String NO_USER = "";

	/**
	 * Read content.
	 * 
	 * @param content
	 *            The {@link HttpEntity}'s content.
	 * @return The content string.
	 * @throws IOException
	 */
	private static String readContent(InputStream content) throws IOException {
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				content, "UTF-8"));
		for (String temp = reader.readLine(); temp != null; temp = reader
				.readLine()) {
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
	public void testBusinessInfoListRequest() {
		final String EXPECT_CONTENT = "transCode=101";
		try {
			HttpPost request = RequestFactory
					.createBusinessInfoListRequest(SERVER_URL);
			assertNotNull(request);
			String content = readContent(request.getEntity().getContent());
			assertNotNull(content);
			assertEquals(EXPECT_CONTENT, content);
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue("Create business info list request failed!", false);
	}

	/** The business id. */
	private static final int BUSINESS_ID = 10301;

	/**
	 * Test {@link RequestFactory#createEnterpriseInfoListRequest(String, int)}.
	 * 
	 * @author Luo Yinzhuo
	 */
	public void testEnterpriseInfoListRequest() {
		final String EXPECT_CONTENT = "transCode=102&param={\"code_id\":10301}";
		try {
			HttpPost request = RequestFactory.createEnterpriseInfoListRequest(
					SERVER_URL, BUSINESS_ID);
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
		assertTrue("Create enterprise info list request failed!", false);
	}

	/** The account name. */
	private static final String ACCOUNT = "panguso";
	/** The password. */
	private static final String PASSWORD = "123456";
	/** The phone number. */
	private static final String PHONE_NUM = "13812345678";
	/** The enterprise id. */
	private static final int ENTERPRISE_ID = 15;
	/** The enterprise name. */
	private static final String ENTERPRISE_NAME = "大唐国际发电股份有限公司";
	/** The UUID. */
	private static final String UUID = "ffffffff-aa13-3f0f-ffff-ffffd0fe3dcb";
	/** The terminal type . */
	private static final String TERMINAL_TYPE = "HUAWEI G606-T00";
	/** The user type id. */
	private static final int USER_TYPE = 10601;

	/**
	 * Test
	 * {@link RequestFactory#createRegisterRequest(String, String, String, String, int, String, String, String, int)}
	 * 
	 * @author Luo Yinzhuo
	 */
	public void testRegisterRequest() {
		final String EXPECT_CONTENT = "transCode=103&param={\"deviceToken\":\"ffffffff-aa13-3f0f-ffff-ffffd0fe3dcb\",\"enterpriseid\":15,\"terminalType\":\"HUAWEI G606-T00\",\"account\":\"panguso\",\"usertype\":10601,\"enterprisename\":\"大唐国际发电股份有限公司\",\"phonenum\":\"13812345678\",\"password\":\"123456\"}";
		try {
			HttpPost request = RequestFactory.createRegisterRequest(SERVER_URL,
					ACCOUNT, PASSWORD, PHONE_NUM, ENTERPRISE_ID,
					ENTERPRISE_NAME, UUID, TERMINAL_TYPE, USER_TYPE);
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
		assertTrue("Create register request failed!", false);
	}

	/**
	 * Test
	 * {@link RequestFactory#createLoginRequest(String, String, String, String, String)}
	 * 
	 * @author Luo Yinzhuo
	 */
	public void testLoginRequest() {
		final String EXPECT_CONTENT = "transCode=104&param={\"terminalType\":\"HUAWEI G606-T00\",\"password\":\"123456\",\"account\":\"panguso\",\"deviceToken\":\"ffffffff-aa13-3f0f-ffff-ffffd0fe3dcb\"}";
		try {
			HttpPost request = RequestFactory.createLoginRequest(SERVER_URL,
					ACCOUNT, PASSWORD, UUID, TERMINAL_TYPE);
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
		assertTrue("Create login request failed!", false);
	}

	/**
	 * Test {@link RequestFactory#createUserTypeInfoListRequest(String)}.
	 * 
	 * @author Luo Yinzhuo
	 */
	public void testUserTypeInfoListRequest() {
		final String EXPECT_CONTENT = "transCode=108";
		try {
			HttpPost request = RequestFactory
					.createUserTypeInfoListRequest(SERVER_URL);
			assertNotNull(request);
			String content = readContent(request.getEntity().getContent());
			assertNotNull(content);
			assertEquals(EXPECT_CONTENT, content);
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue("Create user type info list request failed!", false);
	}

	/**
	 * Test {@link RequestFactory#createColumnInfoListRequest(String, String)}.
	 * 
	 * @author Luo Yinzhuo
	 */
	public void testColumnInfoListRequestWithNoUser() {
		final String EXPECT_CONTENT = "transCode=201&param={\"account\":\"\"}";
		try {
			HttpPost request = RequestFactory.createColumnInfoListRequest(
					SERVER_URL, NO_USER);
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

	/** The subscribe id. */
	private static final int SUBSCRIBE_ID = 103;

	/**
	 * Test
	 * {@link RequestFactory#createSaveSubscribeInfoListRequest(String, String, int)}
	 * 
	 * @author Luo Yinzhuo
	 */
	public void testSaveSubscribeInfoListRequest() {
		final String EXPECT_CONTENT = "transCode=204&param={\"account\":\"panguso\",\"columnIds\":103}";
		final List<Integer> subscribeIds = new ArrayList<Integer>();
		subscribeIds.add(SUBSCRIBE_ID);
		try {
			HttpPost request = RequestFactory
					.createSaveSubscribeInfoListRequest(SERVER_URL, ACCOUNT,
							subscribeIds);
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
		assertTrue("Create add subscribe info Request failed!", false);
	}

	/**
	 * Test
	 * {@link RequestFactory#createSearchSubscribeInfoListRequest(String, String)}
	 * 
	 * @author Luo Yinzhuo
	 */
	public void testSearchSubscribeInfoListRequest() {
		final String EXPECT_CONTENT = "transCode=205&param={\"account\":\"panguso\"}";
		try {
			HttpPost request = RequestFactory
					.createSearchSubscribeInfoListRequest(SERVER_URL, ACCOUNT);
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
		assertTrue("Create search subscribe column list Request failed!", false);
	}
}
