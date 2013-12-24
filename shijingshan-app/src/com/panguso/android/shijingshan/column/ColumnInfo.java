package com.panguso.android.shijingshan.column;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.panguso.android.shijingshan.R;

/**
 * Specific for store a {@link Column}'s information.
 * 
 * @author Luo Yinzhuo
 */
public final class ColumnInfo {
	/** The column icon map. */
	private static final Map<String, Integer> COLUMN_ICON_MAP = new HashMap<String, Integer>();

	static {
		// zheng ce fa gui
		COLUMN_ICON_MAP.put("100", R.drawable.column_zcfg);
		// gong gao lan
		COLUMN_ICON_MAP.put("101", R.drawable.column_ggl);
		// hang ye dong tai
		COLUMN_ICON_MAP.put("102", R.drawable.column_hydt);
		// ren cai fu wu
		COLUMN_ICON_MAP.put("103", R.drawable.column_rcfw);
		// tou zi xuan chuan
//		COLUMN_ICON_MAP.put("104", R.drawable.column_tzxc);
		// cai gou zhao biao
//		COLUMN_ICON_MAP.put("105", R.drawable.column_cgzb);
		// yi jian fan kui
//		COLUMN_ICON_MAP.put("106", R.drawable.column_yjfk);
		// ling dao xin xiang
		COLUMN_ICON_MAP.put("107", R.drawable.column_ldxx);
		// ban shi zhi nan
//		COLUMN_ICON_MAP.put("108", R.drawable.column_bszn);
	}

	/** The column ID. */
	private final String mID;
	/** The column name. */
	private final String mName;
	/** Whether the column is open or not. */
	private final boolean mOpen;

	/**
	 * Construct a new instance.
	 * 
	 * @param id The column ID.
	 * @param name The column name.
	 * @param open The column is open or not.
	 */
	private ColumnInfo(String id, String name, boolean open) {
		mID = id;
		mName = name;
		mOpen = open;
	}

	/**
	 * Check if it is an open column.
	 * 
	 * @return True if it is an open column, otherwise false.
	 * @author Luo Yinzhuo
	 */
	public boolean isOpen() {
		return mOpen;
	}

	/**
	 * Get the {@link Column} based on the {@link ColumnInfo}.
	 * 
	 * @param context The system context.
	 * @return The {@link Column}.
	 * @author Luo Yinzhuo
	 */
	public final Column getColumn(Context context) {
		Integer iconId = COLUMN_ICON_MAP.get(mID);
		Drawable icon = iconId == null ? null : context.getResources().getDrawable(iconId);
		return new Column(mID, mName, icon);
	}

	/**
	 * Get the {@link Column} based on the id and name.
	 * 
	 * @param id The column's id.
	 * @param name The column's name.
	 * @param context The system context.
	 * @return The {@link Column}.
	 * @author Luo Yinzhuo
	 */
	public static Column getColumn(String id, String name, Context context) {
		Integer iconId = COLUMN_ICON_MAP.get(id);
		Drawable icon = iconId == null ? null : context.getResources().getDrawable(iconId);
		return new Column(id, name, icon);
	}

	/** The key to get column ID. */
	private static final String KEY_COLUMN_ID = "columnId";
	/** The key to get column name. */
	private static final String KEY_COLUMN_NAME = "columnName";
	/** The key to get sfgk. */
	private static final String KEY_SFGK = "sfgk";
	/** The value of sfgk yes. */
	private static final String VALUE_SFGK_YES = "yes";

	/**
	 * Parse a column info from its JSON format.
	 * 
	 * @param json The column info in JSON format.
	 * @return The column info.
	 * @throws JSONException If the column info has error.
	 * @author Luo Yinzhuo
	 */
	public static ColumnInfo parse(JSONObject json) throws JSONException {
		Log.d("ColumnInfo", json.toString());
		return new ColumnInfo(json.getString(KEY_COLUMN_ID), json.getString(KEY_COLUMN_NAME), json
		        .getString(KEY_SFGK).equals(VALUE_SFGK_YES));
	}

	@Override
	public String toString() {
		return "ColumnInfo [mID=" + mID + ", mName=" + mName + ", mOpen=" + mOpen + "]";
	}
}
