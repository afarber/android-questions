package de.afarber.testabs;

import java.util.Random;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;

@SuppressWarnings("deprecation")
public class MyView extends AbsoluteLayout {
	
	public final static int NUM = 3;
	public final static String STR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public final static String APP = "de.afarber.testabs";
	
	private Random rnd = new Random();
	private int oldX;
	private int oldY;

    public MyView(Context context) {
        this(context, null, 0);
    }

    public MyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        for (int i = 0; i < NUM; i++) {
        	String letter = String.valueOf(STR.charAt(rnd.nextInt(STR.length())));
        	
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
    protected void onSizeChanged (int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

    	Log.d(APP, "w=" + w + "; h=" + h + ", oldw=" + oldW + ", oldh=" + oldH);

        for (int i = getChildCount() - 1; i >= 0; i--) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE)
            	return;
            
        	int x = rnd.nextInt(w - child.getWidth());
        	int y = rnd.nextInt(h - child.getHeight());
        	AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
    			LayoutParams.WRAP_CONTENT, 
    			LayoutParams.WRAP_CONTENT, 
    			x, 
    			y
        	);
        	
        	child.setLayoutParams(params);
        	
        	Log.d(APP, i + ": x=" + x + "; y=" + y);
        }
    }
    
    private View hitTest(int x, int y) {
    	Rect rect = new Rect();
 
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE)
            	return null;

            child.getHitRect(rect);
            if (rect.contains(x, y)) {
            	return child;
            }
        }
        
        return null;
    }
    
    public boolean onTouchEvent(MotionEvent event) {
    	//super.onTouchEvent(event);
    	
    	int x = (int)event.getX();
    	int y = (int)event.getY();
    	
        //PointF pt = new PointF(event.getX(), event.getY());

        View view = hitTest((int)event.getX(), (int)event.getY());
        Log.d(APP, "hit: " + event.getAction() + " " + view);
        if (view == null || !(view instanceof SmallTile))
        	return false;
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:
            	int newX = view.getLeft() + x - oldX;
            	int newY = view.getTop() + y - oldY;
            	AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
        			LayoutParams.WRAP_CONTENT, 
        			LayoutParams.WRAP_CONTENT, 
        			newX, 
        			newY
            	);
            	
            	view.setLayoutParams(params);
            	invalidate();
                
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        
    	oldX = x;
    	oldY = y;
        return true;
    }
}
