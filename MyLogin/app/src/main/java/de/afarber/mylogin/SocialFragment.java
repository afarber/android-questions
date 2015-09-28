package de.afarber.mylogin;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class SocialFragment extends ListFragment {
    public final static String TAG = "SocialFragment";

    public SocialFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        try {
            String title = Utils.SOCIAL_LABELS[position];
            ((MainActivity) getActivity()).setTitle(title);

            Class<?> fragmentClass = Utils.SOCIAL_FRAGMENTS[position];
            Fragment f = (Fragment) fragmentClass.newInstance();
            String tag = (String) fragmentClass.getField("TAG").get(null);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.root, f, tag)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        setListAdapter(new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_single_choice,
                Utils.SOCIAL_LABELS) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setCompoundDrawablePadding(24);
                view.setCompoundDrawablesWithIntrinsicBounds(Utils.SOCIAL_ICONS[position], 0, 0, 0);
                return view;
            }
        });
    }
}
