package de.afarber.mybars;

/* 
brown color: #663300

res/drawable-mdpi:
big_english.png 1872 × 72 pixels
small_english.png 1040 × 40 pixels
convert big_english.png -crop 72x72 big_%d.png
convert small_english.png -crop 40x40 small_%d.png

res/drawable-xxhdpi:
big_english.png 5200 × 200 pixels
small_english.png 3120 × 120 pixels
convert big_english.png -crop 200x200 big_%d.png
convert small_english.png -crop 120x120 small_%d.png
*/

import java.util.HashMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class BigTile {
	private static final int ALPHA = 0xCC;
	private static final int SHADOW_ALPHA = 0x66;
	private static final int COLOR = 0xFFFFCC00;
	private static final String PREFIX = "big_";
	private static final char[] LETTERS = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
	private static final int[] VALUES =   { 1,   4,   4,   2,   1,   4,   3,   3,   1,  10,   5,   2,   4,   2,   1,   4,  12,   1,   1,   1,   2,   5,   4,   8,   3,  10 };
	private static HashMap<Character, Drawable> sLetters = new HashMap<Character, Drawable>();
	private static HashMap<Character, Integer> sValues = new HashMap<Character, Integer>();
	
	public int left;
	public int top;
	public int savedLeft;
	public int savedTop;
	public static int width = 0;
	public static int height = 0;
	public boolean visible = true;
	
    private float mScale;
    
    private Paint mPaint;
    private Paint mPaintLight;
    private Paint mPaintDark;
    
    private float[] mLightLines;
    private float[] mDarkLines;

	private char mLetter;
	private int mValue;
	
    public BigTile(Context context) {
	    if (sLetters.size() > 0)
	    	return;
	    
	    for (int i = 0; i < LETTERS.length; i++) {
	    	char c = LETTERS[i];
	    	int v = VALUES[i];
	    	int id = context.getResources().getIdentifier(PREFIX + i, "drawable", context.getPackageName());
	    	Drawable d = context.getResources().getDrawable(id);
	    	width = d.getIntrinsicWidth();
	    	height = d.getIntrinsicHeight();
	    	d.setBounds(0, 0, width, height);
	    	Log.d("BigTile", "w=" + width + ", h=" + height);
	    	sLetters.put(c, d);
	    	sValues.put(c, v);
	    }
	    
        mScale = context.getResources().getDisplayMetrics().density;
        
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(COLOR);   
		mPaint.setAlpha(ALPHA);
		
 		mPaintLight = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
		mPaintLight.setStyle(Paint.Style.STROKE);
		mPaintLight.setStrokeWidth(2 * mScale);
		mPaintLight.setColor(Color.WHITE);
		mPaintLight.setAlpha(SHADOW_ALPHA);
		
		mPaintDark = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
		mPaintDark.setStyle(Paint.Style.STROKE);
		mPaintDark.setStrokeWidth(3 * mScale);
		mPaintDark.setColor(Color.BLACK);
		mPaintDark.setAlpha(SHADOW_ALPHA);
		
		mLightLines = new float[]{
				0, height, 
				0, 0, 
				
				0, 0, 
				width, 0
		};
		
		mDarkLines = new float[]{
				mPaintDark.getStrokeWidth(), height, 
				width, height, 
				
				width, height, 
				width, mPaintDark.getStrokeWidth()
		};
	}
    
	public void draw(Canvas canvas) {
		if (!visible)
			return;
		
		canvas.save();
		canvas.translate(left, top);
		
        canvas.drawRect(0, 0, width, height, mPaint);
        canvas.drawLines(mLightLines, mPaintLight);
        canvas.drawLines(mDarkLines, mPaintDark);

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
