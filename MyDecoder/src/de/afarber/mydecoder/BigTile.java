package de.afarber.mydecoder;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class BigTile {
	private static BitmapRegionDecoder mDecoder;
	private static int mHeight;

	public int left;
	public int top;
	public int savedLeft;
	public int savedTop;
	public int width;
	public int height;
	public boolean visible = true;
	
	private Drawable mImage;
	private Bitmap mText;
	private Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
	
	private String mLetter;
	private String mValue;
	
    public BigTile(Context context) {
    	mImage = context.getResources().getDrawable(R.drawable.big_tile);
        mImage.setAlpha(200);
    	width = mImage.getIntrinsicWidth();
    	height = mImage.getIntrinsicHeight();
    	mImage.setBounds(0, 0, width, height); 

    	if (mDecoder == null) {
			InputStream is = context.getResources().openRawResource(R.drawable.big_english);
			try {
				mDecoder = BitmapRegionDecoder.newInstance(is, false);
			} catch (IOException ex) {
				
			}
			
			mHeight = mDecoder.getHeight();
    	}
    	
		Rect r = new Rect(0, 0, mHeight, mHeight);
		mText = mDecoder.decodeRegion(r, null);
	}
    
	public void draw(Canvas canvas) {
		if (!visible)
			return;
		
		canvas.save();
		canvas.translate(left, top);
		mImage.draw(canvas);
		canvas.drawBitmap(mText, 0, 0, mPaint);
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

	public String getLetter() {
		return mLetter;
	}

	public void setLetter(String str) {
		mLetter = str;
		
		// TODO
	}

	public String getValue() {
		return mValue;
	}

	public void setValue(int n) {
		setValue(String.valueOf(n));
	}
	
	public void setValue(String str) {
		mValue = str;
		
		// TODO
	}
	
	public void copy(SmallTile tile) {
		int dX = (width - tile.width) / 2;
		int dY = (height - tile.height) / 2;
		left = tile.left - dX;
		top = tile.top - dY;
		savedLeft = tile.savedLeft - dX;
		savedTop = tile.savedTop - dY;
		setLetter(tile.getLetter());
		setValue(tile.getValue());
	}	
}
