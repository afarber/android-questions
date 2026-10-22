package de.afarber.mylist;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MyMainFragment extends Fragment implements MyConstants {
	
    private MainListener mListener;
	private TextView mSelectedTextView;
	private Button mSelectButton;

    @Override
    public View onCreateView(LayoutInflater inflater,
                    ViewGroup container,
                    Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mSelectedTextView = (TextView) view.findViewById(R.id.selectedTextView);
        mSelectButton = (Button) view.findViewById(R.id.selectButton);
        mSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	mListener.selectedButtonClicked();
            }
        });
        
        return view;
    }

    public interface MainListener {
    	public void selectedButtonClicked();
    	public int getIndex();
    }

    @Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
    	if (activity instanceof MainListener) {
    		mListener = (MainListener) activity;
    	} else {
    		throw new ClassCastException(activity.toString() +
    				" must implement MyMainFragment.MainListener");
    	}
    }

    @Override
    public void onDetach() {
    	super.onDetach();
    	mListener = null;
    }
    
    @Override
    public void onResume() {
    	super.onResume();

    	int index = mListener.getIndex();
    	if (index < 0)
    		return;
    	
   		mSelectedTextView.setText("Selected index: " + index);
    }
    
    public void setText(String str) {
    	mSelectedTextView.setText(str);
    }
}

