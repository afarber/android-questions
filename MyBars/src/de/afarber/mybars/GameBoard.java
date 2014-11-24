package de.afarber.mybars;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;

public class GameBoard {
	private Drawable mBackground;

	public int width;
	public int height;
	
    private Matrix mMatrix = new Matrix();
    
    private float mX;
    private float mY;
    private float mScaleX;
    private float mScaleY;
    private float mMaxX;
    private float mMaxY;
    private float mMinX;
    private float mMinY;	    

    public GameBoard(Context context) {
    	mBackground = context.getResources().getDrawable(R.drawable.game_board);
    	width = mBackground.getIntrinsicWidth();
    	height = mBackground.getIntrinsicHeight();
    	mBackground.setBounds(0, 0, width, height);
    }
    
	public void draw(Canvas canvas, Matrix matrix) {
        canvas.save();
        canvas.concat(matrix);
        mBackground.draw(canvas);
        canvas.restore();
	}
	
	public void getValues(int parentW, int parentH) {
	    float[] values = new float[9];
	    mMatrix.getValues(values);
	    
        mX = values[Matrix.MTRANS_X];
        mY = values[Matrix.MTRANS_Y];
        
        mScaleX = values[Matrix.MSCALE_X];
        mScaleY = values[Matrix.MSCALE_Y];
        
        mMaxX = 0;
        mMaxY = 0;
        
        mMinX = parentW - mScaleX * width;
        mMinY = parentH - mScaleY * height;	
    }
}
