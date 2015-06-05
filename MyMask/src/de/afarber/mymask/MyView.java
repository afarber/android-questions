package de.afarber.mymask;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class MyView extends View {
    private Drawable mGameBoard = getResources().getDrawable(R.drawable.game_board);
    private Bitmap mMask;
    private Canvas mMaskCanvas;
    private Paint mMaskPaint;
    
    private Random mRandom = new Random();
    private int mWidth;
    private int mHeight;
    private int mCellWidth;

    public MyView(Context context) {
        this(context, null);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mWidth = mGameBoard.getIntrinsicWidth();
        mHeight = mGameBoard.getIntrinsicHeight();
        mGameBoard.setBounds(0, 0, mWidth, mHeight);
        
        mMask = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        //mMask = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ALPHA_8);
        mMask.setHasAlpha(true);
        mMask.eraseColor(Color.TRANSPARENT);
        mMaskCanvas = new Canvas(mMask);
		mMaskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		//mMaskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));		
		mMaskPaint.setColor(Color.argb(250, 0, 250, 0));		        		

        // there are 15 cells in a row and 1 padding at each side
        mCellWidth = Math.round(mWidth / 17.0f);
		mMaskCanvas.drawRect(mCellWidth, mCellWidth, mCellWidth, mCellWidth, mMaskPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mGameBoard.draw(canvas);
        
        canvas.drawBitmap(mMask, 0, 0, mMaskPaint);
    }
}

