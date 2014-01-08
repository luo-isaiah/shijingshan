package com.panguso.android.shijingshan.column;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
		this.mColumnPageManager = new ColumnPageManager();
		this.mGestureDetector = new GestureDetector(context,
				this.mColumnPageManager);
	}

	/**
	 * Initialize the {@link ColumnPageManager}.
	 * 
	 * @param columnPages
	 *            The list of {@link ColumnPage}.
	 * @param columnPagePosition
	 *            The initial column page position.
	 * @author Luo Yinzhuo
	 */
	public void initialize(List<ColumnPage> columnPages, int columnPagePosition) {
		this.mColumnPageManager.initialize(columnPages, columnPagePosition);
		this.invalidate();
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
		this.mColumnPageManager.initialize(json);
		this.invalidate();
	}

	/**
	 * Save the current column page data in JSON format to the
	 * {@link SharedPreferences}.
	 * 
	 * @param sharedPreferences
	 *            The {@link SharedPreferences}.
	 * @param key
	 *            The key to store.
	 * @throws JSONException
	 *             If error occurs in JSON format.
	 * @author Luo Yinzhuo
	 */
	public void save(SharedPreferences sharedPreferences, String key)
			throws JSONException {
		this.mColumnPageManager.save(sharedPreferences, key);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		this.mColumnPageManager.draw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.mGestureDetector.onTouchEvent(event)
				|| this.mColumnPageManager.onTouchEvent(event);
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
		 * Initialize itself from a list of {@link ColumnPage}s.
		 * 
		 * @param columnPages
		 *            The list of {@link ColumnPage}s.
		 * @param columnPagePosition
		 *            The initial column page position.
		 * @author Luo Yinzhuo
		 */
		public void initialize(List<ColumnPage> columnPages,
				int columnPagePosition) {
			this.mColumnPages.clear();
			this.mColumnPages.addAll(columnPages);
			this.mColumnPagePositionManager
					.setColumnPagePosition(columnPagePosition);
			this.mMode = MODE_EXPLORE;
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
			JSONObject root = new JSONObject(json);
			JSONArray columnPages = root.getJSONArray(KEY_COLUMN_PAGES);
			this.mColumnPages.clear();
			for (int i = 0; i < columnPages.length(); i++) {
				this.mColumnPages.add(ColumnPage.parse(ColumnPageView.this
						.getContext(), columnPages.get(i).toString()));
			}
		}

		/**
		 * Save the current column page data in JSON format to the
		 * {@link SharedPreferences}.
		 * 
		 * @param sharedPreferences
		 *            The {@link SharedPreferences}.
		 * @param key
		 *            The key to store.
		 * @throws JSONException
		 *             If error occurs in JSON format.
		 * @author Luo Yinzhuo
		 */
		public void save(SharedPreferences sharedPreferences, String key)
				throws JSONException {
			final int columnPagePosition = (int) Math
					.floor(this.mColumnPagePositionManager
							.getColumnPagePosition() + 0.5f);
			JSONObject root = new JSONObject();
			root.put(KEY_COLUMN_PAGE_POSITION, columnPagePosition);

			JSONArray columnPages = new JSONArray();
			for (ColumnPage page : this.mColumnPages) {
				columnPages.put(new JSONArray(page.getJson()));
			}
			root.put(KEY_COLUMN_PAGES, columnPages);

			Editor editor = sharedPreferences.edit();
			editor.putString(key, root.toString());
			editor.commit();
		}

		/** The explore mode. */
		private static final int MODE_EXPLORE = 0;
		/** The edit mode. */
		private static final int MODE_EDIT = 1;
		/** The mode. */
		private int mMode = MODE_EXPLORE;

		/**
		 * Draw the column pages to the canvas.
		 * 
		 * @param canvas
		 *            The {@link ColumnPageView}'s canvas.
		 * @author Luo Yinzhuo
		 */
		public void draw(Canvas canvas) {
			final float columnPagePosition = this.mColumnPagePositionManager
					.getColumnPagePosition();
			final int jumpIndex = this.mPressDownColumn.isEmpty() ? ColumnPage.MAX_COLUMN_SIZE
					: this.mPressDownColumn.getTakePosition();
			final boolean editing = this.mMode == MODE_EDIT;
			boolean redraw = false;

			if (this.mColumnPages.size() > 0) {
				final int left = (int) Math.floor(columnPagePosition);
				float offsetX = (left - columnPagePosition)
						* ColumnPageView.this.getWidth();
				canvas.save();
				canvas.translate(offsetX, 0);

				if (left >= 0 && left < this.mColumnPages.size()) {
					ColumnPage leftPage = this.mColumnPages.get(left);
					leftPage.draw(canvas, offsetX,
							ColumnPageView.this.getWidth(), jumpIndex, editing);
					redraw |= leftPage.hasColumnAnimation();
				}

				final int right = left + 1;
				if (right < this.mColumnPages.size()) {
					ColumnPage rightPage = this.mColumnPages.get(right);
					canvas.translate(ColumnPageView.this.getWidth(), 0);
					rightPage.draw(canvas,
							offsetX + ColumnPageView.this.getWidth(),
							ColumnPageView.this.getWidth(), jumpIndex, editing);
					redraw |= rightPage.hasColumnAnimation();
				}

				canvas.restore();
			}

			if (!this.mPressDownColumn.isEmpty()) {
				this.mPressDownColumn.draw(canvas);
			}

			// Check whether there are any column page animation or column
			// animation to draw again.
			if (redraw || this.mColumnPagePositionManager.hasAnimation()) {
				ColumnPageView.this.invalidate();
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
				if (!this.mPressDownColumn.isEmpty()
						&& this.mPressDownColumn.isLongPress()) {
					this.mPressDownColumn.onScroll(event);
					ColumnPageView.this.invalidate();
				}
				break;
			case MotionEvent.ACTION_UP:
				boolean redraw = false;
				if (!this.mPressDownColumn.isEmpty()) {
					this.mPressDownColumn.release();
					redraw = true;
				}

				if (this.mMode == MODE_EDIT) {
					// Remove the empty column page.
					for (int i = 0; i < this.mColumnPages.size() - 1; i++) {
						ColumnPage page = this.mColumnPages.get(i);
						if (page.isEmpty()) {
							this.mColumnPages.remove(page);
							this.mColumnPagePositionManager.onRemovePage(i);
							i--;
						}
					}

					// Add an extra empty column page at the end.
					if (!this.mColumnPages.get(this.mColumnPages.size() - 1)
							.isEmpty()) {
						this.mColumnPages.add(new ColumnPage());
					}
				}

				redraw |= this.mColumnPagePositionManager.resume();
				if (redraw) {
					ColumnPageView.this.invalidate();
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
				if (this.mColumnPageAnimation == null) {
					this.mColumnPageAnimation = new ColumnPageAnimation(
							direction, bouncing);
				} else {
					this.mColumnPageAnimation.animate(direction, bouncing);
				}
				this.mDirection = direction;
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
				final float rightEdge = ColumnPageManager.this.mColumnPages
						.size() > 0 ? ColumnPageManager.this.mColumnPages
						.size() - 1 : 0;
				final float rightBorder = rightEdge + BOUNCING;
				float estimateColumnPagePosition;
				if (this.mColumnPagePosition < leftEdge
						|| this.mColumnPagePosition > rightEdge) {
					estimateColumnPagePosition = this.mColumnPagePosition
							+ distanceX / ColumnPageView.this.getWidth()
							* BOUNCING;
				} else {
					estimateColumnPagePosition = this.mColumnPagePosition
							+ distanceX / ColumnPageView.this.getWidth();
				}

				if (distanceX > 0) {
					this.mColumnPagePosition = Math.max(
							estimateColumnPagePosition, leftBorder);
				} else {
					this.mColumnPagePosition = Math.min(
							estimateColumnPagePosition, rightBorder);
				}
			}

			/**
			 * Pause the animation.
			 * 
			 * @author Luo Yinzhuo
			 */
			public void pause() {
				this.mColumnPagePosition = this.mColumnPageAnimation
						.getColumnPagePosition();
				this.mColumnPageAnimation = null;
			}

			/**
			 * Resume to animate the nearest {@link ColumnPage}.
			 * 
			 * @return True if it needs to resume, otherwise false.
			 * @author Luo Yinzhuo
			 */
			public boolean resume() {
				if (this.mColumnPagePosition != Math
						.floor(this.mColumnPagePosition + 0.5f)) {
					this.mColumnPageAnimation = new ColumnPageAnimation(
							(int) Math.floor(this.mColumnPagePosition + 0.5f));
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
				return this.mColumnPageAnimation != null;
			}

			/**
			 * Get the animation direction.
			 * 
			 * @return True to animate to the left page, otherwise to the right
			 *         page.
			 * @author Luo Yinzhuo
			 */
			public boolean getAnimationDirection() {
				return this.mDirection;
			}

			/**
			 * Invoked when the column page animation is finished.
			 * 
			 * @author Luo Yinzhuo
			 */
			void onAnimationFinished() {
				this.mColumnPageAnimation = null;
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
					this.mStartTime = System.currentTimeMillis();
					this.mStartPagePosition = ColumnPagePositionManager.this.mColumnPagePosition;
					this.mDuration = DURATION_BASE;
					if (direction) {
						this.mTargetPagePosition = (int) Math
								.floor(this.mStartPagePosition + 0.5f) - 1;
						if (this.mTargetPagePosition < 0) {
							this.mTargetPagePosition = 0;
							if (bouncing) {
								this.mBouncing = BOUNCE_LEFT;
							}
						}
					} else {
						this.mTargetPagePosition = (int) Math
								.floor(this.mStartPagePosition + 0.5f) + 1;
						final int maxColumnPagePosition = ColumnPageManager.this.mColumnPages
								.size() - 1;
						if (this.mTargetPagePosition > maxColumnPagePosition) {
							this.mTargetPagePosition = maxColumnPagePosition;
							if (bouncing) {
								this.mBouncing = BOUNCE_RIGHT;
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
					this.mStartTime = System.currentTimeMillis();
					this.mStartPagePosition = ColumnPagePositionManager.this.mColumnPagePosition;
					this.mDuration = (long) (DURATION_BASE * Math
							.abs(this.mTargetPagePosition
									- this.mStartPagePosition));
					this.mTargetPagePosition = targetPagePosition;
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
					this.mStartTime = System.currentTimeMillis();
					this.mStartPagePosition = ColumnPagePositionManager.this.mColumnPagePosition;
					this.mDuration = DURATION_BASE;
					if (direction) {
						this.mTargetPagePosition--;
						if (this.mTargetPagePosition < 0) {
							this.mTargetPagePosition = 0;
							if (bouncing) {
								this.mBouncing = BOUNCE_LEFT;
							}
						}
					} else {
						this.mTargetPagePosition++;
						final int maxColumnPagePosition = ColumnPageManager.this.mColumnPages
								.size() - 1;
						if (this.mTargetPagePosition > maxColumnPagePosition) {
							this.mTargetPagePosition = maxColumnPagePosition;
							if (bouncing) {
								this.mBouncing = BOUNCE_RIGHT;
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
					long passTime = System.currentTimeMillis()
							- this.mStartTime;
					if (passTime >= this.mDuration) {
						ColumnPagePositionManager.this.onAnimationFinished();
						return this.mTargetPagePosition;
					} else {
						float estimateColumnPagePosition;
						if (this.mBouncing == BOUNCE_RIGHT) {
							estimateColumnPagePosition = (this.mTargetPagePosition
									+ 2 * BOUNCING - this.mStartPagePosition)
									* passTime
									/ this.mDuration
									+ this.mStartPagePosition;
							if (estimateColumnPagePosition > this.mTargetPagePosition
									+ BOUNCING) {
								estimateColumnPagePosition = 2
										* (this.mTargetPagePosition + BOUNCING)
										- estimateColumnPagePosition;
							}
						} else if (this.mBouncing == BOUNCE_LEFT) {
							estimateColumnPagePosition = (this.mTargetPagePosition
									- 2 * BOUNCING - this.mStartPagePosition)
									* passTime
									/ this.mDuration
									+ this.mStartPagePosition;
							if (estimateColumnPagePosition < this.mTargetPagePosition
									- BOUNCING) {
								estimateColumnPagePosition = 2
										* (this.mTargetPagePosition - BOUNCING)
										- estimateColumnPagePosition;
							}
						} else {
							estimateColumnPagePosition = (this.mTargetPagePosition - this.mStartPagePosition)
									* passTime
									/ this.mDuration
									+ this.mStartPagePosition;
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
					if (this.mTargetPagePosition >= i) {
						this.mTargetPagePosition -= 1;
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
				this.mColumnPagePosition = columnPagePosition;
			}

			/**
			 * Get the column page position.
			 * 
			 * @return The new column page position.
			 * @author Luo Yinzhuo
			 */
			public float getColumnPagePosition() {
				if (this.mColumnPageAnimation != null) {
					this.mColumnPagePosition = this.mColumnPageAnimation
							.getColumnPagePosition();
				}
				return this.mColumnPagePosition;
			}

			/**
			 * Invoked when a {@link ColumnPage} has been removed.
			 * 
			 * @param i
			 *            The removed page's index.
			 * @author Luo Yinzhuo
			 */
			public void onRemovePage(int i) {
				if (this.mColumnPagePosition >= i) {
					this.mColumnPagePosition -= 1;
				}

				if (this.mColumnPageAnimation != null) {
					this.mColumnPageAnimation.onRemovePage(i);
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
				this.mColumn = column;
				this.mColumnPage = page;
				this.mTakePosition = takePosition;
				this.mRectF = rectF;
				this.mLongPress = false;
			}

			/**
			 * Release the column back to the column page.
			 * 
			 * @author Luo Yinzhuo
			 */
			public void release() {
				if (this.mColumnPage.isFull()) {
					Column excess = this.mColumnPage.removeExcessColumn();
					ColumnPage nextPage = ColumnPageManager.this.mColumnPages
							.get(ColumnPageManager.this.mColumnPages
									.indexOf(this.mColumnPage) + 1);
					while (nextPage.isFull()) {
						Column temp = nextPage.removeExcessColumn();
						nextPage.addColumn(0, excess);
						nextPage = ColumnPageManager.this.mColumnPages
								.get(ColumnPageManager.this.mColumnPages
										.indexOf(nextPage) + 1);
						excess = temp;
					}
					nextPage.addColumn(0, excess);
				}

				this.mColumnPage.addColumn(this.mTakePosition, this.mColumn,
						this.mRectF);
				this.mColumn = null;
			}

			/**
			 * Check if the press down column exist or not.
			 * 
			 * @return True if the column is not null, else false.
			 * @author Luo Yinzhuo
			 */
			public boolean isEmpty() {
				return this.mColumn == null;
			}

			/**
			 * Get the taking position.
			 * 
			 * @return The taking position.
			 * @author Luo Yinzhuo
			 */
			public int getTakePosition() {
				return this.mTakePosition;
			}

			/**
			 * Draw the press down column.
			 * 
			 * @param canvas
			 *            The canvas.
			 * @author Luo Yinzhuo
			 */
			public void draw(Canvas canvas) {
				RectF rectF = new RectF(this.mRectF);
				if (this.mLongPress) {
					final float size = rectF.width() * 1.1f;
					final float centerX = rectF.centerX();
					final float centerY = rectF.centerY();
					rectF.set(centerX - size / 2, centerY - size / 2, centerX
							+ size / 2, centerY + size / 2);
				}

				this.mColumn.draw(canvas, rectF, false);
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
				this.mLongPress = true;
				this.mLastMotionX = event.getX();
				this.mLastMotionY = event.getY();
			}

			/**
			 * Check if the column being long pressed.
			 * 
			 * @return True if the column being long pressed, otherwise false.
			 * @author Luo Yinzhuo
			 */
			public boolean isLongPress() {
				return this.mLongPress;
			}

			/**
			 * Invoked when a single tap occurs on a {@link Column} when the
			 * mode is {@link ColumnPageManager#MODE_EXPLORE}.
			 * 
			 * @author Luo Yinzhuo
			 */
			public void onSingleTapUp() {
				this.mColumn.onSingleTapUp(ColumnPageView.this.getContext());
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
				final float distanceX = event.getX() - this.mLastMotionX;
				final float distanceY = event.getY() - this.mLastMotionY;
				this.mRectF.offset(distanceX, distanceY);

				this.mLastMotionX = event.getX();
				this.mLastMotionY = event.getY();

				if (this.switchPage(distanceX)) {
					return;
				}

				if (this.mColumnPage.isReleasePosition(this.mTakePosition,
						this.mRectF)) {
					final int takePosition = this.mColumnPage
							.getTakePosition(this.mRectF);

					if (takePosition != this.mTakePosition) {
						this.mColumnPage.onReleasePosition(this.mTakePosition,
								ColumnPageView.this.getWidth());
						this.mColumnPage.onTakePosition(takePosition,
								ColumnPageView.this.getWidth());
						this.mTakePosition = takePosition;
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
						- this.mLastSwitchPageTime;

				if (switchPageInterval < MIN_SWITCH_PAGE_INTERVAL) {
					return true;
				}

				if (this.mLastMotionX < SWITCH_PAGE_SCALE
						* ColumnPageView.this.getWidth()
						&& distanceX < 0) {
					if (ColumnPageManager.this.mColumnPagePositionManager
							.hasAnimation()
							&& ColumnPageManager.this.mColumnPagePositionManager
									.getAnimationDirection()) {
						return true;
					}

					final int leftPage = ColumnPageManager.this.mColumnPages
							.indexOf(this.mColumnPage) - 1;
					if (leftPage >= 0) {
						this.mColumnPage.onReleasePosition(this.mTakePosition,
								ColumnPageView.this.getWidth());
						this.mColumnPage = ColumnPageManager.this.mColumnPages
								.get(leftPage);
						this.mTakePosition = ColumnPage.MAX_COLUMN_SIZE - 1;
						this.mColumnPage.onTakePosition(this.mTakePosition,
								ColumnPageView.this.getWidth());
						ColumnPageManager.this.mColumnPagePositionManager
								.animate(true, false);
						this.mLastSwitchPageTime = System.currentTimeMillis();
						ColumnPageView.this.invalidate();
					}
					return true;
				} else if (this.mLastMotionX > (1 - SWITCH_PAGE_SCALE)
						* ColumnPageView.this.getWidth()
						&& distanceX > 0) {
					if (ColumnPageManager.this.mColumnPagePositionManager
							.hasAnimation()
							&& !ColumnPageManager.this.mColumnPagePositionManager
									.getAnimationDirection()) {
						return true;
					}

					final int rightPage = ColumnPageManager.this.mColumnPages
							.indexOf(this.mColumnPage) + 1;
					if (rightPage < ColumnPageManager.this.mColumnPages.size()) {
						this.mColumnPage.onReleasePosition(this.mTakePosition,
								ColumnPageView.this.getWidth());
						this.mColumnPage = ColumnPageManager.this.mColumnPages
								.get(rightPage);
						this.mTakePosition = ColumnPage.MAX_COLUMN_SIZE - 1;
						this.mColumnPage.onTakePosition(this.mTakePosition,
								ColumnPageView.this.getWidth());
						ColumnPageManager.this.mColumnPagePositionManager
								.animate(false, false);
						this.mLastSwitchPageTime = System.currentTimeMillis();
						ColumnPageView.this.invalidate();
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

			if (this.mMode == MODE_EDIT
					&& column.isDelete(relativeX, relativeY)) {
				if (page.isEmpty()) {
					int pageIndex = this.mColumnPages.indexOf(page);
					this.mColumnPages.remove(page);
					this.mColumnPagePositionManager.onRemovePage(pageIndex);

					if (pageIndex == 0) {
						this.mColumnPagePositionManager.animate(false, false);
					} else {
						this.mColumnPagePositionManager.animate(true, false);
					}
				} else {
					page.onReleasePosition(takePosition,
							ColumnPageView.this.getWidth());
				}
				this.mDeleteColumn = true;
			} else {
				this.mPressDownColumn
						.extract(column, page, takePosition, rectF);
			}
			ColumnPageView.this.invalidate();
		}

		@Override
		public boolean onDown(MotionEvent e) {
			this.mDeleteColumn = false;

			if (this.mColumnPagePositionManager.hasAnimation()) {
				this.mColumnPagePositionManager.pause();
			} else {
				final float columnPagePosition = this.mColumnPagePositionManager
						.getColumnPagePosition();
				final int page = (int) Math.floor(columnPagePosition + 0.5f);
				if (!this.mColumnPages.isEmpty()) {
					this.mColumnPages.get(page).onDown(e, this);
				}
			}
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			boolean redraw = false;
			if (!this.mPressDownColumn.isEmpty()) {
				this.mPressDownColumn.release();
				redraw = true;
			}

			if (Math.abs(velocityX) > 500) {
				this.mColumnPagePositionManager.animate(velocityX > 0, true);
				redraw = true;
			} else {
				redraw |= this.mColumnPagePositionManager.resume();
			}

			if (redraw) {
				ColumnPageView.this.invalidate();
			}
			return true;
		}

		@Override
		public void onShowPress(MotionEvent e) {
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			boolean redraw = false;
			if (!this.mPressDownColumn.isEmpty()) {
				if (this.mMode == MODE_EXPLORE) {
					this.mPressDownColumn.onSingleTapUp();
				}
				this.mPressDownColumn.release();
				redraw = true;
			}

			if (this.mMode == MODE_EDIT && !this.mDeleteColumn) {
				for (int i = 0; i < this.mColumnPages.size(); i++) {
					ColumnPage page = this.mColumnPages.get(i);
					if (page.isEmpty()) {
						this.mColumnPages.remove(page);
						this.mColumnPagePositionManager.onRemovePage(i);
						i--;
					}
				}
				this.mMode = MODE_EXPLORE;
				redraw = true;
			}

			redraw |= this.mColumnPagePositionManager.resume();
			if (redraw) {
				ColumnPageView.this.invalidate();
			}
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (!this.mPressDownColumn.isEmpty()
					&& !this.mPressDownColumn.isLongPress()) {
				this.mPressDownColumn.release();
			}

			this.mColumnPagePositionManager.scroll(distanceX);
			ColumnPageView.this.invalidate();
			return true;
		}

		@Override
		public void onLongPress(MotionEvent event) {
			if (!this.mPressDownColumn.isEmpty()) {
				this.mPressDownColumn.onLongPress(event);

				if (this.mMode != MODE_EDIT) {
					this.mColumnPages.add(new ColumnPage());
					this.mMode = MODE_EDIT;
				}
				ColumnPageView.this.invalidate();
			}
		}
	}
}
