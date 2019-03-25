package de.afarber.pinchzoom;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
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

    private final RectF mBoardSrc = new RectF();
    private final RectF mBoardDst = new RectF();
    private final RectF mMaxBounds = new RectF();
    private final RectF mMinBounds = new RectF();

    private float mMinScale;
    private float mMaxScale;

    private Boxer mBoxer;

    private final Paint mRedPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final Paint mBluePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    public MyView(Context context) {
        this(context, null, 0);
    }

    public MyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mRedPaint.setStyle(Paint.Style.FILL);
        mRedPaint.setARGB(50, 250, 0, 0);

        mBluePaint.setStyle(Paint.Style.FILL);
        mBluePaint.setARGB(40, 0, 0, 250);

        mBoardDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.board, null);
        mBoardWidth = mBoardDrawable.getIntrinsicWidth();
        mBoardHeight = mBoardDrawable.getIntrinsicHeight();
        mBoardDrawable.setBounds(0, 0, (int) mBoardWidth, (int) mBoardHeight);
        mBoardSrc.set(0f, 0f, mBoardWidth, mBoardHeight);

        ScaleGestureDetector.OnScaleGestureListener scaleListener =
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector scaleDetector) {
                float currScale = getBoardScale();
                float minFactor = mMinScale / currScale;
                float maxFactor = mMaxScale / currScale;
                float factor = scaleDetector.getScaleFactor();
                if (factor < minFactor) {
                    factor = minFactor;
                } else if (factor > maxFactor) {
                    factor = maxFactor;
                }
                mBoardMatrix.postScale(factor, factor, scaleDetector.getFocusX(), scaleDetector.getFocusY());

                mBoxer.clamp(mBoardMatrix);
                constraint();

                ViewCompat.postInvalidateOnAnimation(MyView.this);
                return true;
            }
        };

        GestureDetector.OnGestureListener listener =
                new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(final MotionEvent e) {
                mBoardMatrix.getValues(mBoardValues);
                float startScale = getBoardScale();
                float endScale = (startScale < mMaxScale ? mMaxScale : mMinScale);
                ValueAnimator zoomAnimator = ValueAnimator.ofFloat(startScale, endScale).setDuration(1000);
                zoomAnimator.addUpdateListener(animator -> {
                    float nextScale = (float) animator.getAnimatedValue();
                    float factor = nextScale / getBoardScale();
                    mBoardMatrix.postScale(factor, factor, e.getX(), e.getY());

                    mBoxer.clamp(mBoardMatrix);
                    constraint();

                    ViewCompat.postInvalidateOnAnimation(MyView.this);
                });
                zoomAnimator.start();
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float dX, float dY) {
                mBoardMatrix.postTranslate(-dX, -dY);

                mBoxer.clamp(mBoardMatrix);
                constraint();

                ViewCompat.postInvalidateOnAnimation(MyView.this);
                return true;
            }
        };

        mScaleDetector = new ScaleGestureDetector(context, scaleListener);
        mGestureDetector = new GestureDetector(context, listener);
    }

    private float getBoardScale() {
        mBoardMatrix.getValues(mBoardValues);
        return mBoardValues[Matrix.MSCALE_X];
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mMinScale = Math.min(w / mBoardWidth, h / mBoardHeight);
        mMaxScale = 2 * Math.max(w / mBoardWidth, h / mBoardHeight);

        float min = Math.min(w, h);
        float padX = (w - min) / 2f;
        mMinBounds.set(padX, 0, padX + min, min);
        mMaxBounds.set(w - mBoardWidth, h - mBoardHeight, mBoardWidth - w, mBoardHeight - h);

        float scale = mMaxScale / 2f;
        mBoardMatrix.setScale(scale, scale);
        mBoardMatrix.postTranslate((w - scale * mBoardWidth) / 2f, (h - scale * mBoardHeight) / 2f);

        RectF bounds = new RectF();
        RectF src = new RectF(0, 0, 2 * mBoardWidth, 2 * mBoardHeight);
        mBoardMatrix.mapRect(bounds, src);
        mBoxer = new Boxer(bounds, src);
    }

    private void constraint() {
        mBoardMatrix.mapRect(mBoardDst, mBoardSrc);

        Log.d(TAG,"XXX mBoardDst = " + mBoardDst);
        Log.d(TAG,"XXX mMaxBounds = " + mMaxBounds);
        Log.d(TAG,"XXX mMinBounds = " + mMinBounds);


        if (mMaxBounds.contains(mBoardDst)) {
            Log.d(TAG,"XXX mMaxBounds ok");
        }

        if (mBoardDst.contains(mMinBounds)) {
            Log.d(TAG,"XXX mMinBounds ok");
        }

        Log.d(TAG,"XXX XXX XXX XXX XXX");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.concat(mBoardMatrix);
        mBoardDrawable.draw(canvas);
        canvas.restore();

        canvas.drawRect(mMaxBounds, mBluePaint);
        canvas.drawRect(mMinBounds, mRedPaint);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
        mScaleDetector.onTouchEvent(e);
        return true;
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
