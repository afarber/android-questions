package de.afarber.myprefs;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SeekBarPreference extends Preference implements OnSeekBarChangeListener {
	private SeekBar mSeekBar;
	private int mProgress;

	public SeekBarPreference(Context context) {
		super(context);
	}

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SeekBarPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected View onCreateView(ViewGroup parent)	{
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.preference_seekbar, parent, false);
		mSeekBar = (SeekBar) view.findViewById(R.id.seekbar);
		mSeekBar.setProgress(mProgress);
		mSeekBar.setOnSeekBarChangeListener(this);
		return view;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (!fromUser)
			return;
		
		setValue(progress);
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// not used
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// not used
	}
	
	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		setValue(restoreValue ? getPersistedInt(mProgress) : (Integer) defaultValue);
	}

	public void setValue(int value) {
		if (shouldPersist()) {
			persistInt(value);
		}
		
		if (value != mProgress) {
			mProgress = value;
			notifyChanged();
		}
	}
}

