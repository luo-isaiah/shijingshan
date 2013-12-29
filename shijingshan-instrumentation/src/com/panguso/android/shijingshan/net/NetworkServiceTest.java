package com.panguso.android.shijingshan.net;

import java.util.List;

import junit.framework.Assert;

import org.json.JSONArray;

import com.panguso.android.shijingshan.column.ColumnInfo;
import com.panguso.android.shijingshan.net.NetworkService.ColumnInfoListRequestListener;
import com.panguso.android.shijingshan.net.NetworkService.ImageRequestListener;
import com.panguso.android.shijingshan.net.NetworkService.NewsListRequestListener;
import com.panguso.android.shijingshan.news.NewsInfo;

import android.graphics.Bitmap;
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
			public void onColumnInfoListRequestFailed() {
				assertTrue("Create Column Info List Request Failed!", false);
				// Let main thread finish.
				synchronized (LOCK) {
					LOCK.notify();
				}
			}

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
	public void testGetNewsList() {
		/** The column ID. */
		final String COLUMN_ID = "100";
		/** The lock to synchronize. */
		final Object LOCK = new Object();
		NetworkService.getNewsList(SERVER_URL, COLUMN_ID, new NewsListRequestListener() {

			@Override
			public void onNewsListRequestFailed() {
				assertTrue("Create News Info List Request Failed!", false);
				// Let main thread finish.
				synchronized (LOCK) {
					LOCK.notify();
				}
			}

			@Override
			public void onNewsListResponseSuccess(List<NewsInfo> newsInfos,
			        List<ColumnInfo> childColumnInfos) {
				assertNotNull("News info is empty!", newsInfos);
				assertTrue("News info is empty!", newsInfos.size() > 0);
				// Let main thread finish.
				synchronized (LOCK) {
					LOCK.notify();
				}
			}

			@Override
			public void onNewsListResponseFailed(String columnID) {
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
	
	/**
	 * Test {@link NetworkService#getImage(String, ImageRequestListener)
	 * 
	 * @author Luo Yinzhuo
	 */
	public void testGetImage() {
		/** The image URL. */
		final String IMAGE_URL = "http://tsinghuacims.oicp.net:45476/sjs//html/100/2013/10/2013_10_7177.png";
		/** The lock to synchronize. */
		final Object LOCK = new Object();
		NetworkService.getImage(IMAGE_URL, new ImageRequestListener() {

			@Override
			public void onImageResponseSuccess(Bitmap bitmap) {
				assertNotNull("Bitmap is null!", bitmap);
				assertEquals("Bitmap width error!", 638, bitmap.getWidth());
				assertEquals("Bitmap height error!", 162, bitmap.getHeight());
				// Let main thread finish.
				synchronized (LOCK) {
					LOCK.notify();
				}
			}

			@Override
			public void onImageResponseFailed() {
				assertTrue("Get Image Failed!", false);
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
