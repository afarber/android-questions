package de.afarber.mybars;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class GameBoard {
	private Drawable mBackground;
    private Matrix matrix = new Matrix();
    
	private float mParentW;
    private float mParentH;
    
    private float mMinZoom;
    private float mMaxZoom;

	public int width;
	public int height;
    public float x;
    public float y;
    public float scaleX;
    public float scaleY;
    public float maxX;
    public float maxY;
    public float minX;
    public float minY;
    public float cellWidth;

    public GameBoard(Context context) {
    	mBackground = context.getResources().getDrawable(R.drawable.game_board);
    	width = mBackground.getIntrinsicWidth();
    	height = mBackground.getIntrinsicHeight();
    	mBackground.setBounds(0, 0, width, height);
        // there are 15 cells in a row and 1 padding at each side
        cellWidth = width / (1 + 15 + 1);
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
            tile.draw(canvas);
        }
        canvas.restore();
	}
	
	public void getValues() {
	    float[] values = new float[9];
	    matrix.getValues(values);
	    
        x = values[Matrix.MTRANS_X];
        y = values[Matrix.MTRANS_Y];
        
        scaleX = values[Matrix.MSCALE_X];
        scaleY = values[Matrix.MSCALE_Y];
        
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
	
    public void scrollTo(float newX, float newY) {
        getValues();
        float dX = newX - x;
        float dY = newY - y;
        matrix.postTranslate(dX, dY);
    }
    
    public void scrollBy(float dX, float dY) {
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
    
    public float getScrollLeft() {
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
   		
    public float getScrollRight() {
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
    
    public float getScrollTop() {
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
