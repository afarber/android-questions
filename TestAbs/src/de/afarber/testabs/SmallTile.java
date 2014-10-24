package de.afarber.testabs;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class SmallTile extends FrameLayout {
	
	private ImageView mImage;
    private TextView mLetter;
    private TextView mValue;

    public SmallTile(Context context) {
        this(context, null, null, null);
    }

    public SmallTile(Context context, AttributeSet attrs) {
        this(context, attrs, null, null);
    }
    
    public SmallTile(Context context, AttributeSet attrs, String letter, String value) {
        super(context, attrs);
        
        mImage = new ImageView(context);
        mImage.setImageResource(R.drawable.small_tile);
        addView(mImage, new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, 
                LayoutParams.MATCH_PARENT));

        mLetter = new TextView(context);
        mLetter.setTextColor(Color.BLACK);
        //mLetter.setBackgroundColor(Color.CYAN);
        mLetter.setTextSize(28);
        mLetter.setText(letter);
        FrameLayout.LayoutParams letterParams = new FrameLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT, 
            LayoutParams.WRAP_CONTENT,
            Gravity.CENTER
        );
        addView(mLetter, letterParams);
        
        mValue = new TextView(context);
        mValue.setTextColor(Color.BLACK);
        //mValue.setBackgroundColor(Color.CYAN);
        mValue.setTextSize(12);
        mValue.setText(value);
        FrameLayout.LayoutParams valueParams = new FrameLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT, 
            LayoutParams.WRAP_CONTENT,
            Gravity.BOTTOM | Gravity.RIGHT
        );
        valueParams.setMargins(0, 0, 4, 4);
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
    
    @Override
    public String toString() {
		return "SmallTile " + getLetter();
    }
}
