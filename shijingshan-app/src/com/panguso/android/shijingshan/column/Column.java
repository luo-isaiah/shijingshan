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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * Represent a column.
 * 
 * @author Luo Yinzhuo
 * @date 2013-8-7
 */
public class Column {
	/** The paint shared by all the columns. */
	private static final Paint PAINT = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
	/** The text offset from bottom. */
	private static float OFFSET_BOTTOM;
	/** The delete mark. */
	private static Drawable DELETE_MARK;
	/** The background blue color. */
	private static int BACKGROUND_BLUE;
	/** The background orange color. */
	private static int BACKGROUND_ORANGE;

	/**
	 * Initialize the font size, text offset from bottom and delete mark.
	 * 
	 * @param fontSize The font size.
	 * @param offsetBottom The text offset from bottom.
	 * @param delete The delete mark.
	 * @param blue The background blue color.
	 * @param orange The background orange color.
	 * @author Luo Yinzhuo
	 */
	public static void initialize(float fontSize, float offsetBottom, Drawable delete, int blue,
	        int orange) {
		PAINT.setTextSize(fontSize);
		OFFSET_BOTTOM = offsetBottom;
		DELETE_MARK = delete;
		DELETE_MARK.setBounds(0, 0, DELETE_MARK.getIntrinsicWidth(),
		        DELETE_MARK.getIntrinsicHeight());
		BACKGROUND_BLUE = blue;
		BACKGROUND_ORANGE = orange;
	}

	/** The column id. */
	private final String mID;
	/** The column display name. */
	private final String mName;
	/** The column's icon. */
	private final Drawable mIcon;

	/** The key to store column id. */
	public static final String KEY_ID = "id";
	/** The key to sotre column name. */
	public static final String KEY_NAME = "name";

	/**
	 * Get the column's data in JSON format.
	 * 
	 * @return The column's data in JSON format.
	 * @throws JSONException If error occurs in JSON format.
	 * @author Luo Yinzhuo
	 */
	public String getJson() throws JSONException {
		JSONObject column = new JSONObject();
		column.put(KEY_ID, mID);
		column.put(KEY_NAME, mName);
		return column.toString();
	}

	/**
	 * Construct a {@link Column} from its data in JSON format.
	 * 
	 * @param context The context.
	 * @param json The JSON format data.
	 * @return The {@link Column}.
	 * @throws JSONException If error occurs in JSON format.
	 * @author Luo Yinzhuo
	 */
	public static Column parse(Context context, String json) throws JSONException {
		JSONObject column = new JSONObject(json);
		final String id = column.getString(KEY_ID);

		if (id.equals(AddColumn.ID)) {
			return AddColumn.getInstance(context);
		} else {
			return ColumnInfo.getColumn(id, column.getString(KEY_NAME), context);
		}
	}

	/**
	 * Construct a new instance.
	 * 
	 * @param id The ID.
	 * @param name The display name.
	 * @param icon The display icon.
	 */
	Column(String id, String name, Drawable icon) {
		mID = id;
		mName = name;
		mIcon = icon;

		if (mIcon != null) {
			mIcon.setBounds(0, 0, mIcon.getIntrinsicWidth(), mIcon.getIntrinsicHeight());
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
	/** The column is animating. */
	private static final int STATE_ANIMATION = 3;

	/** The column state. */
	private int mState = STATE_IDLE;

	/**
	 * Invoked by {@link ColumnPage} to draw the column on it.
	 * 
	 * @param canvas The {@link ColumnPageView}'s canvas.
	 * @param rect The column rectangle.
	 * @param editing True if the column page is editing, otherwise false.
	 */
	public final void draw(Canvas canvas, RectF rect, boolean editing) {
		// Draw the background color.
		PAINT.setColor(BACKGROUND_BLUE);
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
			float textX = rect.centerX() - PAINT.measureText(mName) / 2;
			float textY = rect.bottom - OFFSET_BOTTOM;
			canvas.drawText(mName, textX, textY, PAINT);
		}

		// Draw the delete mark.
		if (editing && !mID.equals(AddColumn.ID)) {
			canvas.save();
			canvas.translate(rect.right - DELETE_MARK.getIntrinsicWidth(), rect.top);
			DELETE_MARK.draw(canvas);
			canvas.restore();
		}
	}

	/**
	 * Check if a down event is on the delete mark.
	 * 
	 * @param x The event X coordinate.
	 * @param y The event Y coordinate.
	 * @return True if the down event is on the delete icon, otherwise false.
	 * @author Luo Yinzhuo
	 */
	public boolean isDelete(float x, float y) {
		return DELETE_MARK.getBounds().contains((int) (x + DELETE_MARK.getIntrinsicWidth()),
		        (int) y);
	}

	/**
	 * Invoked when a single tap occurs on the {@link Column}.
	 * 
	 * @param context The system context.
	 * 
	 * @author Luo Yinzhuo
	 */
	public void onSingleTapUp(Context context) {
		Intent intent = new Intent(context, NewsPageActivity.class);
		intent.putExtra(KEY_ID, mID);
		intent.putExtra(KEY_NAME, mName);
		context.startActivity(intent);
	}
}

/**
 * Specified class for add column.
 * 
 * @author Luo Yinzhuo
 */
final class AddColumn extends Column {
	/** The id. */
	static final String ID = "add";

	/** The single instance. */
	private static AddColumn SINGLE_INSTANCE;

	/**
	 * Construct a new instance.
	 * 
	 * @param icon The add mark.
	 */
	private AddColumn(Drawable icon) {
		super(ID, null, icon);
	}

	/**
	 * Get the single {@link AddColumn} instance.
	 * 
	 * @param context The {@link Context}.
	 * @return The single {@link AddColumn} instance.
	 * @author Luo Yinzhuo
	 */
	public synchronized static AddColumn getInstance(Context context) {
		if (SINGLE_INSTANCE == null) {
			SINGLE_INSTANCE = new AddColumn(context.getResources().getDrawable(
			        R.drawable.column_add));
		}
		return SINGLE_INSTANCE;
	}

	@Override
    public void onSingleTapUp(Context context) {
    }
}
