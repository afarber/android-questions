package de.afarber.pinchzoom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
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
    private Matrix mBoardMatrix;
    //private float[] mBoardValues = new float[9];

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

        mBoardMatrix = new Matrix();

        mScaleDetector = new ScaleGestureDetector(context, scaleListener);
        mGestureDetector = new GestureDetector(context, listener);
    }

    ScaleGestureDetector.OnScaleGestureListener scaleListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector scaleDetector) {
            float factor = scaleDetector.getScaleFactor();
            mBoardMatrix.postScale(factor, factor, scaleDetector.getFocusX(), scaleDetector.getFocusY());
            ViewCompat.postInvalidateOnAnimation(MyView.this);
            return true;
        }
    };

    GestureDetector.OnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float dX, float dY) {
            mBoardMatrix.postTranslate(-dX, -dY);
            ViewCompat.postInvalidateOnAnimation(MyView.this);
            return true;
        }
    };

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        float scale = Math.max(w / mBoardWidth, h / mBoardHeight);
        mBoardMatrix.setScale(scale, scale);
        mBoardMatrix.postTranslate((w - scale * mBoardWidth) / 2f, (h - scale * mBoardHeight) / 2f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.concat(mBoardMatrix);
        mBoardDrawable.draw(canvas);
        canvas.restore();
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
