package de.afarber.testscroll2;

import java.util.ArrayList;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.GetChars;
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
	private final int NUM_TILES = 3;
	
    private Drawable mGameBoard = getResources().getDrawable(R.drawable.game_board);
    private Drawable mBigTile = getResources().getDrawable(R.drawable.big_tile);
    private ArrayList<Drawable> mTiles = new ArrayList<Drawable>();

    private Random mRandom = new Random();
    private Matrix mMatrix = new Matrix();
    
    private int	  mOffsetX = 0;
    private int   mOffsetY = 0;
    private float mScale = 1.0f;
    private float mMinZoom;
    private float mMaxZoom;
    
    private OverScroller     	 mScroller;
    private GestureDetector		 mGestureDetector;
    private ScaleGestureDetector mScaleDetector;
    
    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        mScroller = new OverScroller(context);
        
        for (int i = 0; i < NUM_TILES; i++) {
        	mTiles.add(getResources().getDrawable(R.drawable.small_tile));
        }

        SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float dX, float dY) {
            	//Log.d("onScroll", "dX=" + dX + ", deY=" + dY);
                scroll(dX, dY);
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float vX, float vY) {
            	//Log.d("onFling", "vX=" + vX + ", vY=" + vY);
                fling(vX, vY);
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
            	adjustZoom();
            	shuffleTiles(getWidth(), getHeight());
            	invalidate();
                return true;
            }
        };
        
        SimpleOnScaleGestureListener scaleListener = new SimpleOnScaleGestureListener() {
        	@Override
        	public boolean onScale(ScaleGestureDetector detector) {
        		mScale *= detector.getScaleFactor();
        		constrainZoom();

        		mOffsetX = diffX() / 2;
        		mOffsetY = diffY() / 2;
        		
        		Log.d("onScale", "mScale=" + mScale + ", focusX=" + detector.getFocusX() + ", focusY=" + detector.getFocusY());
        		
        		invalidate();
        		return true;
        	}
        };
        
        mGestureDetector = new GestureDetector(context, gestureListener);
        mScaleDetector = new ScaleGestureDetector(context, scaleListener);
    }

    private Drawable hitTest(int x, int y) {
        for (Drawable tile: mTiles) {
            Rect rect = tile.getBounds();
            if (rect.contains(x, y))
                return tile;
        }

        return null;
    }
    
    @SuppressLint("NewApi") public boolean onTouchEvent(MotionEvent e) {
    	Log.d("onToucheEvent", "mScale=" + mScale +
    			", mOffsetX=" + mOffsetX +
    			", mOffsetY=" + mOffsetY +
    			", e.getX()=" + e.getX() +
    			", e.getY()=" + e.getY() +
    			", e.getRawX()=" + e.getRawX() +
    			", e.getRawY()=" + e.getRawY()
    			);
    	
    	float[] point = new float[] {e.getX(), e.getY()};

    	Matrix inverse = new Matrix();
    	mMatrix.invert(inverse);
    	inverse.mapPoints(point);

    	float density = getResources().getDisplayMetrics().density;
    	point[0] /= density;
    	point[1] /= density;
    	
    	Drawable tile = hitTest((int) point[0], (int) point[1]);
    	Log.d("onToucheEvent", "tile=" + tile);
    	
        boolean retVal = mScaleDetector.onTouchEvent(e);
        retVal = mGestureDetector.onTouchEvent(e) || retVal;
        return retVal || super.onTouchEvent(e);
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        
    	mMinZoom = Math.min((float) getWidth() / (float) mGameBoard.getIntrinsicWidth(), 
    					   (float) getHeight() / (float) mGameBoard.getIntrinsicHeight());

    	mMaxZoom = 2 * mMinZoom;
    	
    	adjustZoom();
    	
        mGameBoard.setBounds(
        	0, 
        	0, 
        	mGameBoard.getIntrinsicWidth(),
        	mGameBoard.getIntrinsicHeight()
        );

        shuffleTiles(w, h);
    }
    
    private void shuffleTiles(int w, int h) {
        for (Drawable tile: mTiles) {
        	int tileW = tile.getIntrinsicWidth();
        	int tileH = tile.getIntrinsicHeight();
           	int x = mRandom.nextInt(w - tileW);
        	int y = mRandom.nextInt(h - tileH);
        	tile.setBounds(
        		x,
        		y,
    			x + tileW, 
    			y + tileH
    		);
        	
	    	Log.d("shuffleTiles", "tile=" + tile.getBounds());
        }
    }

    private void adjustZoom() {
    	//Log.d("adjustZoom", "getWidth()=" + getWidth() + ", getHeight()=" + getHeight());
    	//Log.d("adjustZoom", "getIntrinsicWidth()=" + gameBoard.getIntrinsicWidth() + ", getIntrinsicHeight()=" + gameBoard.getIntrinsicHeight());
    	//Log.d("adjustZoom", "mMinZoom=" + mMinZoom + ", mMaxZoom=" + mMaxZoom);

    	mScale = (mScale > mMinZoom ? mMinZoom : mMaxZoom);
    	mOffsetX = diffX() / 2;
    	mOffsetY = diffY() / 2;

    	//Log.d("adjustZoom", "mScale=" + mScale + ", mOffsetX=" + mOffsetX + ", mOffsetY=" + mOffsetY);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // computeScrollOffset() returns true if a fling is in progress
        if (mScroller.computeScrollOffset()) {
            mOffsetX = mScroller.getCurrX();
            mOffsetY = mScroller.getCurrY();
            postInvalidateDelayed(50);
        }
        
        mMatrix.reset();
        mMatrix.setTranslate(mOffsetX, mOffsetY);
        mMatrix.postScale(mScale, mScale);
        canvas.setMatrix(mMatrix);
        
        mGameBoard.draw(canvas);
        
        for (Drawable tile: mTiles) {
        	tile.draw(canvas);
        }
        canvas.restore();
    }

    // called when the GestureListener detects scroll
    public void scroll(float distanceX, float distanceY) {
        mScroller.forceFinished(true);
        mOffsetX -= (int) distanceX;
        mOffsetY -= (int) distanceY;
        constrainOffsets();
        invalidate();
    }

    // called when the GestureListener detects fling
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
