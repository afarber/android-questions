package de.afarber.mydecoder2;

import java.io.IOException;
import java.io.InputStream;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashMap;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class SmallTile {
	private static final int EN = R.drawable.small_english;
	private static final int TILE = R.drawable.small_tile;
	private static final float SCALE = 1.0f;
	private static final int ALPHA = 220;
	
	private static final CharacterIterator ABC = new StringCharacterIterator("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
	private static HashMap<Character, Bitmap> sBitmaps = new HashMap<Character, Bitmap>();
	
	public int left;
	public int top;
	public int savedLeft;
	public int savedTop;
	public int width;
	public int height;
	public boolean visible = true;
	
	private Drawable mBackground;
	private Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
	
	private char mLetter;
	private int mValue;
	
    public SmallTile(Context context) {
    	mBackground = context.getResources().getDrawable(TILE);
        mBackground.setAlpha(ALPHA);
    	width = mBackground.getIntrinsicWidth();
    	height = mBackground.getIntrinsicHeight();
    	mBackground.setBounds(0, 0, width, height);
    	
    	if (sBitmaps.size() > 0)
    		return;
    	
		try {
			InputStream is = context.getResources().openRawResource(EN);
			BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, false);
			int h = decoder.getHeight();
			Rect r = new Rect(0, 0, h, h);
			for (char c = ABC.first(); 
				c != CharacterIterator.DONE; 
				c = ABC.next(), r.offset(h, 0)) {
				   Bitmap unscaled = decoder.decodeRegion(r, null);
				   Bitmap scaled = Bitmap.createScaledBitmap(unscaled, (int) (SCALE * width), (int) (SCALE * height), true);
				   sBitmaps.put(c, scaled);
			}
		} catch (IOException ex) {
			Log.e("SmallTile", "Can not decode region", ex);
		}
	}
    
	public void draw(Canvas canvas) {
		if (!visible)
			return;
		
		canvas.save();
		canvas.translate(left, top);
		mBackground.draw(canvas);
		Bitmap bmp = sBitmaps.get(mLetter);
		canvas.drawBitmap(bmp, 0, 0, mPaint);
		canvas.restore();
	}

	public boolean contains(int x, int y) {
		return x >= left && 
				y >= top && 
				x <= left + width &&
				y <= top + height;
	}
	
	public void save() {
		savedLeft = left;
		savedTop = top;
	}
	
	public void offset(int dx, int dy) {
		left = savedLeft + dx;
		top = savedTop + dy;
	}
	
	public void move(int x, int y) {
		left = x;
		top = y;
	}
	
	public String toString() {
		return mLetter + " " + mValue;
	}

	public char getLetter() {
		return mLetter;
	}

	public void setLetter(char c) {
		mLetter = c;
	}

	public int getValue() {
		return mValue;
	}

	public void setValue(int n) {
		mValue = n;
	}
}
