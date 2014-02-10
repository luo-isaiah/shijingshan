package com.panguso.android.shijingshan.net;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Factory to create requests.
 * 
 * @author Luo Yinzhuo
 */
public final class RequestFactory {
	/** The transCode. */
	private static final String TRANS_CODE = "transCode";
	/** The parameter. */
	private static final String PARAM = "param";

	/**
	 * Create a business info list request.
	 * 
	 * @param serverURL
	 *            The server URL.
	 * @return The business info list request.
	 * @throws UnsupportedEncodingException
	 *             If device doesn't support UTF-8 encode.
	 * @author Luo Yinzhuo
	 */
	static HttpPost createBusinessInfoListRequest(String serverURL)
			throws UnsupportedEncodingException {
		HttpPost post = new HttpPost(serverURL);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TRANS_CODE, "101"));
		post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		return post;
	}

	/** The business id. */
	private static final String BUSINESS_ID = "code_id";

	/**
	 * Create an enterprise info list request.
	 * 
	 * @param serverURL
	 *            The server URL.
	 * @param businessId
	 *            The business id.
	 * @return The enterprise info list request.
	 * @throws UnsupportedEncodingException
	 *             If device doesn't support UTF-8 encode.
	 * @throws JSONException
	 *             If an error occurs when create JSON parameters.
	 * @author Luo Yinzhuo
	 */
	static HttpPost createEnterpriseInfoListRequest(String serverURL,
			int businessId) throws JSONException, UnsupportedEncodingException {
		HttpPost post = new HttpPost(serverURL);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TRANS_CODE, "102"));
		JSONObject param = new JSONObject();
		param.put(BUSINESS_ID, businessId);
		params.add(new BasicNameValuePair(PARAM, param.toString()));
		post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		return post;
	}

	/** The account. */
	private static final String ACCOUNT = "account";
	/** The password. */
	private static final String PASSWORD = "password";
	/** The phone number. */
	private static final String PHONE_NUM = "phonenum";
	/** The enterprise id. */
	private static final String ENTERPRISE_ID = "enterpriseid";
	/** The enterprise name. */
	private static final String ENTERPRISE_NAME = "enterprisename";
	/** The deviceToken. */
	private static final String DEVICE_TOKEN = "deviceToken";
	/** The terminal type. */
	private static final String TERMINAL_TYPE = "terminalType";
	/** The user type. */
	private static final String USER_TYPE = "usertype";

	/**
	 * Create a register request.
	 * 
	 * @param serverURL
	 *            The server URL.
	 * @param account
	 *            The account name.
	 * @param password
	 *            The password.
	 * @param phoneNum
	 *            The phone number.
	 * @param enterpriseId
	 *            The enterprise id.
	 * @param enterpriseName
	 *            The enterprise name.
	 * @param deviceToken
	 *            The device UUID.
	 * @param terminalType
	 *            The device terminal type.
	 * @param userTypeId
	 *            The user type id.
	 * @return The register request.
	 * @throws UnsupportedEncodingException
	 *             If device doesn't support UTF-8 encode.
	 * @throws JSONException
	 *             If an error occurs when create JSON parameters.
	 * @author Luo Yinzhuo
	 */
	static HttpPost createRegisterRequest(String serverURL, String account,
			String password, String phoneNum, int enterpriseId,
			String enterpriseName, String deviceToken, String terminalType,
			int userTypeId) throws JSONException, UnsupportedEncodingException {
		HttpPost post = new HttpPost(serverURL);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TRANS_CODE, "103"));
		JSONObject param = new JSONObject();
		param.put(ACCOUNT, account);
		param.put(PASSWORD, password);
		param.put(PHONE_NUM, phoneNum);
		param.put(ENTERPRISE_ID, enterpriseId);
		param.put(ENTERPRISE_NAME, enterpriseName);
		param.put(DEVICE_TOKEN, deviceToken);
		param.put(TERMINAL_TYPE, terminalType);
		param.put(USER_TYPE, userTypeId);
		params.add(new BasicNameValuePair(PARAM, param.toString()));
		post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		return post;
	}

	/**
	 * Create a login request.
	 * 
	 * @param serverURL
	 *            The server URL.
	 * @param account
	 *            The account name.
	 * @param password
	 *            The password.
	 * @param deviceToken
	 *            The device UUID.
	 * @param terminalType
	 *            The device terminal type.
	 * @return The login request.
	 * @throws UnsupportedEncodingException
	 *             If device doesn't support UTF-8 encode.
	 * @throws JSONException
	 *             If an error occurs when create JSON parameters.
	 * 
	 * @author Luo Yinzhuo
	 */
	static HttpPost createLoginRequest(String serverURL, String account,
			String password, String deviceToken, String terminalType)
			throws JSONException, UnsupportedEncodingException {
		HttpPost post = new HttpPost(serverURL);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TRANS_CODE, "104"));
		JSONObject param = new JSONObject();
		param.put(ACCOUNT, account);
		param.put(PASSWORD, password);
		param.put(DEVICE_TOKEN, deviceToken);
		param.put(TERMINAL_TYPE, terminalType);
		params.add(new BasicNameValuePair(PARAM, param.toString()));
		post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		return post;
	}

	/** The old password. */
	private static final String OLD_PASSWORD = "oldpwd";
	/** The new password. */
	private static final String NEW_PASSWORD = "newpwd";

	/**
	 * Create change password request.
	 * 
	 * @param serverURL
	 *            The server URL.
	 * @param account
	 *            The account name.
	 * @param oldPassword
	 *            The old password.
	 * @param newPassword
	 *            The new password.
	 * @return The change password request.
	 * @throws UnsupportedEncodingException
	 *             If device doesn't support UTF-8 encode.
	 * @throws JSONException
	 *             If an error occurs when create JSON parameters.
	 * 
	 * @author Luo Yinzhuo
	 */
	static HttpPost createChangePasswordRequest(String serverURL,
			String account, String oldPassword, String newPassword)
			throws JSONException, UnsupportedEncodingException {
		HttpPost post = new HttpPost(serverURL);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TRANS_CODE, "106"));
		JSONObject param = new JSONObject();
		param.put(ACCOUNT, account);
		param.put(OLD_PASSWORD, oldPassword);
		param.put(NEW_PASSWORD, newPassword);
		params.add(new BasicNameValuePair(PARAM, param.toString()));
		post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		return post;
	}

	/**
	 * Create a user type info list request.
	 * 
	 * @param serverURL
	 *            The server URL.
	 * @return The user type info list request.
	 * @throws UnsupportedEncodingException
	 *             If device doesn't support UTF-8 encode.
	 * @author Luo Yinzhuo
	 */
	static HttpPost createUserTypeInfoListRequest(String serverURL)
			throws UnsupportedEncodingException {
		HttpPost post = new HttpPost(serverURL);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TRANS_CODE, "108"));
		post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		return post;
	}

	/**
	 * Create a column info list request.
	 * 
	 * @param serverURL
	 *            The server URL.
	 * @param account
	 *            The account name.
	 * @return The column list request.
	 * @throws UnsupportedEncodingException
	 *             If device doesn't support UTF-8 encode.
	 * @throws JSONException
	 *             If an error occurs when create JSON parameters.
	 * @author Luo Yinzhuo
	 */
	static HttpPost createColumnInfoListRequest(String serverURL, String account)
			throws UnsupportedEncodingException, JSONException {
		HttpPost post = new HttpPost(serverURL);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TRANS_CODE, "201"));
		JSONObject param = new JSONObject();
		param.put(ACCOUNT, account);
		params.add(new BasicNameValuePair(PARAM, param.toString()));
		post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		return post;
	}

	/** The column ID. */
	private static final String COLUMN_ID = "columnId";

	/**
	 * Create a news info list request based on specified column.
	 * 
	 * @param serverURL
	 *            The server URL.
	 * @param columnID
	 *            The column ID.
	 * @return The news list request.
	 * @throws UnsupportedEncodingException
	 *             If device doesn't support UTF-8 encode.
	 * @throws JSONException
	 *             If an error occurs when create JSON parameters.
	 * @author Luo Yinzhuo
	 */
	static HttpPost createNewsInfoListRequest(String serverURL, int columnID)
			throws UnsupportedEncodingException, JSONException {
		HttpPost post = new HttpPost(serverURL);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TRANS_CODE, "202"));
		JSONObject param = new JSONObject();
		param.put(COLUMN_ID, columnID);
		params.add(new BasicNameValuePair(PARAM, param.toString()));
		post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		return post;
	}

	/** The column IDs. */
	private static final String COLUMN_IDS = "columnIds";
	/** The column delimiter. */
	private static final String COLUMN_DELIMETER = "#";

	/**
	 * Create a add subscribe info to specified account.
	 * 
	 * @param serverURL
	 *            The server URL.
	 * @param account
	 *            The account name.
	 * @param subscribeIds
	 *            The subscribe info id list.
	 * @return The search subscribe column info list request.
	 * @throws JSONException
	 *             If an error occurs when create JSON parameters.
	 * @throws UnsupportedEncodingException
	 *             If device doesn't support UTF-8 encode.
	 * @author Luo Yinzhuo
	 */
	static HttpPost createSaveSubscribeInfoListRequest(String serverURL,
			String account, List<Integer> subscribeIds) throws JSONException,
			UnsupportedEncodingException {
		HttpPost post = new HttpPost(serverURL);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TRANS_CODE, "204"));
		JSONObject param = new JSONObject();
		param.put(ACCOUNT, account);

		StringBuilder columnIds = new StringBuilder();
		for (Integer subscribeId : subscribeIds) {
			if (columnIds.length() > 0) {
				columnIds.append(COLUMN_DELIMETER);
			}
			columnIds.append(subscribeId);
		}
		param.put(COLUMN_IDS, columnIds.toString());

		params.add(new BasicNameValuePair(PARAM, param.toString()));
		post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		return post;
	}

	/**
	 * Create a search subscribe info list request based on specified account.
	 * 
	 * @param serverURL
	 *            The server URL.
	 * @param account
	 *            The account name.
	 * @return The search subscribe column info list request.
	 * @throws JSONException
	 *             If an error occurs when create JSON parameters.
	 * @throws UnsupportedEncodingException
	 *             If device doesn't support UTF-8 encode.
	 * @author Luo Yinzhuo
	 */
	static HttpPost createSearchSubscribeInfoListRequest(String serverURL,
			String account) throws JSONException, UnsupportedEncodingException {
		HttpPost post = new HttpPost(serverURL);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TRANS_CODE, "205"));
		JSONObject param = new JSONObject();
		param.put(ACCOUNT, account);
		params.add(new BasicNameValuePair(PARAM, param.toString()));
		post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		return post;
	}

}
