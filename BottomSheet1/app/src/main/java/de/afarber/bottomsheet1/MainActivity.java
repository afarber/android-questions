package de.afarber.bottomsheet1;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class MainActivity extends AppCompatActivity {
    public final static String TAG = "BottomSheet1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomSheetDialogFragment dialog = ModalBottomSheet.newInstance();

        Button mButton1 = findViewById(R.id.button1);
        mButton1.setOnClickListener(view -> dialog.show(getSupportFragmentManager(), ModalBottomSheet.TAG));


        LinearLayout bottomSheet = findViewById(R.id.bottom_sheet_behavior_id);
        BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                Log.d(TAG, "onStateChanged: " + newState);
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                Log.d(TAG, "onSlide: " + v);
            }
        });
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        Button mButton2 = findViewById(R.id.button2);
        mButton2.setOnClickListener(view -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });
    }
}