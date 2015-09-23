package de.afarber.navifab;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GoogleFragment extends Fragment {

    public final static String TAG = "GoogleFragment";

    private ImageView mPhotoImageView;
    private ProgressBar mProgressBar;
    private TextView mGivenTextView;
    private TextView mPlaceTextView;

    public GoogleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_google, container, false);

        mPhotoImageView = (ImageView) v.findViewById(R.id.photo);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress);
        mGivenTextView = (TextView) v.findViewById(R.id.given);
        mPlaceTextView = (TextView) v.findViewById(R.id.place);

        mGivenTextView.setText("Alexander");
        mPlaceTextView.setText("Bochum");
        mPhotoImageView.setImageResource(R.drawable.farber);

        return v;
    }
}



