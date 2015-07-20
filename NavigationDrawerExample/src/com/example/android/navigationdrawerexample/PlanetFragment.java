package com.example.android.navigationdrawerexample;

import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class PlanetFragment extends Fragment {
    public static final String ARG_PLANET_NUMBER = "planet_number";
    
    public final static String ACTION_PLAY    ="com.example.android.navigationdrawerexample.play";
    public final static String ACTION_PAUSE   ="com.example.android.navigationdrawerexample.pause";
    public final static String ACTION_STOP    ="com.example.android.navigationdrawerexample.stop";
    public final static String ACTION_SHUFFLE ="com.example.android.navigationdrawerexample.shuffle";
    
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ACTION_PLAY.equals(action))
                    Toast.makeText(getActivity(), "Play music", Toast.LENGTH_LONG).show();
                else if (ACTION_PAUSE.equals(action))
                    Toast.makeText(getActivity(), "Pause music", Toast.LENGTH_LONG).show();
                else if (ACTION_STOP.equals(action))
                    Toast.makeText(getActivity(), "Stop music", Toast.LENGTH_LONG).show();
                else if (ACTION_SHUFFLE.equals(action))
                    Toast.makeText(getActivity(), "Shuffle music", Toast.LENGTH_LONG).show();
        }
    };
  
    private void register() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PLAY);
        filter.addAction(ACTION_PAUSE);
        filter.addAction(ACTION_STOP);
        filter.addAction(ACTION_SHUFFLE);
        getActivity().registerReceiver(mMessageReceiver, filter);
    }
  
    private void unregister() {
        getActivity().unregisterReceiver(mMessageReceiver);
    }
  
    @Override
    public void onResume() {
        super.onResume();
        register();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregister();
    }

    public PlanetFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_planet, container, false);
        int i = getArguments().getInt(ARG_PLANET_NUMBER);
        String[] planetTitles = getResources().getStringArray(R.array.planets_array);
        String planet = planetTitles[i];
        int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
                        "drawable", getActivity().getPackageName());
        ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
        getActivity().setTitle(planet);
        return rootView;
    }
}
