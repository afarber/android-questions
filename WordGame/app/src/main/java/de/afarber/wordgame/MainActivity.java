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
        SwapTilesDialogFragment f = SwapTilesDialogFragment.newInstance("ABC*XYZ");
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

    @Override
    public void swapTiles(String letters) {
        Toast.makeText(this,
                "swapTiles: " + letters,
                Toast.LENGTH_LONG).show();
    }
}

