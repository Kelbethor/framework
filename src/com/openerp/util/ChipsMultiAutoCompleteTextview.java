/*
 * OpenERP, Open Source Management Solution
 * Copyright (C) 2012-today OpenERP SA (<http://www.openerp.com>)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 * 
 */
package com.openerp.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.openerp.R;

// TODO: Auto-generated Javadoc
/**
 * The Class ChipsMultiAutoCompleteTextview.
 */
public class ChipsMultiAutoCompleteTextview extends MultiAutoCompleteTextView
		implements OnItemClickListener {

	/** The tag. */
	private final String TAG = "ChipsMultiAutoCompleteTextview";

	/** The selected list. */
	HashMap<String, Object> selectedList = new HashMap<String, Object>();

	/** The context. */
	public Context context = null;

	/* Constructor */
	/**
	 * Instantiates a new chips multi auto complete textview.
	 * 
	 * @param context
	 *            the context
	 */
	public ChipsMultiAutoCompleteTextview(Context context) {
		super(context);
		init(context);
	}

	/* Constructor */
	/**
	 * Instantiates a new chips multi auto complete textview.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 */
	public ChipsMultiAutoCompleteTextview(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/* Constructor */
	/**
	 * Instantiates a new chips multi auto complete textview.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 * @param defStyle
	 *            the def style
	 */
	public ChipsMultiAutoCompleteTextview(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	/* set listeners for item click and text change */
	/**
	 * Inits the.
	 * 
	 * @param context
	 *            the context
	 */
	public void init(Context context) {
		this.context = context;
		setOnItemClickListener(this);
		addTextChangedListener(textWather);
	}

	/*
	 * TextWatcher, If user type any country name and press comma then following
	 * code will regenerate chips
	 */
	/** The text wather. */
	private TextWatcher textWather = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (count >= 1) {
				if (s.charAt(start) == ';') {
					// setChips(); // generate chips
				}
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	/* This function has whole logic for chips generate */
	/**
	 * Sets the chips.
	 * 
	 * @param name
	 *            the new chips
	 */
	public void setChips(String name) {

		if (selectedList.size() > 0) // check comman in string
		{
			int x = 0;

			Object[] values = selectedList.values().toArray();
			SpannableStringBuilder ssb = new SpannableStringBuilder(
					TextUtils.join(",", values));
			for (final Object val : values) {
				String c = val.toString();
				// inflate chips_edittext layout
				LayoutInflater lf = (LayoutInflater) getContext()
						.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

				TextView textView = (TextView) lf.inflate(
						R.layout.chips_edittext, null);
				textView.setText(c); // set text
				int spec = MeasureSpec.makeMeasureSpec(0,
						MeasureSpec.UNSPECIFIED);
				textView.measure(spec, spec);
				textView.layout(0, 0, textView.getMeasuredWidth(),
						textView.getMeasuredHeight());

				Bitmap b = Bitmap.createBitmap(textView.getWidth(),
						textView.getHeight(), Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(b);
				canvas.translate(-textView.getScrollX(), -textView.getScrollY());
				textView.draw(canvas);
				textView.setDrawingCacheEnabled(true);
				Bitmap cacheBmp = textView.getDrawingCache();
				Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
				textView.destroyDrawingCache(); // destory drawable
				// create bitmap drawable for imagespan
				BitmapDrawable bmpDrawable = new BitmapDrawable(viewBmp);
				bmpDrawable.setBounds(0, 0, bmpDrawable.getIntrinsicWidth(),
						bmpDrawable.getIntrinsicHeight());
				// create and set imagespan
				ssb.setSpan(new ImageSpan(bmpDrawable), x, x + c.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				x = x + c.length() + 1;
			}
			// set chips span
			setText(ssb);
			// move cursor to last
			setSelection(getText().length());
		}

	}

	/**
	 * Gets the selected ids.
	 * 
	 * @return the selected ids
	 */
	public JSONArray getSelectedIds() {
		JSONArray arr = new JSONArray();
		for (String key : selectedList.keySet()) {
			arr.put(Integer.parseInt(key));
		}
		return arr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		TextView txvId = (TextView) view.findViewById(R.id.txvMultiId);
		TextView txvEmail = (TextView) view.findViewById(R.id.txvMultiEmail);
		TextView txvName = (TextView) view.findViewById(R.id.txvMultiName);
		selectedList.put(txvId.getText().toString(), txvName.getText()
				.toString());
		setChips(txvName.getText().toString());
	}

}