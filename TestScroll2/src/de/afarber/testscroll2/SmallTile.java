package de.afarber.testscroll2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class SmallTile {
	
	public int left;
	public int top;
	public int width;
	public int height;
	public String letter;
	public String value;
	
	private int mLetterFontSize;
	private int mValueFontSize;
	private Drawable mImage;
	private int mSavedLeft;
	private int mSavedTop;
	
    public SmallTile(Context context) {
    	mLetterFontSize = context.getResources().getDimensionPixelSize(R.dimen.small_tile_letter);
    	mValueFontSize = context.getResources().getDimensionPixelSize(R.dimen.small_tile_value);
    	
    	mImage = context.getResources().getDrawable(R.drawable.small_tile);
        mImage.setAlpha(200);
    	width = mImage.getIntrinsicWidth();
    	height = mImage.getIntrinsicHeight();
    	mImage.setBounds(0, 0, width, height); 
    }
    
	public void draw(Canvas canvas) {
		canvas.save();
		canvas.translate(left, top);
		mImage.draw(canvas);
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
}
