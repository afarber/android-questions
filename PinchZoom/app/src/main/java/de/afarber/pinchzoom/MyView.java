package de.afarber.pinchzoom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;

import static de.afarber.pinchzoom.MainActivity.TAG;

public class MyView extends View {
    private final ScaleGestureDetector mScaleDetector;
    private final GestureDetector mGestureDetector;

    private final Drawable mBoard;
    private final float mBoardWidth;
    private final float mBoardHeight;

    private float mBoardScale = 2f;
    // 0f/0f values are max and indicate that the left/top of the scaled board is displayed
    private float mBoardScrollX = 0f;
    private float mBoardScrollY = 0f;

    public MyView(Context context) {
        this(context, null, 0);
    }

    public MyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mBoard = ResourcesCompat.getDrawable(context.getResources(), R.drawable.board, null);
        mBoardWidth = mBoard.getIntrinsicWidth();
        mBoardHeight = mBoard.getIntrinsicHeight();
        mBoard.setBounds(0, 0, (int) mBoardWidth, (int) mBoardHeight);

        mScaleDetector = new ScaleGestureDetector(context,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector scaleDetector) {
                float focusX = getWidth() / 2f; //scaleDetector.getFocusX();
                float focusY = getHeight() / 2f; //scaleDetector.getFocusY();
                float factor = scaleDetector.getScaleFactor();

                mBoardScrollX = mBoardScrollX + focusX * (1 - factor) * mBoardScale;
                mBoardScrollY = mBoardScrollY + focusY * (1 - factor) * mBoardScale;
                mBoardScale *= factor;

                Log.d(TAG, "mBoardScale=" + mBoardScale + ", mBoardScrollX=" + mBoardScrollX + ", mBoardScrollY=" + mBoardScrollY);

                ViewCompat.postInvalidateOnAnimation(MyView.this);
                return true;
            }
        });

        mGestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float dX, float dY) {
                mBoardScrollX -= dX;
                mBoardScrollY -= dY;
                ViewCompat.postInvalidateOnAnimation(MyView.this);
                return true;
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //mBoardScale = Math.min(w / mBoardWidth, h / mBoardHeight);
        mBoardScale = Math.max(w / mBoardWidth, h / mBoardHeight);

        float minScrollX = w - mBoardScale * mBoardWidth;
        float minScrollY = h - mBoardScale * mBoardHeight;

        // center-align the board
        mBoardScrollX = minScrollX / 2;
        mBoardScrollY = minScrollY / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.scale(mBoardScale, mBoardScale);
        canvas.translate(mBoardScrollX / mBoardScale, mBoardScrollY / mBoardScale);
        mBoard.draw(canvas);
        canvas.restore();
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent e) {
        return mGestureDetector.onTouchEvent(e) ||
                mScaleDetector.onTouchEvent(e) ||
                super.onTouchEvent(e);
    }









    // the code below is used for screen rotation

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.mBoardScale = mBoardScale;
        ss.mBoardScrollX = mBoardScrollX;
        ss.mBoardScrollY = mBoardScrollY;
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

        mBoardScale = ss.mBoardScale;
        mBoardScrollX = ss.mBoardScrollX;
        mBoardScrollY = ss.mBoardScrollY;
    }

    public static class SavedState extends BaseSavedState {
        private float mBoardScale;
        private float mBoardScrollX;
        private float mBoardScrollY;

        protected SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(mBoardScale);
            out.writeFloat(mBoardScrollX);
            out.writeFloat(mBoardScrollY);
        }

        @NonNull
        @Override
        public String toString() {
            return "MyView.SavedState{" +
                    Integer.toHexString(System.identityHashCode(this)) +
                    " mBoardScale=" + mBoardScale +
                    " mBoardScrollX=" + mBoardScrollX +
                    " mBoardScrollY=" + mBoardScrollY +
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
            mBoardScale = in.readFloat();
            mBoardScrollX = in.readFloat();
            mBoardScrollY = in.readFloat();
        }
    }
}
