package de.afarber.mylist;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

public class MyListFragment extends ListFragment implements MyConstants {
    static class ViewHolder {
        public CheckedTextView text1;
    }

    public interface ListListener {
        public void itemSelected(int index);
    }

    private ListListener mListener;
    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> mItems = new ArrayList<String>(Arrays.asList(
    		"Item 00",
    		"Item 01",
    		"Item 02",
    		"Item 03",
    		"Item 04",
    		"Item 05",
    		"Item 06",
    		"Item 07",
    		"Item 08",
    		"Item 09",
    		"Item 10",
    		"Item 11",
    		"Item 12"
    ));

    @Override
    public void onAttach(Activity activity) {
            super.onAttach(activity);
            if (activity instanceof ListListener) {
                    mListener = (ListListener) activity;
            } else {
                    throw new ClassCastException(activity.toString() +
                        " must implement MyListFragment.ListListener");
            }
    }

    @Override
    public void onDetach() {
            super.onDetach();
            mListener = null;
    }
    
    public static MyListFragment newInstance(int index) {
    	MyListFragment fragment = new MyListFragment();
        Bundle args = new Bundle();
        args.putInt(INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }

    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        mAdapter = new ArrayAdapter<String>(getActivity(),
        		android.R.layout.simple_list_item_checked,
        		android.R.id.text1, 
        		mItems) {

        	@Override
        	public View getView(int position, View convertView, ViewGroup parent) {
        		ViewHolder holder;

        		if (convertView == null) {
        			LayoutInflater inflater = getActivity().getLayoutInflater();
        			convertView = inflater.inflate(android.R.layout.simple_list_item_checked, null);
        			holder = new ViewHolder();
        			holder.text1 = (CheckedTextView) convertView.findViewById(android.R.id.text1);
        			convertView.setTag(holder);
        		} else {
        			holder = (ViewHolder) convertView.getTag();
        		}

        		String str = (String) getItem(position);
        		holder.text1.setText(str);
        		return convertView;
        	}
        };

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        setListAdapter(mAdapter);
    }
    
    @Override
    public void onResume() {
    	super.onResume();

    	int index = getArguments().getInt(INDEX);
    	getListView().setItemChecked(index, true);
    }
    
    @Override
	 public void onListItemClick(ListView l, View v, int position, long id) {
		 mListener.itemSelected(position);
	}
}

