package de.afarber.mybars;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class SmallTile {
	private static final int ALPHA = 0x99;
	private static final String PREFIX = "small_";

	private static final int NORTH = 0;
	private static final int EAST = 1;
	private static final int SOUTH = 2;
	private static final int WEST = 3;
    private boolean[] mDrawSides = new boolean[] { true, true, true, true };

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
    private Paint mPaintLight;
    private Paint mPaintDark;

    private Path mFillPath;
    private Path mLightPath;
    private Path mDarkPath;

	private char mLetter;
	private int mValue;
	
    public SmallTile(Context context) {
	    if (sLetters.size() == 0) {
		    String packageName = context.getPackageName();
		    
		    for (int i = 0; i < LETTERS.length; i++) {
		    	char c = LETTERS[i];
		    	int v  = VALUES[i];
		    	int id = context.getResources().getIdentifier(PREFIX + i, "drawable", packageName);
		    	Drawable d = context.getResources().getDrawable(id);
		    	width = d.getIntrinsicWidth();
		    	height = d.getIntrinsicHeight();
		    	d.setBounds(0, 0, width, height);
		    	Log.d("SmallTile", "width=" + width + ", height=" + height);
		    	sLetters.put(c, d);
		    	sValues.put(c, v);
		    }
	    }
	    
        mScale = context.getResources().getDisplayMetrics().density;
        
		mPaintLight = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
		mPaintLight.setStyle(Paint.Style.STROKE);
		mPaintLight.setStrokeWidth(2 * mScale);
		mPaintLight.setColor(Color.WHITE);
		mPaintLight.setAlpha(ALPHA);
		
		mPaintDark = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
		mPaintDark.setStyle(Paint.Style.STROKE);
		mPaintDark.setStrokeWidth(2 * mScale);
		mPaintDark.setColor(Color.BLACK);
		mPaintDark.setAlpha(ALPHA);
		
		final int corner = Math.max(width, height) / 6;
		
		mFillPath = new Path();
		mFillPath.lineTo(width - corner, 0);
		mFillPath.lineTo(width, corner);
		mFillPath.lineTo(width, height);
		mFillPath.lineTo(0, height);
		mFillPath.close();
		
		mLightPath = new Path();
		mLightPath.moveTo(0, height);
		mLightPath.lineTo(0, 0);
		mLightPath.lineTo(width - corner, 0);
		mLightPath.lineTo(width, corner);
		
		mDarkPath = new Path();
		mDarkPath.moveTo(0, height);
		mDarkPath.lineTo(width, height);
		mDarkPath.lineTo(width, corner);
	}

    public void setDrawSides(boolean[] sides) {
    	mDrawSides = sides;
    }
    
	public void draw(Canvas canvas, Paint paint) {
		if (!visible)
			return;
		
		canvas.save();
		canvas.translate(left, top);
        
		if (mDrawSides[NORTH] && mDrawSides[EAST]) {
	        canvas.drawPath(mFillPath, paint);
	        
			int corner = Math.max(width, height) / 6;
			
			canvas.drawLine(0, 0, width - corner, 0, mPaintLight);
			canvas.drawLine(width - corner, 0, width, corner, mPaintLight);
			canvas.drawLine(width, corner, width, height, mPaintDark);
		} else {
			canvas.drawRect(0, 0, width, height, paint);
			
			if (mDrawSides[NORTH])
				canvas.drawLine(0, 0, width, 0, mPaintLight);
			
			if (mDrawSides[EAST])
				canvas.drawLine(width, 0, width, height, mPaintDark);
		}
		
		if (mDrawSides[WEST])
			canvas.drawLine(0, 0, 0, height, mPaintLight);
        
		if (mDrawSides[SOUTH])
			canvas.drawLine(0, height, width, height, mPaintDark);
		
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
	
	/* in which column and row of game board is this tile placed? */
	public int getColumn(int cellWidth) {
		int col = (left + width / 2) / cellWidth - 1;
		
    	if (col < 0)
    		col = 0;
    	else if (col > 14)
    		col = 14;

    	return col;
	}

	public int getRow(int cellWidth) {
		int row = (top + height / 2) / cellWidth - 1;
		
    	if (row < 0)
    		row = 0;
    	else if (row > 14)
    		row = 14;

    	return row;
	}
}

