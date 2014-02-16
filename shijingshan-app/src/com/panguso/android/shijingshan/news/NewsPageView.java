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

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.WHITE);

		// TODO: If there's animation, update mNewsPagePosition

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

		// if (this.mNewsPagePositionManager.hasAnimation()) {
		// Log.d("NewsPageManager", "draw has animation.");
		// NewsPageView.this.invalidate();
		// }
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
		return mGestureDetector.onTouchEvent(event);
	}

	/** The pressing news. */
	private News mPressingNews;

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO: stop the animation.

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
	public void onShowPress(MotionEvent e) {
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

//	/**
//	 * Specified for manage the news page animation.
//	 * 
//	 * @author Luo Yinzhuo
//	 */
//	private class NewsPageAnimation {
//		
//		
//		/** The no bounce. */
//		private static final int BOUNCE_NONE = 0;
//		/** The left bounce. */
//		private static final int BOUNCE_LEFT = 1;
//		/** The right bounce. */
//		private static final int BOUNCE_RIGHT = 2;
//
//		/** The animation start time. */
//		private long mStartTime = 0L;
//		/** The animation start page position. */
//		private float mStartPagePosition = 0f;
//		/** The target page position. */
//		private int mTargetPagePosition = 0;
//		/** The bounce. */
//		private int mBouncing = BOUNCE_NONE;
//
//		/** The duration. */
//		private long mDuration;
//
//		/**
//		 * Construct a new instance.
//		 * 
//		 * @param direction
//		 *            True to animate to the left page, otherwise to the
//		 *            right page.
//		 * @param bouncing
//		 *            True to add bounce effect if need, otherwise
//		 *            false.
//		 */
//		public NewsPageAnimation(boolean direction, boolean bouncing) {
//			this.mStartTime = System.currentTimeMillis();
//			this.mStartPagePosition = NewsPagePositionManager.this.mNewsPagePosition;
//			this.mDuration = DURATION_BASE;
//			if (direction) {
//				this.mTargetPagePosition = (int) Math
//						.floor(this.mStartPagePosition + 0.5f) - 1;
//				if (this.mTargetPagePosition < 0) {
//					this.mTargetPagePosition = 0;
//					if (bouncing) {
//						this.mBouncing = BOUNCE_LEFT;
//					}
//				}
//			} else {
//				this.mTargetPagePosition = (int) Math
//						.floor(this.mStartPagePosition + 0.5f) + 1;
//				final int maxColumnPagePosition = NewsPageManager.this.mNewsPages
//						.size() - 1;
//				if (this.mTargetPagePosition > maxColumnPagePosition) {
//					this.mTargetPagePosition = maxColumnPagePosition;
//					if (bouncing) {
//						this.mBouncing = BOUNCE_RIGHT;
//					}
//				}
//			}
//		}
//
//		/**
//		 * Construct a new instance.
//		 * 
//		 * @param targetPagePosition
//		 *            The target page position.
//		 */
//		public NewsPageAnimation(int targetPagePosition) {
//			this.mStartTime = System.currentTimeMillis();
//			this.mStartPagePosition = NewsPagePositionManager.this.mNewsPagePosition;
//			this.mDuration = (long) (DURATION_BASE * Math
//					.abs(this.mTargetPagePosition
//							- this.mStartPagePosition));
//			this.mTargetPagePosition = targetPagePosition;
//		}
//
//		/**
//		 * Append one more animate action based on the original one.
//		 * 
//		 * @param direction
//		 *            The new animate action direction, True to animate
//		 *            to the left page, otherwise to the right page.
//		 * @param bouncing
//		 *            True to add bounce effect if need, otherwise
//		 *            false.
//		 * @author Luo Yinzhuo
//		 */
//		public void animate(boolean direction, boolean bouncing) {
//			this.mStartTime = System.currentTimeMillis();
//			this.mStartPagePosition = NewsPagePositionManager.this.mNewsPagePosition;
//			this.mDuration = DURATION_BASE;
//			if (direction) {
//				this.mTargetPagePosition--;
//				if (this.mTargetPagePosition < 0) {
//					this.mTargetPagePosition = 0;
//					if (bouncing) {
//						this.mBouncing = BOUNCE_LEFT;
//					}
//				}
//			} else {
//				this.mTargetPagePosition++;
//				final int maxColumnPagePosition = NewsPageManager.this.mNewsPages
//						.size() - 1;
//				if (this.mTargetPagePosition > maxColumnPagePosition) {
//					this.mTargetPagePosition = maxColumnPagePosition;
//					if (bouncing) {
//						this.mBouncing = BOUNCE_RIGHT;
//					}
//				}
//			}
//		}
//
//		/**
//		 * Get the current news page position.
//		 * 
//		 * @return The current news page position.
//		 * @author Luo Yinzhuo
//		 */
//		public float getNewsPagePosition() {
//			long passTime = System.currentTimeMillis()
//					- this.mStartTime;
//			if (passTime >= this.mDuration) {
//				NewsPagePositionManager.this.onAnimationFinished();
//				return this.mTargetPagePosition;
//			} else {
//				float estimateNewsPagePosition;
//				if (this.mBouncing == BOUNCE_RIGHT) {
//					estimateNewsPagePosition = (this.mTargetPagePosition
//							+ 2 * BOUNCING - this.mStartPagePosition)
//							* passTime
//							/ this.mDuration
//							+ this.mStartPagePosition;
//					if (estimateNewsPagePosition > this.mTargetPagePosition
//							+ BOUNCING) {
//						estimateNewsPagePosition = 2
//								* (this.mTargetPagePosition + BOUNCING)
//								- estimateNewsPagePosition;
//					}
//				} else if (this.mBouncing == BOUNCE_LEFT) {
//					estimateNewsPagePosition = (this.mTargetPagePosition
//							- 2 * BOUNCING - this.mStartPagePosition)
//							* passTime
//							/ this.mDuration
//							+ this.mStartPagePosition;
//					if (estimateNewsPagePosition < this.mTargetPagePosition
//							- BOUNCING) {
//						estimateNewsPagePosition = 2
//								* (this.mTargetPagePosition - BOUNCING)
//								- estimateNewsPagePosition;
//					}
//				} else {
//					estimateNewsPagePosition = (this.mTargetPagePosition - this.mStartPagePosition)
//							* passTime
//							/ this.mDuration
//							+ this.mStartPagePosition;
//				}
//				return estimateNewsPagePosition;
//			}
//		}
//	}
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}
}
