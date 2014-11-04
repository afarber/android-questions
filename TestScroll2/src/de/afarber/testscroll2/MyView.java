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
                mMatrix.getValues(mValues);
                //float oldX = mValues[Matrix.MTRANS_X];
                //float oldY = mValues[Matrix.MTRANS_Y];
                float scaleX = mValues[Matrix.MSCALE_X];
                //float scaleY = mValues[Matrix.MSCALE_Y];
                
                float factor = detector.getScaleFactor();
                float newScale = scaleX * factor;

                Log.d("onScale", "scaleX=" + scaleX + ", newScale=" + newScale);

                if (newScale > mMaxZoom)
                	factor = mMaxZoom / scaleX;
                else if (newScale < mMinZoom)
                	factor = mMinZoom / scaleX;
                
                mMatrix.postScale(factor, factor);
                
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
        mMatrix.getValues(mValues);
        float oldX = mValues[Matrix.MTRANS_X];
        float oldY = mValues[Matrix.MTRANS_Y];
        float scaleX = mValues[Matrix.MSCALE_X];
        float scaleY = mValues[Matrix.MSCALE_Y];
        
        float newX = oldX - dX;
        float newY = oldY - dY;
        
        float minX = getWidth() - scaleX * mGameBoard.getIntrinsicWidth();
        float minY = getHeight() - scaleY * mGameBoard.getIntrinsicHeight();

        Log.d("scroll", "dX=" + dX + ", dY=" + dY +
			", oldX=" + oldX + ", oldY=" + oldY +
			", newX=" + newX + ", newY=" + newY +
			", minX=" + minX + ", minY=" + minY);
        
        if (minX >= 0)
        	dX = oldX - minX / 2;
        else if (newX > 0)
        	dX += newX;
        else if (newX < minX)
        	dX -= (minX - newX);
        
        if (minY >= 0)
        	dY = oldY - minY / 2;
        else if (newY > 0)
        	dY += newY;
        else if (newY < minY)
        	dY -= (minY - newY);
        
        mMatrix.postTranslate(-dX, -dY);
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
}
