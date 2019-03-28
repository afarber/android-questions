package de.afarber.pinchzoom;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.OverScroller;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;

public class Board {
    private final View mParentView;

    private final OverScroller mScroller;

    private final Matrix mMatrix = new Matrix();
    private final float[] mValues = new float[9];
    private final PointF mZoomFocus = new PointF();

    private final Drawable mDrawable;
    private final float mWidth;
    private final float mHeight;

    private float mMinScale;
    private float mMaxScale;

    private float mMinX;
    private float mMinY;

    private final float mMaxX = 0f;
    private final float mMaxY = 0f;

    public Board(View v) {
        mParentView = v;

        mScroller = new OverScroller(v.getContext());

        mDrawable = ResourcesCompat.getDrawable(v.getContext().getResources(), R.drawable.board, null);
        mWidth = mDrawable.getIntrinsicWidth();
        mHeight = mDrawable.getIntrinsicHeight();
        mDrawable.setBounds(0, 0, (int) mWidth, (int) mHeight);
    }

    // update scroll limits when the view dimensions or the zoom change
    public void updateTranslationLimits(float scale) {
        mMinX = mParentView.getWidth() - scale * mWidth;
        mMinY = mParentView.getHeight() - scale * mHeight;
    }

    public void fixTranslationAndRedraw() {
        // get the current translation x and y, which could be off limits
        getValues(mValues);
        float x = mValues[Matrix.MTRANS_X];
        float y = mValues[Matrix.MTRANS_Y];

        float newX = Float.NaN;
        float newY = Float.NaN;

        if (mMinX >= 0f) {
            // the width of the scaled drawable is less than the view width -
            // so put the drawable in the horizontal middle of the view
            newX = mMinX / 2f;
        } else if (x < mMinX) {
            // the drawable is too much to the left (shows white background on the right)
            newX = mMinX;
        } else if (x > mMaxX) {
            // the drawable is too much to the right (shows white background on the left)
            newX = mMaxX;
        }

        if (mMinY >= 0f) {
            // the height of the scaled drawable is less than the view height -
            // so put the drawable at the top of the parent view
            newY = 0f;
        } else if (y < mMinY) {
            // the drawable is placed too high (shows white background on the bottom)
            newY = mMinY;
        } else if (y > mMaxY) {
            // the drawable is placed too low (shows white background on the top)
            newY = mMaxY;
        }

        // NaN means that no translation adjustment is needed
        float dx = Float.isNaN(newX) ? 0 : newX - x;
        float dy = Float.isNaN(newY) ? 0 : newY - y;

        mZoomFocus.offset(dx, dy);
        mMatrix.postTranslate(dx, dy);
        ViewCompat.postInvalidateOnAnimation(mParentView);
    }

    private float getBoardScale() {
        getValues(mValues);
        return mValues[Matrix.MSCALE_X];
    }

    public void onSizeChanged(int w, int h) {
        mMinScale = Math.min(w / mWidth, h / mHeight);
        mMaxScale = 2 * Math.max(w / mWidth, h / mHeight);

        float scale = mMaxScale / 2f;
        updateTranslationLimits(scale);
        mMatrix.setScale(scale, scale);
        mMatrix.postTranslate(mMinX / 2f, 0f);
    }

    public boolean onScale(float factor, float focusX, float focusY) {
        mZoomFocus.set(focusX, focusY);
        // ensure that the zoom stays between mMinScale and mMaxScale
        float currScale = getBoardScale();
        float minFactor = mMinScale / currScale;
        float maxFactor = mMaxScale / currScale;
        if (factor < minFactor) {
            factor = minFactor;
        } else if (factor > maxFactor) {
            factor = maxFactor;
        }
        float nextScale = factor * currScale;

        mMatrix.postScale(factor, factor, focusX, focusY);
        updateTranslationLimits(nextScale);
        fixTranslationAndRedraw();
        return true;
    }

    public boolean onDoubleTap(float focusX, float focusY) {
        mZoomFocus.set(focusX, focusY);

        getValues(mValues);
        float startScale = getBoardScale();
        float endScale = (startScale < mMaxScale ? mMaxScale : mMinScale);

        ValueAnimator zoomAnimator = ValueAnimator.ofFloat(startScale, endScale).setDuration(4000);
        zoomAnimator.addUpdateListener(animator -> {
            float nextScale = (float) animator.getAnimatedValue();
            float factor = nextScale / getBoardScale();
            mMatrix.postScale(factor, factor, mZoomFocus.x, mZoomFocus.y);
            updateTranslationLimits(nextScale);
            fixTranslationAndRedraw();
        });
        zoomAnimator.start();
        return true;
    }

    public boolean onScroll(float dx, float dy) {
        mMatrix.postTranslate(-dx, -dy);
        fixTranslationAndRedraw();
        return true;
    }

    public boolean onFling(float vx, float vy) {
        mScroller.forceFinished(true);

        getValues(mValues);
        float scale = mValues[Matrix.MSCALE_X];
        float x = mValues[Matrix.MTRANS_X];
        float y = mValues[Matrix.MTRANS_Y];

        // for flinging the scaled drawable must be smaller than the view
        boolean canFlingX = (mMinX < 0f);
        boolean canFlingY = (mMinY < 0f);

        mScroller.fling(
                (int) x,
                (int) y,
                (int) (canFlingX ? vx : 0f),
                (int) (canFlingY ? vy : 0f),
                (int) (canFlingX ? mMinX : 0f), 0,
                (int) (canFlingY ? mMinY : 0f), 0,
                (int) (canFlingX ? scale * mWidth / 16f : 0f),
                (int) (canFlingY ? scale * mHeight / 16f : 0f)
        );

        ViewCompat.postInvalidateOnAnimation(mParentView);
        return true;
    }

    public void computeScroll() {
        // if the scroller is not finished yet
        if (mScroller.computeScrollOffset()) {
            mMatrix.getValues(mValues);
            float x = mValues[Matrix.MTRANS_X];
            float y = mValues[Matrix.MTRANS_Y];

            float newX = mScroller.getCurrX();
            float newY = mScroller.getCurrY();

            float dx = newX - x;
            float dy = newY - y;

            mMatrix.postTranslate(dx, dy);
            ViewCompat.postInvalidateOnAnimation(mParentView);
        }
    }


    public void draw(Canvas canvas) {
        canvas.save();
        canvas.concat(mMatrix);
        mDrawable.draw(canvas);
        canvas.restore();
    }

    public void getValues(float[] values) {
        mMatrix.getValues(values);
    }

    public void setValues(float[] values) {
        mMatrix.setValues(values);
    }
}
