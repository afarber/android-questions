package de.afarber.mybars;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;

public class GameBoard {
	private Drawable mBackground;

	public int width;
	public int height;
	
    public Matrix matrix = new Matrix();
    
    public float x;
    public float y;
    public float scaleX;
    public float scaleY;
    public float maxX;
    public float maxY;
    public float minX;
    public float minY;	    

    public GameBoard(Context context) {
    	mBackground = context.getResources().getDrawable(R.drawable.game_board);
    	width = mBackground.getIntrinsicWidth();
    	height = mBackground.getIntrinsicHeight();
    	mBackground.setBounds(0, 0, width, height);
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
	
	public void getValues(float parentW, float parentH) {
	    float[] values = new float[9];
	    matrix.getValues(values);
	    
        x = values[Matrix.MTRANS_X];
        y = values[Matrix.MTRANS_Y];
        
        scaleX = values[Matrix.MSCALE_X];
        scaleY = values[Matrix.MSCALE_Y];
        
        maxX = 0;
        maxY = 0;
        
        minX = parentW - scaleX * width;
        minY = parentH - scaleY * height;	
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
	
    public void scrollTo(float newX, float newY, float parentW, float parentH) {
        getValues(parentW, parentH);
        float dX = newX - x;
        float dY = newY - y;
        matrix.postTranslate(dX, dY);
    }
    
    public void scrollBy(float dX, float dY, float parentW, float parentH) {
        matrix.postTranslate(dX, dY);
        fixTranslation(parentW, parentH);
   }

    private void fixTranslation(float parentW, float parentH) {
        getValues(parentW, parentH);

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
}
