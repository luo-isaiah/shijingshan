package com.panguso.android.shijingshan.news;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent a single page of articles.
 * 
 * @author Luo Yinzhuo
 */
public class NewsPage {
	/** The column count. */
	private static final int COLUMN_COUNT = 2;
	/** The row count. */
	private static final int ROW_COUNT = 5;

	/** The cell width. */
	private static int CELL_WIDTH;
	/** The cell height. */
	private static int CELL_HEIGHT;

	/**
	 * Initialize the article cell width and height.
	 * 
	 * @param width The {@link NewsPageView} width.
	 * @param height The {@link NewsPageView} height.
	 * @author Luo Yinzhuo
	 */
	public static void initialize(int width, int height) {
		CELL_WIDTH = width / COLUMN_COUNT;
		CELL_HEIGHT = height / ROW_COUNT;
	}
	
	private final List<News> mArticles = new ArrayList<News>();

}
