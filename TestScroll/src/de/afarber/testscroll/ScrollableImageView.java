package de.afarber.testscroll;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.OverScroller;


public class ScrollableImageView extends ImageView {

	private GestureDetectorCompat gestureDetector;
	private OverScroller overScroller;

	private final int screenW;
	private final int screenH;

	private int positionX = 0;
	private int positionY = 0;

	public ScrollableImageView(Context context) {
        this(context, null, 0);
	}

    public ScrollableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollableImageView(Context context, AttributeSet attrs, int defStyle) {		
    	super(context, attrs, defStyle);

		// We will need screen dimensions to make sure
		// we don't overscroll the image
		DisplayMetrics dm = getResources().getDisplayMetrics();
		screenW = dm.widthPixels;
		screenH = dm.heightPixels;
		Log.d(MainActivity.DEBUG_TAG, "screenW=" + screenW + ", screenH=" + screenH);


		gestureDetector = new GestureDetectorCompat(context, gestureListener);
		overScroller = new OverScroller(context);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		return true;
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		// computeScrollOffset() returns true only when the scrolling isn't already finished
		if (overScroller.computeScrollOffset()) {
			positionX = overScroller.getCurrX();
			positionY = overScroller.getCurrY();
			Log.d(MainActivity.DEBUG_TAG, "scrollTo: positionX=" + positionX + ", positionY=" + positionY);
			scrollTo(positionX, positionY);
		} else {
			// when scrolling is over, we will want to "spring back" if the image is overscrolled
			overScroller.springBack(positionX, positionY, 0,
					getMaxHorizontal(), 0, getMaxVertical());
		}
	}

	private int getMaxHorizontal() {
		Log.d(MainActivity.DEBUG_TAG, "getMaxHorizontal=" + (getDrawable().getBounds().width() - screenW));
		Log.d(MainActivity.DEBUG_TAG, "getMeasuredWidth=" + getMeasuredWidth());
		Log.d(MainActivity.DEBUG_TAG, "getWidth=" + getWidth());
		
		return (getDrawable().getBounds().width() - screenW);
	}

	private int getMaxVertical() {
		Log.d(MainActivity.DEBUG_TAG, "getMaxVertical=" + (getDrawable().getBounds().height() - screenH));
		Log.d(MainActivity.DEBUG_TAG, "getMeasuredHeight=" + getMeasuredHeight());
		Log.d(MainActivity.DEBUG_TAG, "getHeight=" + getHeight());
	
		return (getDrawable().getBounds().height() - screenH);
	}

	private SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {

		@Override
		public boolean onDown(MotionEvent e) {
			overScroller.forceFinished(true);
			postInvalidate();
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			/*
			Log.d(MainActivity.DEBUG_TAG, "onFling: velocityX=" + velocityX + ", velocityY=" + velocityY);
			
			overScroller.forceFinished(true);
			overScroller.fling(positionX, positionY, (int) -velocityX,
					(int) -velocityY, 0, getMaxHorizontal(), 0,
					getMaxVertical());
			ViewCompat.postInvalidateOnAnimation(ScrollableImageView.this);
			*/
			
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			
			overScroller.forceFinished(true);
			// normalize scrolling distances to not overscroll the image
			int dx = (int) distanceX;
			int dy = (int) distanceY;
			
			int newPositionX = positionX + dx;
			int newPositionY = positionY + dy;
			
			
			if (newPositionX < 0) {
				dx -= newPositionX;
			} else if (newPositionX > getMaxHorizontal()) {
				dx -= (newPositionX - getMaxHorizontal());
			}
			
			if (newPositionY < 0) {
				dy -= newPositionY;
			} else if (newPositionY > getMaxVertical()) {
				dy -= (newPositionY - getMaxVertical());
			}
			
			
			Log.d(MainActivity.DEBUG_TAG, "onScroll: distanceX=" + distanceX + 
					", dx=" + dx + 
					", distanceY=" + distanceY +
					", dy=" + dy);
			
			overScroller.startScroll(positionX, positionY, dx, dy, 0);
			postInvalidate();
			
			return true;
		}
	};

}
