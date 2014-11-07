package de.afarber.testscroll2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class BigTile {
	
	public int left;
	public int top;
	public int width;
	public int height;
	public String letter = "W";
	public String value = "10";
	
	private Paint mLetterPaint;
	private Paint mValuePaint;
	private Drawable mImage;
	private int mSavedLeft;
	private int mSavedTop;
	
	private float mLetterX; 
	private float mLetterY;
	
	private float mValueX; 
	private float mValueY;
	
    public BigTile(Context context) {
    	mImage = context.getResources().getDrawable(R.drawable.big_tile);
        mImage.setAlpha(200);
    	width = mImage.getIntrinsicWidth();
    	height = mImage.getIntrinsicHeight();
    	mImage.setBounds(0, 0, width, height); 

    	int letterSize = context.getResources().getDimensionPixelSize(R.dimen.big_tile_letter);
    	mLetterPaint = new Paint();
    	mLetterPaint.setTextSize(letterSize);
    	mLetterPaint.setAntiAlias(true);
    	//mLetterPaint.setTextAlign(Align.CENTER);
    	
    	Rect letterBounds = new Rect();  
    	mLetterPaint.getTextBounds(letter, 0, letter.length(), letterBounds);
    	mLetterX = 0.45f * (width - letterBounds.width());
    	mLetterY = 0.45f * (height + letterBounds.height());

    	int valueSize = context.getResources().getDimensionPixelSize(R.dimen.big_tile_value);
    	mValuePaint = new Paint();
    	mValuePaint.setTextSize(valueSize);
    	mLetterPaint.setAntiAlias(true);
    	//mValuePaint.setTextAlign(Align.RIGHT);

    	Rect valueBounds = new Rect();  
    	mValuePaint.getTextBounds(value, 0, value.length(), valueBounds);
    	mValueX = width - 2.5f * valueBounds.width();
    	mValueY = height - 2.0f * valueBounds.height();
    }
    
	public void draw(Canvas canvas) {
		canvas.save();
		canvas.translate(left, top);
		mImage.draw(canvas);
		canvas.drawText(letter, mLetterX, mLetterY, mLetterPaint);
		canvas.drawText(value, mValueX, mValueY, mValuePaint);
		canvas.restore();
	}

	public boolean contains(int x, int y) {
		return x >= left && 
				y >= top && 
				x <= left + width &&
				y <= top + height;
	}
	
	public void save() {
		mSavedLeft = left;
		mSavedTop = top;
	}
	
	public void offset(int dx, int dy) {
		left = mSavedLeft + dx;
		top = mSavedTop + dy;
	}
	
	public void move(int x, int y) {
		left = x;
		top = y;
	}
	
	public String toString() {
		return letter + " " + value;
	}
}
