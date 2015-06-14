package de.afarber.mybars;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
    private Paint mPaint;

    private final Random mRandom = new Random();

    private float mBoardX;
    private float mBoardY;

    private float mScreenX;
    private float mScreenY;

    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleDetector;
    
    private SmallTile[][] mGrid = new SmallTile[15][15];

    private ColorDrawable mBar = new ColorDrawable(Color.BLUE);

    public MyView(Context context) {
        this(context, null);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);

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
                mGameBoard.scrollBy(-dX, -dY);
                invalidate();
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float vX, float vY) {
            	if (TOO_OLD)
            		return false;
            	
                mGameBoard.fling(vX, vY);
                invalidate();
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                mGameBoard.toggleScale();
                invalidate();
                return true;
            }
        };

        ScaleGestureDetector.SimpleOnScaleGestureListener scaleListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
            	if (TOO_OLD)
            		return false;
            	
                float factor = detector.getScaleFactor();
                Log.d("onScale", "factor=" + factor);
                mGameBoard.scaleBy(factor);
                invalidate();
                return true;
            }
        };

        mGestureDetector = new GestureDetector(context, gestureListener);
        mScaleDetector = new ScaleGestureDetector(context, scaleListener);

        mBar.setAlpha(60);
        
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
		mPaint.setAlpha(0xCC);        
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
        	"getAction=" + e.getAction() +
        	", getX=" + e.getX() +
            ", getY=" + e.getY());
    	*/
    	PointF boardPoint = mGameBoard.screenToBoard(e.getX(), e.getY());

        if (e.getPointerCount() == 1) {
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
		            	
		            	int col = mSmallTile.getColumn(mGameBoard.cellWidth);
		            	int row = mSmallTile.getRow(mGameBoard.cellWidth);
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
		            	mGameBoard.draggedToEdge(e.getX(), e.getY());
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
        
	    Point start = new Point(3 * w / 4, h / 3);
	    Point end = new Point(w / 4, 2 * h / 3);
		LinearGradient gradient = new LinearGradient(
				start.x,
				start.y,
				end.x,
				end.y,
				new int[]{ 0xFFFFCC00, 0xFFFFCC99, 0xFFFFCC00 },
		        null,
		        TileMode.CLAMP);
		mPaint.setShader(gradient);   
        
        invalidate();
    }

    private void placeTiles() {
        for (int col = 0; col < 15; col++)
            for (int row = 0; row < 15; row++)
            	mGrid[col][row] = null;
        
        for (SmallTile tile: mBoardTiles) {
            tile.move(
            	mRandom.nextInt(mGameBoard.width - tile.width),
                mRandom.nextInt(mGameBoard.height - tile.height)
            );
            alignToGrid(tile);
            //Log.d("placeTiles", "tile=" + tile);
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

    @Override
    protected void onDraw(Canvas canvas) {
        // if the game board is still moving
    	if (mGameBoard.isFlinging())
            postInvalidateDelayed(30);
 
        mGameBoard.draw(canvas, mBoardTiles);
        
        mBar.draw(canvas);
        
        for (SmallTile tile: mBarTiles) 
            tile.draw(canvas, mPaint);
        
        mBigTile.draw(canvas);
    }

    private boolean[] buildSides(int col, int row) {
	    return new boolean[] {
		    // NORTH (true means: there is no neighbor tile)
		    (row <= 0 || mGrid[col][row - 1] == null),
	
		    // EAST
		    (col >= 14 || mGrid[col + 1][row] == null),
			
		    // SOUTH
		    (row >= 14 || mGrid[col][row + 1] == null),
		    
		    // WEST
		    (col <= 0 || mGrid[col - 1][row] == null)
	    };
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
        	    	boolean[] sides = buildSides(i, j);
        		    tile.setDrawSides(sides);
        		}
        	}
    	}
    }
    
    private void alignToGrid(SmallTile tile) {
    	int col = tile.getColumn(mGameBoard.cellWidth);
    	int row = tile.getRow(mGameBoard.cellWidth);
    	
    	// find a free cell at the game board
    	while (mGrid[col][row] != null) {
    		col = (col + 1) % 15;

    		if (col == 0)
        		row = (row + 1) % 15;
    	}
    	
    	mGrid[col][row] = tile;
    	updateNeighbors(col, row);
    	
    	tile.left = (col + 1) * mGameBoard.cellWidth;
    	tile.top = (row + 1) * mGameBoard.cellWidth;
    }
}

