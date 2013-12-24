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

	/** The deviceToken. */
	private static final String DEVICE_TOKEN = "deviceToken";

	/**
	 * Create a column list request.
	 * 
	 * @param serverURL The server URL.
	 * @param UUID The device's UUID.
	 * @return The column list request.
	 * @throws UnsupportedEncodingException If device doesn't support UTF-8
	 *         encode.
	 * @throws JSONException If an error occurs when create JSON parameters.
	 * @author Luo Yinzhuo
	 */
	static HttpPost createColumnListRequest(String serverURL, String UUID)
	        throws UnsupportedEncodingException, JSONException {
		HttpPost post = new HttpPost(serverURL);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TRANS_CODE, "201"));
		JSONObject param = new JSONObject();
		param.put(DEVICE_TOKEN, UUID);
		params.add(new BasicNameValuePair(PARAM, param.toString()));
		post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		return post;
	}

	/** The column ID. */
	private static final String COLUMN_ID = "columnId";

	/**
	 * Create a news list request.
	 * 
	 * @param serverURL The server URL.
	 * @param columnID The column ID.
	 * @return The news list request.
	 * @throws UnsupportedEncodingException If device doesn't support UTF-8
	 *         encode.
	 * @throws JSONException If an error occurs when create JSON parameters.
	 * @author Luo Yinzhuo
	 */
	static HttpPost createNewsListRequest(String serverURL, String columnID)
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

}
