package de.afarber.wordgame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity
    implements ChooseLetterDialogFragment.ChooseLetterListener,
        SwapTilesDialogFragment.SwapTilesListener {

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
    public void swapTiles(List<Character> selected) {
        Toast.makeText(this,
                "swapTiles: " + TextUtils.join(", ", selected),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void chooseLetter(char c) {
        Toast.makeText(this,
                "chooseLetter: " + c,
                Toast.LENGTH_SHORT).show();
    }
}


