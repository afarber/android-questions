package de.afarber.testscroll3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.widget.OverScroller;

public class MyView extends View {
    private Drawable gameBoard = getResources().getDrawable(R.drawable.game_board);
	private ScaleGestureDetector scaleGestureDetector;
	private GestureDetector gestureDetector;
	private OverScroller scroller;
	private float scale = 1;

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
		MyGestureListener listener = new MyGestureListener();
		scaleGestureDetector = new ScaleGestureDetector(context, listener);
		gestureDetector = new GestureDetector(context, listener);
		scroller = new OverScroller(context);
	}

    @Override
    protected void onSizeChanged (int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
    
        gameBoard.setBounds(0, 0, gameBoard.getIntrinsicWidth(), gameBoard.getIntrinsicHeight());
    }
    
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.save();
		canvas.scale(scale, scale);
		//Do your painting here
		gameBoard.draw(canvas);
		canvas.restore();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event)
				|| scaleGestureDetector.onTouchEvent(event);
	}

	@Override
	public void computeScroll() {
		if (!scroller.isFinished()) {
			if (scroller.computeScrollOffset()) {
				int oldX = getScrollX();
				int oldY = getScrollY();
				int x = scroller.getCurrX();
				int y = scroller.getCurrY();
				if (oldX != x || oldY != y) {
					scrollTo(x, y);
				}
				// Keep on drawing until the animation has finished.
				invalidate();
			}
		}
	}

	private void scale(float scale, int fx, int fy) {
		this.scale *= scale;
		int scX = (int) ((scale - 1) * fx);
		int scY = (int) ((scale - 1) * fy);
		scrollBy(scX, scY);
	}

	@Override
	public void scrollTo(int x, int y) {
		if (x < 0) {
			x = 0;
		} else if (x > getMaxOffsetX()) {
			x = getMaxOffsetX();
		}

		if (y < 0) {
			y = 0;
		} else if (y > getMaxOffsetY()) {
			y = getMaxOffsetY();
		}
		super.scrollTo(x, y);
	}

	private int getMaxOffsetX() {
    	return (int) (getWidth() - scale * gameBoard.getIntrinsicWidth());
	}

	private int getMaxOffsetY() {
    	return (int) (getHeight() - scale * gameBoard.getIntrinsicWidth());
	}

	private class MyGestureListener extends SimpleOnGestureListener implements OnScaleGestureListener {
		@Override
		public boolean onDown(MotionEvent e) {
			scroller.forceFinished(true);
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			scrollBy((int) distanceX, (int) distanceY);
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			scroller.forceFinished(true);
			scroller.fling(getScrollX(), getScrollY(), (int) -velocityX,
					(int) -velocityY, 0, getMaxOffsetX(), 0, getMaxOffsetY(), 10, 10);
			invalidate();
			return true;
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			scale(detector.getScaleFactor(), (int) detector.getFocusX()
					+ getScrollX(), (int) detector.getFocusY() + getScrollY());
			return true;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {

		}
	}
}