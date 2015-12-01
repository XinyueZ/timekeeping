package com.timekeeping.utils;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.util.LruCache;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

/**
 * Style a {@link android.text.Spannable} with a custom {@link Typeface}.
 * <p/>
 * See this <a href="http://www.tristanwaddington.com/2013/03/styling-the-android-action-bar-with-a-custom-font/"> Blog </a> for more information.
 *
 * @author Tristan Waddington
 */
public class TypefaceSpan extends MetricAffectingSpan {
	/**
	 * An <code>LruCache</code> for previously loaded typefaces.
	 */
	private static LruCache<String, Typeface> sTypefaceCache = new LruCache<String, Typeface>( 12 );

	private Typeface mTypeface;

	/**
	 * Load the {@link Typeface} and apply to a {@link android.text.Spannable}.
	 */
	public TypefaceSpan( Context context, String typefaceName ) {
		mTypeface = sTypefaceCache.get( typefaceName );

		if( mTypeface == null ) {
			mTypeface = Typeface.createFromAsset( context.getApplicationContext().getAssets(), String.format( "fonts/%s", typefaceName ) );

			// Cache the loaded Typeface
			sTypefaceCache.put( typefaceName, mTypeface );
		}
	}

	@Override
	public void updateMeasureState( TextPaint p ) {
		p.setTypeface( mTypeface );

		// Note: This flag is required for proper typeface rendering
		p.setFlags( p.getFlags()|Paint.SUBPIXEL_TEXT_FLAG );
	}

	@Override
	public void updateDrawState( TextPaint tp ) {
		tp.setTypeface( mTypeface );

		// Note: This flag is required for proper typeface rendering
		tp.setFlags( tp.getFlags()|Paint.SUBPIXEL_TEXT_FLAG );
	}
}
