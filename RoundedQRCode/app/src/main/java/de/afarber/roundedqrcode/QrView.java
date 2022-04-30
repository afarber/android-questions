package de.afarber.roundedqrcode;


import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class QrView extends AppCompatImageView {
    public QrView(Context context) {
        this(context, null, 0);
    }

    public QrView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QrView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            prepareContent(w, h);
        }
    }

    public void setData(String str) {
        prepareContent(getWidth(), getHeight());
        invalidate();
    }

    private void prepareContent(int w, int h) {

    }
}