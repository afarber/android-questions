package de.afarber.pinchzoom;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;

import static de.afarber.pinchzoom.MainActivity.TAG;

public class Boxer {
    private final RectF mBounds;
    private final RectF mSrc;
    private final RectF mDst = new RectF();
    private final RectF mTmp = new RectF();

    protected Boxer(RectF bounds, RectF src) {
        mBounds = bounds;
        mSrc = src;
    }

    protected void clamp(Matrix matrix) {
        matrix.mapRect(mDst, mSrc);
        if (mBounds.contains(mDst)) {
            Log.d(TAG, "clamp OK: " + mDst + " is inside " + mBounds);
            return;
        }
        /*
        if (mDst.width() > mBounds.width() || mDst.height() > mBounds.height()) {
            Log.d(TAG, "clamp BIGGER: " + mDst + " is bigger than " + mBounds);
            // TODO how to do it smarter?
            mTmp.set(mBounds);
            mTmp.intersect(mDst);
            matrix.setRectToRect(mSrc, mTmp, Matrix.ScaleToFit.CENTER);
            return;
        }
        if (mDst.left < mBounds.left) {
            matrix.postTranslate(mBounds.left - mDst.left, 0);
        }
        if (mDst.top < mBounds.top) {
            matrix.postTranslate(0, mBounds.top - mDst.top);
        }
        if (mDst.right > mBounds.right) {
            matrix.postTranslate(mBounds.right - mDst.right, 0);
        }
        if (mDst.bottom > mBounds.bottom) {
            matrix.postTranslate(0, mBounds.bottom - mDst.bottom);
        }
        */
    }
}
