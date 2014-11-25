package de.afarber.mybars;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
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
	
	private GameBoard mGameBoard;
    private ArrayList<SmallTile> mBoardTiles = new ArrayList<SmallTile>();
    private ArrayList<SmallTile> mBarTiles = new ArrayList<SmallTile>();
    private SmallTile mSmallTile = null;
    private BigTile mBigTile;

    private Random mRandom = new Random();

    private float mBoardX;
    private float mBoardY;

    private float mScreenX;
    private float mScreenY;

    private ScrollerCompat mScroller;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleDetector;
    
    private SmallTile[][] mGrid = new SmallTile[15][15];

    private ColorDrawable mBar = new ColorDrawable(Color.BLUE);

    public MyView(Context context) {
        this(context, null);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mScroller = ScrollerCompat.create(context);

        mGameBoard = new GameBoard(getContext());
        
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
                doubleTap();
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
                mGameBoard.scaleBy(factor);
                invalidate();
                return true;
            }
        };

        mGestureDetector = new GestureDetector(context, gestureListener);
        mScaleDetector = new ScaleGestureDetector(context, scaleListener);

        // there are 15 cells in a row and 1 padding at each side
        SmallTile.sCellWidth = Math.round(mGameBoard.width / 17.0f);
        
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
        Log.d("onToucheEvent", 
        	"e.getX()=" + e.getX() +
            ", e.getY()=" + e.getY());
		*/
    	
    	PointF boardPoint = mGameBoard.screenToBoard(e.getX(), e.getY());

        if (e.getPointerCount() == 1) {
    		mScroller.abortAnimation();

        	switch (e.getAction()) {
		        case MotionEvent.ACTION_DOWN: 
		            SmallTile tile = hitTest(boardPoint.x, boardPoint.y);
		            Log.d("onToucheEvent", "touched tile = " + tile);
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
		            	
		            	mBigTile.copy(mSmallTile.getLetter(), e.getX(), e.getY());
		            	mBigTile.visible = true;
		            	mBoardX = boardPoint.x;
		            	mBoardY = boardPoint.y;
		            	mScreenX = e.getX();
		            	mScreenY = e.getY();
		            	invalidate();
		            	return true;
		            }
		        break;
		            
		        case MotionEvent.ACTION_MOVE:
		        	if (mSmallTile != null) {
		        		mSmallTile.offset(Math.round(boardPoint.x - mBoardX), Math.round(boardPoint.y - mBoardY));
		            	mBigTile.offset(Math.round(e.getX() - mScreenX), Math.round(e.getY() - mScreenY));
		            	draggedToEdge(e.getX(), e.getY());
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

        mGameBoard.setParentSize(w, h);        
        mGameBoard.toggleScale();
        mBar.setBounds(0, h - mBigTile.height, w, h);
        placeTiles();
        invalidate();
    }

    private void placeTiles() {
        Log.d("shuffleTiles", "mGameBoard.width=" + mGameBoard.width + ", mGameBoard.height=" + mGameBoard.height + ", sCellWidth=" + SmallTile.sCellWidth);
        
        for (int col = 0; col < 15; col++)
            for (int row = 0; row < 15; row++)
            	mGrid[col][row] = null;
        
        for (SmallTile tile: mBoardTiles) {
            tile.move(
            	mRandom.nextInt(mGameBoard.width - tile.width),
                mRandom.nextInt(mGameBoard.height - tile.height)
            );
            alignToGrid(tile);
            Log.d("shuffleTiles", "tile=" + tile);
        }
        
        if (mBarTiles.size() > 0) {
        	int smallTileWidth = mBarTiles.get(0).width;
	        int padding = (getWidth() - 7 * smallTileWidth) / 8;
	        for (int i = mBarTiles.size() - 1; i >= 0; i--) {
	        	SmallTile tile = mBarTiles.get(i);
	        	tile.move(padding + i * (padding + tile.width), getHeight() - tile.height - padding);
	        }
        }
    }

    private void doubleTap() {
        mScroller.abortAnimation();
        mGameBoard.toggleScale();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // if fling is in progress
        if (mScroller.computeScrollOffset()) {
/*            
            Log.d("onDraw", 
            	"getCurrX()=" + mScroller.getCurrX() + 
            	", getCurrY()=" + mScroller.getCurrY());
*/
            mGameBoard.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidateDelayed(30);
        }

        mGameBoard.draw(canvas, mBoardTiles);
        
        mBar.draw(canvas);
        for (SmallTile tile: mBarTiles) {
            tile.draw(canvas);
        }
        
        mBigTile.draw(canvas);
    }

    public void scroll(float dX, float dY) {
        mScroller.abortAnimation();
        mGameBoard.scrollBy(-dX, -dY);
        invalidate();
    }

    public void fling(float vX, float vY) {
        mScroller.abortAnimation();
        float half = Math.min(mBigTile.width, mBigTile.height) / 2;
        mGameBoard.getValues();
/*      
        Log.d("fling", "vX=" + vX + ", vY=" + vY);
*/        
        mScroller.fling(
            (int) mGameBoard.x,
            (int) mGameBoard.y,
            (int) vX,
            (int) vY,
            (int) mGameBoard.minX,
            (int) mGameBoard.maxX,
            (int) mGameBoard.minY,
            (int) mGameBoard.maxY,
            (int) half,
            (int) half
        );
        invalidate();
    }
    
    // scroll game board if a tile has been dragged to screen edge
    private void draggedToEdge(float screenX, float screenY) {
        mScroller.abortAnimation();
        
        float half = Math.min(mBigTile.width, mBigTile.height) / 2;
        float scrollX = 0;
        float scrollY = 0;
        
    	if (screenX < half) {
    		scrollX = mGameBoard.getScrollLeft();
    	} else if (screenX > getWidth() - half) {
    		scrollX = mGameBoard.getScrollRight();
        }

    	if (screenY < half) {
    		scrollY = mGameBoard.getScrollTop();
    	}
        
		mScroller.startScroll(
			(int) mGameBoard.x, 
			(int) mGameBoard.y, 
			(int) scrollX, 
			(int) scrollY 
		);
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

