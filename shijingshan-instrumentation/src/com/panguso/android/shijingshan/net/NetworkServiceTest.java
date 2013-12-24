package com.panguso.android.shijingshan.net;

import java.util.List;

import com.panguso.android.shijingshan.column.ColumnInfo;
import com.panguso.android.shijingshan.net.NetworkService.ColumnInfoListRequestListener;
import com.panguso.android.shijingshan.net.NetworkService.NewsListRequestListener;
import com.panguso.android.shijingshan.news.NewsInfo;

import android.test.AndroidTestCase;

public class NetworkServiceTest extends AndroidTestCase {
	/** The server url. */
	private static final String SERVER_URL = "http://tsinghuacims.oicp.net:45476/sjs/JsonAction";

	/**
	 * Test
	 * {@link NetworkService#getColumnInfoList(String, String, ColumnInfoListRequestListener)}
	 * 
	 * @author Luo Yinzhuo
	 */
	public void testGetColumnInfoList() {
		/** The UUID. */
		final String UUID = "ffffffff-aa13-3f0f-ffff-ffffd0fe3dcb";
		/** The lock to synchronize. */
		final Object LOCK = new Object();
		NetworkService.getColumnInfoList(SERVER_URL, UUID, new ColumnInfoListRequestListener() {

			@Override
			public void onColumnInfoListResponseSuccess(List<ColumnInfo> columnInfos) {
				assertNotNull("Column info is empty!", columnInfos);
				assertTrue("Column info is empty!", columnInfos.size() > 0);
				// Let main thread finish.
				synchronized (LOCK) {
					LOCK.notify();
				}
			}

			@Override
			public void onColumnInfoListResponseFailed() {
				assertTrue("Get Column Info List Failed!", false);
				// Let main thread finish.
				synchronized (LOCK) {
					LOCK.notify();
				}
			}
		});

		// Wait for the executor thread finish job.
		synchronized (LOCK) {
			try {
				LOCK.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

/**
	 * Test {@link NetworkService#getNewsList(String, String, NewsListRequestListener)
	 * 
	 * @author Luo Yinzhuo
	 */
	public void testGetArticleList() {
		/** The column ID. */
		final String COLUMN_ID = "100";
		/** The lock to synchronize. */
		final Object LOCK = new Object();
		NetworkService.getNewsList(SERVER_URL, COLUMN_ID,  false, new NewsListRequestListener() {

			@Override
			public void onNewsListResponseSuccess(List<NewsInfo> newsInfos) {
				assertNotNull("News info is empty!", newsInfos);
				assertTrue("News info is empty!", newsInfos.size() > 0);
				// Let main thread finish.
				synchronized (LOCK) {
					LOCK.notify();
				}
			}

			@Override
			public void onNewsListResponseFailed() {
				assertTrue("Get News Info List Failed!", false);
				// Let main thread finish.
				synchronized (LOCK) {
					LOCK.notify();
				}
			}
		});

		// Wait for the executor thread finish job.
		synchronized (LOCK) {
			try {
				LOCK.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
