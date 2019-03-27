package de.afarber.pinchzoom;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;

import static de.afarber.pinchzoom.MainActivity.TAG;

public class MyView extends View {
    private final ScaleGestureDetector mScaleDetector;
    private final GestureDetector mGestureDetector;

    private final Drawable mBoardDrawable;
    private final float mBoardWidth;
    private final float mBoardHeight;

    private final Matrix mBoardMatrix = new Matrix();
    private final float[] mBoardValues = new float[9];

    private final PointF mZoomFocus = new PointF();

    private float mMinScale;
    private float mMaxScale;

    private float mMinTransX;
    private float mMinTransY;

    private final float mMaxTransX = 0f;
    private final float mMaxTransY = 0f;

    public MyView(Context context) {
        this(context, null, 0);
    }

    public MyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mBoardDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.board, null);
        mBoardWidth = mBoardDrawable.getIntrinsicWidth();
        mBoardHeight = mBoardDrawable.getIntrinsicHeight();
        mBoardDrawable.setBounds(0, 0, (int) mBoardWidth, (int) mBoardHeight);

        ScaleGestureDetector.OnScaleGestureListener scaleListener =
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                mZoomFocus.set(detector.getFocusX(), detector.getFocusY());
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

                mBoardMatrix.postScale(factor, factor, mZoomFocus.x, mZoomFocus.y);
                updateTranslationLimits(nextScale);
                fixTranslationAndRedraw();
                return true;
            }
        };

        GestureDetector.OnGestureListener listener =
                new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                mZoomFocus.set(e.getX(), e.getY());

                mBoardMatrix.getValues(mBoardValues);
                float startScale = getBoardScale();
                float endScale = (startScale < mMaxScale ? mMaxScale : mMinScale);

                ValueAnimator zoomAnimator = ValueAnimator.ofFloat(startScale, endScale).setDuration(4000);
                zoomAnimator.addUpdateListener(animator -> {
                    float nextScale = (float) animator.getAnimatedValue();
                    float factor = nextScale / getBoardScale();
                    mBoardMatrix.postScale(factor, factor, mZoomFocus.x, mZoomFocus.y);
                    updateTranslationLimits(nextScale);
                    fixTranslationAndRedraw();
                });
                zoomAnimator.start();
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float dX, float dY) {
                mBoardMatrix.postTranslate(-dX, -dY);
                fixTranslationAndRedraw();
                return true;
            }
        };

        mScaleDetector = new ScaleGestureDetector(context, scaleListener);
        mGestureDetector = new GestureDetector(context, listener);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
        mScaleDetector.onTouchEvent(e);
        return true;
    }

    private float getBoardScale() {
        mBoardMatrix.getValues(mBoardValues);
        return mBoardValues[Matrix.MSCALE_X];
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mMinScale = Math.min(w / mBoardWidth, h / mBoardHeight);
        mMaxScale = 2 * Math.max(w / mBoardWidth, h / mBoardHeight);

        float scale = mMaxScale / 2f;
        updateTranslationLimits(scale);
        mBoardMatrix.setScale(scale, scale);
        mBoardMatrix.postTranslate(mMinTransX / 2f, 0f);
    }

    // update scroll limits when the view dimensions or the zoom change
    private void updateTranslationLimits(float scale) {
        mMinTransX = getWidth() - scale * mBoardWidth;
        mMinTransY = getHeight() - scale * mBoardHeight;
    }

    private void fixTranslationAndRedraw() {
        // get the current translation x and y, which could be off limits
        mBoardMatrix.getValues(mBoardValues);
        float x = mBoardValues[Matrix.MTRANS_X];
        float y = mBoardValues[Matrix.MTRANS_Y];

        float newX = Float.NaN;
        float newY = Float.NaN;

        if (mMinTransX >= 0f) {
            // the width of the scaled drawable is less than the view width -
            // so put the drawable in the horizontal middle of the view
            newX = mMinTransX / 2f;
        } else if (x < mMinTransX) {
            // the drawable is too much to the left (shows white background on the right)
            newX = mMinTransX;
        } else if (x > mMaxTransX) {
            // the drawable is too much to the right (shows white background on the left)
            newX = mMaxTransX;
        }

        if (mMinTransY >= 0f) {
            // the height of the scaled drawable is less than the view height -
            // so put the drawable at the top of the parent view
            newY = 0f;
        } else if (y < mMinTransY) {
            // the drawable is placed too high (shows white background on the bottom)
            newY = mMinTransY;
        } else if (y > mMaxTransY) {
            // the drawable is placed too low (shows white background on the top)
            newY = mMaxTransY;
        }

        // NaN means that no translation adjustment is needed
        float dx = Float.isNaN(newX) ? 0 : newX - x;
        float dy = Float.isNaN(newY) ? 0 : newY - y;

        mZoomFocus.offset(dx, dy);
        mBoardMatrix.postTranslate(dx, dy);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.concat(mBoardMatrix);
        mBoardDrawable.draw(canvas);
        canvas.restore();
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
