package de.afarber.testabs;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BigTile extends LinearLayout {
	
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
        
        this.setOrientation(VERTICAL);

        mImage = new ImageView(context);
        mImage.setImageResource(R.drawable.big_tile);
        addView(mImage, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, 
                LayoutParams.MATCH_PARENT));

        mLetter = new TextView(context);
        mLetter.setText(letter);
        addView(mLetter, new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, 
                LayoutParams.WRAP_CONTENT));
        
        mValue = new TextView(context);
        mValue.setText(value);
        addView(mValue, new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, 
                LayoutParams.WRAP_CONTENT));
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
