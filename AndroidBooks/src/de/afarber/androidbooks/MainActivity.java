package de.afarber.androidbooks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;

public class MainActivity extends FragmentActivity
    implements OnSelectedBookChangeListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }


  @Override
  public void onSelectedBookChanged(int bookIndex) {
    // Access the FragmentManager
    FragmentManager fragmentManager = getSupportFragmentManager();
    // Get the book description fragment
    BookDescFragment bookDescFragment = (BookDescFragment)
        fragmentManager.findFragmentById(R.id.fragmentDescription);

    // Check validity of fragment reference
    if (bookDescFragment == null || !bookDescFragment.isVisible()) {
      // Use activity to display description
      Intent intent = new Intent(this, BookDescActivity.class);

      intent.putExtra("bookIndex", bookIndex);
      startActivity(intent);
    } else {
      // Use contained fragment to display description
      bookDescFragment.setBook(bookIndex);
    }
  }
}
