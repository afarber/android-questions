package de.afarber.mybars;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
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
    
	public void draw(Canvas canvas) {
        canvas.save();
        canvas.concat(matrix);
        mBackground.draw(canvas);
        canvas.restore();
	}
	
	public void getValues(int parentW, int parentH) {
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
}
