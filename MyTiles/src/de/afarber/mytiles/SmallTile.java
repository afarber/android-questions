package de.afarber.mytiles;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class SmallTile {
	private static final String PREFIX = "small_";
	private static final String SQUARE = "square_";
	private static final String ROUND = "round_";
	private static final int TILE = R.drawable.round;
	private static final int ALPHA = 200;
	
	private static final char[] LETTERS = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
	private static final int[] VALUES =   { 1,   4,   4,   2,   1,   4,   3,   3,   1,  10,   5,   2,   4,   2,   1,   4,  12,   1,   1,   1,   2,   5,   4,   8,   3,  10 };
	private static HashMap<Character, Drawable> sLetters = new HashMap<Character, Drawable>();
	private static HashMap<Character, Integer> sValues = new HashMap<Character, Integer>();
	private static Drawable[] sSquare = new Drawable[4];
	private static Drawable[] sRound = new Drawable[4];
	
	public static int sCellWidth;
	
	public int left;
	public int top;
	public int savedLeft;
	public int savedTop;
	public int width;
	public int height;
	public boolean visible = true;
	
	private Bitmap mBitmap;
	private Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
	
	private char mLetter;
	private int mValue;
	
    public SmallTile(Context context) {
	    if (sLetters.size() == 0) {
		    String packageName = context.getPackageName();
		    
		    for (int i = 0; i < 4; i++) {
		    	int id = context.getResources().getIdentifier(SQUARE + i, "drawable", packageName);
			    sSquare[i] = context.getResources().getDrawable(id);
			    sSquare[i].setAlpha(ALPHA);
		    	int w = sSquare[i].getIntrinsicWidth();
		    	int h = sSquare[i].getIntrinsicHeight();
		    	int x = (i % 2) * w;
		    	int y = (i / 2) * h;
		    	sSquare[i].setBounds(x, y, x + w, y + h);
		    }
		    
		    for (int i = 0; i < 4; i++) {
		    	int id = context.getResources().getIdentifier(ROUND + i, "drawable", packageName);
			    sRound[i] = context.getResources().getDrawable(id);
			    sRound[i].setAlpha(ALPHA);
		    	int w = sRound[i].getIntrinsicWidth();
		    	int h = sRound[i].getIntrinsicHeight();
		    	int x = (i % 2) * w;
		    	int y = (i / 2) * h;
		    	sRound[i].setBounds(x, y, x + w, y + h);
		    }
		    
		    for (int i = 0; i < LETTERS.length; i++) {
		    	char c = LETTERS[i];
		    	int v  = VALUES[i];
		    	int id = context.getResources().getIdentifier(PREFIX + i, "drawable", packageName);
		    	Drawable d = context.getResources().getDrawable(id);
		    	d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
		    	sLetters.put(c, d);
		    	sValues.put(c, v);
		    }
	    }
	    
	    width = height = 2 * sSquare[0].getIntrinsicWidth();
	    boolean[] corner = {false, false, false, false};
	    setCorners(corner);
	}
    
    public void setCorners(boolean[] corner) {
	    mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(mBitmap);
	    
	    // true means: there is a neighbor tile
	    for (int i = 0; i < 4; i++) {
	    	if (corner[i])
	    		sSquare[i].draw(canvas);
	    	else
	    		sRound[i].draw(canvas);
	    }
    }
    
	public void draw(Canvas canvas) {
		if (!visible)
			return;
		
		canvas.save();
		canvas.translate(left, top);
		canvas.drawBitmap(mBitmap, 0, 0, mPaint);
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
	public int getColumn() {
		int col = (left + width / 2) / sCellWidth - 1;
		
    	if (col < 0)
    		col = 0;
    	else if (col > 14)
    		col = 14;

    	return col;
	}

	public int getRow() {
		int row = (top + height / 2) / sCellWidth - 1;
		
    	if (row < 0)
    		row = 0;
    	else if (row > 14)
    		row = 14;

    	return row;
	}
}

