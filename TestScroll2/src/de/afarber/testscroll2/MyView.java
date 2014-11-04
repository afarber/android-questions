package de.afarber.testscroll2;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.OverScroller;

public class MyView extends View {
    private final int NUM_TILES = 3;

    private Drawable mGameBoard = getResources().getDrawable(R.drawable.game_board);
    private Drawable mBigTile = getResources().getDrawable(R.drawable.big_tile);
    private ArrayList<Drawable> mTiles = new ArrayList<Drawable>();

    private Random mRandom = new Random();
    private Matrix mMatrix = new Matrix();
    private float[] mValues = new float[9];

    private float mMinZoom;
    private float mMaxZoom;

    private OverScroller mScroller;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleDetector;

    public MyView(Context context) {
        this(context, null);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mScroller = new OverScroller(context);

        for (int i = 0; i < NUM_TILES; i++) {
            mTiles.add(getResources().getDrawable(R.drawable.small_tile));
        }

        GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
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
                shuffleTiles(getWidth(), getHeight());
                invalidate();
                return true;
            }
        };

        ScaleGestureDetector.SimpleOnScaleGestureListener scaleListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                mScroller.forceFinished(true);
                float factor = detector.getScaleFactor();
                Log.d("onScale", "factor=" + factor);
                mMatrix.postScale(factor, factor);
                fixScaling();
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

    public boolean onTouchEvent(MotionEvent e) {
    	/*
        Log.d("onToucheEvent", "mScale=" + mScale +
                        ", e.getX()=" + e.getX() +
                        ", e.getY()=" + e.getY() +
                        ", e.getRawX()=" + e.getRawX() +
                        ", e.getRawY()=" + e.getRawY()
        );
		*/
    	
        float[] point = new float[] {e.getX(), e.getY()};

        Matrix inverse = new Matrix();
        mMatrix.invert(inverse);
        inverse.mapPoints(point);

        Drawable tile = hitTest((int) point[0], (int) point[1]);
        Log.d("onToucheEvent", "tile = " + tile);
        
        if (tile != null) {
        	// XXX display/drag/hide big tile
        	return true;
        }

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
        mScroller.forceFinished(true);
        mMatrix.getValues(mValues);
        //float oldX = mValues[Matrix.MTRANS_X];
        //float oldY = mValues[Matrix.MTRANS_Y];
        float scaleX = mValues[Matrix.MSCALE_X];
        //float scaleY = mValues[Matrix.MSCALE_Y];
        
        float newScale = (scaleX > mMinZoom ? mMinZoom : mMaxZoom);
        float minX = getWidth() - newScale * mGameBoard.getIntrinsicWidth();
        float minY = getHeight() - newScale * mGameBoard.getIntrinsicHeight();
      
        Log.d("adjustZoom", "scaleX=" + scaleX + ", newScale=" + newScale +
        		", minX=" + minX + ", minY=" + minY);

        mMatrix.setScale(newScale, newScale);
        mMatrix.postTranslate(minX / 2, minY / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // if fling is in progress
        if (mScroller.computeScrollOffset()) {
        	mMatrix.getValues(mValues);
            float oldX = mValues[Matrix.MTRANS_X];
            float oldY = mValues[Matrix.MTRANS_Y];
            //float scaleX = mValues[Matrix.MSCALE_X];
            //float scaleY = mValues[Matrix.MSCALE_Y];
            
            float dX = mScroller.getCurrX() - oldX;
            float dY = mScroller.getCurrY() - oldY;
/*            
            Log.d("onDraw", "oldX=" + oldX + ", oldY=" + oldY +
            		", getCurrX()=" + mScroller.getCurrX() + ", getCurrY()=" + mScroller.getCurrY());
*/
            mMatrix.postTranslate(dX, dY);
            postInvalidateDelayed(50);
        }

        canvas.concat(mMatrix);

        mGameBoard.draw(canvas);
        for (Drawable tile: mTiles) {
            tile.draw(canvas);
        }
    }

    public void scroll(float dX, float dY) {
        mScroller.forceFinished(true);
        mMatrix.postTranslate(-dX, -dY);
        fixTranslation();
        invalidate();
    }

    public void fling(float vX, float vY) {
        mScroller.forceFinished(true);
        mMatrix.getValues(mValues);
        float oldX = mValues[Matrix.MTRANS_X];
        float oldY = mValues[Matrix.MTRANS_Y];
        float scaleX = mValues[Matrix.MSCALE_X];
        float scaleY = mValues[Matrix.MSCALE_Y];

        float minX = getWidth() - scaleX * mGameBoard.getIntrinsicWidth();
        float minY = getHeight() - scaleY * mGameBoard.getIntrinsicHeight();
        float maxX = 0;
        float maxY = 0;
        
        // if scaled game board is smaller than this view -
        // then place it in the middle of the view
        if (minX >= 0)
        	minX = maxX = minX / 2;
        if (minY >= 0)
        	minY = maxY = minY / 2;
      
        Log.d("fling", "vX=" + vX + ", vY=" + vY +
			", oldX=" + oldX + ", oldY=" + oldY +
			", scaleX=" + scaleX + ", scaleY=" + scaleY +
			", minX=" + minX + ", minY=" + minY);
        
        mScroller.forceFinished(true);
        mScroller.fling(
                (int) oldX,
                (int) oldY,
                (int) vX,
                (int) vY,
                (int) minX,
                (int) maxX,
                (int) minY,
                (int) maxY,
                50,
                50
        );
        invalidate();
    }
    
    private void fixScaling() {
        mMatrix.getValues(mValues);
        //float x = mValues[Matrix.MTRANS_X];
        //float y = mValues[Matrix.MTRANS_Y];
        float scaleX = mValues[Matrix.MSCALE_X];
        //float scaleY = mValues[Matrix.MSCALE_Y];

        //float minX = getWidth() - scaleX * mGameBoard.getIntrinsicWidth();
        //float minY = getHeight() - scaleY * mGameBoard.getIntrinsicHeight();   
        
        if (scaleX > mMaxZoom) {
        	float factor = mMaxZoom / scaleX;
            mMatrix.postScale(factor, factor);
        } else if (scaleX < mMinZoom) {
        	float factor = mMinZoom / scaleX;
            mMatrix.postScale(factor, factor);
        }
    }
    
    private void fixTranslation() {
        mMatrix.getValues(mValues);
        float x = mValues[Matrix.MTRANS_X];
        float y = mValues[Matrix.MTRANS_Y];
        float scaleX = mValues[Matrix.MSCALE_X];
        float scaleY = mValues[Matrix.MSCALE_Y];

        float minX = getWidth() - scaleX * mGameBoard.getIntrinsicWidth();
        float minY = getHeight() - scaleY * mGameBoard.getIntrinsicHeight();    	

        float dX = 0.0f;
        float dY = 0.0f;
        
        if (minX >= 0)
        	dX = minX / 2 - x;
        else if (x > 0)
        	dX = -x;
        else if (x < minX)
        	dX = minX - x;
        
        if (minY >= 0)
        	dY = minY / 2 - y;
        else if (y > 0)
        	dY = -y;
        else if (y < minY)
        	dY = minY - y;
        
        if (dX != 0.0 || dY != 0.0)
        	mMatrix.postTranslate(dX, dY);
    }
    
    
}
