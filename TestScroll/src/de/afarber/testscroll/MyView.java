package de.afarber.testscroll;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.widget.OverScroller;

public class MyView extends View {
    private OverScroller     	 mScroller;
    private GestureDetector		 mGestureDetector;
    private ScaleGestureDetector mScaleDetector;
    private Drawable mGameBoard = getResources().getDrawable(R.drawable.game_board);

    private int		mOffsetX = 0;
    private int     mOffsetY = 0;
    private float	mScale = 1.0f;
    private float	mMinZoom;
    private float	mMaxZoom;

    public MyView(Context context) {
        this(context, null);
    }
    
    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        mScroller = new OverScroller(context);

        SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float dX, float dY) {
                scroll(dX, dY);
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float vX, float vY) {
                fling(vX, vY);
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
            	adjustZoom();
            	invalidate();
                return true;
            }
        };
        
        SimpleOnScaleGestureListener scaleListener = new SimpleOnScaleGestureListener() {
        	@Override
        	public boolean onScale(ScaleGestureDetector detector) {
        		mScale *= detector.getScaleFactor();

        		constrainZoom();

        		float focusX = detector.getFocusX();
                float focusY = detector.getFocusY();
        		
                mOffsetX = (int) ((mOffsetX + focusX) * mScale - focusX);
                mOffsetX = Math.min(Math.max(mOffsetX, 0), (int) getWidth() - mGameBoard.getIntrinsicWidth());
        		
                mOffsetY = (int) ((mOffsetY + focusY) * mScale - focusY);
                mOffsetY = Math.min(Math.max(mOffsetY, 0), (int) getHeight() - mGameBoard.getIntrinsicHeight());
        		
        		constrainOffsets();
        		
        		Log.d("onScale", "mScale=" + mScale + ", focusX=" + detector.getFocusX() + ", focusY=" + detector.getFocusY());
        		
        		invalidate();
        		return true;
        	}
        };
        
        mGestureDetector = new GestureDetector(context, gestureListener);
        mScaleDetector = new ScaleGestureDetector(context, scaleListener);
        
        mGameBoard.setBounds(
            	0, 
            	0, 
            	mGameBoard.getIntrinsicWidth(),
            	mGameBoard.getIntrinsicHeight()
            );
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean retVal = mScaleDetector.onTouchEvent(event);
        retVal = mGestureDetector.onTouchEvent(event) || retVal;
        return retVal || super.onTouchEvent(event);
    }
    
    @Override
    protected void onSizeChanged (int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        
    	mMinZoom = Math.min((float) getWidth() / (float) mGameBoard.getIntrinsicWidth(), 
    					   (float) getHeight() / (float) mGameBoard.getIntrinsicHeight());

    	mMaxZoom = 2 * mMinZoom;
    	
    	adjustZoom();
    }
    
    private void adjustZoom() {
    	mScale = (mScale > mMinZoom ? mMinZoom : mMaxZoom);
    	mOffsetX = diffX() / 2;
    	mOffsetY = diffY() / 2;

    	Log.d("adjustZoom", "mScale=" + mScale + ", mOffsetX=" + mOffsetX + ", mOffsetY=" + mOffsetY);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // is a fling in progress?
        if (mScroller.computeScrollOffset()) {
            mOffsetX = mScroller.getCurrX();
            mOffsetY = mScroller.getCurrY();
            postInvalidateDelayed(50);
        }
        
        canvas.translate(mOffsetX, mOffsetY);
        canvas.scale(mScale, mScale);
        mGameBoard.draw(canvas);  
    }

    public void scroll(float dX, float dY) {
        mScroller.forceFinished(true);
        mOffsetX -= (int) dX;
        mOffsetY -= (int) dY;
        constrainOffsets();
        invalidate();
    }

    public void fling(float vX, float vY) {
    	int minX = diffX();
    	int maxX = 0;
    	
    	int minY = diffY();
    	int maxY = 0;
    	
    	if (minX > maxX)
    		minX = maxX = diffX() / 2;
    			
    	if (minY > maxY)
    		minY = maxY = diffY() / 2;
    	
        mScroller.forceFinished(true);
        mScroller.fling(
        	mOffsetX, 
        	mOffsetY, 
        	(int) vX, 
        	(int) vY,  
        	minX,
        	maxX,
        	minY, 
        	maxY,
        	50,
        	50
        );
        invalidate();
    }

    private int diffX() {
    	return (int) (getWidth() - mScale * mGameBoard.getIntrinsicWidth());

    }

    private int diffY() {
    	return (int) (getHeight() - mScale * mGameBoard.getIntrinsicHeight());
    }

    private void constrainZoom() {
		mScale = Math.max(mScale, mMinZoom);
		mScale = Math.min(mScale, mMaxZoom);
    }
    
    private void constrainOffsets() {
    	int minX = diffX();
    	int maxX = 0;
    	
    	int minY = diffY();
    	int maxY = 0;
    	
    	if (minX > maxX)
    		mOffsetX = diffX() / 2;
    	else {
    		mOffsetX = Math.max(mOffsetX, minX);
    		mOffsetX = Math.min(mOffsetX, maxX);
    	}
    	
    	if (minY > maxY)
    		mOffsetY = diffY() / 2;
    	else {
    		mOffsetY = Math.max(mOffsetY, minY);
    		mOffsetY = Math.min(mOffsetY, maxY);
    	}
    }
}
