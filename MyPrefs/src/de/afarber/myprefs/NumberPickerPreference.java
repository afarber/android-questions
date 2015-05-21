package de.afarber.myprefs;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public class NumberPickerPreference extends DialogPreference {
	public NumberPickerPreference(Context context) {
		this(context, null, 0);
	}

	public NumberPickerPreference(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public NumberPickerPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setLayoutResource(R.layout.preference_picker);
	}
}
