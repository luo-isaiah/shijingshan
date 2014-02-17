package com.panguso.android.shijingshan.news;

import java.util.ArrayList;
import java.util.List;

import com.panguso.android.shijingshan.net.NetworkService;
import com.panguso.android.shijingshan.net.NetworkService.NewsImageRequestListener;
import com.panguso.android.shijingshan.news.News.Status;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

/**
 * The article page view.
 * 
 * @author Luo Yinzhuo
 */
public class NewsPageView extends View implements NewsImageRequestListener,
		OnGestureListener {
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
	public NewsPageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mGestureDetector = new GestureDetector(context, this);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		NewsPage.setSize(w, h);
		if (mNewsPages.size() > 0) {
			invalidate();
		}
	}

	/** The {@link NewsPage} list. */
	private final List<NewsPage> mNewsPages = new ArrayList<NewsPage>();
	/** The news page position. */
	private float mNewsPagePosition = 0.0f;

	/**
	 * Initialization.
	 * 
	 * @param newses
	 *            The {@link News} list.
	 * 
	 * @author Luo Yinzhuo
	 */
	public void initialize(List<News> newses) {
		mNewsPages.clear();
		mNewsPagePosition = 0;

		Resources resources = getResources();
		NewsPage page = new NewsPage(resources);
		mNewsPages.add(page);
		for (int i = 0; i < newses.size(); i++) {
			News news = newses.get(i);

			if (!page.addNews(news)) {
				page = new NewsPage(resources);
				mNewsPages.add(page);
				page.addNews(news);
			}
		}
		invalidate();
	}

	/** The invalidate interval. */
	private static final int INVALIDATE_INTERVAL = 50;
	/** The animation. */
	private NewsPageAnimation mNewsPageAnimation;

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.WHITE);

		if (mNewsPageAnimation != null) {
			mNewsPagePosition = mNewsPageAnimation.getPagePosition();
			if (mNewsPageAnimation.mComplete) {
				mNewsPageAnimation = null;
			} else {
				postInvalidateDelayed(INVALIDATE_INTERVAL);
			}
		}

		if (this.mNewsPages.size() > 0) {
			final int left = (int) Math.floor(mNewsPagePosition);
			float offsetX = (left - mNewsPagePosition)
					* NewsPageView.this.getWidth();
			canvas.save();
			canvas.translate(offsetX, 0);

			if (left >= 0 && left < this.mNewsPages.size()) {
				NewsPage leftPage = this.mNewsPages.get(left);
				leftPage.draw(canvas, left, this);
			}

			final int right = left + 1;
			if (right < this.mNewsPages.size()) {
				NewsPage rightPage = this.mNewsPages.get(right);
				canvas.translate(NewsPageView.this.getWidth(), 0);
				rightPage.draw(canvas, right, this);
			}

			canvas.restore();
		}
	}

	@Override
	public void onNewsImageResponseSuccess(int page) {
		if (Math.abs(mNewsPagePosition - page) < 1) {
			postInvalidate();
		}
	}

	@Override
	public void onNewsImageResponseFailed(int page, String imageURL) {
		if (Math.abs(mNewsPagePosition - page) < 1) {
			NetworkService.getNewsImage(page, imageURL, this);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mGestureDetector.onTouchEvent(event) || onUp(event);
	}

	/** The pressing news. */
	private News mPressingNews;
	/** The page position when {@link onDown(MotionEvent)} invoked. */
	private float mOnDownPagePosition;

	@Override
	public boolean onDown(MotionEvent e) {
		if (mNewsPageAnimation != null) {
			mNewsPagePosition = mNewsPageAnimation.getPagePosition();
			mNewsPageAnimation = null;
		}
		mOnDownPagePosition = mNewsPagePosition;

		int page = (int) Math.floor(mNewsPagePosition + 0.5f);
		if (Math.abs(mNewsPagePosition - page) < 1E-6
				&& page < mNewsPages.size()) {
			NewsPage newsPage = mNewsPages.get(page);
			mPressingNews = newsPage.onDown(e);

			if (mPressingNews != null) {
				mPressingNews.setStatus(Status.PRESS);
				invalidate();
			}
		}
		return true;
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		if (mPressingNews != null) {
			mPressingNews.setStatus(Status.NORMAL);
			invalidate();
			mPressingNews.onSingleTapUp(getContext());
			return true;
		}
		return false;
	}

	/** The mount of bouncing value. */
	private static final float BOUNCING = 0.3f;

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if (mPressingNews != null) {
			mPressingNews.setStatus(Status.NORMAL);
			mPressingNews = null;
		}

		final float leftEdge = 0;
		final float rightEdge = mNewsPages.size() > 0 ? mNewsPages.size() - 1
				: 0;

		final float estimateColumnPagePosition;
		if (mNewsPagePosition < leftEdge || mNewsPagePosition > rightEdge) {
			estimateColumnPagePosition = mNewsPagePosition + distanceX
					/ getWidth() * BOUNCING;
		} else {
			estimateColumnPagePosition = mNewsPagePosition + distanceX
					/ getWidth();
		}

		final float leftBorder = -BOUNCING;
		final float rightBorder = rightEdge + BOUNCING;

		if (distanceX > 0) {
			mNewsPagePosition = Math
					.max(estimateColumnPagePosition, leftBorder);
		} else {
			mNewsPagePosition = Math.min(estimateColumnPagePosition,
					rightBorder);
		}
		invalidate();
		return true;
	}

	/** The animation basic duration time. */
	private static final long DURATION_BASE = 300L;

	/**
	 * The animation specified for {@link NewsPageView}.
	 * 
	 * @author Luo Yinzhuo
	 */
	private static class NewsPageAnimation {
		/**
		 * The bounce type enumeration.
		 * 
		 * @author Luo Yinzhuo
		 */
		private enum Bounce {
			NONE, LEFT, RIGHT
		}

		/** The bounce type. */
		private final Bounce mBounce;
		/** The animation start time. */
		private final long mStartTime;
		/** The animation start page position. */
		private final float mStartPagePosition;
		/** The target page position. */
		private final int mTargetPagePosition;
		/** The duration. */
		private final long mDuration;

		/**
		 * Construct a new instance.
		 * 
		 * @param startPagePosition
		 *            The page position when animation start.
		 * @param onDownPagePosition
		 *            The page position when {@link onDown(MotionEvent)}
		 *            invoked.
		 * @param direction
		 *            The animation direction.
		 * @param minTargetPagePosition
		 *            The minimum page position could be target.
		 * @param maxTargetPagePosition
		 *            The maximum page position could be target.
		 */
		public NewsPageAnimation(float startPagePosition,
				float onDownPagePosition, boolean direction,
				int minTargetPagePosition, int maxTargetPagePosition) {
			mStartTime = System.currentTimeMillis();
			mStartPagePosition = startPagePosition;
			mDuration = DURATION_BASE;

			int targetPagePosition;
			if (direction) {
				targetPagePosition = (int) Math.floor(onDownPagePosition) - 1;
				if (targetPagePosition < minTargetPagePosition) {
					targetPagePosition = minTargetPagePosition;
					mBounce = Bounce.LEFT;
				} else {
					mBounce = Bounce.NONE;
				}
			} else {
				targetPagePosition = (int) Math.floor(onDownPagePosition) + 1;
				if (targetPagePosition > maxTargetPagePosition) {
					targetPagePosition = maxTargetPagePosition;
					mBounce = Bounce.RIGHT;
				} else {
					mBounce = Bounce.NONE;
				}
			}
			mTargetPagePosition = targetPagePosition;
		}

		/**
		 * Construct a new instance.
		 * 
		 * @param startPagePosition
		 *            The page position when animation start.
		 * @param targetPagePosition
		 *            The page position which animation target for.
		 */
		public NewsPageAnimation(float startPagePosition, int targetPagePosition) {
			mBounce = Bounce.NONE;
			mStartTime = System.currentTimeMillis();
			mStartPagePosition = startPagePosition;
			mTargetPagePosition = targetPagePosition;
			mDuration = (long) (DURATION_BASE * Math.abs(mTargetPagePosition
					- mStartPagePosition));
		}

		/** The animation complete flag. */
		private boolean mComplete = false;

		/**
		 * Get the current page position.
		 * 
		 * @return The current page position.
		 * @author Luo Yinzhuo
		 */
		public float getPagePosition() {
			long passTime = System.currentTimeMillis() - mStartTime;
			if (passTime >= mDuration) {
				mComplete = true;
				return mTargetPagePosition;
			} else {
				float estimateNewsPagePosition;
				if (mBounce == Bounce.RIGHT) {
					estimateNewsPagePosition = (mTargetPagePosition + 2
							* BOUNCING - mStartPagePosition)
							* passTime / mDuration + mStartPagePosition;
					if (estimateNewsPagePosition > mTargetPagePosition
							+ BOUNCING) {
						estimateNewsPagePosition = 2
								* (mTargetPagePosition + BOUNCING)
								- estimateNewsPagePosition;
					}
				} else if (mBounce == Bounce.LEFT) {
					estimateNewsPagePosition = (mTargetPagePosition - 2
							* BOUNCING - mStartPagePosition)
							* passTime / mDuration + mStartPagePosition;
					if (estimateNewsPagePosition < mTargetPagePosition
							- BOUNCING) {
						estimateNewsPagePosition = 2
								* (mTargetPagePosition - BOUNCING)
								- estimateNewsPagePosition;
					}
				} else {
					estimateNewsPagePosition = (mTargetPagePosition - mStartPagePosition)
							* passTime / mDuration + mStartPagePosition;
				}
				return estimateNewsPagePosition;
			}
		}
	}

	/**
	 * Invoked when the {@link MotionEvent#ACTION_UP} motion event is not
	 * handled.
	 * 
	 * @param e
	 *            The event.
	 * @return True if handled, otherwise false.
	 */
	private boolean onUp(MotionEvent e) {
		mNewsPageAnimation = new NewsPageAnimation(mNewsPagePosition,
				(int) Math.floor(mNewsPagePosition + 0.5f));
		invalidate();
		return true;
	}

	/** The threshold to determine whether a fling leads to an animation. */
	private final static float ANIMATION_VELOCITY_THRESHOLD = 500;

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (Math.abs(velocityX) > ANIMATION_VELOCITY_THRESHOLD) {
			mNewsPageAnimation = new NewsPageAnimation(mNewsPagePosition,
					mOnDownPagePosition, velocityX > 0, 0,
					mNewsPages.size() - 1);
			invalidate();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}
}
