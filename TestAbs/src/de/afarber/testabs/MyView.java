package de.afarber.testabs;

import java.util.Random;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsoluteLayout;

@SuppressWarnings("deprecation")
public class MyView extends AbsoluteLayout {
	
	public final static int NUM = 3;
	public final static String STR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public final static String APP = "de.afarber.testabs";
	
	private Random mRandom = new Random();

    public MyView(Context context) {
        this(context, null, 0);
    }

    public MyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        for (int i = 0; i < NUM; i++) {
        	String letter = String.valueOf(STR.charAt(mRandom.nextInt(STR.length())));
        	
        	SmallTile tile = new SmallTile(
    			getContext(),
    			null,
    			letter,
    			String.valueOf(i + 1)
    		);
        	
            addView(tile);       	
        }
    }
    
    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    	Log.d(APP, "w=" + w + "; h=" + h + ", oldw=" + oldw + ", oldh=" + oldh);

        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE)
            	return;
            
        	int x = mRandom.nextInt(w - child.getWidth());
        	int y = mRandom.nextInt(h - child.getHeight());
        	AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
    			LayoutParams.WRAP_CONTENT, 
    			LayoutParams.WRAP_CONTENT, 
    			x, 
    			y
        	);
        	
        	Log.d(APP, i + ": x=" + x + "; y=" + y);
        }
        
        requestLayout();
    }
}
