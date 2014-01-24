/**
 * 
 */
package com.panguso.android.shijingshan.register;

import com.panguso.android.shijingshan.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Specific for the {@link RegisterActivity}'s check edit text.
 * 
 * @author Luo Yinzhuo
 * 
 */
public class RegisterCheckEditText extends RelativeLayout implements
		OnFocusChangeListener, TextWatcher {
	/**
	 * Interface definition for a callback to be invoked when the edit text lost
	 * focus.
	 * 
	 * @author Luo Yinzhuo
	 */
	public interface OnRegisterCheckEditTextListener {

		/**
		 * Called when the edit text's input text has been changed.
		 * 
		 * @param id
		 *            The id.
		 * @param text
		 *            The changed input text.
		 * @return True if the input text is correct, otherwise false.
		 * @author Luo Yinzhuo
		 */
		public void onRegisterCheckEditTextChanged(int id, String text);

		/**
		 * Called when the edit text lost focus and need to check the input text
		 * is correct or not.
		 * 
		 * @param id
		 *            The id.
		 * @return True if the input text is correct, otherwise false.
		 */
		public boolean onRegisterCheckEditTextLostFocus(int id);
	}

	/** The edit text. */
	private final EditText mEditText;
	/** The default edit text background. */
	private final Drawable mEditTextBackground;
	/** The check mark. */
	private final ImageView mCheck;
	/** The edit text listener. */
	private OnRegisterCheckEditTextListener mListener;

	/**
	 * Construct a new instance.
	 * 
	 * @param context
	 *            The system context.
	 * @param attrs
	 *            The attributes.
	 */
	public RegisterCheckEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.register_check_edit_text_widget, this);

		mEditText = (EditText) findViewById(R.id.edit_text);
		mEditTextBackground = mEditText.getBackground();
		mEditText.setBackgroundDrawable(null);
		mEditText.addTextChangedListener(this);
		mEditText.setOnFocusChangeListener(this);

		mCheck = (ImageView) findViewById(R.id.check);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (widthMode == MeasureSpec.EXACTLY) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		} else {
			super.onMeasure(MeasureSpec.EXACTLY, heightMeasureSpec);
		}
	}

	/**
	 * Change the input type.
	 * 
	 * @param type
	 *            The input type.
	 * @author Luo Yinzhuo
	 */
	public void setInputType(int type) {
		mEditText.setInputType(type);
	}

	/** The no input filters. */
	private static final InputFilter[] NO_FILTERS = new InputFilter[0];

	/**
	 * To constraint the text length to the specified number.
	 * 
	 * @param maxLength
	 *            The maximum length of the text.
	 * @author Luo Yinzhuo
	 */
	public void setMaxLength(int maxLength) {
		if (maxLength >= 0) {
			mEditText
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							maxLength) });
		} else {
			mEditText.setFilters(NO_FILTERS);
		}
	}

	/**
	 * Set the characters accepted.
	 * 
	 * @param resid
	 *            The string resource's id.
	 * @author Luo Yinzhuo
	 */
	public void setDigits(int resid) {
		String digits = getResources().getString(resid);
		mEditText.setKeyListener(DigitsKeyListener.getInstance(digits));
	}

	/**
	 * Set the text hint.
	 * 
	 * @param resid
	 *            The string resource's id.
	 * 
	 * @author Luo Yinzhuo
	 */
	public void setTextHint(int resid) {
		mEditText.setHint(resid);
	}

	/**
	 * Get the inputed text.
	 * 
	 * @return The inputed text.
	 * @author Luo Yinzhuo
	 */
	public String getText() {
		return mEditText.getText().toString();
	}

	/**
	 * Set the check image.
	 * 
	 * @param check
	 *            True to correct image, otherwise error image.
	 * @author Luo Yinzhuo
	 */
	public void setCheck(boolean check) {
		if (mEditText.getText().toString().length() > 0) {
			if (check) {
				mCheck.setImageResource(R.drawable.correct);
			} else {
				mCheck.setImageResource(R.drawable.error);
			}

			if (mCheck.getVisibility() == View.INVISIBLE) {
				mCheck.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * Set the {@link OnRegisterCheckEditTextListener}.
	 * 
	 * @param listener
	 *            The {@link OnRegisterCheckEditTextListener}.
	 * 
	 * @author Luo Yinzhuo
	 */
	public void setOnRegisterCheckEditTextListenerListener(
			OnRegisterCheckEditTextListener listener) {
		mListener = listener;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (mListener != null) {
			mListener.onRegisterCheckEditTextChanged(getId(), s.toString());
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			if (mCheck.getVisibility() == View.VISIBLE) {
				mCheck.setVisibility(View.INVISIBLE);
			}
			mEditText.setBackgroundDrawable(mEditTextBackground);
			mEditText.setSelection(0, mEditText.getText().toString().length());
		} else {
			mEditText.setSelection(0);
			mEditText.setBackgroundDrawable(null);
			String text = mEditText.getText().toString();
			if (text.length() > 0 && mListener != null) {
				if (mListener.onRegisterCheckEditTextLostFocus(getId())) {
					mCheck.setImageResource(R.drawable.correct);
				} else {
					mCheck.setImageResource(R.drawable.error);
				}
				mCheck.setVisibility(View.VISIBLE);
			}
		}
	}
}
