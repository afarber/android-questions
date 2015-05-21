package de.afarber.myprefs;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

public class NumberPickerPreference extends DialogPreference implements OnValueChangeListener {
	private NumberPicker mPicker;
	private Integer mNumber = 0;
	private Integer mPickerValue = 0;
	
	public NumberPickerPreference(Context context) {
		this(context, null, 0);
	}

	public NumberPickerPreference(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public NumberPickerPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setDialogLayoutResource(R.layout.preference_picker);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
	}
	
	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
		mPicker = (NumberPicker) view.findViewById(R.id.picker);
		mPicker.setValue(mNumber);
		mPicker.setMinValue(1);
		mPicker.setMaxValue(100);
		mPicker.setOnValueChangedListener(this);
	}

	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		mPickerValue = newVal;
    }
	
	@Override
	protected void onDialogClosed(boolean positiveResult) {
	    if (positiveResult) {
	        setValue(mPickerValue);
	    }
	}	
	
	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		setValue(restoreValue ? getPersistedInt(mNumber) : (Integer) defaultValue);
	}

	public void setValue(int value) {
		if (shouldPersist()) {
			persistInt(value);
		}
		
		if (value != mNumber) {
			mNumber = value;
			notifyChanged();
		}
	}
	
	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
	    return a.getInt(index, 0);
	}
}
