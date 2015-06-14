package de.afarber.mybars;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class BigTile {
	private static final int ALPHA = 200;
	private static final String PREFIX = "big_";
	private static final int TILE = R.drawable.shadow;
	
	private static final char[] LETTERS = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
	private static final int[] VALUES =   { 1,   4,   4,   2,   1,   4,   3,   3,   1,  10,   5,   2,   4,   2,   1,   4,  12,   1,   1,   1,   2,   5,   4,   8,   3,  10 };
	private static HashMap<Character, Drawable> sLetters = new HashMap<Character, Drawable>();
	private static HashMap<Character, Integer> sValues = new HashMap<Character, Integer>();
	
	public int left;
	public int top;
	public int savedLeft;
	public int savedTop;
	public int width;
	public int height;
	public boolean visible = true;
	
	private Drawable mBackground;
	
    private float mScale;
    private Paint mPaint;
    private Paint mPaintLight;
    private Paint mPaintDark;

	private char mLetter;
	private int mValue;
	
    public BigTile(Context context) {
    	mBackground = context.getResources().getDrawable(TILE);
        mBackground.setAlpha(ALPHA);
    	width = mBackground.getIntrinsicWidth();
    	height = mBackground.getIntrinsicHeight();
    	Log.d("BigTile", "width=" + width + ", height=" + height);
    	mBackground.setBounds(0, 0, width, height);
    	
	    if (sLetters.size() > 0)
	    	return;
	    
	    for (int i = 0; i < LETTERS.length; i++) {
	    	char c = LETTERS[i];
	    	int v = VALUES[i];
	    	int id = context.getResources().getIdentifier(PREFIX + i, "drawable", context.getPackageName());
	    	Drawable d = context.getResources().getDrawable(id);
	    	d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
	    	Log.d("BigTile", "w=" + d.getIntrinsicWidth() + ", h=" + d.getIntrinsicHeight());
	    	sLetters.put(c, d);
	    	sValues.put(c, v);
	    }
	    
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
		mPaint.setColor(0xFFFFCC00);   
		mPaint.setAlpha(0xCC);

        mScale = context.getResources().getDisplayMetrics().density;
 /*       
        EmbossMaskFilter filter = new EmbossMaskFilter(
        	    new float[] { 0f, 1f, 0.5f }, 0.8f, 3f, mScale * 3f);
*/        
		mPaintLight = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
		mPaintLight.setStrokeWidth(3 * mScale);
		mPaintLight.setColor(Color.WHITE);
		mPaintLight.setAlpha(0x66);
		//mPaintLight.setMaskFilter(filter);
		
		mPaintDark = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
		mPaintDark.setStrokeWidth(3 * mScale);
		mPaintDark.setColor(Color.BLACK);
		mPaintDark.setAlpha(0x66);
		//mPaintDark.setMaskFilter(filter);	    
	}
    
	public void draw(Canvas canvas) {
		if (!visible)
			return;
		
		canvas.save();
		canvas.translate(left, top);
		
        canvas.drawRect(0, 0, width, height, mPaint);
		canvas.drawLine(0, 0, width, 0, mPaintLight);
		canvas.drawLine(0, 0, 0, height, mPaintLight);
		canvas.drawLine(0, height, width, height, mPaintDark);
		canvas.drawLine(0 + width, 0, 0 + width, height, mPaintDark);
		
		//mBackground.draw(canvas);
		Drawable d = sLetters.get(mLetter);
		d.draw(canvas);
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
	
	public void restore() {
		left = savedLeft;
		top = savedTop;
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
		mValue = sValues.get(c);
	}

	public int getValue() {
		return mValue;
	}

	public void copy(char c, float x, float y) {
		savedLeft = left = (int) (x - width / 2);
		savedTop = top = (int) (y - height / 2);
		
		setLetter(c);
	}	
}
