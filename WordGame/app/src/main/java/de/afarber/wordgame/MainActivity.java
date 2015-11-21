package de.afarber.wordgame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
    implements ChooseLetterDialogFragment.ChooseLetterListener,
        SwapTilesDialogFragment.SwapTilesListener,
        FindWordDialogFragment.FindWordListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showChooseLetterDialog(View v) {
        ChooseLetterDialogFragment f = ChooseLetterDialogFragment.newInstance();
        f.show(getSupportFragmentManager(), ChooseLetterDialogFragment.TAG);
    }

    public void showSwapTilesDialog(View v) {
        SwapTilesDialogFragment f = SwapTilesDialogFragment.newInstance("ABC*XYZ");
        f.show(getSupportFragmentManager(), SwapTilesDialogFragment.TAG);
    }

    public void showFindWordDialog(View v) {
        FindWordDialogFragment f = FindWordDialogFragment.newInstance();
        f.show(getSupportFragmentManager(), FindWordDialogFragment.TAG);
    }

    @Override
    public void chooseLetter(char c) {
        Toast.makeText(this,
                "chooseLetter: " + c,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void swapTiles(String letters) {
        Toast.makeText(this,
                "swapTiles: " + letters,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void findWord(String word) {
        Toast.makeText(this,
                "findWord: " + word,
                Toast.LENGTH_SHORT).show();
    }
}


