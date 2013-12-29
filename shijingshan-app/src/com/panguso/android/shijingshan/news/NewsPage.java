package com.panguso.android.shijingshan.news;

import java.util.ArrayList;
import java.util.List;

import com.panguso.android.shijingshan.net.NetworkService.ImageRequestListener;

import android.R.integer;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

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
	 * Initialize the news cell width and height.
	 * 
	 * @param width
	 *        The {@link NewsPageView} width.
	 * @param height
	 *        The {@link NewsPageView} height.
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
	 *        The {@link News}.
	 * @return True if the {@link News} is added, otherwise false.
	 */
	public boolean addNews(News news) {
		int row = 0;
		int column = 0;

		if (news.hasImage()) {
			// A news with image need 2x2 cells.
			while (mCells[row][column] != -1 || mCells[row + 1][column + 1] != -1) {
				if (column >= COLUMN_COUNT - 2) {
					row++;
					column = 0;
				} else {
					column++;
				}

				if (row >= ROW_COUNT - 1) {
					return false;
				}
			}

			// Re-expand the top left news above this news to take one line
			// area.
			if (row > 0 && mCells[row - 1][column + 1] == -1) {
				final int index = mCells[row - 1][column];
				mCells[row - 1][column + 1] = index;
				Rect rect = mRects.get(index);
				rect.right += CELL_WIDTH;
			}

			final int index = mNews.size();
			mCells[row][column] = index;
			mCells[row][column + 1] = index;
			mCells[row + 1][column] = index;
			mCells[row + 1][column + 1] = index;
			Rect rect = new Rect(column * CELL_WIDTH, row * CELL_HEIGHT, (column + 2) * CELL_WIDTH,
			        (row + 2) * CELL_HEIGHT);
			mNews.add(news);
			mRects.add(rect);
			return true;
		} else {
			// A news without image only need 1x1 cell;
			while (mCells[row][column] != -1) {
				if (column >= COLUMN_COUNT - 1) {
					row++;
					column = 0;
				} else {
					column++;
				}

				if (row >= ROW_COUNT) {
					return false;
				}
			}

			final int index = mNews.size();
			mCells[row][column] = index;
			Rect rect = new Rect(column * CELL_WIDTH, row * CELL_HEIGHT, (column + 1) * CELL_WIDTH,
			        (row + 1) * CELL_HEIGHT);
			mNews.add(news);
			mRects.add(rect);
			return true;
		}
	}

	/** The paint shared by all the news pages. */
	private static final Paint PAINT = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
	/** The delimiter color. */
	private static int DELIMITER;

	/**
	 * Initialize the news delimiter color.
	 * 
	 * @param delimiter The news delimiter color.
	 * @author Luo Yinzhuo
	 */
	public static void initialize(int delimiter) {
		DELIMITER = delimiter;
	}

	/**
	 * Draw the news page.
	 * 
	 * @param canvas The {@link NewsPageView}'s canvas.
	 * @param listener The request listener.
	 * @author Luo Yinzhuo
	 */
	public void draw(Canvas canvas, ImageRequestListener listener) {
		Log.d("NewsPage", "draw");
		// Check whether the last news complete a single line.
		final int count = mNews.size();
		if (count > 0) {
			Rect rect = mRects.get(count - 1);
			if (rect.right < CELL_WIDTH * COLUMN_COUNT) {
				int row_start = rect.top / CELL_HEIGHT;
				int row_end = rect.bottom / CELL_HEIGHT;
				int column_start = rect.right / CELL_WIDTH;
				for (int i = row_start; i < row_end; i++) {
					for (int j = column_start; j < COLUMN_COUNT; j++) {
						mCells[i][j] = count - 1;
					}
				}

				rect.right = CELL_WIDTH * COLUMN_COUNT;
			}
		}

		PAINT.setColor(DELIMITER);
		PAINT.setStrokeWidth(1);

		// Draw horizontal delimiters.
		for (int i = 1; i < ROW_COUNT; i++) {
			for (int j = 0; j < COLUMN_COUNT; j++) {
				if (mCells[i - 1][j] != mCells[i][j]) {
					canvas.drawLine(j * CELL_WIDTH, i * CELL_HEIGHT, (j + 1) * CELL_WIDTH, i
					        * CELL_HEIGHT, PAINT);
				}
			}
		}

		// Draw vertical delimiters.
		for (int j = 1; j < COLUMN_COUNT; j++) {
			for (int i = 0; i < ROW_COUNT; i++) {
				if (mCells[i][j - 1] != mCells[i][j]) {
					canvas.drawLine(j * CELL_WIDTH, i * CELL_HEIGHT, j * CELL_WIDTH, (i + 1)
					        * CELL_HEIGHT, PAINT);
				}
			}
		}

		for (int i = 0; i < count; i++) {
			mNews.get(i).draw(canvas, mRects.get(i), listener);
		}
	}
}
