package de.afarber.androidbooks;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;

public class MainActivity extends FragmentActivity
    implements OnSelectedBookChangeListener {

	private boolean mIsDynamic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// load the activity_main layout resource
		setContentView(R.layout.activity_main);

		// Get the book description fragment
		FragmentManager fm = getSupportFragmentManager(); //   getFragmentManager();
		Fragment bookDescFragment =
				fm.findFragmentById(R.id.fragmentDescription);

		// If not found than we’re doing dynamic mgmt
		mIsDynamic = bookDescFragment == null ||
				!bookDescFragment.isInLayout();

		// Load the list fragment if necessary
		if (mIsDynamic) {
			// Begin transaction
			FragmentTransaction ft = fm.beginTransaction();

			// Create the Fragment and add
			BookListFragment2 listFragment = new BookListFragment2();
			ft.add(R.id.layoutRoot, listFragment, "bookList");

			// Commit the changes
			ft.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	@Override
	public void onSelectedBookChanged(int bookIndex) {
		BookDescFragment bookDescFragment;
		FragmentManager fm = getSupportFragmentManager();

		// Check validity of fragment reference
		if (mIsDynamic) {
			// Handle dynamic switch to description fragment
			FragmentTransaction ft = fm.beginTransaction();

			// Create the fragment and attach book index
			bookDescFragment = new BookDescFragment();
			Bundle args = new Bundle();
			args.putInt(BookDescFragment.BOOK_INDEX, bookIndex);
			bookDescFragment.setArguments(args);

			// Replace the book list with the description
			ft.replace(R.id.layoutRoot, bookDescFragment, "bookDescription");
			ft.addToBackStack(null);
			ft.setCustomAnimations(android.R.animator.fade_in, 
								   android.R.animator.fade_out);
			ft.commit();

		} else {
			// Use the already visible description fragment
			bookDescFragment = (BookDescFragment)
					fm.findFragmentById(R.id.fragmentDescription);
			bookDescFragment.setBook(bookIndex);
		}
	}
}
