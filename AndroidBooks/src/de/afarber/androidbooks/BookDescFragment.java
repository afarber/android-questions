package de.afarber.androidbooks;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BookDescFragment extends Fragment {
	// Book index argument name
	public static final String BOOK_INDEX = "book index";
	// Book index default value
	private static final int BOOK_INDEX_NOT_SET = -1;

	String[] mBookDescriptions;
	TextView mBookDescriptionTextView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View viewHierarchy = inflater.inflate(R.layout.fragment_book_desc, container, false);

		// Load array of book descriptions
		mBookDescriptions = getResources().
				getStringArray(R.array.bookDescriptions);
		// Get reference to book description text view
		mBookDescriptionTextView = (TextView)
				viewHierarchy.findViewById(R.id.bookDescription);

		// Retrieve the book index if attached
		Bundle args = getArguments();
		int bookIndex = args != null ?
				args.getInt(BOOK_INDEX, BOOK_INDEX_NOT_SET) :
					BOOK_INDEX_NOT_SET;

		// If we find the book index, use it
		if (bookIndex != BOOK_INDEX_NOT_SET)
			setBook(bookIndex);

		return viewHierarchy;
	}

	public void setBook(int bookIndex) {
		// Lookup the book description
		String bookDescription = mBookDescriptions[bookIndex];

		// Display it
		mBookDescriptionTextView.setText(bookDescription);
	}
}
