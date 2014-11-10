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
	public FontTextView(Context context) {
		super(context);
	}

	public FontTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context, attrs);
	}

	public FontTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context, attrs);
	}

	private void initialize(Context context, AttributeSet attrs) {
		String font;

		TypedArray a = context.obtainStyledAttributes(attrs,
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
		case 2:
			font = Fonts.FONT_BOLD;
			break;
		case 3:
			font = Fonts.FONT_LIGHT;
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

	public void setFont(String font) {
		if (!isInEditMode()) {
			Typeface tf = Fonts.getFont(getContext(), font);
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
		public static final String FONT_BOLD = "RobotoCondensed-Bold.ttf";
		public static final String FONT_LIGHT = "RobotoCondensed-Light.ttf";

		public static Typeface getFont(Context context, String assetPath) {
			Typeface font = sTypefaces.get(assetPath);
			if (font == null) {
				font = Typeface.createFromAsset(context.getAssets(), "fonts/"
						+ assetPath);
				sTypefaces.put(assetPath, font);
			}
			return font;
		}

	}
}
