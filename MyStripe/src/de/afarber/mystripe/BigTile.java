package de.afarber.mystripe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class BigTile {
	public int left;
	public int top;
	public int savedLeft;
	public int savedTop;
	public int width;
	public int height;
	public boolean visible = true;
	
	private Drawable mImage;
	private Bitmap mStripe;
	private Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
	private Rect mSrc;
	private Rect mDst;
	
	private String mLetter;
	private String mValue;
	
    public BigTile(Context context) {
    	mImage = context.getResources().getDrawable(R.drawable.big_tile);
        mImage.setAlpha(200);
    	width = mImage.getIntrinsicWidth();
    	height = mImage.getIntrinsicHeight();
    	mImage.setBounds(0, 0, width, height); 

    	mStripe = BitmapFactory.decodeResource(context.getResources(), R.drawable.big_english);
		int h = mStripe.getHeight();
		mSrc = new Rect(h, 0, 2 * h, h);
		mDst = new Rect(0, 0, h, h);
    }
    
	public void draw(Canvas canvas) {
		if (!visible)
			return;
		
		canvas.save();
		canvas.translate(left, top);
		mImage.draw(canvas);
		canvas.drawBitmap(mStripe, mSrc, mDst, mPaint);
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
