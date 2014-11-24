package de.afarber.mybars;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class MyView extends View {
	private static final char[] LETTERS = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
	private static final boolean TOO_OLD = (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH);
	
    private Drawable mGameBoard = getResources().getDrawable(R.drawable.game_board);
    private ArrayList<SmallTile> mBoardTiles = new ArrayList<SmallTile>();
    private ArrayList<SmallTile> mBarTiles = new ArrayList<SmallTile>();
    private SmallTile mSmallTile = null;
    private BigTile mBigTile;

    private Random mRandom = new Random();
    private Matrix mMatrix = new Matrix();
    private float[] mValues = new float[9];

    private float mMinZoom;
    private float mMaxZoom;
    
    private float mSavedX;
    private float mSavedY;

    private ScrollerCompat mScroller;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleDetector;
    
    private int mWidth;
    private int mHeight;
    private SmallTile[][] mGrid = new SmallTile[15][15];

    private ColorDrawable mBar = new ColorDrawable(Color.BLUE);

    public MyView(Context context) {
        this(context, null);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mScroller = ScrollerCompat.create(context);

        mBigTile = new BigTile(getContext());
        mBigTile.visible = false;
       
	    for (char c: LETTERS) {
        	SmallTile tile = new SmallTile(getContext());
        	tile.setLetter(c);
        	tile.visible = true;
            mBoardTiles.add(tile);
        }

	    for (int i = 0; i < 7; i++) {
        	SmallTile tile = new SmallTile(getContext());
        	char c = LETTERS[i];
        	tile.setLetter(c);
        	tile.visible = true;
            mBarTiles.add(tile);
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
            	if (TOO_OLD)
            		return false;
            	
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

        ScaleGestureDetector.SimpleOnScaleGestureListener scaleListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
            	if (TOO_OLD)
            		return false;
            	
                mScroller.abortAnimation();
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

        mWidth = mGameBoard.getIntrinsicWidth();
        mHeight = mGameBoard.getIntrinsicHeight();
        mGameBoard.setBounds(0, 0, mWidth, mHeight);
        // there are 15 cells in a row and 1 padding at each side
        SmallTile.sCellWidth = Math.round(mWidth / 17.0f);
        
        mBar.setAlpha(60);
    }

    private SmallTile hitTest(float x, float y) {
    	for (int i = mBoardTiles.size() - 1; i >= 0; i--) {
    		SmallTile tile = mBoardTiles.get(i);
        	if (!tile.visible)
        		continue;
            if (tile.contains((int) x, (int) y))
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
        float x = point[0];
        float y = point[1];

        if (e.getPointerCount() == 1) {
    		mScroller.abortAnimation();

        	switch (e.getAction()) {
		        case MotionEvent.ACTION_DOWN: 
		            SmallTile tile = hitTest(x, y);
		            Log.d("onToucheEvent", "tile = " + tile);
		            if (tile != null) {
		            	int depth = mBoardTiles.indexOf(tile);
		            	if (depth >= 0) {
			            	mBoardTiles.remove(depth);
			            	mBoardTiles.add(tile);
		            	}
		            	
		            	mSmallTile = tile;
		            	mSmallTile.save();
		            	mSmallTile.visible = false;
		            	
		            	int col = mSmallTile.getColumn();
		            	int row = mSmallTile.getRow();
		            	mGrid[col][row] = null;
		            	updateNeighbors(col, row);
		            	
		            	mBigTile.copy(mSmallTile);
		            	mBigTile.visible = true;
		            	mSavedX = x;
		            	mSavedY = y;
		            	invalidate();
		            	return true;
		            }
		        break;
		            
		        case MotionEvent.ACTION_MOVE:
		        	if (mSmallTile != null) {
		        		int dX = Math.round(x - mSavedX);
		        		int dY = Math.round(y - mSavedY);
		        		mSmallTile.offset(dX, dY);
		            	mBigTile.offset(dX, dY);
		            	
		                mMatrix.getValues(mValues);
		                float oldX = mValues[Matrix.MTRANS_X];
		                float oldY = mValues[Matrix.MTRANS_Y];
		                float scaleX = mValues[Matrix.MSCALE_X];
		                float scaleY = mValues[Matrix.MSCALE_Y];
		                float maxX = 0;
		                float maxY = 0;
		                float minX = getWidth() - scaleX * mWidth;
		                float minY = getHeight() - scaleY * mHeight;    	
		                float diff = 100;
		                
		            	if (e.getX() < 50) {
		            		if (oldX + diff > maxX)
		            			diff = maxX - oldX;
		            		mScroller.startScroll((int) oldX, (int) oldY, (int) diff, 0);
		            	} else if (e.getX() > getWidth() - 50) {
		            		if (oldX - diff < minX)
		            			diff =  oldX - minX;
		            		mScroller.startScroll((int) oldX, (int) oldY, (int) -diff, 0);
		        	    }
		        	
		            	if (e.getY() < 50) {
		            		if (oldY + diff > maxY)
		            			diff = maxY - oldY;
		            		mScroller.startScroll((int) oldX, (int) oldY, 0, (int) diff);
		            	} else if (e.getY() > getHeight() - 50) {
		            		if (oldY - diff < minY)
		            			diff = oldY - minY;
		            		mScroller.startScroll((int) oldX, (int) oldY, 0, (int) -diff);
		        	    }
		        	
		        		invalidate();
		        		return true;
		        	}
		        break;
		
		        case MotionEvent.ACTION_UP:
		        case MotionEvent.ACTION_CANCEL:
		        	if (mSmallTile != null) {
		            	alignToGrid(mSmallTile);
		            	mBigTile.visible = false;
		            	mSmallTile.visible = true;
		        		mSmallTile = null;
		        		invalidate();
		        		return true;
		        	}
		        break;
	        }
        }
        
        boolean retVal = mScaleDetector.onTouchEvent(e);
        retVal = mGestureDetector.onTouchEvent(e) || retVal;
        return retVal || super.onTouchEvent(e);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        mMinZoom = Math.min((float) w / (float) mWidth,
                	        (float) h / (float) mHeight);

        mMaxZoom = 2 * mMinZoom;
        
        mBar.setBounds(0, h - 80, w, h);

        adjustZoom();
    }

    private void shuffleTiles() {
        Log.d("shuffleTiles", "mWidth=" + mWidth + ", mHeight=" + mHeight + ", sCellWidth=" + SmallTile.sCellWidth);
        
        for (int col = 0; col < 15; col++)
            for (int row = 0; row < 15; row++)
            	mGrid[col][row] = null;
        
        for (SmallTile tile: mBoardTiles) {
            tile.move(
            	mRandom.nextInt(mWidth - tile.width),
                mRandom.nextInt(mHeight - tile.height)
            );
            alignToGrid(tile);
            Log.d("shuffleTiles", "tile=" + tile);
        }
        
        for (int i = mBarTiles.size() - 1; i >= 0; i--) {
        	SmallTile tile = mBarTiles.get(i);
        	tile.move(i * tile.width, getHeight() - tile.height);
        }
    }

    private void adjustZoom() {
        mScroller.abortAnimation();
        mMatrix.getValues(mValues);
        //float oldX = mValues[Matrix.MTRANS_X];
        //float oldY = mValues[Matrix.MTRANS_Y];
        float scaleX = mValues[Matrix.MSCALE_X];
        //float scaleY = mValues[Matrix.MSCALE_Y];
        
        float newScale = (scaleX > mMinZoom ? mMinZoom : mMaxZoom);
        float minX = getWidth() - newScale * mWidth;
        float minY = getHeight() - newScale * mHeight;
      
        Log.d("adjustZoom", "scaleX=" + scaleX + ", newScale=" + newScale +
        		", minX=" + minX + ", minY=" + minY);

        mMatrix.setScale(newScale, newScale);
        mMatrix.postTranslate(minX / 2, minY / 2);
        
        if (newScale == mMinZoom)
        	shuffleTiles();
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
            postInvalidateDelayed(30);
        }

        canvas.save();
        canvas.concat(mMatrix);
        mGameBoard.draw(canvas);
        for (SmallTile tile: mBoardTiles) {
            tile.draw(canvas);
        }
        canvas.restore();
        
        mBar.draw(canvas);
        for (SmallTile tile: mBarTiles) {
            tile.draw(canvas);
        }
        
        canvas.save();
        canvas.concat(mMatrix);
        mBigTile.draw(canvas);
        canvas.restore();
    }

    public void scroll(float dX, float dY) {
        mScroller.abortAnimation();
        mMatrix.postTranslate(-dX, -dY);
        fixTranslation();
        invalidate();
    }

    public void fling(float vX, float vY) {
        mScroller.abortAnimation();
        mMatrix.getValues(mValues);
        float oldX = mValues[Matrix.MTRANS_X];
        float oldY = mValues[Matrix.MTRANS_Y];
        float scaleX = mValues[Matrix.MSCALE_X];
        float scaleY = mValues[Matrix.MSCALE_Y];

        float minX = getWidth() - scaleX * mWidth;
        float minY = getHeight() - scaleY * mHeight;
        float maxX = 0;
        float maxY = 0;
        
        // if scaled game board is smaller than this view -
        // then place it in the middle of the view
        if (minX >= 0)
        	minX = maxX = minX / 2;
        if (minY >= 0)
        	minY = maxY = minY / 2;
/*      
        Log.d("fling", "vX=" + vX + ", vY=" + vY +
			", x=" + x + ", y=" + y +
			", scaleX=" + scaleX + ", scaleY=" + scaleY +
			", minX=" + minX + ", minY=" + minY);
*/        
        mScroller.abortAnimation();
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
        //float oldX = mValues[Matrix.MTRANS_X];
        //float oldY = mValues[Matrix.MTRANS_Y];
        float scaleX = mValues[Matrix.MSCALE_X];
        //float scaleY = mValues[Matrix.MSCALE_Y];

        //float minX = getWidth() - scaleX * w;
        //float minY = getHeight() - scaleY * h;   
        
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
        float oldX = mValues[Matrix.MTRANS_X];
        float oldY = mValues[Matrix.MTRANS_Y];
        float scaleX = mValues[Matrix.MSCALE_X];
        float scaleY = mValues[Matrix.MSCALE_Y];

        float minX = getWidth() - scaleX * mWidth;
        float minY = getHeight() - scaleY * mHeight;    	

        float dX = 0.0f;
        float dY = 0.0f;
        
        if (minX >= 0)
        	dX = minX / 2 - oldX;
        else if (oldX > 0)
        	dX = -oldX;
        else if (oldX < minX)
        	dX = minX - oldX;
        
        if (minY >= 0)
        	dY = minY / 2 - oldY;
        else if (oldY > 0)
        	dY = -oldY;
        else if (oldY < minY)
        	dY = minY - oldY;
        
        if (dX != 0.0 || dY != 0.0)
        	mMatrix.postTranslate(dX, dY);
    }
    
    private boolean[] buildCorners(int col, int row) {
	    boolean[] corner = {
		    // top left corner (true means: there is a neighbor tile)
		    (
		    	(col > 0 && mGrid[col - 1][row] != null) ||  
		    	(row > 0 && mGrid[col][row - 1] != null)
		    ),
	
		    // top right corner
		    (
		    	(col < 14 && mGrid[col + 1][row] != null) ||  
		    	(row > 0  && mGrid[col][row - 1] != null)
		    ),
			
		    // bottom left corner
		    (
		    	(col > 0  && mGrid[col - 1][row] != null) ||  
		    	(row < 14 && mGrid[col][row + 1] != null)
		    ),
		    
		    // bottom right corner
		    (
		    	(col < 14 && mGrid[col + 1][row] != null) ||  
		    	(row < 14 && mGrid[col][row + 1] != null)
		    )
	    };
		    
	    return corner;
    }

    // check the tiles at 3 x 3 or 2 x 2 sub-grid
    private void updateNeighbors(int col, int row) {
    	
    	int startCol = Math.max(0, col - 1);
    	int endCol   = Math.min(14, col + 1);
    	int startRow = Math.max(0, row - 1);
    	int endRow   = Math.min(14, row + 1);
    	
    	for (int i = startCol; i <= endCol; i++) {
        	for (int j = startRow; j <= endRow; j++) {
        		SmallTile tile = mGrid[i][j]; 
        		if (tile != null) {
        	    	boolean[] corner = buildCorners(i, j);
        		    tile.setCorners(corner);
        		}
        	}
    	}
    }
    
    private void alignToGrid(SmallTile tile) {
    	int col = tile.getColumn();
    	int row = tile.getRow();
    	
    	// find a free cell at the game board
    	while (mGrid[col][row] != null) {
    		col = (col + 1) % 15;

    		if (col == 0)
        		row = (row + 1) % 15;
    	}
    	
    	mGrid[col][row] = tile;
    	updateNeighbors(col, row);
    	
    	tile.left = (col + 1) * SmallTile.sCellWidth;
    	tile.top = (row + 1) * SmallTile.sCellWidth;
    }
}

