package com.panguso.android.shijingshan.column;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

/**
 * The column page view.
 * 
 * @author Luo Yinzhuo
 */
public class ColumnPageView extends View {
	/** The column page manager. */
	private final ColumnPageManager mColumnPageManager;
	/** The gesture detector. */
	private final GestureDetector mGestureDetector;

	/**
	 * Construct a new instance.
	 * 
	 * @param context
	 *            The context.
	 * @param attrs
	 *            The attributes.
	 */
	public ColumnPageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mColumnPageManager = new ColumnPageManager();
		mGestureDetector = new GestureDetector(context, mColumnPageManager);
	}

	/**
	 * Initialize the {@link ColumnPageManager}.
	 * 
	 * @param json
	 *            The column pages data in JSON format.
	 * @throws JSONException
	 *             If error occurs in JSON format.
	 * @author Luo Yinzhuo
	 */
	public void initialize(String json) throws JSONException {
		mColumnPageManager.initialize(json);
		invalidate();
	}

	/**
	 * Filter the {@link ColumnPageManager} to show and only show the specified
	 * {@link Column}s.
	 * 
	 * @param columns
	 *            The list of {@link Column}.
	 * @author Luo Yinzhuo
	 */
	public void filter(List<Column> columns) {
		mColumnPageManager.filter(columns);
		invalidate();
	}

	/**
	 * Switch to the explore mode.
	 * 
	 * @author Luo Yinzhuo
	 */
	public void explore() {
		mColumnPageManager.explore();
		invalidate();
	}

	/**
	 * Get the column page view's data in JSON format.
	 * 
	 * @return The column page view's data in JSON format.
	 * @throws JSONException
	 *             If error occurs in JSON format.
	 * @author Luo Yinzhuo
	 */
	public String getJson() throws JSONException {
		return mColumnPageManager.getJson();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		mColumnPageManager.draw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mGestureDetector.onTouchEvent(event)
				|| mColumnPageManager.onTouchEvent(event);
	}

	/**
	 * Listen to the press down column relative event.
	 * 
	 * @author Luo Yinzhuo
	 */
	public interface OnPressDownColumnListener {
		/**
		 * Invoked when a column is pressing down.
		 * 
		 * @param event
		 *            The down event.
		 * @param column
		 *            The column being pressed.
		 * @param page
		 *            The column page.
		 * @param takePosition
		 *            The position which the column takes.
		 * @param rect
		 *            The rectangle which the column takes.
		 * @author Luo Yinzhuo
		 */
		public void onDown(MotionEvent event, Column column, ColumnPage page,
				int takePosition, RectF rectF);

	}

	/**
	 * Specified for manage the column pages.
	 * 
	 * @author Luo Yinzhuo
	 */
	private class ColumnPageManager implements OnGestureListener,
			OnPressDownColumnListener {
		/** The column page list. */
		private final List<ColumnPage> mColumnPages = new ArrayList<ColumnPage>();
		/** The column page position manager. */
		private final ColumnPagePositionManager mColumnPagePositionManager = new ColumnPagePositionManager();
		/** The press down column. */
		private final PressDownColumn mPressDownColumn = new PressDownColumn();

		/**
		 * Filter the {@link ColumnPage}s to show and only show the specified
		 * {@link Column}s.
		 * 
		 * @param columns
		 *            The list of {@link Column}s.
		 * @author Luo Yinzhuo
		 */
		public void filter(List<Column> columns) {
			List<Column> remainColumns = new ArrayList<Column>(columns);
			if (!mColumnPages.isEmpty()) {
				for (ColumnPage columnPage : mColumnPages) {
					columnPage.exclude(remainColumns);
				}
			} else {
				mColumnPages.add(new ColumnPage());
			}

			if (!remainColumns.isEmpty()) {
				int columnPagePosition = (int) mColumnPagePositionManager
						.getColumnPagePosition();
				ColumnPage columnPage = mColumnPages.get(columnPagePosition);
				for (int i = 0; i < remainColumns.size(); i++) {
					Column column = remainColumns.get(i);

					if (!columnPage.isFull()) {
						columnPage.addColumn(column);
					} else {
						columnPagePosition++;
						if (columnPagePosition < mColumnPages.size()) {
							columnPage = mColumnPages.get(columnPagePosition);
						} else {
							columnPage = new ColumnPage();
							mColumnPages.add(columnPage);
						}
						i--;
					}
				}
			}

			mMode = MODE_EXPLORE;
		}

		/** The key to store current column page position. */
		private static final String KEY_COLUMN_PAGE_POSITION = "column_page_position";
		/** The key to store current column pages. */
		private static final String KEY_COLUMN_PAGES = "column_pages";

		/**
		 * Initialize itself from a JSON format data.
		 * 
		 * @param json
		 *            The JSON format data.
		 * @throws JSONException
		 *             If error occurs in JSON format.
		 * @author Luo Yinzhuo
		 */
		public void initialize(String json) throws JSONException {
			mColumnPages.clear();
			Context context = getContext();

			JSONObject root = new JSONObject(json);
			JSONArray columnPages = root.getJSONArray(KEY_COLUMN_PAGES);
			for (int i = 0; i < columnPages.length(); i++) {
				mColumnPages.add(ColumnPage.parse(context, columnPages.get(i)
						.toString()));
			}
			mColumnPagePositionManager.setColumnPagePosition(root
					.getInt(KEY_COLUMN_PAGE_POSITION));
			mMode = MODE_EXPLORE;
		}

		/**
		 * Switch to the explore mode.
		 * 
		 * @author Luo Yinzhuo
		 */
		public void explore() {
			// First, give the press down column back.
			if (!mPressDownColumn.isEmpty()) {
				mPressDownColumn.release();
			}

			// Second, change the mode back to explore mode.
			if (mMode == MODE_EDIT) {
				for (int i = 0; i < mColumnPages.size(); i++) {
					ColumnPage page = mColumnPages.get(i);
					if (page.isEmpty()) {
						mColumnPages.remove(page);
						mColumnPagePositionManager.onRemovePage(i);
						i--;
					}
				}
			}

			// Third, check the column page position.
			final int columnPagePosition;
			if (mColumnPagePositionManager.getColumnPagePosition() > mColumnPages
					.size() - 1) {
				columnPagePosition = mColumnPages.size() - 1;
			} else {
				columnPagePosition = (int) Math
						.floor(mColumnPagePositionManager
								.getColumnPagePosition() + 0.5f);
			}
			mColumnPagePositionManager.setColumnPagePosition(columnPagePosition);
			
			mMode = MODE_EXPLORE;
		}

		/**
		 * Get the column page view's data in JSON format.
		 * 
		 * @return The column page view's data in JSON format.
		 * @throws JSONException
		 *             If error occurs in JSON format.
		 * @author Luo Yinzhuo
		 */
		public String getJson() throws JSONException {
			explore();
			
			JSONObject root = new JSONObject();
			root.put(KEY_COLUMN_PAGE_POSITION, mColumnPagePositionManager.getColumnPagePosition());

			JSONArray columnPages = new JSONArray();
			for (ColumnPage page : mColumnPages) {
				columnPages.put(new JSONArray(page.getJson()));
			}
			root.put(KEY_COLUMN_PAGES, columnPages);

			return root.toString();
		}

		/** The explore mode. */
		private static final int MODE_EXPLORE = 0;
		/** The edit mode. */
		private static final int MODE_EDIT = 1;
		/** The mode. */
		private int mMode = MODE_EXPLORE;

		/** The rotation range. */
		private final float ROTATION = 1.5f;
		/** The rotation. */
		private float mRotation = 0;

		/**
		 * Draw the column pages to the canvas.
		 * 
		 * @param canvas
		 *            The {@link ColumnPageView}'s canvas.
		 * @author Luo Yinzhuo
		 */
		public void draw(Canvas canvas) {
			final float columnPagePosition = mColumnPagePositionManager
					.getColumnPagePosition();
			final int jumpIndex = mPressDownColumn.isEmpty() ? ColumnPage.MAX_COLUMN_SIZE
					: mPressDownColumn.getTakePosition();
			final int width = getWidth();

			if (mMode == MODE_EDIT) {
				mRotation = -1 * mRotation;
			} else {
				mRotation = 0;
			}

			if (mColumnPages.size() > 0) {
				final int left = (int) Math.floor(columnPagePosition);
				float offsetX = (left - columnPagePosition) * width;
				canvas.save();
				canvas.translate(offsetX, 0);

				if (left >= 0 && left < mColumnPages.size()) {
					ColumnPage leftPage = mColumnPages.get(left);
					leftPage.draw(canvas, offsetX, width, jumpIndex, mRotation);
				}

				final int right = left + 1;
				if (right < mColumnPages.size()) {
					ColumnPage rightPage = mColumnPages.get(right);
					canvas.translate(width, 0);
					rightPage.draw(canvas, offsetX + width, width, jumpIndex,
							mRotation);
				}
				canvas.restore();
			}

			if (!mPressDownColumn.isEmpty()) {
				mPressDownColumn.draw(canvas);
			}

			if (mMode == MODE_EDIT || mColumnPagePositionManager.hasAnimation()) {
				invalidate();
			}
		}

/**
 		 * Recover the lose of the {@link GestureDetector#onTouchEvent(MotionEvent).
		 * 
		 * @return True if the animation resumes, otherwise false.
		 * @author Luo Yinzhuo
		 */
		public boolean onTouchEvent(MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE:
				if (!mPressDownColumn.isEmpty()
						&& mPressDownColumn.isLongPress()) {
					mPressDownColumn.onScroll(event);
					invalidate();
				}
				break;
			case MotionEvent.ACTION_UP:
				boolean redraw = false;
				if (!mPressDownColumn.isEmpty()) {
					mPressDownColumn.release();
					redraw = true;
				}

				if (mMode == MODE_EDIT) {
					// Remove the empty column page.
					for (int i = 0; i < mColumnPages.size() - 1; i++) {
						ColumnPage page = mColumnPages.get(i);
						if (page.isEmpty()) {
							mColumnPages.remove(page);
							mColumnPagePositionManager.onRemovePage(i);
							i--;
						}
					}

					// Add an extra empty column page at the end.
					if (!mColumnPages.get(mColumnPages.size() - 1).isEmpty()) {
						mColumnPages.add(new ColumnPage());
					}
				}

				redraw |= mColumnPagePositionManager.resume();
				if (redraw) {
					invalidate();
				}
				break;
			default:
				break;
			}
			return true;
		}

		/**
		 * Specific for manage the column page position.
		 * 
		 * @author Luo Yinzhuo
		 */
		private class ColumnPagePositionManager {
			/** The animation basic duration time. */
			private static final long DURATION_BASE = 300L;
			/** The mount of bouncing value. */
			private static final float BOUNCING = 0.3f;

			/** The current column page animation. */
			private ColumnPageAnimation mColumnPageAnimation;
			/** The direction. */
			private boolean mDirection;

			/**
			 * Start animation.
			 * 
			 * @param direction
			 *            True to animate to the left page, otherwise false.
			 * @param bouncing
			 *            True to have bounce effect when need, otherwise false.
			 * @author Luo Yinzhuo
			 */
			public void animate(boolean direction, boolean bouncing) {
				if (mColumnPageAnimation == null) {
					mColumnPageAnimation = new ColumnPageAnimation(direction,
							bouncing);
				} else {
					mColumnPageAnimation.animate(direction, bouncing);
				}
				mDirection = direction;
			}

			/**
			 * Scroll the column page.
			 * 
			 * @param distanceX
			 *            The scroll distance along X axis.
			 * @author Luo Yinzhuo
			 */
			public void scroll(float distanceX) {
				final float leftBorder = -BOUNCING;
				final float leftEdge = 0;
				final float rightEdge = mColumnPages.size() > 0 ? mColumnPages
						.size() - 1 : 0;
				final float rightBorder = rightEdge + BOUNCING;
				float estimateColumnPagePosition;
				if (mColumnPagePosition < leftEdge
						|| mColumnPagePosition > rightEdge) {
					estimateColumnPagePosition = mColumnPagePosition
							+ distanceX / getWidth() * BOUNCING;
				} else {
					estimateColumnPagePosition = mColumnPagePosition
							+ distanceX / getWidth();
				}

				if (distanceX > 0) {
					mColumnPagePosition = Math.max(estimateColumnPagePosition,
							leftBorder);
				} else {
					mColumnPagePosition = Math.min(estimateColumnPagePosition,
							rightBorder);
				}
			}

			/**
			 * Pause the animation.
			 * 
			 * @author Luo Yinzhuo
			 */
			public void pause() {
				mColumnPagePosition = mColumnPageAnimation
						.getColumnPagePosition();
				mColumnPageAnimation = null;
			}

			/**
			 * Resume to animate the nearest {@link ColumnPage}.
			 * 
			 * @return True if it needs to resume, otherwise false.
			 * @author Luo Yinzhuo
			 */
			public boolean resume() {
				if (mColumnPagePosition != Math
						.floor(mColumnPagePosition + 0.5f)) {
					mColumnPageAnimation = new ColumnPageAnimation(
							(int) Math.floor(mColumnPagePosition + 0.5f));
					return true;
				} else {
					return false;
				}
			}

			/**
			 * Check if the column page animation exist.
			 * 
			 * @return True if the column page animation exist, otherwise false.
			 * @author Luo Yinzhuo
			 */
			public boolean hasAnimation() {
				return mColumnPageAnimation != null;
			}

			/**
			 * Get the animation direction.
			 * 
			 * @return True to animate to the left page, otherwise to the right
			 *         page.
			 * @author Luo Yinzhuo
			 */
			public boolean getAnimationDirection() {
				return mDirection;
			}

			/**
			 * Invoked when the column page animation is finished.
			 * 
			 * @author Luo Yinzhuo
			 */
			void onAnimationFinished() {
				mColumnPageAnimation = null;
			}

			/**
			 * Specified for manage the column page animation.
			 * 
			 * @author Luo Yinzhuo
			 */
			private class ColumnPageAnimation {
				/** The no bounce. */
				private static final int BOUNCE_NONE = 0;
				/** The left bounce. */
				private static final int BOUNCE_LEFT = 1;
				/** The right bounce. */
				private static final int BOUNCE_RIGHT = 2;

				/** The animation start time. */
				private long mStartTime = 0L;
				/** The animation start page position. */
				private float mStartPagePosition = 0f;
				/** The target page position. */
				private int mTargetPagePosition = 0;
				/** The bounce. */
				private int mBouncing = BOUNCE_NONE;

				/** The duration. */
				private long mDuration;

				/**
				 * Construct a new instance.
				 * 
				 * @param direction
				 *            True to animate to the left page, otherwise to the
				 *            right page.
				 * @param bouncing
				 *            True to add bounce effect if need, otherwise
				 *            false.
				 */
				public ColumnPageAnimation(boolean direction, boolean bouncing) {
					mStartTime = System.currentTimeMillis();
					mStartPagePosition = mColumnPagePosition;
					mDuration = DURATION_BASE;
					if (direction) {
						mTargetPagePosition = (int) Math
								.floor(mStartPagePosition + 0.5f) - 1;
						if (mTargetPagePosition < 0) {
							mTargetPagePosition = 0;
							if (bouncing) {
								mBouncing = BOUNCE_LEFT;
							}
						}
					} else {
						mTargetPagePosition = (int) Math
								.floor(mStartPagePosition + 0.5f) + 1;
						final int maxColumnPagePosition = mColumnPages.size() - 1;
						if (mTargetPagePosition > maxColumnPagePosition) {
							mTargetPagePosition = maxColumnPagePosition;
							if (bouncing) {
								mBouncing = BOUNCE_RIGHT;
							}
						}
					}
				}

				/**
				 * Construct a new instance.
				 * 
				 * @param targetPagePosition
				 *            The target page position.
				 */
				public ColumnPageAnimation(int targetPagePosition) {
					mStartTime = System.currentTimeMillis();
					mStartPagePosition = mColumnPagePosition;
					mDuration = (long) (DURATION_BASE * Math
							.abs(mTargetPagePosition - mStartPagePosition));
					mTargetPagePosition = targetPagePosition;
				}

				/**
				 * Append one more animate action based on the original one.
				 * 
				 * @param direction
				 *            The new animate action direction, True to animate
				 *            to the left page, otherwise to the right page.
				 * @param bouncing
				 *            True to add bounce effect if need, otherwise
				 *            false.
				 * @author Luo Yinzhuo
				 */
				public void animate(boolean direction, boolean bouncing) {
					mStartTime = System.currentTimeMillis();
					mStartPagePosition = mColumnPagePosition;
					mDuration = DURATION_BASE;
					if (direction) {
						mTargetPagePosition--;
						if (mTargetPagePosition < 0) {
							mTargetPagePosition = 0;
							if (bouncing) {
								mBouncing = BOUNCE_LEFT;
							}
						}
					} else {
						mTargetPagePosition++;
						final int maxColumnPagePosition = mColumnPages.size() - 1;
						if (mTargetPagePosition > maxColumnPagePosition) {
							mTargetPagePosition = maxColumnPagePosition;
							if (bouncing) {
								mBouncing = BOUNCE_RIGHT;
							}
						}
					}
				}

				/**
				 * Get the current column page position.
				 * 
				 * @return The current column page position.
				 * @author Luo Yinzhuo
				 */
				public float getColumnPagePosition() {
					long passTime = System.currentTimeMillis() - mStartTime;
					if (passTime >= mDuration) {
						onAnimationFinished();
						return mTargetPagePosition;
					} else {
						float estimateColumnPagePosition;
						if (mBouncing == BOUNCE_RIGHT) {
							estimateColumnPagePosition = (mTargetPagePosition
									+ 2 * BOUNCING - mStartPagePosition)
									* passTime / mDuration + mStartPagePosition;
							if (estimateColumnPagePosition > mTargetPagePosition
									+ BOUNCING) {
								estimateColumnPagePosition = 2
										* (mTargetPagePosition + BOUNCING)
										- estimateColumnPagePosition;
							}
						} else if (mBouncing == BOUNCE_LEFT) {
							estimateColumnPagePosition = (mTargetPagePosition
									- 2 * BOUNCING - mStartPagePosition)
									* passTime / mDuration + mStartPagePosition;
							if (estimateColumnPagePosition < mTargetPagePosition
									- BOUNCING) {
								estimateColumnPagePosition = 2
										* (mTargetPagePosition - BOUNCING)
										- estimateColumnPagePosition;
							}
						} else {
							estimateColumnPagePosition = (mTargetPagePosition - mStartPagePosition)
									* passTime / mDuration + mStartPagePosition;
						}
						return estimateColumnPagePosition;
					}
				}

				/**
				 * Invoked when a {@link ColumnPage} has been removed.
				 * 
				 * @param i
				 *            The removed page's index.
				 * @author Luo Yinzhuo
				 */
				public void onRemovePage(int i) {
					if (mTargetPagePosition >= i) {
						mTargetPagePosition -= 1;
					}
				}
			}

			/**
			 * The column page position. In the range of [-BOUNCING,
			 * mColumnPages.size() - 1 + BOUNCING].
			 */
			private float mColumnPagePosition = 0.0f;

			/**
			 * Set the column page position.
			 * 
			 * @param columnPagePosition
			 *            The new column page position.
			 * @author Luo Yinzhuo
			 */
			public void setColumnPagePosition(int columnPagePosition) {
				mColumnPagePosition = columnPagePosition;
			}

			/**
			 * Get the column page position.
			 * 
			 * @return The new column page position.
			 * @author Luo Yinzhuo
			 */
			public float getColumnPagePosition() {
				if (mColumnPageAnimation != null) {
					mColumnPagePosition = mColumnPageAnimation
							.getColumnPagePosition();
				}
				return mColumnPagePosition;
			}

			/**
			 * Invoked when a {@link ColumnPage} has been removed.
			 * 
			 * @param i
			 *            The removed page's index.
			 * @author Luo Yinzhuo
			 */
			public void onRemovePage(int i) {
				if (mColumnPagePosition >= i) {
					mColumnPagePosition -= 1;
				}

				if (mColumnPageAnimation != null) {
					mColumnPageAnimation.onRemovePage(i);
				}
			}
		}

		/**
		 * Specific for manage the press down column.
		 * 
		 * @author Luo Yinzhuo
		 */
		private class PressDownColumn {
			/** The column. */
			private Column mColumn;
			/** The current column page. */
			private ColumnPage mColumnPage;
			/** The current taking position. */
			private int mTakePosition;
			/** The current taking area. */
			private RectF mRectF;
			/** The long press flag. */
			private boolean mLongPress = false;

			/**
			 * Extract a pressed down {@link Column} from a {@link ColumnPage}.
			 * 
			 * @param column
			 *            The column.
			 * @param page
			 *            The column page.
			 * @param takePosition
			 *            The taking position.
			 * @param rectF
			 *            The taking area.
			 * 
			 * @author Luo Yinzhuo
			 */
			public void extract(Column column, ColumnPage page,
					int takePosition, RectF rectF) {
				mColumn = column;
				mColumnPage = page;
				mTakePosition = takePosition;
				mRectF = rectF;
				mLongPress = false;
			}

			/**
			 * Release the column back to the column page.
			 * 
			 * @author Luo Yinzhuo
			 */
			public void release() {
				if (mColumnPage.isFull()) {
					Column excess = mColumnPage.removeExcessColumn();
					ColumnPage nextPage = mColumnPages.get(mColumnPages
							.indexOf(mColumnPage) + 1);
					while (nextPage.isFull()) {
						Column temp = nextPage.removeExcessColumn();
						nextPage.addColumn(0, excess);
						nextPage = mColumnPages.get(mColumnPages
								.indexOf(nextPage) + 1);
						excess = temp;
					}
					nextPage.addColumn(0, excess);
				}

				mColumnPage.addColumn(mTakePosition, mColumn, mRectF);
				mColumn = null;
			}

			/**
			 * Check if the press down column exist or not.
			 * 
			 * @return True if the column is not null, else false.
			 * @author Luo Yinzhuo
			 */
			public boolean isEmpty() {
				return mColumn == null;
			}

			/**
			 * Get the taking position.
			 * 
			 * @return The taking position.
			 * @author Luo Yinzhuo
			 */
			public int getTakePosition() {
				return mTakePosition;
			}

			/**
			 * Draw the press down column.
			 * 
			 * @param canvas
			 *            The canvas.
			 * @author Luo Yinzhuo
			 */
			public void draw(Canvas canvas) {
				RectF rectF = new RectF(mRectF);
				if (mLongPress) {
					final float size = rectF.width() * 1.1f;
					final float centerX = rectF.centerX();
					final float centerY = rectF.centerY();
					rectF.set(centerX - size / 2, centerY - size / 2, centerX
							+ size / 2, centerY + size / 2);
				}

				mColumn.draw(canvas, rectF, 0.0f);
			}

			/** The last motion event's x coordinate. */
			private float mLastMotionY;
			/** The last motion event's y coordinate. */
			private float mLastMotionX;

			/**
			 * Invoked when the column being long pressed.
			 * 
			 * @param event
			 *            The long press down event.
			 * @author Luo Yinzhuo
			 */
			public void onLongPress(MotionEvent event) {
				mLongPress = true;
				mLastMotionX = event.getX();
				mLastMotionY = event.getY();
			}

			/**
			 * Check if the column being long pressed.
			 * 
			 * @return True if the column being long pressed, otherwise false.
			 * @author Luo Yinzhuo
			 */
			public boolean isLongPress() {
				return mLongPress;
			}

			/**
			 * Invoked when a single tap occurs on a {@link Column} when the
			 * mode is {@link ColumnPageManager#MODE_EXPLORE}.
			 * 
			 * @author Luo Yinzhuo
			 */
			public void onSingleTapUp() {
				mColumn.onSingleTapUp(getContext());
			}

			/** The switch page scale. */
			private static final float SWITCH_PAGE_SCALE = 0.15f;
			/** The last switch page time. */
			private long mLastSwitchPageTime;
			/** The minimum time interval between two switch page action. */
			private static final long MIN_SWITCH_PAGE_INTERVAL = 1000L;

			/**
			 * Invoked when the column being long pressed and scrolled.
			 * 
			 * @param event
			 *            The move events after the long press down event.
			 * @author Luo Yinzhuo
			 */
			public void onScroll(MotionEvent event) {
				final float distanceX = event.getX() - mLastMotionX;
				final float distanceY = event.getY() - mLastMotionY;
				mRectF.offset(distanceX, distanceY);

				mLastMotionX = event.getX();
				mLastMotionY = event.getY();

				if (switchPage(distanceX)) {
					return;
				}

				if (mColumnPage.isReleasePosition(mTakePosition, mRectF)) {
					final int takePosition = mColumnPage
							.getTakePosition(mRectF);

					if (takePosition != mTakePosition) {
						mColumnPage
								.onReleasePosition(mTakePosition, getWidth());
						mColumnPage.onTakePosition(takePosition, getWidth());
						mTakePosition = takePosition;
					}
				}
			}

			/**
			 * Switch {@link ColumnPage} if necessary.
			 * 
			 * @author Luo Yinzhuo
			 */
			private boolean switchPage(float distanceX) {
				final long switchPageInterval = System.currentTimeMillis()
						- mLastSwitchPageTime;

				if (switchPageInterval < MIN_SWITCH_PAGE_INTERVAL) {
					return true;
				}

				if (mLastMotionX < SWITCH_PAGE_SCALE * getWidth()
						&& distanceX < 0) {
					if (mColumnPagePositionManager.hasAnimation()
							&& mColumnPagePositionManager
									.getAnimationDirection()) {
						return true;
					}

					final int leftPage = mColumnPages.indexOf(mColumnPage) - 1;
					if (leftPage >= 0) {
						mColumnPage
								.onReleasePosition(mTakePosition, getWidth());
						mColumnPage = mColumnPages.get(leftPage);
						mTakePosition = ColumnPage.MAX_COLUMN_SIZE - 1;
						mColumnPage.onTakePosition(mTakePosition, getWidth());
						mColumnPagePositionManager.animate(true, false);
						mLastSwitchPageTime = System.currentTimeMillis();
						invalidate();
					}
					return true;
				} else if (mLastMotionX > (1 - SWITCH_PAGE_SCALE) * getWidth()
						&& distanceX > 0) {
					if (mColumnPagePositionManager.hasAnimation()
							&& !mColumnPagePositionManager
									.getAnimationDirection()) {
						return true;
					}

					final int rightPage = mColumnPages.indexOf(mColumnPage) + 1;
					if (rightPage < mColumnPages.size()) {
						mColumnPage
								.onReleasePosition(mTakePosition, getWidth());
						mColumnPage = mColumnPages.get(rightPage);
						mTakePosition = ColumnPage.MAX_COLUMN_SIZE - 1;
						mColumnPage.onTakePosition(mTakePosition, getWidth());
						mColumnPagePositionManager.animate(false, false);
						mLastSwitchPageTime = System.currentTimeMillis();
						invalidate();
					}
					return true;
				}
				return false;
			}
		}

		/**
		 * Flag to determine whether there's a {@link Column} has been delete
		 * during the last down event.
		 */
		private boolean mDeleteColumn = false;

		@Override
		public void onDown(MotionEvent event, Column column, ColumnPage page,
				int takePosition, RectF rectF) {
			final float relativeX = event.getX() - rectF.right;
			final float relativeY = event.getY() - rectF.top;

			if (mMode == MODE_EDIT && column.isDelete(relativeX, relativeY)) {
				if (page.isEmpty()) {
					int pageIndex = mColumnPages.indexOf(page);
					mColumnPages.remove(page);
					mColumnPagePositionManager.onRemovePage(pageIndex);

					if (pageIndex == 0) {
						mColumnPagePositionManager.animate(false, false);
					} else {
						mColumnPagePositionManager.animate(true, false);
					}
				} else {
					page.onReleasePosition(takePosition, getWidth());
				}
				mDeleteColumn = true;
			} else {
				mPressDownColumn.extract(column, page, takePosition, rectF);
			}
			invalidate();
		}

		@Override
		public boolean onDown(MotionEvent e) {
			mDeleteColumn = false;

			if (mColumnPagePositionManager.hasAnimation()) {
				mColumnPagePositionManager.pause();
			} else {
				final float columnPagePosition = mColumnPagePositionManager
						.getColumnPagePosition();
				final int page = (int) Math.floor(columnPagePosition + 0.5f);
				if (!mColumnPages.isEmpty()) {
					mColumnPages.get(page).onDown(e, this);
				}
			}
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			boolean redraw = false;
			if (!mPressDownColumn.isEmpty()) {
				mPressDownColumn.release();
				redraw = true;
			}

			if (Math.abs(velocityX) > 500) {
				mColumnPagePositionManager.animate(velocityX > 0, true);
				redraw = true;
			} else {
				redraw |= mColumnPagePositionManager.resume();
			}

			if (redraw) {
				invalidate();
			}
			return true;
		}

		@Override
		public void onShowPress(MotionEvent e) {
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			boolean redraw = false;
			if (!mPressDownColumn.isEmpty()) {
				if (mMode == MODE_EXPLORE) {
					mPressDownColumn.onSingleTapUp();
				}
				mPressDownColumn.release();
				redraw = true;
			}

			if (mMode == MODE_EDIT && !mDeleteColumn) {
				for (int i = 0; i < mColumnPages.size(); i++) {
					ColumnPage page = mColumnPages.get(i);
					if (page.isEmpty()) {
						mColumnPages.remove(page);
						mColumnPagePositionManager.onRemovePage(i);
						i--;
					}
				}
				mMode = MODE_EXPLORE;
				redraw = true;
			}

			if (mColumnPagePositionManager.getColumnPagePosition() > mColumnPages
					.size() - 1) {
				mColumnPagePositionManager.setColumnPagePosition(mColumnPages
						.size() - 1);
				redraw = true;
			}

			if (redraw) {
				invalidate();
			}
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (!mPressDownColumn.isEmpty() && !mPressDownColumn.isLongPress()) {
				mPressDownColumn.release();
			}

			mColumnPagePositionManager.scroll(distanceX);
			invalidate();
			return true;
		}

		@Override
		public void onLongPress(MotionEvent event) {
			if (!mPressDownColumn.isEmpty()) {
				mPressDownColumn.onLongPress(event);

				if (mMode != MODE_EDIT) {
					mColumnPages.add(new ColumnPage());
					mRotation = ROTATION;
					mMode = MODE_EDIT;
				}
				invalidate();
			}
		}
	}
}
