/**
 *  Copyright (c)  2011-2020 Panguso, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Panguso, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Panguso.
 */
package com.panguso.android.shijingshan.column;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.panguso.android.shijingshan.column.ColumnPageView.OnPressDownColumnListener;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.SparseArray;
import android.view.MotionEvent;

/**
 * Represent a single page of columns.
 * 
 * @author Luo Yinzhuo
 * @date 2013-8-7
 */
public final class ColumnPage {
	/** The maximum column size. */
	static final int MAX_COLUMN_SIZE = 8;
	/** The column rectangles. */
	private static final List<RectF> COLUMN_RECTS = new ArrayList<RectF>();

	/**
	 * Initialize the column rectangles.
	 * 
	 * @param offsetLeft
	 *            The first {@link Column}'s left offset.
	 * @param offsetTop
	 *            The first {@link Column}'s top offset.
	 * @param margin
	 *            The margin between two {@link Column}.
	 * @param size
	 *            The size of {@link Column}.
	 * @author Luo Yinzhuo
	 */
	public static void initialize(float offsetLeft, float offsetTop,
			float margin, float size) {
		COLUMN_RECTS.clear();
		for (int i = 0; i < MAX_COLUMN_SIZE; i++) {
			final float left = offsetLeft + i % 2 * (size + margin);
			final float top = offsetTop + i / 2 * (size + margin);
			COLUMN_RECTS.add(new RectF(left, top, left + size, top + size));
		}
	}

	/** The page's column. */
	private final List<Column> mColumns = new ArrayList<Column>(MAX_COLUMN_SIZE);

	/**
	 * Check if the rectangle releases the column's position.
	 * 
	 * @param position
	 *            The column's position.
	 * @param rectF
	 *            The rectangle.
	 * @return True if the rectangle releases the column's position, otherwise
	 *         false.
	 * @author Luo Yinzhuo
	 */
	public boolean isReleasePosition(int position, RectF rectF) {
		return position >= mColumns.size()
				|| !RectF.intersects(rectF, COLUMN_RECTS.get(position));
	}

	/**
	 * Check if the rectangle takes one of the column's position.
	 * 
	 * @param rectF
	 *            The rectangle.
	 * @return The take position.
	 * @author Luo Yinzhuo
	 */
	public int getTakePosition(RectF rectF) {
		int i = 0;
		for (; i < mColumns.size(); i++) {
			RectF position = COLUMN_RECTS.get(i);
			if (rectF.contains(position.centerX(), position.centerY())) {
				return i;
			}
		}
		return Math.min(i, MAX_COLUMN_SIZE - 1);
	}

	/**
	 * Get the column page's data in JSON format.
	 * 
	 * @return The column page's data in JSON format.
	 * @throws JSONException
	 *             If error occurs in JSON format.
	 * @author Luo Yinzhuo
	 */
	public String getJson() throws JSONException {
		JSONArray page = new JSONArray();
		for (Column column : mColumns) {
			page.put(new JSONObject(column.getJson()));
		}
		return page.toString();
	}

	/**
	 * Construct a {@link ColumnPage} from its data in JSON format.
	 * 
	 * @param context
	 *            The context.
	 * @param json
	 *            The JSON format data.
	 * @return The {@link ColumnPage}.
	 * @throws JSONException
	 *             If error occurs in JSON format.
	 * @author Luo Yinzhuo
	 */
	public static ColumnPage parse(Context context, String json)
			throws JSONException {
		JSONArray columns = new JSONArray(json);
		ColumnPage page = new ColumnPage();
		for (int i = 0; i < columns.length(); i++) {
			page.addColumn(Column.parse(context, columns.get(i).toString()));
		}
		return page;
	}

	/**
	 * Exclude the {@link Column}s which this {@link ColumnPage} contains from
	 * the specified {@link Column} list.
	 * 
	 * @param columns The columns.
	 * 
	 * @author Luo Yinzhuo
	 */
	public void exclude(List<Column> columns) {
		columns.removeAll(mColumns);
	}

	/**
	 * Check if the page is empty.
	 * 
	 * @return True if it is empty, otherwise false.
	 * @author Luo Yinzhuo
	 */
	public boolean isEmpty() {
		return mColumns.size() == 0;
	}

	/**
	 * Check if the page is full.
	 * 
	 * @return True if it is full, otherwise false.
	 * @author Luo Yinzhuo
	 * @date 2013-8-7
	 */
	public boolean isFull() {
		return mColumns.size() == MAX_COLUMN_SIZE;
	}

	/**
	 * Add a column to the page.
	 * 
	 * @param column
	 *            The column.
	 * @author Luo Yinzhuo
	 */
	public void addColumn(Column column) {
		mColumns.add(column);
	}

	/**
	 * Add a column to the page with the specific index.
	 * 
	 * @param index
	 *            The index.
	 * @param column
	 *            The column.
	 * @author Luo Yinzhuo
	 */
	public void addColumn(int index, Column column) {
		if (index < mColumns.size()) {
			mColumns.add(index, column);
		} else {
			mColumns.add(column);
		}
	}

	/**
	 * Add a column to the page with the specific index and add a
	 * {@link ColumnAnimation}.
	 * 
	 * @param index
	 *            The index.
	 * @param column
	 *            The column.
	 * @param rectF
	 *            The initial rectangle.
	 * @author Luo Yinzhuo
	 */
	public void addColumn(int index, Column column, RectF rectF) {
		int position = index;
		if (index < mColumns.size()) {
			mColumns.add(index, column);
		} else {
			mColumns.add(column);
			position = mColumns.size() - 1;
		}

		RectF targetRectF = COLUMN_RECTS.get(position);
		if (!RectF.intersects(rectF, targetRectF)) {
			mColumnAnimationArray.put(position, new ColumnAnimation(rectF,
					System.currentTimeMillis(), targetRectF));
		}
	}

	/**
	 * Remove the excess column.
	 * 
	 * @return The excess column.
	 * @author Luo Yinzhuo
	 */
	public Column removeExcessColumn() {
		return mColumns.remove(MAX_COLUMN_SIZE - 1);
	}

	/**
	 * Draw the column page.
	 * 
	 * @param canvas
	 *            The {@link ColumnPageView}'s canvas.
	 * @param offsetX
	 *            The offset on X axis of this page.
	 * @param width
	 *            The {@link ColumnPageView}'s width.
	 * @param jumpPosition
	 *            The position need to be jumped.
	 * @param rotation
	 *            The canvas rotation before drawing.
	 * @author Luo Yinzhuo
	 * @date 2013-8-22
	 */
	public void draw(Canvas canvas, float offsetX, int width, int jumpPosition,
			float rotation) {
		final long time = System.currentTimeMillis();

		for (int i = 0; i < mColumns.size(); i++) {
			Column column = mColumns.get(i);
			RectF rectF;
			ColumnAnimation animation = mColumnAnimationArray.get(i);
			if (animation != null) {
				rectF = animation.getRectF(time);
				if (animation.isComplete()) {
					mColumnAnimationArray.remove(i);
				}
			} else {
				final int rectIndex = i < jumpPosition ? i : i + 1;
				if (rectIndex == MAX_COLUMN_SIZE) {
					rectF = new RectF(COLUMN_RECTS.get(0));
					rectF.offset(width, 0);
				} else {
					rectF = COLUMN_RECTS.get(rectIndex);
				}
			}

			// TODO: Use offsetX to reduce draw items.
			column.draw(canvas, rectF, rotation);
		}
	}

	/**
	 * Invoked when a down event occurs on the page.
	 * 
	 * @param e
	 *            The event.
	 * @param listener
	 *            The press down column listener.
	 */
	public void onDown(MotionEvent e, OnPressDownColumnListener listener) {
		for (int i = 0; i < mColumns.size(); i++) {
			RectF rectF = COLUMN_RECTS.get(i);
			if (rectF.contains(e.getX(), e.getY())) {
				Column column = mColumns.remove(i);
				listener.onDown(e, column, this, i, new RectF(rectF));
			}
		}
	}

	/** The column animation map. */
	private final SparseArray<ColumnAnimation> mColumnAnimationArray = new SparseArray<ColumnAnimation>();

	/**
	 * Check if it has any {@link ColumnAnimation}.
	 * 
	 * @return True if it has, otherwise false.
	 * @author Luo Yinzhuo
	 */
	public boolean hasColumnAnimation() {
		return mColumnAnimationArray.size() > 0;
	}

	/**
	 * Specific for controlling the {@link Column}'s animation.
	 * 
	 * @author Luo Yinzhuo
	 */
	private class ColumnAnimation {
		/** The animation basic duration time. */
		private static final long DURATION_BASE = 300L;
		/** The start rectangle. */
		private final RectF mStartRectF = new RectF();
		/** The start time. */
		private long mStartTime;
		/** The target rectangle. */
		private final RectF mTargetRectF = new RectF();
		/** The complete flag. */
		private boolean mComplete = false;

		/**
		 * Construct a new instance.
		 * 
		 * @param startRectF
		 *            The start rectangle.
		 * @param startTime
		 *            The start time.
		 * @param targetRectF
		 *            The target rectangle.
		 */
		public ColumnAnimation(RectF startRectF, long startTime,
				RectF targetRectF) {
			mStartRectF.set(startRectF);
			mStartTime = startTime;
			mTargetRectF.set(targetRectF);
		}

		/**
		 * Animate to a new target rectangle.
		 * 
		 * @param startTime
		 *            The start time.
		 * @param targetRectF
		 *            The target rectangle.
		 * @author Luo Yinzhuo
		 */
		public void animate(long startTime, RectF targetRectF) {
			mStartRectF.set(getRectF(startTime));
			mStartTime = startTime;
			mTargetRectF.set(targetRectF);
		}

		/**
		 * Get the current rectangle.
		 * 
		 * @param time
		 *            The current time.
		 * @return The current rectangle.
		 * @author Luo Yinzhuo
		 */
		public RectF getRectF(long time) {
			long passTime = time - mStartTime;
			if (passTime >= DURATION_BASE) {
				mComplete = true;
				return mTargetRectF;
			} else {
				RectF rectF = new RectF(mStartRectF);
				final float offsetX = (mTargetRectF.centerX() - mStartRectF
						.centerX()) * passTime / DURATION_BASE;
				final float offsetY = (mTargetRectF.centerY() - mStartRectF
						.centerY()) * passTime / DURATION_BASE;
				rectF.offset(offsetX, offsetY);
				return rectF;
			}
		}

		/**
		 * Check if the animation is completed.
		 * 
		 * @return True if the animation is completed, otherwise false.
		 * @author Luo Yinzhuo
		 */
		public boolean isComplete() {
			return mComplete;
		}
	}

	/**
	 * Invoked when the position has been released by a {@link Column}.
	 * 
	 * @param position
	 *            The position released.
	 * @param width
	 *            The {@link ColumnPageView}'s width.
	 * @author Luo Yinzhuo
	 */
	public void onReleasePosition(int position, int width) {
		final long time = System.currentTimeMillis();
		for (int i = position; i < mColumns.size(); i++) {
			ColumnAnimation animation = mColumnAnimationArray.get(i);
			if (animation != null) {
				animation.animate(time, COLUMN_RECTS.get(i));
			} else {
				if (i == MAX_COLUMN_SIZE - 1) {
					RectF startRectF = new RectF(COLUMN_RECTS.get(0));
					startRectF.offset(width, 0);
					mColumnAnimationArray.put(i, new ColumnAnimation(
							startRectF, time, COLUMN_RECTS.get(i)));
				} else {
					mColumnAnimationArray
							.put(i,
									new ColumnAnimation(
											COLUMN_RECTS.get(i + 1), time,
											COLUMN_RECTS.get(i)));
				}
			}
		}
	}

	/**
	 * Invoked when the position has been taken by a {@link Column}.
	 * 
	 * @param position
	 *            The position taken.
	 * @param width
	 *            The {@link ColumnPageView}'s width.
	 * @author Luo Yinzhuo
	 */
	public void onTakePosition(int position, int width) {
		final long time = System.currentTimeMillis();
		for (int i = position; i < mColumns.size(); i++) {
			ColumnAnimation animation = mColumnAnimationArray.get(i);
			if (animation != null) {
				if (i + 1 == MAX_COLUMN_SIZE) {
					RectF targetRectF = new RectF(COLUMN_RECTS.get(0));
					targetRectF.offset(width, 0);
					animation.animate(time, targetRectF);
				} else {
					animation.animate(time, COLUMN_RECTS.get(i + 1));
				}
			} else {
				if (i == MAX_COLUMN_SIZE - 1) {
					RectF targetRectF = new RectF(COLUMN_RECTS.get(0));
					targetRectF.offset(width, 0);
					mColumnAnimationArray.put(i, new ColumnAnimation(
							COLUMN_RECTS.get(i), time, targetRectF));
				} else {
					mColumnAnimationArray
							.put(i, new ColumnAnimation(COLUMN_RECTS.get(i),
									time, COLUMN_RECTS.get(i + 1)));
				}
			}
		}
	}
}