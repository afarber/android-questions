package de.afarber.mybars;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.ScrollerCompat;
import android.util.Log;

public class GameBoard {
    private ScrollerCompat mScroller;
	private Drawable mBackground;
    private Matrix matrix = new Matrix();
    private float[] mValues = new float[9];
    
	private float mParentW;
    private float mParentH;
    
    private float mMinZoom;
    private float mMaxZoom;
    
    private Paint mPaint;

	private float x;
	private float y;
	private float scaleX;
	private float scaleY;
	private float maxX;
	private float maxY;
	private float minX;
	private float minY;

	public int width;
	public int height;
    public int cellWidth;
    
    public GameBoard(Context context) {
        mScroller = ScrollerCompat.create(context);
        mBackground = context.getResources().getDrawable(R.drawable.game_board);
    	width = mBackground.getIntrinsicWidth();
    	height = mBackground.getIntrinsicHeight();
    	mBackground.setBounds(0, 0, width, height);
        // there are 15 cells in a row and 1 padding at each side
        cellWidth = width / (1 + 15 + 1);
        
	    Point start = new Point(3 * width / 4, height / 3);
	    Point end = new Point(width / 4, 2 * height / 3);
		LinearGradient gradient = new LinearGradient(
				start.x,
				start.y,
				end.x,
				end.y,
				new int[]{ 0xFFFFCC00, 0xFFFFCC99, 0xFFFFCC00 },
		        null,
		        TileMode.CLAMP);

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
		mPaint.setShader(gradient);   
		mPaint.setAlpha(0xFF); // TODO change to 0xCC
    }
    
	public void setParentSize(float w, float h) {
		mParentW = w;
		mParentH = h;
		
        mMinZoom = Math.min(w / width, h / height);
        mMaxZoom = 2 * mMinZoom;
        
        Log.d("setParentSize", 
        		"w=" + w + 
        		", h=" + h + 
        		", min zoom=" + mMinZoom + 
        		", max zoom=" + mMaxZoom);
	}
    
	public void draw(Canvas canvas, ArrayList<SmallTile> tiles) {
        canvas.save();
        canvas.concat(matrix);
        mBackground.draw(canvas);
        
        for (SmallTile tile: tiles) {
        	if (!tile.visible)
        		continue;
        	
            canvas.drawRect(
            		tile.left, 
            		tile.top, 
            		tile.left + tile.width, 
            		tile.top + tile.height, 
            		mPaint);
            
            tile.draw(canvas);
        }
        canvas.restore();
	}
	
	private void getValues() {
	    matrix.getValues(mValues);
	    
        x = mValues[Matrix.MTRANS_X];
        y = mValues[Matrix.MTRANS_Y];
        
        scaleX = mValues[Matrix.MSCALE_X];
        scaleY = mValues[Matrix.MSCALE_Y];
        
        maxX = 0;
        maxY = 0;
        
        minX = mParentW - scaleX * width;
        minY = mParentH - scaleY * height;
        
        // if scaled game board is smaller than parent
        if (minX >= 0)
        	minX = maxX = minX / 2;
        if (minY >= 0)
        	minY = maxY = 0;        
    }
	
	public PointF screenToBoard(float screenX, float screenY) {
	    float[] point = new float[] {screenX, screenY};
	    Matrix inverse = new Matrix();
	    matrix.invert(inverse);
	    inverse.mapPoints(point);
	    float boardX = point[0];
	    float boardY = point[1];
	    return new PointF(boardX, boardY);
	}
	
    private void scrollTo(float newX, float newY) {
        getValues();
        float dX = newX - x;
        float dY = newY - y;
        matrix.postTranslate(dX, dY);
    }
    
    public void scrollBy(float dX, float dY) {
        mScroller.abortAnimation();
        matrix.postTranslate(dX, dY);
        fixTranslation();
   }

    private void fixTranslation() {
        getValues();

        float dX = 0.0f;
        float dY = 0.0f;
        
        if (minX >= 0)
        	dX = minX / 2 - x;
        else if (x > 0)
        	dX = -x;
        else if (x < minX)
        	dX = minX -x;
        
        if (minY >= 0)
        	dY = minY / 2 - y;
        else if (y > 0)
        	dY = -y;
        else if (y < minY)
        	dY = minY - y;
        
        if (dX != 0.0 || dY != 0.0)
        	matrix.postTranslate(dX, dY);
    }
    
    public void toggleScale() {
        mScroller.abortAnimation();
        getValues();
        float oldScale = Math.min(scaleX, scaleY);
        float newScale = (oldScale > mMinZoom ? mMinZoom : mMaxZoom);
        Log.d("toggleScale", "oldScale=" + oldScale + ", newScale=" + newScale);
        matrix.setScale(newScale, newScale);
        
        // center the game board after scaling it
        getValues();
        float midX = minX / 2;
        float midY = minY / 2;
        scrollTo(midX, midY);
    }
    
    public void scaleBy(float factor) {
    	matrix.postScale(factor, factor);
        fixScaling();
    }
    
    private void fixScaling() {
        getValues();
        float oldScale = Math.min(scaleX, scaleY);
        if (oldScale > mMaxZoom) {
        	float factor = mMaxZoom / oldScale;
            matrix.postScale(factor, factor);
        } else if (oldScale < mMinZoom) {
        	float factor = mMinZoom / oldScale;
            matrix.postScale(factor, factor);
        }
    }
    
    public void fling(float vX, float vY) {
        mScroller.abortAnimation();
        getValues();
/*      
        Log.d("fling", "vX=" + vX + ", vY=" + vY);
*/        
        mScroller.fling(
            (int) x,
            (int) y,
            (int) vX,
            (int) vY,
            (int) minX,
            (int) maxX,
            (int) minY,
            (int) maxY,
            (int) cellWidth,
            (int) cellWidth
        );
    }
 
    public boolean isFlinging() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            return true;
        }
        
        return false;
    }
    
    // scroll game board if a tile has been dragged to screen edge
    public void draggedToEdge(float screenX, float screenY) {
        float scrollX = 0;
        float scrollY = 0;
        
    	if (screenX < 10) {
    		scrollX = getScrollLeft();
    	} else if (screenX > mParentW - 10) {
    		scrollX = getScrollRight();
        }

    	if (screenY < 10) {
    		scrollY = getScrollTop();
    	}
        
    	//Log.d("draggedToEdge", "scrollX=" + scrollX + ", scrollY=" + scrollY);
    	if (Math.abs(scrollX) > 1 || Math.abs(scrollY) > 1)
			mScroller.startScroll(
				(int) x, 
				(int) y, 
				(int) scrollX, 
				(int) scrollY
			);
    }
    
    private float getScrollLeft() {
        getValues();
        
        // the board is zoomed out and centered, no need to scroll
        if (minX >= 0)
        	return 0;
        
        float scale = Math.min(scaleX, scaleY);
        float scroll = scale * cellWidth;
   		if (x + scroll > maxX)
   			scroll = maxX - x;
   		
   		return scroll;
    }
   		
    private float getScrollRight() {
        getValues();
        
        // the board is zoomed out and centered, no need to scroll
        if (minX >= 0)
        	return 0;
        
        float scale = Math.min(scaleX, scaleY);
        float scroll = -scale * cellWidth;
   		if (x + scroll < minX)
   			scroll = minX - x;
   		
   		return scroll;
    }
    
    private float getScrollTop() {
        getValues();
        
        // the board is zoomed out, no need to scroll
        if (minY >= 0)
        	return 0;
        
        float scale = Math.min(scaleX, scaleY);
        float scroll = scale * cellWidth;
   		if (y + scroll > maxY)
   			scroll = maxY - y;
   		
   		return scroll;
    }
    
    
}
