package com.panguso.android.shijingshan.news;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Rect;

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
	 * @param width
	 *            The {@link NewsPageView} width.
	 * @param height
	 *            The {@link NewsPageView} height.
	 * @author Luo Yinzhuo
	 */
	public static void initialize(int width, int height) {
		CELL_WIDTH = width / COLUMN_COUNT;
		CELL_HEIGHT = height / ROW_COUNT;
	}

	/** The cells. */
	private final int[][] mCells = new int[ROW_COUNT][COLUMN_COUNT];

	/**
	 * Construct a new instance.
	 */
	public NewsPage() {
		for (int row = 0; row < ROW_COUNT; row++) {
			for (int column = 0; column < COLUMN_COUNT; column++) {
				mCells[row][column] = -1;
			}
		}
	}

	/** The news list. */
	private final List<News> mNews = new ArrayList<News>();
	/** The rectangle list. */
	private final List<Rect> mRects = new ArrayList<Rect>();

	/**
	 * Add a {@link News} to this {@link NewsPage}.
	 * 
	 * @param news
	 *            The {@link News}.
	 * @return True if the {@link News} is added, otherwise false.
	 */
	public boolean addNews(News news) {
		int row = 0;
		int column = 0;

		// A news with image need 2x2 cells.
		if (news.hasImage()) {
			while (mCells[row][column] != -1
					|| mCells[row + 1][column + 1] != -1) {
				if (row >= ROW_COUNT - 1) {
					return false;
				}

				if (column == COLUMN_COUNT - 2) {
					row++;
					column = 0;
				} else {
					column++;
				}
			}
			return true;
		} else {
			while (mCells[row][column] != -1) {
				if (row >= ROW_COUNT) {
					return false;
				}

				if (column == COLUMN_COUNT - 1) {
					row++;
					column = 0;
				} else {
					column++;
				}
			}
			return true;
		}
	}

}
