package com.panguso.android.shijingshan.net;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.panguso.android.shijingshan.column.ColumnInfo;
import com.panguso.android.shijingshan.news.NewsInfo;
import com.panguso.android.shijingshan.register.business.BusinessInfo;
import com.panguso.android.shijingshan.register.enterprise.EnterpriseInfo;
import com.panguso.android.shijingshan.register.usertype.UserTypeInfo;
import com.panguso.android.shijingshan.subscribe.SubscribeInfo;

/**
 * Provide network service.
 * 
 * @author Luo Yinzhuo
 */
public final class NetworkService {
	/** The socket time out. */
	private static final int DEFAULT_SOCKET_TIMEOUT = 60000;
	/** The maximum connections per route. */
	private static final int DEFAULT_HOST_CONNECTIONS = 2;
	/** The maximum total connections. */
	private static final int DEFAULT_MAX_CONNECTIONS = 10;
	/** The socket buffer size. */
	private static final int DEFAULT_SOCKET_BUFFER_SIZE = 10240;
	/** The threads share {@link HttpClient}. */
	private static final HttpClient HTTP_CLIENT;

	static {
		final HttpParams httpParams = new BasicHttpParams();
		// timeout: get connections from connection pool
		ConnManagerParams.setTimeout(httpParams, 1000);
		// timeout: connect to the server
		HttpConnectionParams.setConnectionTimeout(httpParams,
				DEFAULT_SOCKET_TIMEOUT);
		// timeout: transfer data from server
		HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_SOCKET_TIMEOUT);

		// set max connections per host
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams,
				new ConnPerRouteBean(DEFAULT_HOST_CONNECTIONS));
		// set max total connections
		ConnManagerParams.setMaxTotalConnections(httpParams,
				DEFAULT_MAX_CONNECTIONS);

		// use expect-continue handshake
		HttpProtocolParams.setUseExpectContinue(httpParams, true);
		// disable stale check
		HttpConnectionParams.setStaleCheckingEnabled(httpParams, false);

		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);

		HttpClientParams.setRedirecting(httpParams, false);

		// set user agent
		final String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
		HttpProtocolParams.setUserAgent(httpParams, userAgent);

		// disable Nagle algorithm
		HttpConnectionParams.setTcpNoDelay(httpParams, true);

		HttpConnectionParams.setSocketBufferSize(httpParams,
				DEFAULT_SOCKET_BUFFER_SIZE);

		// scheme: http and https
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory
				.getSocketFactory(), 443));

		ClientConnectionManager manager = new ThreadSafeClientConnManager(
				httpParams, schemeRegistry);
		HTTP_CLIENT = new DefaultHttpClient(manager, httpParams);
	}

	/** The commands queue. */
	private static final BlockingQueue<Runnable> COMMANDS = new ArrayBlockingQueue<Runnable>(
			200);
	/** The thread pool. */
	private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
			2, 2, 15 * 60, TimeUnit.SECONDS, COMMANDS);

	/**
	 * Read the HTTP response's content.
	 * 
	 * @param response
	 *            The HTTP response.
	 * @return The response's content.
	 * @throws IOException
	 *             If an I/O error occurs.
	 * @throws IllegalStateException
	 *             If the response is in illegal state.
	 * @throws UnsupportedEncodingException
	 *             If the device doesn't support UTF-8 encode.
	 * @author Luo Yinzhuo
	 */
	public static String getContent(HttpResponse response)
			throws UnsupportedEncodingException, IllegalStateException,
			IOException {
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent(), "UTF-8"));
		for (String temp = reader.readLine(); temp != null; temp = reader
				.readLine()) {
			sb.append(temp);
		}
		reader.close();
		return sb.toString();
	}

	/** The xCode to identify the response is successful. */
	private static final int XCODE_SUCCESS = 0;
	/** The xCode to identify the request's account name already exist. */
	private static final int XCODE_ACCOUNT_EXIST = 201;
	/** The xCode to identify the request's account name doesn't exist. */
	private static final int XCODE_ACCOUNT_NOT_EXIST = 204;
	/** The xCode to identify the request's account has been canceled. */
	private static final int XCODE_ACCOUNT_CANCELED = 205;
	/** The xCode to identify the request's account has been frozen. */
	private static final int XCODE_ACCOUNT_FROZEN = 206;
	/** The xCode to identify the request's account has not been activated. */
	private static final int XCODE_ACCOUNT_NOT_ACTIVATED = 207;
	/** The xCode to identify the request's account and password not match. */
	private static final int XCODE_ACCOUNT_PASSWORD_NOT_MATCH = 208;
	/** The xCode to identify the request's old password incorrect. */
	private static final int XCODE_OLD_PASSWORD_INCORRECT = 209;
	/** The xCode to identify the request's old password and new password same. */
	private static final int XCODE_OLD_PASSWORD_NEW_PASSWORD_SAME = 210;
	/** The xCode to identify the request execution encounters no data error. */
	private static final int XCODE_NO_DATA = 997;
	/** The xCode to identify the request execution encounters database error. */
	private static final int XCODE_DATABASE_ERROR = 998;

	/**
	 * Interface definition for a callback to be invoked when a business info
	 * list request is executed.
	 * 
	 * @author Luo Yinzhuo
	 */
	public interface BusinessInfoListRequestListener {
		/**
		 * Called when the business info list request creation is failed.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onBusinessInfoListRequestFailed();

		/**
		 * Called when the business info list request execution is successful.
		 * 
		 * @param businessInfos
		 *            The list of {@link BusinessInfo} from server.
		 * @author Luo Yinzhuo
		 */
		public void onBusinessInfoListResponseSuccess(
				List<BusinessInfo> businessInfos);

		/**
		 * Called when the business info list request execution is failed.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onBusinessInfoListResponseFailed();
	}

	/**
	 * Specified for execute business info list request.
	 * 
	 * @author Luo Yinzhuo
	 */
	private static class BusinessInfoListCommand implements Runnable {
		/** The server URL. */
		private final String mServerURL;
		/** The request listener. */
		private final BusinessInfoListRequestListener mListener;

		/**
		 * Construct a new instance.
		 * 
		 * @param serverURL
		 *            The server URL.
		 * @param listener
		 *            The request listener.
		 */
		private BusinessInfoListCommand(String serverURL,
				BusinessInfoListRequestListener listener) {
			mServerURL = serverURL;
			mListener = listener;
		}

		/** The key to get xCode. */
		private static final String KEY_XCODE = "xCode";
		/** The key to get xData. */
		private static final String KEY_XDATA = "xData";

		@Override
		public void run() {
			HttpPost request;
			try {
				request = RequestFactory
						.createBusinessInfoListRequest(mServerURL);
			} catch (Exception e) {
				e.printStackTrace();
				mListener.onBusinessInfoListRequestFailed();
				return;
			}

			String content;
			try {
				HttpResponse response = HTTP_CLIENT.execute(request);
				content = NetworkService.getContent(response);
			} catch (IOException e) {
				e.printStackTrace();
				mListener.onBusinessInfoListResponseFailed();
				return;
			}

			try {
				JSONObject jsonResponse = new JSONObject(content);
				if (jsonResponse.getInt(KEY_XCODE) == 0) {
					JSONArray jsonBusinessInfo = jsonResponse
							.getJSONArray(KEY_XDATA);
					List<BusinessInfo> businessInfos = new ArrayList<BusinessInfo>();
					for (int i = 0; i < jsonBusinessInfo.length(); i++) {
						JSONObject businessInfo = jsonBusinessInfo
								.getJSONObject(i);
						if (BusinessInfo.isBusinessInfo(businessInfo)) {
							businessInfos.add(BusinessInfo.parse(businessInfo));
						}
					}
					mListener.onBusinessInfoListResponseSuccess(businessInfos);
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.e("BusinessInfoListCommand", content);
			mListener.onBusinessInfoListResponseFailed();
		}
	}

	/**
	 * Get the business info list.
	 * 
	 * @param serverURL
	 *            The server URL.
	 * @param listener
	 *            The request listener.
	 * @author Luo Yinzhuo
	 */
	public static void getBusinessInfoList(String serverURL,
			BusinessInfoListRequestListener listener) {
		EXECUTOR.execute(new BusinessInfoListCommand(serverURL, listener));
	}

	/**
	 * Interface definition for a callback to be invoked when a enterprise info
	 * list request is executed.
	 * 
	 * @author Luo Yinzhuo
	 */
	public interface EnterpriseInfoListRequestListener {
		/**
		 * Called when the enterprise info list request creation is failed.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onEnterpriseInfoListRequestFailed();

		/**
		 * Called when the enterprise info list request execution is successful.
		 * 
		 * @param businessId
		 *            The business id which the enterprises belong to.
		 * @param enterpriseInfos
		 *            The list of {@link EnterpriseInfo} from server.
		 * @author Luo Yinzhuo
		 */
		public void onEnterpriseInfoListResponseSuccess(int businessId,
				List<EnterpriseInfo> enterpriseInfos);

		/**
		 * Called when the enterprise info list request execution is failed.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onEnterpriseInfoListResponseFailed();
	}

	/**
	 * Specified for execute enterprise info list request.
	 * 
	 * @author Luo Yinzhuo
	 */
	private static class EnterpriseInfoListCommand implements Runnable {
		/** The server URL. */
		private final String mServerURL;
		/** The business id. */
		private final int mBusinessId;
		/** The request listener. */
		private final EnterpriseInfoListRequestListener mListener;

		/**
		 * Construct a new instance.
		 * 
		 * @param serverURL
		 *            The server URL.
		 * @param businessId
		 *            The business id.
		 * @param listener
		 *            The request listener.
		 */
		private EnterpriseInfoListCommand(String serverURL, int businessId,
				EnterpriseInfoListRequestListener listener) {
			mServerURL = serverURL;
			mBusinessId = businessId;
			mListener = listener;
		}

		/** The key to get xCode. */
		private static final String KEY_XCODE = "xCode";
		/** The key to get xData. */
		private static final String KEY_XDATA = "xData";

		@Override
		public void run() {
			HttpPost request;
			try {
				request = RequestFactory.createEnterpriseInfoListRequest(
						mServerURL, mBusinessId);
			} catch (Exception e) {
				e.printStackTrace();
				mListener.onEnterpriseInfoListRequestFailed();
				return;
			}

			String content;
			try {
				HttpResponse response = HTTP_CLIENT.execute(request);
				content = NetworkService.getContent(response);
			} catch (IOException e) {
				e.printStackTrace();
				mListener.onEnterpriseInfoListResponseFailed();
				return;
			}

			try {
				JSONObject jsonResponse = new JSONObject(content);
				if (jsonResponse.getInt(KEY_XCODE) == 0) {
					JSONArray jsonEnterpriseInfo = jsonResponse
							.getJSONArray(KEY_XDATA);
					List<EnterpriseInfo> enterpriseInfos = new ArrayList<EnterpriseInfo>();
					for (int i = 0; i < jsonEnterpriseInfo.length(); i++) {
						JSONObject enterpriseInfo = jsonEnterpriseInfo
								.getJSONObject(i);
						if (EnterpriseInfo.isEnterpriseInfo(enterpriseInfo)) {
							enterpriseInfos.add(EnterpriseInfo
									.parse(enterpriseInfo));
						}
					}
					mListener.onEnterpriseInfoListResponseSuccess(mBusinessId,
							enterpriseInfos);
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.e("EnterpriseInfoListCommand", content);
			mListener.onEnterpriseInfoListResponseFailed();
		}
	}

	/**
	 * Get the enterprise info list.
	 * 
	 * @param serverURL
	 *            The server URL.
	 * @param businessId
	 *            The business id.
	 * @param listener
	 *            The request listener.
	 * @author Luo Yinzhuo
	 */
	public static void getEnterpriseInfoList(String serverURL, int businessId,
			EnterpriseInfoListRequestListener listener) {
		EXECUTOR.execute(new EnterpriseInfoListCommand(serverURL, businessId,
				listener));
	}

	/**
	 * Interface definition for a callback to be invoked when a register request
	 * is executed.
	 * 
	 * @author Luo Yinzhuo
	 */
	public interface RegisterRequestListener {
		/**
		 * Called when the register request creation is failed.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onRegisterRequestFailed();

		/**
		 * Called when the register request execution is successful.
		 * 
		 * @param account
		 *            The account name has been registered successfully.
		 * @param password
		 *            The password followed by the account.
		 * @author Luo Yinzhuo
		 */
		public void onRegisterResponseSuccess(String account, String password);

		/**
		 * Called when the register request execution is failed.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onRegisterResponseFailed();

		/**
		 * Called when the register request's account name has already exist.
		 * 
		 * @param account
		 *            The account name already exist.
		 * @param errorMessage
		 *            The error message.
		 * @author Luo Yinzhuo
		 */
		public void onRegisterResponseAccountExist(String account,
				String errorMessage);

		/**
		 * Called when the register request execution encounters database error.
		 * 
		 * @param errorMessage
		 *            The error message.
		 * @author Luo Yinzhuo
		 */
		public void onRegisterResponseDatabaseError(String errorMessage);
	}

	/**
	 * Specified for execute register request.
	 * 
	 * @author Luo Yinzhuo
	 */
	private static class RegisterCommand implements Runnable {
		/** The server URL. */
		private final String mServerURL;
		/** The account name. */
		private final String mAccount;
		/** The password. */
		private final String mPassword;
		/** The phone number. */
		private final String mPhoneNum;
		/** The enterprise id. */
		private final int mEnterpriseId;
		/** The enterprise name. */
		private final String mEnterpriseName;
		/** The device token. */
		private final String mDeviceToken;
		/** The terminal type. */
		private final String mTerminalType;
		/** The user type id. */
		private final int mUserTypeId;
		/** The request listener. */
		private final RegisterRequestListener mListener;

		/**
		 * Construct a new instance.
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
		 * @param listener
		 *            The request listener.
		 */
		private RegisterCommand(String serverURL, String account,
				String password, String phoneNum, int enterpriseId,
				String enterpriseName, String deviceToken, String terminalType,
				int userTypeId, RegisterRequestListener listener) {
			mServerURL = serverURL;
			mAccount = account;
			mPassword = password;
			mPhoneNum = phoneNum;
			mEnterpriseId = enterpriseId;
			mEnterpriseName = enterpriseName;
			mDeviceToken = deviceToken;
			mTerminalType = terminalType;
			mUserTypeId = userTypeId;
			mListener = listener;
		}

		/** The key to get xCode. */
		private static final String KEY_XCODE = "xCode";
		/** The key to get error message. */
		private static final String KEY_XMSG = "xMsg";

		@Override
		public void run() {
			HttpPost request;
			try {
				request = RequestFactory.createRegisterRequest(mServerURL,
						mAccount, mPassword, mPhoneNum, mEnterpriseId,
						mEnterpriseName, mDeviceToken, mTerminalType,
						mUserTypeId);
			} catch (Exception e) {
				e.printStackTrace();
				mListener.onRegisterRequestFailed();
				return;
			}

			String content;
			try {
				HttpResponse response = HTTP_CLIENT.execute(request);
				content = NetworkService.getContent(response);
			} catch (IOException e) {
				e.printStackTrace();
				mListener.onRegisterResponseFailed();
				return;
			}

			try {
				JSONObject jsonResponse = new JSONObject(content);
				int xCode = jsonResponse.getInt(KEY_XCODE);
				switch (xCode) {
				case XCODE_SUCCESS:
					mListener.onRegisterResponseSuccess(mAccount, mPassword);
					return;
				case XCODE_ACCOUNT_EXIST:
					mListener.onRegisterResponseAccountExist(mAccount,
							jsonResponse.getString(KEY_XMSG));
					return;
				case XCODE_DATABASE_ERROR:
					mListener.onRegisterResponseDatabaseError(jsonResponse
							.getString(KEY_XMSG));
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.e("RegisterCommand", content);
			mListener.onRegisterResponseFailed();
		}
	}

	/**
	 * Register.
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
	 * @param listener
	 *            The request listener.
	 * @author Luo Yinzhuo
	 */
	public static void register(String serverURL, String account,
			String password, String phoneNum, int enterpriseId,
			String enterpriseName, String deviceToken, String terminalType,
			int userTypeId, RegisterRequestListener listener) {
		EXECUTOR.execute(new RegisterCommand(serverURL, account, password,
				phoneNum, enterpriseId, enterpriseName, deviceToken,
				terminalType, userTypeId, listener));
	}

	/**
	 * Interface definition for a callback to be invoked when a login request is
	 * executed.
	 * 
	 * @author Luo Yinzhuo
	 */
	public interface LoginRequestListener {
		/**
		 * Called when the login request creation is failed.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onLoginRequestFailed();

		/**
		 * Called when the login request execution is successful.
		 * 
		 * @param account
		 *            The account name has been logged in successfully.
		 * @param password
		 *            The password followed by the account.
		 * @author Luo Yinzhuo
		 */
		public void onLoginResponseSuccess(String account, String password);

		/**
		 * Called when the login request execution is failed.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onLoginResponseFailed();

		/**
		 * Called when the login request's account name not exist.
		 * 
		 * @param errorMessage
		 *            The error message.
		 * @author Luo Yinzhuo
		 */
		public void onLoginResponseAccountNotExist(String errorMessage);

		/**
		 * Called when the login request's account has been canceled.
		 * 
		 * @param errorMessage
		 *            The error message.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onLoginResponseAccountCanceled(String errorMessage);

		/**
		 * Called when the login request's account has been frozen.
		 * 
		 * @param errorMessage
		 *            The error message.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onLoginResponseAccountFrozen(String errorMessage);

		/**
		 * Called when the login request's account has not been activated.
		 * 
		 * @param errorMessage
		 *            The error message.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onLoginResponseAccountNotActivated(String errorMessage);

		/**
		 * Called when the login request's account and password not match.
		 * 
		 * @param errorMessage
		 *            The error message.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onLoginResponseAccountPasswordNotMatch(String errorMessage);

		/**
		 * Called when the login request execution encounters no data error.
		 * 
		 * @param errorMessage
		 *            The error message.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onLoginResponseNoDataError(String errorMessage);

		/**
		 * Called when the login request execution encounters database error.
		 * 
		 * @param errorMessage
		 *            The error message.
		 * @author Luo Yinzhuo
		 */
		public void onLoginResponseDatabaseError(String errorMessage);
	}

	/**
	 * Specified for execute login request.
	 * 
	 * @author Luo Yinzhuo
	 */
	private static class LoginCommand implements Runnable {
		/** The server URL. */
		private final String mServerURL;
		/** The account name. */
		private final String mAccount;
		/** The password. */
		private final String mPassword;
		/** The device token. */
		private final String mDeviceToken;
		/** The terminal type. */
		private final String mTerminalType;
		/** The request listener. */
		private final LoginRequestListener mListener;

		/**
		 * Construct a new instance.
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
		 * @param listener
		 *            The request listener.
		 */
		private LoginCommand(String serverURL, String account, String password,
				String deviceToken, String terminalType,
				LoginRequestListener listener) {
			mServerURL = serverURL;
			mAccount = account;
			mPassword = password;
			mDeviceToken = deviceToken;
			mTerminalType = terminalType;
			mListener = listener;
		}

		/** The key to get xCode. */
		private static final String KEY_XCODE = "xCode";
		/** The key to get error message. */
		private static final String KEY_XMSG = "xMsg";

		@Override
		public void run() {
			HttpPost request;
			try {
				request = RequestFactory.createLoginRequest(mServerURL,
						mAccount, mPassword, mDeviceToken, mTerminalType);
			} catch (Exception e) {
				e.printStackTrace();
				mListener.onLoginRequestFailed();
				return;
			}

			String content;
			try {
				HttpResponse response = HTTP_CLIENT.execute(request);
				content = NetworkService.getContent(response);
			} catch (IOException e) {
				e.printStackTrace();
				mListener.onLoginResponseFailed();
				return;
			}

			try {
				JSONObject jsonResponse = new JSONObject(content);
				int xCode = jsonResponse.getInt(KEY_XCODE);
				switch (xCode) {
				case XCODE_SUCCESS:
					mListener.onLoginResponseSuccess(mAccount, mPassword);
					return;
				case XCODE_ACCOUNT_NOT_EXIST:
					mListener.onLoginResponseAccountNotExist(jsonResponse
							.getString(KEY_XMSG));
					return;
				case XCODE_ACCOUNT_CANCELED:
					mListener.onLoginResponseAccountCanceled(jsonResponse
							.getString(KEY_XMSG));
					return;
				case XCODE_ACCOUNT_FROZEN:
					mListener.onLoginResponseAccountFrozen(jsonResponse
							.getString(KEY_XMSG));
					return;
				case XCODE_ACCOUNT_NOT_ACTIVATED:
					mListener.onLoginResponseAccountNotActivated(jsonResponse
							.getString(KEY_XMSG));
					return;
				case XCODE_ACCOUNT_PASSWORD_NOT_MATCH:
					mListener
							.onLoginResponseAccountPasswordNotMatch(jsonResponse
									.getString(KEY_XMSG));
					return;
				case XCODE_NO_DATA:
					mListener.onLoginResponseNoDataError(jsonResponse
							.getString(KEY_XMSG));
					return;
				case XCODE_DATABASE_ERROR:
					mListener.onLoginResponseDatabaseError(jsonResponse
							.getString(KEY_XMSG));
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.e("LoginCommand", content);
			mListener.onLoginResponseFailed();
		}
	}

	/**
	 * Login.
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
	 * @param listener
	 *            The request listener.
	 * @author Luo Yinzhuo
	 */
	public static void login(String serverURL, String account, String password,
			String deviceToken, String terminalType,
			LoginRequestListener listener) {
		EXECUTOR.execute(new LoginCommand(serverURL, account, password,
				deviceToken, terminalType, listener));
	}

	/**
	 * Interface definition for a callback to be invoked when a change password
	 * request is executed.
	 * 
	 * @author Luo Yinzhuo
	 */
	public interface ChangePasswordRequestListener {
		/**
		 * Called when the change password request creation is failed.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onChangePasswordRequestFailed();

		/**
		 * Called when the change password request execution is successful.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onChangePasswordResponseSuccess();

		/**
		 * Called when the change password request execution is failed.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onChangePasswordResponseFailed();

		/**
		 * Called when the change password request's old password incorrect.
		 * 
		 * @param errorMessage
		 *            The error message.
		 * @author Luo Yinzhuo
		 */
		public void onChangePasswordResponseOldPasswordIncorrect(
				String errorMessage);

		/**
		 * Called when the change password request's old password and new
		 * password same.
		 * 
		 * @param errorMessage
		 *            The error message.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onChangePasswordResponseOldPasswordNewPasswordSame(
				String errorMessage);

		/**
		 * Called when the change password request execution encounters database
		 * error.
		 * 
		 * @param errorMessage
		 *            The error message.
		 * @author Luo Yinzhuo
		 */
		public void onChangePasswordResponseDatabaseError(String errorMessage);
	}

	/**
	 * Specified for execute change password request.
	 * 
	 * @author Luo Yinzhuo
	 */
	private static class ChangePasswordCommand implements Runnable {
		/** The server URL. */
		private final String mServerURL;
		/** The account name. */
		private final String mAccount;
		/** The old password. */
		private final String mOldPassword;
		/** The new password. */
		private final String mNewPassword;
		/** The request listener. */
		private final ChangePasswordRequestListener mListener;

		/**
		 * Construct a new instance.
		 * 
		 * @param serverURL
		 *            The server URL.
		 * @param account
		 *            The account name.
		 * @param oldPassword
		 *            The old password.
		 * @param newPassword
		 *            The new password.
		 * @param listener
		 *            The request listener.
		 */
		private ChangePasswordCommand(String serverURL, String account,
				String oldPassword, String newPassword,
				ChangePasswordRequestListener listener) {
			mServerURL = serverURL;
			mAccount = account;
			mOldPassword = oldPassword;
			mNewPassword = newPassword;
			mListener = listener;
		}

		/** The key to get xCode. */
		private static final String KEY_XCODE = "xCode";
		/** The key to get error message. */
		private static final String KEY_XMSG = "xMsg";

		@Override
		public void run() {
			HttpPost request;
			try {
				request = RequestFactory.createChangePasswordRequest(
						mServerURL, mAccount, mOldPassword, mNewPassword);
			} catch (Exception e) {
				e.printStackTrace();
				mListener.onChangePasswordRequestFailed();
				return;
			}

			String content;
			try {
				HttpResponse response = HTTP_CLIENT.execute(request);
				content = NetworkService.getContent(response);
			} catch (IOException e) {
				e.printStackTrace();
				mListener.onChangePasswordResponseFailed();
				return;
			}

			try {
				JSONObject jsonResponse = new JSONObject(content);
				int xCode = jsonResponse.getInt(KEY_XCODE);
				switch (xCode) {
				case XCODE_SUCCESS:
					mListener.onChangePasswordResponseSuccess();
					return;
				case XCODE_OLD_PASSWORD_INCORRECT:
					mListener
							.onChangePasswordResponseOldPasswordIncorrect(jsonResponse
									.getString(KEY_XMSG));
					return;
				case XCODE_OLD_PASSWORD_NEW_PASSWORD_SAME:
					mListener
							.onChangePasswordResponseOldPasswordNewPasswordSame(jsonResponse
									.getString(KEY_XMSG));
					return;
				case XCODE_DATABASE_ERROR:
					mListener
							.onChangePasswordResponseDatabaseError(jsonResponse
									.getString(KEY_XMSG));
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.e("ChangePasswordCommand", content);
			mListener.onChangePasswordResponseFailed();
		}
	}

	/**
	 * Change password.
	 * 
	 * @param serverURL
	 *            The server URL.
	 * @param account
	 *            The account name.
	 * @param oldPassword
	 *            The old password.
	 * @param newPassword
	 *            The new password.
	 * @param listener
	 *            The request listener.
	 * @author Luo Yinzhuo
	 */
	public static void changePassword(String serverURL, String account,
			String oldPassword, String newPassword,
			ChangePasswordRequestListener listener) {
		EXECUTOR.execute(new ChangePasswordCommand(serverURL, account,
				oldPassword, newPassword, listener));
	}

	/**
	 * Interface definition for a callback to be invoked when a user type info
	 * list request is executed.
	 * 
	 * @author Luo Yinzhuo
	 */
	public interface UserTypeInfoListRequestListener {
		/**
		 * Called when the user type info list request creation is failed.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onUserTypeInfoListRequestFailed();

		/**
		 * Called when the user type info list request execution is successful.
		 * 
		 * @param userTypeInfos
		 *            The list of {@link UserTypeInfo} from server.
		 * @author Luo Yinzhuo
		 */
		public void onUserTypeInfoListResponseSuccess(
				List<UserTypeInfo> userTypeInfos);

		/**
		 * Called when the user type info list request execution is failed.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onUserTypeInfoListResponseFailed();
	}

	/**
	 * Specified for execute user type info list request.
	 * 
	 * @author Luo Yinzhuo
	 */
	private static class UserTypeInfoListCommand implements Runnable {
		/** The server URL. */
		private final String mServerURL;
		/** The request listener. */
		private final UserTypeInfoListRequestListener mListener;

		/**
		 * Construct a new instance.
		 * 
		 * @param serverURL
		 *            The server URL.
		 * @param listener
		 *            The request listener.
		 */
		private UserTypeInfoListCommand(String serverURL,
				UserTypeInfoListRequestListener listener) {
			mServerURL = serverURL;
			mListener = listener;
		}

		/** The key to get xCode. */
		private static final String KEY_XCODE = "xCode";
		/** The key to get xData. */
		private static final String KEY_XDATA = "xData";

		@Override
		public void run() {
			HttpPost request;
			try {
				request = RequestFactory
						.createUserTypeInfoListRequest(mServerURL);
			} catch (Exception e) {
				e.printStackTrace();
				mListener.onUserTypeInfoListRequestFailed();
				return;
			}

			String content;
			try {
				HttpResponse response = HTTP_CLIENT.execute(request);
				content = NetworkService.getContent(response);
			} catch (IOException e) {
				e.printStackTrace();
				mListener.onUserTypeInfoListResponseFailed();
				return;
			}

			try {
				JSONObject jsonResponse = new JSONObject(content);
				if (jsonResponse.getInt(KEY_XCODE) == 0) {
					JSONArray jsonUserTypeInfo = jsonResponse
							.getJSONArray(KEY_XDATA);
					List<UserTypeInfo> userTypeInfos = new ArrayList<UserTypeInfo>();
					for (int i = 0; i < jsonUserTypeInfo.length(); i++) {
						JSONObject userTypeInfo = jsonUserTypeInfo
								.getJSONObject(i);
						if (UserTypeInfo.isUserTypeInfo(userTypeInfo)) {
							userTypeInfos.add(UserTypeInfo.parse(userTypeInfo));
						}
					}
					mListener.onUserTypeInfoListResponseSuccess(userTypeInfos);
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.e("UserTypeInfoListCommand", content);
			mListener.onUserTypeInfoListResponseFailed();
		}
	}

	/**
	 * Get the user type info list.
	 * 
	 * @param serverURL
	 *            The server URL.
	 * @param listener
	 *            The request listener.
	 * @author Luo Yinzhuo
	 */
	public static void getUserTypeInfoList(String serverURL,
			UserTypeInfoListRequestListener listener) {
		EXECUTOR.execute(new UserTypeInfoListCommand(serverURL, listener));
	}

	/**
	 * Interface definition for a callback to be invoked when a column info list
	 * request is executed.
	 * 
	 * @author Luo Yinzhuo
	 */
	public interface ColumnInfoListRequestListener {

		/**
		 * Called when the column info list request creation is failed.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onColumnInfoListRequestFailed();

		/**
		 * Called when the column info list request execution is successful.
		 * 
		 * @param columnInfos
		 *            The list of {@link ColumnInfo} from server.
		 * @author Luo Yinzhuo
		 */
		public void onColumnInfoListResponseSuccess(List<ColumnInfo> columnInfos);

		/**
		 * Called when the column info list request execution is failed.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onColumnInfoListResponseFailed();
	}

	/**
	 * Specified for execute column info list request.
	 * 
	 * @author Luo Yinzhuo
	 */
	private static class ColumnInfoListCommand implements Runnable {
		/** The server URL. */
		private final String mServerURL;
		/** The account name. */
		private final String mAccount;
		/** The request listener. */
		private final ColumnInfoListRequestListener mListener;

		/**
		 * Construct a new instance.
		 * 
		 * @param serverURL
		 *            The server URL.
		 * @param account
		 *            The account name.
		 * @param listener
		 *            The request listener.
		 */
		private ColumnInfoListCommand(String serverURL, String account,
				ColumnInfoListRequestListener listener) {
			mServerURL = serverURL;
			mAccount = account;
			mListener = listener;
		}

		/** The key to get xCode. */
		private static final String KEY_XCODE = "xCode";
		/** The key to get xData. */
		private static final String KEY_XDATA = "xData";

		@Override
		public void run() {
			HttpPost request;
			try {
				request = RequestFactory.createColumnInfoListRequest(
						mServerURL, mAccount);
			} catch (Exception e) {
				e.printStackTrace();
				mListener.onColumnInfoListRequestFailed();
				return;
			}

			String content;
			try {
				HttpResponse response = HTTP_CLIENT.execute(request);
				content = NetworkService.getContent(response);
			} catch (IOException e) {
				e.printStackTrace();
				mListener.onColumnInfoListResponseFailed();
				return;
			}

			try {
				JSONObject jsonResponse = new JSONObject(content);
				if (jsonResponse.getInt(KEY_XCODE) == 0) {
					JSONArray jsonColumnInfo = jsonResponse
							.getJSONArray(KEY_XDATA);
					List<ColumnInfo> columnInfos = new ArrayList<ColumnInfo>();
					for (int i = 0; i < jsonColumnInfo.length(); i++) {
						JSONObject columnInfo = jsonColumnInfo.getJSONObject(i);
						if (ColumnInfo.isColumnInfo(columnInfo)) {
							columnInfos.add(ColumnInfo.parse(columnInfo));
						}
					}
					mListener.onColumnInfoListResponseSuccess(columnInfos);
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.e("ColumnInfoListCommand", content);
			mListener.onColumnInfoListResponseFailed();
		}
	}

	/**
	 * Get the whole column info list.
	 * 
	 * @param serverURL
	 *            The server URL.
	 * @param account
	 *            The account name.
	 * @param listener
	 *            The request listener.
	 * @author Luo Yinzhuo
	 */
	public static void getColumnInfoList(String serverURL, String account,
			ColumnInfoListRequestListener listener) {
		EXECUTOR.execute(new ColumnInfoListCommand(serverURL, account, listener));
	}

	/**
	 * Interface definition for a callback to be invoked when a article list
	 * request is executed.
	 * 
	 * @author Luo Yinzhuo
	 */
	public interface NewsListRequestListener {
		/**
		 * Called when the news list request creation is failed.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onNewsListRequestFailed();

		/**
		 * Called when the news list request execution is successful.
		 * 
		 * @param columnInfos
		 *            The list of {@link NewsInfo} from server.
		 * @param childColumnInfos
		 *            The list of child {@link ColumnInfo} from server.
		 * @author Luo Yinzhuo
		 */
		public void onNewsListResponseSuccess(List<NewsInfo> newsInfos,
				List<ColumnInfo> childColumnInfos);

		/**
		 * Called when the news list request execution is failed.
		 * 
		 * @param columnID
		 *            The request column ID.
		 * @author Luo Yinzhuo
		 */
		public void onNewsListResponseFailed(int columnID);
	}

	/**
	 * Specified for execute news list request.
	 * 
	 * @author Luo Yinzhuo
	 */
	private static class NewsListCommand implements Runnable {
		/** The server URL. */
		private final String mServerURL;
		/** The column ID. */
		private final int mColumnID;
		/** The request listener. */
		private final NewsListRequestListener mListener;

		/**
		 * Construct a new instance.
		 * 
		 * @param serverURL
		 *            The server URL.
		 * @param columnID
		 *            The column ID.
		 * @param listener
		 *            The request listener.
		 */
		private NewsListCommand(String serverURL, int columnID,
				NewsListRequestListener listener) {
			mServerURL = serverURL;
			mColumnID = columnID;
			mListener = listener;
		}

		/** The key to get xCode. */
		private static final String KEY_XCODE = "xCode";
		/** The key to get childColumns. */
		private static final String KEY_CHILD_COLUMNS = "childColumns";
		/** The key to get xData. */
		private static final String KEY_XDATA = "xData";

		@Override
		public void run() {
			HttpPost request;
			try {
				request = RequestFactory.createNewsInfoListRequest(mServerURL,
						mColumnID);
			} catch (Exception e) {
				e.printStackTrace();
				mListener.onNewsListRequestFailed();
				return;
			}

			String content;
			try {
				HttpResponse response = HTTP_CLIENT.execute(request);
				content = NetworkService.getContent(response);
			} catch (IOException e) {
				e.printStackTrace();
				mListener.onNewsListResponseFailed(mColumnID);
				return;
			}

			try {
				JSONObject jsonResponse = new JSONObject(content);
				if (jsonResponse.getInt(KEY_XCODE) == 0) {
					JSONArray jsonNewsInfo = jsonResponse
							.getJSONArray(KEY_XDATA);
					List<NewsInfo> newsInfos = new ArrayList<NewsInfo>();
					for (int i = 0; i < jsonNewsInfo.length(); i++) {
						JSONObject newsInfo = jsonNewsInfo.getJSONObject(i);
						if (NewsInfo.isNewsInfo(newsInfo)) {
							newsInfos.add(NewsInfo.parse(newsInfo));
						}
					}

					List<ColumnInfo> childColumnInfos = new ArrayList<ColumnInfo>();
					if (jsonResponse.has(KEY_CHILD_COLUMNS)) {
						JSONArray jsonChildColumnInfo = jsonResponse
								.getJSONArray(KEY_CHILD_COLUMNS);
						for (int i = 0; i < jsonChildColumnInfo.length(); i++) {
							JSONObject childColumnInfo = jsonChildColumnInfo
									.getJSONObject(i);
							if (ColumnInfo.isColumnInfo(childColumnInfo)) {
								childColumnInfos.add(ColumnInfo
										.parse(childColumnInfo));
							}
						}
					}
					mListener.onNewsListResponseSuccess(newsInfos,
							childColumnInfos);
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.e("NewsListCommand", content);
			mListener.onNewsListResponseFailed(mColumnID);
		}
	}

	/**
	 * Get the news list.
	 * 
	 * @param serverURL
	 *            The server URL.
	 * @param columnID
	 *            The column's ID.
	 * @param listener
	 *            The request listener.
	 * @author Luo Yinzhuo
	 */
	public static void getNewsList(String serverURL, int columnID,
			NewsListRequestListener listener) {
		EXECUTOR.execute(new NewsListCommand(serverURL, columnID, listener));
	}

	/**
	 * Interface definition for a callback to be invoked when a save subscribe
	 * info list request is executed.
	 * 
	 * @author Luo Yinzhuo
	 */
	public interface SaveSubscribeInfoListRequestListener {

		/**
		 * Called when the save subscribe info list request creation is failed.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onSaveSubscribeInfoListRequestFailed();

		/**
		 * Called when the save subscribe info list request execution is
		 * successful.
		 * 
		 * @param subscribeIds
		 *            The subscribe info id list.
		 * @author Luo Yinzhuo
		 */
		public void onSaveSubscribeInfoListResponseSuccess(
				List<Integer> subscribeIds);

		/**
		 * Called when the save subscribe info list request execution is failed.
		 * 
		 * @param subscribeIds
		 *            The subscribe info id list.
		 * @author Luo Yinzhuo
		 */
		public void onSaveSubscribeInfoListResponseFailed(
				List<Integer> subscribeIds);
	}

	/**
	 * Specified for execute save subscribe info list request.
	 * 
	 * @author Luo Yinzhuo
	 */
	private static class SaveSubscribeInfoListCommand implements Runnable {
		/** The server URL. */
		private final String mServerURL;
		/** The account. */
		private final String mAccount;
		/** The subscribe info id list. */
		private final List<Integer> mSubscribeIds = new ArrayList<Integer>();
		/** The request listener. */
		private final SaveSubscribeInfoListRequestListener mListener;

		/**
		 * Construct a new instance.
		 * 
		 * @param serverURL
		 *            The server URL.
		 * @param account
		 *            The account.
		 * @param subscribeIds
		 *            The subscribe info id list.
		 * @param listener
		 *            The request listener.
		 */
		private SaveSubscribeInfoListCommand(String serverURL, String account,
				List<Integer> subscribeIds,
				SaveSubscribeInfoListRequestListener listener) {
			mServerURL = serverURL;
			mAccount = account;
			mSubscribeIds.addAll(subscribeIds);
			mListener = listener;
		}

		/** The key to get xCode. */
		private static final String KEY_XCODE = "xCode";

		@Override
		public void run() {
			HttpPost request;
			try {
				request = RequestFactory.createSaveSubscribeInfoListRequest(
						mServerURL, mAccount, mSubscribeIds);
			} catch (Exception e) {
				e.printStackTrace();
				mListener.onSaveSubscribeInfoListRequestFailed();
				return;
			}

			String content;
			try {
				HttpResponse response = HTTP_CLIENT.execute(request);
				content = NetworkService.getContent(response);
			} catch (IOException e) {
				e.printStackTrace();
				mListener.onSaveSubscribeInfoListResponseFailed(mSubscribeIds);
				return;
			}

			try {
				JSONObject jsonResponse = new JSONObject(content);
				int xCode = jsonResponse.getInt(KEY_XCODE);
				switch (xCode) {
				case XCODE_SUCCESS:
					mListener
							.onSaveSubscribeInfoListResponseSuccess(mSubscribeIds);
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.e("SearchSubscribeColumnInfoListCommand", content);
			mListener.onSaveSubscribeInfoListResponseFailed(mSubscribeIds);
		}
	}

	/**
	 * Save the account's subscribe info list.
	 * 
	 * @param serverURL
	 *            The server URL.
	 * @param account
	 *            The account name.
	 * @param subscribeIds
	 *            The subscribe info id list.
	 * @param listener
	 *            The request listener.
	 * @author Luo Yinzhuo
	 */
	public static void saveSubscribeInfoList(String serverURL, String account,
			List<Integer> subscribeIds,
			SaveSubscribeInfoListRequestListener listener) {
		EXECUTOR.execute(new SaveSubscribeInfoListCommand(serverURL, account,
				subscribeIds, listener));
	}

	/**
	 * Interface definition for a callback to be invoked when a search subscribe
	 * info list request is executed.
	 * 
	 * @author Luo Yinzhuo
	 */
	public interface SearchSubscribeInfoListRequestListener {

		/**
		 * Called when the search subscribe info list request creation is
		 * failed.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onSearchSubscribeInfoListRequestFailed();

		/**
		 * Called when the search subscribe info list request execution is
		 * successful.
		 * 
		 * @param subscribeInfos
		 *            The list of {@link SubscribeInfo} from server.
		 * @author Luo Yinzhuo
		 */
		public void onSearchSubscribeInfoListResponseSuccess(
				List<SubscribeInfo> subscribeInfos);

		/**
		 * Called when the search subscribe column info list request execution
		 * is failed.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onSearchSubscribeInfoListResponseFailed();
	}

	/**
	 * Specified for execute search subscribe list request.
	 * 
	 * @author Luo Yinzhuo
	 */
	private static class SearchSubscribeInfoListCommand implements Runnable {
		/** The server URL. */
		private final String mServerURL;
		/** The account. */
		private final String mAccount;
		/** The request listener. */
		private final SearchSubscribeInfoListRequestListener mListener;

		/**
		 * Construct a new instance.
		 * 
		 * @param serverURL
		 *            The server URL.
		 * @param account
		 *            The account.
		 * @param listener
		 *            The request listener.
		 */
		private SearchSubscribeInfoListCommand(String serverURL,
				String account, SearchSubscribeInfoListRequestListener listener) {
			mServerURL = serverURL;
			mAccount = account;
			mListener = listener;
		}

		/** The key to get xCode. */
		private static final String KEY_XCODE = "xCode";
		/** The key to get xData. */
		private static final String KEY_XDATA = "xData";

		@Override
		public void run() {
			HttpPost request;
			try {
				request = RequestFactory.createSearchSubscribeInfoListRequest(
						mServerURL, mAccount);
			} catch (Exception e) {
				e.printStackTrace();
				mListener.onSearchSubscribeInfoListRequestFailed();
				return;
			}

			String content;
			try {
				HttpResponse response = HTTP_CLIENT.execute(request);
				content = NetworkService.getContent(response);
			} catch (IOException e) {
				e.printStackTrace();
				mListener.onSearchSubscribeInfoListResponseFailed();
				return;
			}

			try {
				JSONObject jsonResponse = new JSONObject(content);
				int xCode = jsonResponse.getInt(KEY_XCODE);
				switch (xCode) {
				case XCODE_SUCCESS:
					JSONArray jsonSubscribeColumnInfo = jsonResponse
							.getJSONArray(KEY_XDATA);
					List<SubscribeInfo> subscribeColumnInfos = new ArrayList<SubscribeInfo>();
					for (int i = 0; i < jsonSubscribeColumnInfo.length(); i++) {
						JSONObject subscribeColumnInfo = jsonSubscribeColumnInfo
								.getJSONObject(i);
						if (SubscribeInfo
								.isSubscribeColumnInfo(subscribeColumnInfo)) {
							subscribeColumnInfos.add(SubscribeInfo
									.parse(subscribeColumnInfo));
						}
					}
					mListener
							.onSearchSubscribeInfoListResponseSuccess(subscribeColumnInfos);
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.e("SearchSubscribeColumnInfoListCommand", content);
			mListener.onSearchSubscribeInfoListResponseFailed();
		}
	}

	/**
	 * Search the account's subscribe info list.
	 * 
	 * @param serverURL
	 *            The server URL.
	 * @param account
	 *            The account name.
	 * @param listener
	 *            The request listener.
	 * @author Luo Yinzhuo
	 */
	public static void searchSubscribeInfoList(String serverURL,
			String account, SearchSubscribeInfoListRequestListener listener) {
		EXECUTOR.execute(new SearchSubscribeInfoListCommand(serverURL, account,
				listener));
	}

	/**
	 * Interface definition for a callback to be invoked when a suggestion
	 * request is executed.
	 * 
	 * @author Luo Yinzhuo
	 */
	public interface SuggestionRequestListener {

		/**
		 * Called when the suggestion request creation is failed.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onSuggestionRequestFailed();

		/**
		 * Called when the suggestion request execution is successful.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onSuggestionResponseSuccess();

		/**
		 * Called when the suggestion request execution is failed.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void onSuggestionResponseFailed();
	}

	/**
	 * Specified for execute suggestion request.
	 * 
	 * @author Luo Yinzhuo
	 */
	private static class SuggestionCommand implements Runnable {
		/** The server URL. */
		private final String mServerURL;
		/** The account. */
		private final String mAccount;
		/** The contact. */
		private final String mContact;
		/** The content. */
		private final String mContent;
		/** The request listener. */
		private final SuggestionRequestListener mListener;

		/**
		 * Construct a new instance.
		 * 
		 * @param serverURL
		 *            The server URL.
		 * @param account
		 *            The account.
		 * @param contact
		 *            The contact information.
		 * @param content
		 *            The suggestion content.
		 * @param listener
		 *            The request listener.
		 */
		private SuggestionCommand(String serverURL, String account,
				String contact, String content,
				SuggestionRequestListener listener) {
			mServerURL = serverURL;
			mAccount = account;
			mContact = contact;
			mContent = content;
			mListener = listener;
		}

		/** The key to get xCode. */
		private static final String KEY_XCODE = "xCode";

		@Override
		public void run() {
			HttpPost request;
			try {
				request = RequestFactory.createSuggestionRequest(mServerURL,
						mAccount, mContact, mContent);
			} catch (Exception e) {
				e.printStackTrace();
				mListener.onSuggestionRequestFailed();
				return;
			}

			String content;
			try {
				HttpResponse response = HTTP_CLIENT.execute(request);
				content = NetworkService.getContent(response);
			} catch (IOException e) {
				e.printStackTrace();
				mListener.onSuggestionRequestFailed();
				return;
			}

			try {
				JSONObject jsonResponse = new JSONObject(content);
				int xCode = jsonResponse.getInt(KEY_XCODE);
				switch (xCode) {
				case XCODE_SUCCESS:
					mListener.onSuggestionResponseSuccess();
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.e("SuggestionCommand", content);
			mListener.onSuggestionResponseFailed();
		}
	}

	/**
	 * Suggestion.
	 * 
	 * @param serverURL
	 *            The server URL.
	 * @param account
	 *            The account name.
	 * @param contact
	 *            The contact information.
	 * @param content
	 *            The suggestion content.
	 * @param listener
	 *            The request listener.
	 * @author Luo Yinzhuo
	 */
	public static void suggestion(String serverURL, String account,
			String contact, String content, SuggestionRequestListener listener) {
		EXECUTOR.execute(new SuggestionCommand(serverURL, account, contact,
				content, listener));
	}

	/** The image LRU cache. */
	private static final Map<String, Bitmap> BITMAP_LRU_CACHE = new LinkedHashMap<String, Bitmap>(
			8, 0.75f, true) {
		/** The serial id. */
		private static final long serialVersionUID = 4274009940420181912L;

		@Override
		protected boolean removeEldestEntry(Entry<String, Bitmap> eldest) {
			if (size() >= 6) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public Bitmap remove(Object key) {
			Bitmap bitmap = super.remove(key);
			synchronized (bitmap) {
				bitmap.recycle();
			}
			return bitmap;
		}
	};

	/**
	 * Used to record the accepted image request to prevent re-accept same image
	 * request.
	 */
	private static final Set<String> IMAGE_REQUEST_SET = new HashSet<String>();

	/**
	 * Interface definition for a callback to be invoked when a news image
	 * request is executed.
	 * 
	 * @author Luo Yinzhuo
	 */
	public interface NewsImageRequestListener {
		/**
		 * Called when the news image request execution is successful.
		 * 
		 * @param page
		 *            The {@link NewsPage} index.
		 * @param bitmap
		 *            The image's bitmap.
		 * @author Luo Yinzhuo
		 */
		public void onNewsImageResponseSuccess(int page);

		/**
		 * Called when the image request execution is failed.
		 * 
		 * @param page
		 *            The {@link NewsPage} index.
		 * @param imageURL
		 *            The image URL.
		 * @author Luo Yinzhuo
		 */
		public void onNewsImageResponseFailed(int page, String imageURL);
	}

	/**
	 * Specified for execute news image request.
	 * 
	 * @author Luo Yinzhuo
	 */
	private static class NewsImageCommand implements Runnable {
		/** The {@link NewsPage} index. */
		private final int mPage;
		/** The image URL. */
		private final String mImageURL;
		/** The request listener. */
		private final NewsImageRequestListener mListener;

		/**
		 * Construct a new instance.
		 * 
		 * @param page
		 *            The {@link NewsPage} index.
		 * @param imageURL
		 *            The image URL.
		 * @param listener
		 *            The request listener.
		 */
		private NewsImageCommand(int page, String imageURL,
				NewsImageRequestListener listener) {
			mPage = page;
			mImageURL = imageURL;
			mListener = listener;
		}

		@Override
		public void run() {
			HttpGet request = new HttpGet(mImageURL);

			try {
				HttpResponse response = HTTP_CLIENT.execute(request);
				BufferedInputStream bufferedInputStream = new BufferedInputStream(
						response.getEntity().getContent());

				ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer(1024);
				int current = bufferedInputStream.read();
				while (current != -1) {
					byteArrayBuffer.append(current);
					current = bufferedInputStream.read();
				}

				Bitmap bitmap = BitmapFactory.decodeByteArray(
						byteArrayBuffer.toByteArray(), 0,
						byteArrayBuffer.length());
				if (bitmap != null) {
					saveImageToExternalStorage(mImageURL,
							byteArrayBuffer.toByteArray());

					BITMAP_LRU_CACHE.put(mImageURL, bitmap);
					mListener.onNewsImageResponseSuccess(mPage);
				} else {
					mListener.onNewsImageResponseFailed(mPage, mImageURL);
				}
			} catch (Exception e) {
				e.printStackTrace();
				mListener.onNewsImageResponseFailed(mPage, mImageURL);
				return;
			} finally {
				IMAGE_REQUEST_SET.remove(mImageURL);
			}
		}
	}

	/** The external storage folder to store downloaded images. */
	private static final String EXTERNAL_STORAGE_FOLDER = "Shijingshan";

	private static void saveImageToExternalStorage(String imageURL, byte[] data) {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File externalStorage = new File(
					Environment.getExternalStorageDirectory(),
					EXTERNAL_STORAGE_FOLDER);
			
			if (!externalStorage.exists()) {
				externalStorage.mkdirs();
			}
			
			
		}
	}

	/**
	 * Get the news image.
	 * 
	 * @param page
	 *            The {@link NewsPage} index.
	 * @param imageURL
	 *            The image URL.
	 * @param listener
	 *            The request listener.
	 * @return The bitmap requested if exist in the cache, otherwise null.
	 * @author Luo Yinzhuo
	 */
	public static Bitmap getNewsImage(int page, String imageURL,
			NewsImageRequestListener listener) {
		Bitmap bitmap = BITMAP_LRU_CACHE.get(imageURL);
		if (bitmap == null && !IMAGE_REQUEST_SET.contains(imageURL)) {
			IMAGE_REQUEST_SET.add(imageURL);
			EXECUTOR.execute(new NewsImageCommand(page, imageURL, listener));
		}
		return bitmap;
	}
}
