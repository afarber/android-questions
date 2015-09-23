package de.afarber.navifab;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;



public class SocialFragment extends ListFragment {
    public final static String TAG = "SocialFragment";

    private static final String[] SOCIAL_LABELS = {
            "Google",
            "Facebook",
            "Twitter"
    };

    private static final int[] SOCIAL_ICONS = {
            R.drawable.ic_google_plus_grey600_24dp,
            R.drawable.ic_facebook_grey600_24dp,
            R.drawable.ic_twitter_grey600_24dp
    };

    public SocialFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_social, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
         getActivity().getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.root, new GoogleFragment(), GoogleFragment.TAG)
                .commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        setListAdapter(new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_single_choice,
                SOCIAL_LABELS) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setCompoundDrawablePadding(24);
                view.setCompoundDrawablesWithIntrinsicBounds(SOCIAL_ICONS[position], 0, 0, 0);
                return view;
            }
        });
    }
}
