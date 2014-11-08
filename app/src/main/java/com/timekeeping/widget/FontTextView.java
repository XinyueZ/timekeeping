package com.timekeeping.widget;

import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.timekeeping.R;


/**
 * A TextView that allows a custom font to be defined in a layout. The font must
 * be in the assets folder.
 * 
 * @see http
 *      ://stackoverflow.com/questions/2376250/custom-fonts-and-xml-layouts-
 *      android
 */
public class FontTextView extends TextView {
	public FontTextView(Context _context) {
		super(_context);
	}

	public FontTextView(Context _context, AttributeSet _attrs) {
		super(_context, _attrs);
		initialize(_context, _attrs);
	}

	public FontTextView(Context _context, AttributeSet _attrs, int _defStyle) {
		super(_context, _attrs, _defStyle);
		initialize(_context, _attrs);
	}

	private void initialize(Context _context, AttributeSet _attrs) {
		String font;

		TypedArray a = _context.obtainStyledAttributes(_attrs,
				R.styleable.FontTextView);
		int fontIndex = a.getInt(R.styleable.FontTextView_font, -1);

		// defined in attrs.xml
		switch (fontIndex) {
		case 0:
			font = Fonts.FONT_REGULAR;
			break;
		case 1:
			font = Fonts.FONT_THIN;
			break;
		default:
			font = Fonts.FONT_REGULAR;
			break;
		}

		a.recycle();

		if (font != null) {
			setFont(font);
		}
	}

	public void setFont(String _font) {
		if (!isInEditMode()) {
			Typeface tf = Fonts.getFont(getContext(), _font);
			setTypeface(tf);
		}
	}

	/**
	 * A cache for Fonts. Works around a known memory leak in
	 * <code>Typeface.createFromAsset</code>.
	 * 
	 * @see http://code.google.com/p/android/issues/detail?id=9904
	 */
	public final static class Fonts {
		private static final ConcurrentHashMap<String, Typeface> sTypefaces = new ConcurrentHashMap<String, Typeface>();

		public static final String FONT_REGULAR = "Roboto-Regular.ttf";
		public static final String FONT_THIN = "Roboto-Thin.ttf";

		public static Typeface getFont(Context _context, String _assetPath) {
			Typeface font = sTypefaces.get(_assetPath);
			if (font == null) {
				font = Typeface.createFromAsset(_context.getAssets(), "fonts/"
						+ _assetPath);
				sTypefaces.put(_assetPath, font);
			}
			return font;
		}
	}
}
