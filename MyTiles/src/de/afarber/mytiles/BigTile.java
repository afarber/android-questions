package de.afarber.mytiles;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class BigTile {
	private static final String PREFIX = "big_";
	private static final int TILE = R.drawable.big_tile;
	private static final int ALPHA = 200;
	
	private static final CharacterIterator ABC = new StringCharacterIterator("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
	private static HashMap<Character, Drawable> sDrawables = new HashMap<Character, Drawable>();
	
	public int left;
	public int top;
	public int savedLeft;
	public int savedTop;
	public int width;
	public int height;
	public boolean visible = true;
	
	private Drawable mBackground;
	
	private char mLetter;
	private int mValue;
	
    public BigTile(Context context) {
    	mBackground = context.getResources().getDrawable(TILE);
        mBackground.setAlpha(ALPHA);
    	width = mBackground.getIntrinsicWidth();
    	height = mBackground.getIntrinsicHeight();
    	mBackground.setBounds(0, 0, width, height);
    	
	    if (sDrawables.size() > 0)
	    	return;
	    
	    int i = 0;
	    for (char c = ABC.first(); 
	    		c != CharacterIterator.DONE; 
	    		c = ABC.next(), i++) {
	    	int id = context.getResources().getIdentifier(PREFIX + i, "drawable", context.getPackageName());
	    	Drawable d = context.getResources().getDrawable(id);
	    	d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
	    	sDrawables.put(c, d);
	    }	    
	}
    
	public void draw(Canvas canvas) {
		if (!visible)
			return;
		
		canvas.save();
		canvas.translate(left, top);
		mBackground.draw(canvas);
		Drawable d = sDrawables.get(mLetter);
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
