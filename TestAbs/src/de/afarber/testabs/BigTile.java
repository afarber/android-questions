package de.afarber.testabs;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class BigTile extends FrameLayout {
	
	private ImageView mImage;
    private TextView mLetter;
    private TextView mValue;

    public BigTile(Context context) {
        this(context, null, null, null);
    }

    public BigTile(Context context, AttributeSet attrs) {
        this(context, attrs, null, null);
    }
    
    public BigTile(Context context, AttributeSet attrs, String letter, String value) {
        super(context, attrs);
        
        mImage = new ImageView(context);
        mImage.setImageResource(R.drawable.big_tile);
        addView(mImage, new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, 
                LayoutParams.MATCH_PARENT));

        mLetter = new TextView(context);
        mLetter.setTextColor(Color.BLACK);
        //mLetter.setBackgroundColor(Color.CYAN);
        mLetter.setTextSize(60);
        mLetter.setText(letter);
        FrameLayout.LayoutParams letterParams = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, 
                LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        letterParams.setMargins(0, 0, 4, 8);
        addView(mLetter, letterParams);
        
        mValue = new TextView(context);
        mValue.setTextColor(Color.BLACK);
        //mValue.setBackgroundColor(Color.CYAN);
        mValue.setTextSize(18);
        mValue.setText(value);
        FrameLayout.LayoutParams valueParams = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, 
                LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM | Gravity.RIGHT);
        valueParams.setMargins(0, 0, 30, 36);
        addView(mValue, valueParams);
    }

	public String getLetter() {
		return mLetter.getText().toString();
	}

	public void setLetter(String letter) {
        mLetter.setText(letter);
	}

	public String getValue() {
		return mValue.getText().toString();
	}

	public void setValue(String value) {
		mValue.setText(value);
	}
    
    
}
