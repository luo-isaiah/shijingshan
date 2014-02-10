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

import org.json.JSONException;
import org.json.JSONObject;

import com.panguso.android.shijingshan.R;
import com.panguso.android.shijingshan.news.NewsPageActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.SparseIntArray;

/**
 * Represent a column.
 * 
 * @author Luo Yinzhuo
 * @date 2013-8-7
 */
public class Column {
	/** The column icon array. */
	private static final SparseIntArray COLUMN_ICON_ARRAY = new SparseIntArray();

	static {
		// zheng ce fa gui
		COLUMN_ICON_ARRAY.put(100, R.drawable.column_zcfg);
		// gong gao lan
		COLUMN_ICON_ARRAY.put(101, R.drawable.column_ggl);
		// hang ye dong tai
		COLUMN_ICON_ARRAY.put(102, R.drawable.column_hydt);
		// ren cai fu wu
		COLUMN_ICON_ARRAY.put(103, R.drawable.column_rcfw);
		// tou zi xuan chuan
		COLUMN_ICON_ARRAY.put(104, R.drawable.column_tzxc);
		// cai gou zhao biao
		COLUMN_ICON_ARRAY.put(105, R.drawable.column_cgzb);
		// yi jian fan kui
		COLUMN_ICON_ARRAY.put(106, R.drawable.column_yjfk);
		// ling dao xin xiang
		COLUMN_ICON_ARRAY.put(107, R.drawable.column_ldxx);
		// ban shi zhi nan
		COLUMN_ICON_ARRAY.put(113, R.drawable.column_bszn);

		// Add column
		COLUMN_ICON_ARRAY.put(AddColumn.ID, R.drawable.column_add);
	}

	/** The paint shared by all the columns. */
	private static final Paint PAINT = new Paint(Paint.ANTI_ALIAS_FLAG
			| Paint.DITHER_FLAG);

	/** The column id. */
	private final int mId;
	/** The column display name. */
	private final String mName;
	/** The subscribe flag. */
	private final boolean mSubscribe;
	/** The text font size. */
	private final float mTextSize;
	/** The text offset from bottom. */
	private final float mTextOffsetBottom;
	/** The background color. */
	private final int mBackgroundColor;
	/** The column's icon. */
	private final Drawable mIcon;
	/** The delete mark. */
	private final Drawable mDelete;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + mId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Column other = (Column) obj;
		if (mId != other.mId) {
			return false;
		}
		return true;
	}

	/** The key to store column id. */
	public static final String KEY_ID = "id";
	/** The key to store column name. */
	public static final String KEY_NAME = "name";
	/** The key to store subscribe flag. */
	public static final String KEY_SUBSCRIBE = "subscribe";

	/**
	 * Get the column's data in JSON format.
	 * 
	 * @return The column's data in JSON format.
	 * @throws JSONException
	 *             If error occurs in JSON format.
	 * @author Luo Yinzhuo
	 */
	public final String getJson() throws JSONException {
		JSONObject column = new JSONObject();
		column.put(KEY_ID, mId);
		column.put(KEY_NAME, mName);
		column.put(KEY_SUBSCRIBE, mSubscribe);
		return column.toString();
	}

	/**
	 * Construct a {@link Column} from its data in JSON format.
	 * 
	 * @param context
	 *            The context.
	 * @param json
	 *            The JSON format data.
	 * @return The {@link Column}.
	 * @throws JSONException
	 *             If error occurs in JSON format.
	 * @author Luo Yinzhuo
	 */
	public static Column parse(Context context, String json)
			throws JSONException {
		JSONObject column = new JSONObject(json);
		final int id = column.getInt(KEY_ID);

		if (id == AddColumn.ID) {
			return AddColumn.getInstance(context);
		} else {
			return new Column(context, id, column.getString(KEY_NAME),
					column.getBoolean(KEY_SUBSCRIBE));
		}
	}

	/**
	 * Construct a new instance.
	 * 
	 * @param context
	 *            The context,
	 * @param id
	 *            The column id.
	 * @param name
	 *            The column name.
	 * @param subscribe
	 *            True if the column need to be subscribed, otherwise false.
	 */
	Column(Context context, int id, String name, boolean subscribe) {
		mId = id;
		mName = name;
		mSubscribe = subscribe;

		Resources resources = context.getResources();
		mTextSize = resources.getDimension(R.dimen.column_font_size);
		mTextOffsetBottom = resources
				.getDimension(R.dimen.column_font_offset_bottom);

		if (mSubscribe) {
			mBackgroundColor = resources.getColor(R.color.column_subscribe);
			if (mId != AddColumn.ID) {
				mDelete = resources.getDrawable(R.drawable.delete_mark);
				mDelete.setBounds(0, 0, mDelete.getIntrinsicWidth(),
						mDelete.getIntrinsicHeight());
			} else {
				mDelete = null;
			}
		} else {
			mBackgroundColor = resources.getColor(R.color.column_public);
			mDelete = null;
		}

		int icon = COLUMN_ICON_ARRAY.get(mId);
		if (icon == 0) {
			mIcon = null;
		} else {
			mIcon = resources.getDrawable(icon);
			mIcon.setBounds(0, 0, mIcon.getIntrinsicWidth(),
					mIcon.getIntrinsicHeight());
		}
	}

	public String getName() {
		return mName;
	}

	/** The column is idle. */
	private static final int STATE_IDLE = 0;
	/** The column is being pressed. */
	private static final int STATE_PRESSED = 1;
	/** The column is being long pressed. */
	private static final int STATE_LONG_PRESSED = 2;

	/** The column state. */
	private int mState = STATE_IDLE;

	/**
	 * Invoked by {@link ColumnPage} to draw the column on it.
	 * 
	 * @param canvas
	 *            The {@link ColumnPageView}'s canvas.
	 * @param rect
	 *            The column rectangle.
	 * @param rotation
	 *            The canvas rotation before drawing.
	 */
	public final void draw(Canvas canvas, RectF rect, float rotation) {
		canvas.save();
		canvas.rotate(rotation, rect.centerX(), rect.centerY());

		// Draw the background color.
		PAINT.setColor(mBackgroundColor);
		canvas.drawRect(rect, PAINT);

		// Draw the icon in the rect's center.
		if (mIcon != null) {
			canvas.save();
			canvas.translate(rect.centerX() - mIcon.getIntrinsicWidth() / 2,
					rect.centerY() - mIcon.getIntrinsicHeight() / 2);
			mIcon.draw(canvas);
			canvas.restore();
		}

		// Draw the text.
		if (mName != null) {
			PAINT.setColor(Color.WHITE);
			PAINT.setTextSize(mTextSize);
			float textX = rect.centerX() - PAINT.measureText(mName) / 2;
			float textY = rect.bottom - mTextOffsetBottom;
			canvas.drawText(mName, textX, textY, PAINT);
		}

		// Draw the delete mark.
		if (rotation != 0 && mDelete != null) {
			canvas.save();
			canvas.translate(rect.right - mDelete.getIntrinsicWidth(), rect.top);
			mDelete.draw(canvas);
			canvas.restore();
		}

		canvas.restore();
	}

	/**
	 * Check if a down event is on the delete mark.
	 * 
	 * @param x
	 *            The event X coordinate.
	 * @param y
	 *            The event Y coordinate.
	 * @return True if the down event is on the delete icon, otherwise false.
	 * @author Luo Yinzhuo
	 */
	public boolean isDelete(float x, float y) {
		return mDelete != null
				&& mDelete.getBounds().contains(
						(int) (x + mDelete.getIntrinsicWidth()), (int) y);
	}

	/**
	 * Invoked when a single tap occurs on the {@link Column}.
	 * 
	 * @param context
	 *            The system context.
	 * 
	 * @author Luo Yinzhuo
	 */
	public void onSingleTapUp(Context context) {
		Intent intent = new Intent(context, NewsPageActivity.class);
		intent.putExtra(KEY_ID, mId);
		intent.putExtra(KEY_NAME, mName);
		context.startActivity(intent);
	}

	@Override
	public String toString() {
		return "Column [mId=" + mId + ", mName=" + mName + ", mSubscribe="
				+ mSubscribe + "]";
	}
}

/**
 * Specified class for add column.
 * 
 * @author Luo Yinzhuo
 */
final class AddColumn extends Column {
	/** The id. */
	static final int ID = -1;

	/** The single instance. */
	private static AddColumn SINGLE_INSTANCE;

	/**
	 * Construct a new instance.
	 * 
	 * @param context
	 *            The context.
	 */
	private AddColumn(Context context) {
		super(context, ID, "", true);
	}

	/**
	 * Get the single {@link AddColumn} instance.
	 * 
	 * @param context
	 *            The {@link Context}.
	 * @return The single {@link AddColumn} instance.
	 * @author Luo Yinzhuo
	 */
	public synchronized static AddColumn getInstance(Context context) {
		if (SINGLE_INSTANCE == null) {
			SINGLE_INSTANCE = new AddColumn(context);
		}
		return SINGLE_INSTANCE;
	}

	@Override
	public void onSingleTapUp(Context context) {
		if (context instanceof ColumnPageActivity) {
			((ColumnPageActivity) context).subscribe();
		}
	}
}
