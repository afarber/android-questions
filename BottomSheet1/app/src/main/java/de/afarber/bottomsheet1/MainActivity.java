package de.afarber.bottomsheet1;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_DRAGGING;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_SETTLING;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public final static String TAG = "BottomSheet1";

    private final static Map<Integer, String> STATES = new HashMap<>();

    static {
        STATES.put(STATE_EXPANDED, "STATE_EXPANDED");
        STATES.put(STATE_COLLAPSED, "STATE_COLLAPSED");
        STATES.put(STATE_DRAGGING, "STATE_DRAGGING");
        STATES.put(STATE_SETTLING, "STATE_SETTLING");
        STATES.put(STATE_HIDDEN, "STATE_HIDDEN");
        STATES.put(STATE_HALF_EXPANDED, "STATE_HALF_EXPANDED");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomSheetDialogFragment dialog = ModalBottomSheet.newInstance();
        Button showDialog = findViewById(R.id.showDialogButton);
        showDialog.setOnClickListener(view -> dialog.show(getSupportFragmentManager(), ModalBottomSheet.TAG));

        LinearLayout sheet = findViewById(R.id.bottom_sheet_behavior_id);
        BottomSheetBehavior<LinearLayout> behavior = BottomSheetBehavior.from(sheet);
        Log.d(TAG, "before onStateChanged: " + STATES.get(behavior.getState()));
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                Log.d(TAG, "onStateChanged: " + STATES.get(newState));
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                Log.d(TAG, "onSlide: " + v);
            }
        });

        Button showSheet = findViewById(R.id.showSheetButton);
        showSheet.setOnClickListener(view -> {
            if (behavior.getState() == STATE_EXPANDED) {
                behavior.setState(STATE_COLLAPSED);
            } else {
                behavior.setState(STATE_EXPANDED);
            }
        });
    }
}