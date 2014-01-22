package com.panguso.android.shijingshan.net;

import java.util.List;

import com.panguso.android.shijingshan.column.ColumnInfo;
import com.panguso.android.shijingshan.net.NetworkService.BusinessInfoListRequestListener;
import com.panguso.android.shijingshan.net.NetworkService.ColumnInfoListRequestListener;
import com.panguso.android.shijingshan.net.NetworkService.EnterpriseInfoListRequestListener;
import com.panguso.android.shijingshan.net.NetworkService.ImageRequestListener;
import com.panguso.android.shijingshan.net.NetworkService.NewsListRequestListener;
import com.panguso.android.shijingshan.net.NetworkService.RegisterRequestListener;
import com.panguso.android.shijingshan.net.NetworkService.UserTypeInfoListRequestListener;
import com.panguso.android.shijingshan.news.NewsInfo;
import com.panguso.android.shijingshan.register.business.BusinessInfo;
import com.panguso.android.shijingshan.register.enterprise.EnterpriseInfo;
import com.panguso.android.shijingshan.register.usertype.UserTypeInfo;

import android.graphics.Bitmap;
import android.test.AndroidTestCase;

public class NetworkServiceTest extends AndroidTestCase {
	/** The server url. */
	private static final String SERVER_URL = "http://s-94379.gotocdn.com/sjs/JsonAction";

	/**
	 * Test
	 * {@link NetworkService#getBusinessInfoList(String, BusinessInfoListRequestListener)}
	 * 
	 * @author Luo Yinzhuo
	 */
	public void testGetBusinessInfoList() {
		/** The lock to synchronize. */
		final Object LOCK = new Object();
		NetworkService.getBusinessInfoList(SERVER_URL,
				new BusinessInfoListRequestListener() {

					@Override
					public void onBusinessInfoListRequestFailed() {
						assertTrue("Create Business Info List Request Failed!",
								false);
						// Let main thread finish.
						synchronized (LOCK) {
							LOCK.notify();
						}
					}

					@Override
					public void onBusinessInfoListResponseSuccess(
							List<BusinessInfo> businessInfos) {
						assertNotNull("Business info is empty!", businessInfos);
						assertTrue("Business info is empty!",
								businessInfos.size() > 0);
						// Let main thread finish.
						synchronized (LOCK) {
							LOCK.notify();
						}
					}

					@Override
					public void onBusinessInfoListResponseFailed() {
						assertTrue("Get Business Info List Failed!", false);
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

	/** The business id. */
	private static final int BUSINESS_ID = 10301;

	/**
	 * Test
	 * {@link NetworkService#getEnterpriseInfoList(String, int, EnterpriseInfoListRequestListener)}
	 * .
	 * 
	 * @author Luo Yinzhuo
	 */
	public void testGetEnterpriseInfoList() {
		/** The lock to synchronize. */
		final Object LOCK = new Object();
		NetworkService.getEnterpriseInfoList(SERVER_URL, BUSINESS_ID,
				new EnterpriseInfoListRequestListener() {

					@Override
					public void onEnterpriseInfoListRequestFailed() {
						assertTrue(
								"Create Enterprise Info List Request Failed!",
								false);
						// Let main thread finish.
						synchronized (LOCK) {
							LOCK.notify();
						}
					}

					@Override
					public void onEnterpriseInfoListResponseSuccess(
							int businessId, List<EnterpriseInfo> enterpriseInfos) {
						assertEquals("Business id doesn't match!", BUSINESS_ID,
								businessId);
						assertNotNull("Enterprise info is empty!",
								enterpriseInfos);
						assertTrue("Enterprise info is empty!",
								enterpriseInfos.size() > 0);
						// Let main thread finish.
						synchronized (LOCK) {
							LOCK.notify();
						}
					}

					@Override
					public void onEnterpriseInfoListResponseFailed() {
						assertTrue("Get Enterprise Info List Failed!", false);
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
	 * {@link NetworkService#getEnterpriseInfoList(String, int, EnterpriseInfoListRequestListener)}
	 * .
	 * 
	 * @author Luo Yinzhuo
	 */
	public void testRegister() {
		/** The lock to synchronize. */
		final Object LOCK = new Object();
		NetworkService.register(SERVER_URL, ACCOUNT, PASSWORD, PHONE_NUM,
				ENTERPRISE_ID, ENTERPRISE_NAME, UUID, TERMINAL_TYPE, USER_TYPE,
				new RegisterRequestListener() {

					@Override
					public void onRegisterRequestFailed() {
						assertTrue("Create Register Request Failed!", false);
						// Let main thread finish.
						synchronized (LOCK) {
							LOCK.notify();
						}
					}

					@Override
					public void onRegisterResponseSuccess() {
						// Let main thread finish.
						synchronized (LOCK) {
							LOCK.notify();
						}
					}

					@Override
					public void onRegisterResponseFailed() {
						assertTrue("Register Failed!", false);
						// Let main thread finish.
						synchronized (LOCK) {
							LOCK.notify();
						}
					}

					@Override
					public void onRegisterResponseFailed(String errorMessage) {
						assertNotNull(errorMessage);
						assertEquals("该用户名已存在", errorMessage);
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
	 * Test
	 * {@link NetworkService#getUserTypeInfoList(String, UserTypeInfoListRequestListener)}
	 * 
	 * @author Luo Yinzhuo
	 */
	public void testGetUserTypeInfoList() {
		/** The lock to synchronize. */
		final Object LOCK = new Object();
		NetworkService.getUserTypeInfoList(SERVER_URL,
				new UserTypeInfoListRequestListener() {

					@Override
					public void onUserTypeInfoListRequestFailed() {
						assertTrue(
								"Create User Type Info List Request Failed!",
								false);
						// Let main thread finish.
						synchronized (LOCK) {
							LOCK.notify();
						}
					}

					@Override
					public void onUserTypeInfoListResponseSuccess(
							List<UserTypeInfo> userTypeInfos) {
						assertNotNull("User type info is empty!", userTypeInfos);
						assertTrue("User type info is empty!",
								userTypeInfos.size() > 0);
						// Let main thread finish.
						synchronized (LOCK) {
							LOCK.notify();
						}
					}

					@Override
					public void onUserTypeInfoListResponseFailed() {
						assertTrue("Get User Type Info List Failed!", false);
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
	 * Test
	 * {@link NetworkService#getColumnInfoList(String, String, ColumnInfoListRequestListener)}
	 * 
	 * @author Luo Yinzhuo
	 */
	public void testGetColumnInfoListWithNoAccount() {
		/** The account */
		final String account = "";
		/** The lock to synchronize. */
		final Object LOCK = new Object();
		NetworkService.getColumnInfoList(SERVER_URL, account,
				new ColumnInfoListRequestListener() {

					@Override
					public void onColumnInfoListRequestFailed() {
						assertTrue("Create Column Info List Request Failed!",
								false);
						// Let main thread finish.
						synchronized (LOCK) {
							LOCK.notify();
						}
					}

					@Override
					public void onColumnInfoListResponseSuccess(
							List<ColumnInfo> columnInfos) {
						assertNotNull("Column info is empty!", columnInfos);
						assertTrue("Column info is empty!",
								columnInfos.size() > 0);
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
	public void testGetNewsInfoList() {
		/** The column ID. */
		final String COLUMN_ID = "100";
		/** The lock to synchronize. */
		final Object LOCK = new Object();
		NetworkService.getNewsList(SERVER_URL, COLUMN_ID,
				new NewsListRequestListener() {

					@Override
					public void onNewsListRequestFailed() {
						assertTrue("Create News Info List Request Failed!",
								false);
						// Let main thread finish.
						synchronized (LOCK) {
							LOCK.notify();
						}
					}

					@Override
					public void onNewsListResponseSuccess(
							List<NewsInfo> newsInfos,
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
		final String IMAGE_URL = "http://s-94379.gotocdn.com/sjs//html/100/2013/11/2013_11_9228.jpg";
		/** The lock to synchronize. */
		final Object LOCK = new Object();
		NetworkService.getImage(IMAGE_URL, new ImageRequestListener() {

			@Override
			public void onImageResponseSuccess(Bitmap bitmap) {
				assertNotNull("Bitmap is null!", bitmap);
				assertEquals("Bitmap width error!", 450, bitmap.getWidth());
				assertEquals("Bitmap height error!", 299, bitmap.getHeight());
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
