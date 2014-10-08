package de.afarber.androidbooks;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class BookListFragment2 extends ListFragment {
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		String[] bookTitles = getResources().getStringArray(R.array.bookTitles);
		
		ArrayAdapter<String> bookTitlesAdapter = new ArrayAdapter<String>(
			getActivity(),
			android.R.layout.simple_list_item_1, 
			bookTitles
		);
		setListAdapter(bookTitlesAdapter);
	}

	public void onListItemClick(ListView l, View v, int position, long id) {
		// Access the Activity and cast to the interface
		OnSelectedBookChangeListener listener =	(OnSelectedBookChangeListener) getActivity();
		// Notify the Activity of the selection
		listener.onSelectedBookChanged(position);
	}
}
