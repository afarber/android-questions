package de.afarber.testscroll2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class BigTile {
	private static final float LETTER_FACTOR_X = 0.4f;
	private static final float LETTER_FACTOR_Y = 0.4f;
	private static final float VALUE_FACTOR_X = 1.5f;
	private static final float VALUE_FACTOR_Y = 1.5f;
	
	public int left;
	public int top;
	public int savedLeft;
	public int savedTop;
	public int width;
	public int height;
	public boolean visible = true;
	
	private Drawable mImage;
	
	private String mLetter;
	private int mLetterSize;
	private Paint mLetterPaint;
	private float mLetterX; 
	private float mLetterY;
	
	private String mValue;
	private int mValueSize;
	private Paint mValuePaint;
	private float mValueX; 
	private float mValueY;
	
    public BigTile(Context context) {
    	mImage = context.getResources().getDrawable(R.drawable.big_tile);
        mImage.setAlpha(200);
    	width = mImage.getIntrinsicWidth();
    	height = mImage.getIntrinsicHeight();
    	mImage.setBounds(0, 0, width, height); 

    	mLetterSize = context.getResources().getDimensionPixelSize(R.dimen.big_tile_letter);
		mLetterPaint = new Paint();
    	mLetterPaint.setTextSize(mLetterSize);
    	mLetterPaint.setAntiAlias(true);

    	mValueSize = context.getResources().getDimensionPixelSize(R.dimen.big_tile_value);
    	mValuePaint = new Paint();
    	mValuePaint.setTextSize(mValueSize);
    	mValuePaint.setAntiAlias(true);
    	//mValuePaint.setLetterSpacing(-0.05);
    }
    
	public void draw(Canvas canvas) {
		if (!visible)
			return;
		
		canvas.save();
		canvas.translate(left, top);
		mImage.draw(canvas);
		canvas.drawText(mLetter, mLetterX, mLetterY, mLetterPaint);
		canvas.drawText(mValue, mValueX, mValueY, mValuePaint);
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
    	
    	Rect letterBounds = new Rect();  
    	mLetterPaint.getTextBounds(mLetter, 0, mLetter.length(), letterBounds);
//    	mLetterX = LETTER_FACTOR_X * (width - letterBounds.width());
//    	mLetterY = LETTER_FACTOR_Y * (height + letterBounds.height());
    	mLetterX = (width - letterBounds.width()) / 2;
    	mLetterY = (height + letterBounds.height()) / 2;
	}

	public String getValue() {
		return mValue;
	}

	public void setValue(int n) {
		setValue(String.valueOf(n));
	}
	
	public void setValue(String str) {
		mValue = str;

    	Rect valueBounds = new Rect();  
    	mValuePaint.getTextBounds(mValue, 0, mValue.length(), valueBounds);
//    	mValueX = width - VALUE_FACTOR_X * valueBounds.width();
//    	mValueY = height - VALUE_FACTOR_Y * valueBounds.height();
    	mValueX = width - valueBounds.width();
    	mValueY = height - valueBounds.height();
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
