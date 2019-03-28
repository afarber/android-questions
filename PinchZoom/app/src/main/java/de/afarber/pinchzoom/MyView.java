package de.afarber.pinchzoom;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.OverScroller;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import static de.afarber.pinchzoom.MainActivity.TAG;

public class MyView extends View {
    private final ScaleGestureDetector mScaleDetector;
    private final GestureDetector mGestureDetector;
    private final OverScroller mScroller;

    private final Matrix mBoardMatrix = new Matrix();
    private final float[] mBoardValues = new float[9];

    private final Board mBoard;

    private float mMinScale;
    private float mMaxScale;

    public MyView(Context context) {
        this(context, null, 0);
    }

    public MyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        ScaleGestureDetector.OnScaleGestureListener scaleListener =
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                mBoard.setZoomFocus(detector.getFocusX(), detector.getFocusY());
                // ensure that the zoom stays between mMinScale and mMaxScale
                float currScale = getBoardScale();
                float minFactor = mMinScale / currScale;
                float maxFactor = mMaxScale / currScale;
                float factor = detector.getScaleFactor();
                if (factor < minFactor) {
                    factor = minFactor;
                } else if (factor > maxFactor) {
                    factor = maxFactor;
                }
                float nextScale = factor * currScale;

                mBoardMatrix.postScale(factor, factor, mBoard.getZoomFocusX(), mBoard.getZoomFocusY());
                mBoard.updateTranslationLimits(nextScale);
                mBoard.fixTranslationAndRedraw();
                return true;
            }
        };

        GestureDetector.OnGestureListener listener =
                new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                mBoard.setZoomFocus(e.getX(), e.getY());

                mBoardMatrix.getValues(mBoardValues);
                float startScale = getBoardScale();
                float endScale = (startScale < mMaxScale ? mMaxScale : mMinScale);

                ValueAnimator zoomAnimator = ValueAnimator.ofFloat(startScale, endScale).setDuration(4000);
                zoomAnimator.addUpdateListener(animator -> {
                    float nextScale = (float) animator.getAnimatedValue();
                    float factor = nextScale / getBoardScale();
                    mBoardMatrix.postScale(factor, factor, mBoard.getZoomFocusX(), mBoard.getZoomFocusY());
                    mBoard.updateTranslationLimits(nextScale);
                    mBoard.fixTranslationAndRedraw();
                });
                zoomAnimator.start();
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy) {
                mBoardMatrix.postTranslate(-dx, -dy);
                mBoard.fixTranslationAndRedraw();
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float vx, float vy) {
                mScroller.forceFinished(true);

                mBoardMatrix.getValues(mBoardValues);
                float scale = mBoardValues[Matrix.MSCALE_X];
                float x = mBoardValues[Matrix.MTRANS_X];
                float y = mBoardValues[Matrix.MTRANS_Y];

                mScroller.fling(
                        (int) x,
                        (int) y,
                        (int) (mBoard.canFlingX() ? vx : 0f),
                        (int) (mBoard.canFlingY() ? vy : 0f),
                        (int) mBoard.getMaxFlingX(), 0,
                        (int) mBoard.getMaxFlingY(), 0,
                        (int) (mBoard.canFlingX() ? scale * mBoard.getBoardWidth() / 16f : 0f),
                        (int) (mBoard.canFlingY() ? scale * mBoard.getBoardHeight() / 16f : 0f)
                );

                ViewCompat.postInvalidateOnAnimation(MyView.this);
                return true;
            }
        };

        mBoard = new Board(this, mBoardMatrix);

        mScaleDetector = new ScaleGestureDetector(context, scaleListener);
        mGestureDetector = new GestureDetector(context, listener);
        mScroller = new OverScroller(context);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
        mScaleDetector.onTouchEvent(e);
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        // if the scroller is not finished yet
        if (mScroller.computeScrollOffset()) {
            mBoardMatrix.getValues(mBoardValues);
            float x = mBoardValues[Matrix.MTRANS_X];
            float y = mBoardValues[Matrix.MTRANS_Y];

            float newX = mScroller.getCurrX();
            float newY = mScroller.getCurrY();

            float dx = newX - x;
            float dy = newY - y;

            mBoardMatrix.postTranslate(dx, dy);
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private float getBoardScale() {
        mBoardMatrix.getValues(mBoardValues);
        return mBoardValues[Matrix.MSCALE_X];
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mMinScale = Math.min(w / mBoard.getBoardWidth(), h / mBoard.getBoardHeight());
        mMaxScale = 2 * Math.max(w / mBoard.getBoardWidth(), h / mBoard.getBoardHeight());

        float scale = mMaxScale / 2f;
        mBoard.updateTranslationLimits(scale);
        mBoardMatrix.setScale(scale, scale);
        mBoardMatrix.postTranslate((getWidth() - scale * mBoard.getBoardWidth()) / 2f, 0f);

        mBoard.onSizeChanged(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mBoard.draw(canvas);
    }

    // the code below is used for screen rotation
    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        mBoardMatrix.getValues(ss.mBoardValues);
        Log.d(TAG, "onSaveInstanceState: " + ss);
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        Log.d(TAG, "onRestoreInstanceState: " + ss);

        mBoardMatrix.setValues(ss.mBoardValues);
    }

    public static class SavedState extends BaseSavedState {
        private float[] mBoardValues = new float[9];

        protected SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloatArray(mBoardValues);
        }

        @NonNull
        @Override
        public String toString() {
            return "MyView.SavedState{" +
                    Integer.toHexString(System.identityHashCode(this)) +
                    " mBoardValues=" + Arrays.toString(mBoardValues) +
                    "}";
        }

        public static final ClassLoaderCreator<SavedState> CREATOR
                = new ClassLoaderCreator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel source, ClassLoader loader) {
                return new SavedState(source);
            }

            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        SavedState(Parcel in) {
            super(in);
            in.readFloatArray(mBoardValues);
        }
    }
}
