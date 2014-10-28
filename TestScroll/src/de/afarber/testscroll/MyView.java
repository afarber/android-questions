package de.afarber.testscroll;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.widget.OverScroller;

public class MyView extends View {
    private Drawable         gameBoard;
    //private ArrayList<Drawable> tiles = new ArrayList<Drawable>();

    private int              offsetX = 0;
    private int              offsetY = 0;
    private float			 scale = 1.0f;
    private float			 focusX = 0.0f;
    private float			 focusY = 0.0f;
    
    private OverScroller     scroller;
    private GestureDetector  gestureDetector;
    private ScaleGestureDetector scaleDetector;

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gameBoard = getResources().getDrawable(R.drawable.game_board);

        scroller = new OverScroller(context);

        SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            	//Log.d("onScroll", "distanceX=" + distanceX + ", distanceY=" + distanceY);
                scroll(distanceX, distanceY);
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            	//Log.d("onFling", "velocityX=" + velocityX + ", velocityY=" + velocityY);
                fling(velocityX, velocityY);
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
            	Log.d("onDoubleTap", "e" + e);
            	offsetX = 0;
            	offsetY = 0;
            	scale = 1.0f;
            	invalidate();
                return true;
            }
        };
        
        SimpleOnScaleGestureListener scaleListener = new SimpleOnScaleGestureListener() {
        	@Override
        	public boolean onScale(ScaleGestureDetector detector) {
        		scale *= detector.getScaleFactor();
    			focusX = detector.getFocusX();
    			focusY = detector.getFocusY();
        		
        		Log.d("onScale", "scale=" + scale + ", focusX=" + focusX + ", focusY=" + focusY);
        		
        		invalidate();
        		return true;
        	}
        };
        
        gestureDetector = new GestureDetector(context, gestureListener);
        scaleDetector = new ScaleGestureDetector(context, scaleListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) ||
        		scaleDetector.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // computeScrollOffset() returns true if a fling is in progress
        if (scroller.computeScrollOffset()) {
            offsetX = scroller.getCurrX();
            offsetY = scroller.getCurrY();
            postInvalidateDelayed(50);
        }
        
        canvas.save();
        canvas.translate(offsetX, offsetY);
        canvas.scale(scale, scale, focusX, focusY);
        gameBoard.setBounds(
        	0, 
        	0, 
        	gameBoard.getIntrinsicWidth(),
        	gameBoard.getIntrinsicHeight()
        );
        gameBoard.draw(canvas);  
        canvas.restore();
    }

    // called when the GestureListener detects scroll
    public void scroll(float distanceX, float distanceY) {
        scroller.forceFinished(true);
        offsetX -= (int) distanceX;
        offsetY -= (int) distanceY;
        checkOffset();
        invalidate();
    }

    // called when the GestureListener detects fling
    public void fling(float velocityX, float velocityY) {
        scroller.forceFinished(true);
        scroller.fling(
        	offsetX, 
        	offsetY, 
        	(int) velocityX, 
        	(int) velocityY,  
        	-getMaxOffsetX(),
        	0,
        	-getMaxOffsetY(), 
        	0,
        	50,
        	50
        );
        invalidate();
    }

    private int getMaxOffsetX() {
        return (int) (1 * (gameBoard.getIntrinsicWidth() - getWidth()));
    }

    private int getMaxOffsetY() {
        return (int) (1 * (gameBoard.getIntrinsicHeight() - getHeight()));
    }

    private void checkOffset() {
        if (offsetX > 0) {
            offsetX = 0;
        } else if (offsetX < -getMaxOffsetX()) {
            offsetX = -getMaxOffsetX();
        }
        
        if (offsetY > 0) {
            offsetY = 0;
        } else if (offsetY < -getMaxOffsetY()) {
            offsetY = -getMaxOffsetY();
        }
    }
}
