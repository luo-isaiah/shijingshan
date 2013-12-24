package com.panguso.android.shijingshan;

import android.test.ApplicationTestCase;

public class ApplicationTest extends ApplicationTestCase<Application> {

	public ApplicationTest() {
		super(Application.class);
	}

	/**
	 * Test {@link Application#getUUID()}.
	 * 
	 * @author Luo Yinzhuo
	 */
	public void testUUID() {
		createApplication();
		String uuid = getApplication().getUUID();
		assertNotNull(uuid);
	}

}
