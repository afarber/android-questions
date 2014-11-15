package de.afarber.mytiles;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashMap;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class SmallTile {
	private static final int TILE = R.drawable.small_tile;
	private static final int ALPHA = 220;
	
	private static final CharacterIterator ABC = new StringCharacterIterator("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
	private static HashMap<Character, Drawable> sDrawables = new HashMap<Character, Drawable>();
	
	public int left;
	public int top;
	public int savedLeft;
	public int savedTop;
	public int width;
	public int height;
	public boolean visible = true;
	
	private Context mContext;
	private Drawable mBackground;
	private Drawable mForeground;
	
	private char mLetter;
	private int mValue;
	
    public SmallTile(Context context) {
    	mContext = context;
    	
    	mBackground = context.getResources().getDrawable(TILE);
        mBackground.setAlpha(ALPHA);
    	width = mBackground.getIntrinsicWidth();
    	height = mBackground.getIntrinsicHeight();
    	mBackground.setBounds(0, 0, width, height);
    	
    	int n = (new Random()).nextInt(26);
    	int id = context.getResources().getIdentifier("small_" + n,"drawable", context.getPackageName());
	    mForeground = context.getResources().getDrawable(id);
	    mForeground.setBounds(0, 0, mForeground.getIntrinsicWidth(), mForeground.getIntrinsicHeight());
	}
    
	public void draw(Canvas canvas) {
		if (!visible)
			return;
		
		canvas.save();
		canvas.translate(left, top);
		mBackground.draw(canvas);
		mForeground.draw(canvas);
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
}
