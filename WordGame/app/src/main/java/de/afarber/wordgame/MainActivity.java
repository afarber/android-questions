package de.afarber.wordgame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
    implements ChooseLetterDialogFragment.MyListener,
        SwapTilesDialogFragment.MyListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void chooseLetter(View v) {
        ChooseLetterDialogFragment f = ChooseLetterDialogFragment.newInstance();
        f.show(getSupportFragmentManager(), ChooseLetterDialogFragment.TAG);
    }

    public void swapTiles(View v) {
        final char[] letters = new char[]{'A', 'B', 'C', 'D', 'X', 'Y', 'Z'};
        SwapTilesDialogFragment f = SwapTilesDialogFragment.newInstance(letters);
        f.show(getSupportFragmentManager(), SwapTilesDialogFragment.TAG);
    }

    @Override
    public void doPositiveClick() {
        Toast.makeText(this,
                "doPositiveClick",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void doNegativeClick() {
        Toast.makeText(this,
                "doNegativeClick",
                Toast.LENGTH_SHORT).show();
    }
}

