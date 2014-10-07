package de.afarber.androidbooks;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Jim on 6/5/13.
 */
public class BookDescFragment extends Fragment {

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

    return viewHierarchy;
  }

  public void setBook(int bookIndex) {
    // Lookup the book description
    String bookDescription = mBookDescriptions[bookIndex];

    // Display it
    mBookDescriptionTextView.setText(bookDescription);
  }
}
