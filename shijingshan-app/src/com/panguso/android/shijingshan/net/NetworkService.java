package com.panguso.android.shijingshan.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.panguso.android.shijingshan.column.ColumnInfo;
import com.panguso.android.shijingshan.news.NewsInfo;

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
	private static String getContent(HttpResponse response)
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
	 * Specified for execute column list request.
	 * 
	 * @author Luo Yinzhuo
	 */
	private static class ColumnInfoListCommand implements Runnable {
		/** The server URL. */
		private final String mServerURL;
		/** The device's UUID. */
		private final String mUUID;
		/** The request listener. */
		private final ColumnInfoListRequestListener mListener;

		/**
		 * Construct a new instance.
		 * 
		 * @param serverURL
		 *            The server URL.
		 * @param UUID
		 *            The device's UUID.
		 * @param listener
		 *            The request listener.
		 */
		private ColumnInfoListCommand(String serverURL, String UUID,
				ColumnInfoListRequestListener listener) {
			mServerURL = serverURL;
			mUUID = UUID;
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
				request = RequestFactory.createColumnListRequest(mServerURL,
						mUUID);
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
						columnInfos.add(ColumnInfo.parse(jsonColumnInfo
								.getJSONObject(i)));
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
	 * @param UUID
	 *            The device's UUID.
	 * @param listener
	 *            The request listener.
	 * @author Luo Yinzhuo
	 */
	public static void getColumnInfoList(String serverURL, String UUID,
			ColumnInfoListRequestListener listener) {
		EXECUTOR.execute(new ColumnInfoListCommand(serverURL, UUID, listener));
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
		 * @author Luo Yinzhuo
		 */
		public void onNewsListResponseFailed();
	}

	/**
	 * Specified for execute article list request.
	 * 
	 * @author Luo Yinzhuo
	 */
	private static class NewsListCommand implements Runnable {
		/** The server URL. */
		private final String mServerURL;
		/** The column ID. */
		private final String mColumnID;
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
		private NewsListCommand(String serverURL, String columnID,
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
				request = RequestFactory.createNewsListRequest(mServerURL,
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
				mListener.onNewsListResponseFailed();
				return;
			}

			try {
				JSONObject jsonResponse = new JSONObject(content);
				if (jsonResponse.getInt(KEY_XCODE) == 0) {
					JSONArray jsonNewsInfo = jsonResponse
							.getJSONArray(KEY_XDATA);
					List<NewsInfo> newsInfos = new ArrayList<NewsInfo>();
					for (int i = 0; i < jsonNewsInfo.length(); i++) {
						newsInfos.add(NewsInfo.parse(jsonNewsInfo
								.getJSONObject(i)));
					}

					List<ColumnInfo> childColumnInfos = new ArrayList<ColumnInfo>();
					if (jsonResponse.has(KEY_CHILD_COLUMNS)) {
						JSONArray jsonChildColumnInfo = jsonResponse
								.getJSONArray(KEY_CHILD_COLUMNS);
						for (int i = 0; i < jsonChildColumnInfo.length(); i++) {
							childColumnInfos
									.add(ColumnInfo.parse(jsonChildColumnInfo
											.getJSONObject(i)));
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
			mListener.onNewsListResponseFailed();
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
	public static void getNewsList(String serverURL, String columnID,
			NewsListRequestListener listener) {
		EXECUTOR.execute(new NewsListCommand(serverURL, columnID, listener));
	}
}
