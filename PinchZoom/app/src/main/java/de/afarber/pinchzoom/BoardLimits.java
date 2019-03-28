package de.afarber.pinchzoom;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.View;

import androidx.core.view.ViewCompat;

public class BoardLimits {
    private final View mParentView;
    private final Matrix mBoardMatrix;

    private final float[] mBoardValues = new float[9];
    private final PointF mZoomFocus = new PointF();

    private final float mBoardWidth;
    private final float mBoardHeight;

    private float mMinTransX;
    private float mMinTransY;

    private final float mMaxTransX = 0f;
    private final float mMaxTransY = 0f;

    protected BoardLimits(View v, Matrix m, float w, float h) {
        mParentView = v;
        mBoardMatrix = m;
        mBoardWidth = w;
        mBoardHeight = h;
    }

    protected void setZoomFocus(float x, float y) {
        mZoomFocus.set(x, y);
    }

    protected float getZoomFocusX() {
        return mZoomFocus.x;
    }

    protected float getZoomFocusY() {
        return mZoomFocus.y;
    }

    // for flinging the scaled drawable must be smaller than the view
    protected boolean canFlingX() {
        return (mMinTransX < 0f);
    }

    protected boolean canFlingY() {
        return (mMinTransY < 0f);
    }

    protected float getMaxFlingX() {
        return (canFlingX() ? mMinTransX : 0f);
    }

    protected float getMaxFlingY() {
        return (canFlingY() ? mMinTransY : 0f);
    }

    // update scroll limits when the view dimensions or the zoom change
    protected void updateTranslationLimits(float scale) {
        mMinTransX = mParentView.getWidth() - scale * mBoardWidth;
        mMinTransY = mParentView.getHeight() - scale * mBoardHeight;
    }

    protected void fixTranslationAndRedraw() {
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
        ViewCompat.postInvalidateOnAnimation(mParentView);
    }
}
