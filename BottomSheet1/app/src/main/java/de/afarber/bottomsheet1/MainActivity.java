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
import android.widget.TextView;
import android.widget.Toast;

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

        BottomSheetDialogFragment dialog = ModalSheet.newInstance();
        Button showDialog = findViewById(R.id.showDialogButton);
        showDialog.setOnClickListener(view -> dialog.show(getSupportFragmentManager(), ModalSheet.TAG));

        TextView dragLabel = findViewById(R.id.dragLabel);
        LinearLayout sheet = findViewById(R.id.bottom_sheet_behavior_id);
        sheet.findViewById(R.id.button10).setOnClickListener(this::closeSheet);
        sheet.findViewById(R.id.button20).setOnClickListener(this::closeSheet);
        sheet.findViewById(R.id.button30).setOnClickListener(this::closeSheet);
        BottomSheetBehavior<LinearLayout> behavior = BottomSheetBehavior.from(sheet);
        Button showSheet = findViewById(R.id.showSheetButton);
        showSheet.setOnClickListener(view -> {
            if (behavior.getState() == STATE_EXPANDED) {
                behavior.setState(STATE_COLLAPSED);
            } else {
                behavior.setState(STATE_EXPANDED);
            }
        });
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                Log.d(TAG, "onStateChanged: " + STATES.get(newState));
                if (newState == STATE_EXPANDED) {
                    showSheet.setText(R.string.close_bottom_sheet);
                    dragLabel.setText(R.string.drag_me_down);
                    dragLabel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_south_24, 0, R.drawable.ic_baseline_south_24, 0);
                } else if (newState == STATE_COLLAPSED) {
                    showSheet.setText(R.string.show_bottom_sheet);
                    dragLabel.setText(R.string.drag_me_up);
                    dragLabel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_north_24, 0, R.drawable.ic_baseline_north_24, 0);
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                Log.d(TAG, "onSlide: " + v);
            }
        });
    }

    private void closeSheet(View view) {
        Button button = (Button) view;
        LinearLayout sheet = (LinearLayout) button.getParent();
        BottomSheetBehavior<LinearLayout> behavior = BottomSheetBehavior.from(sheet);
        Toast.makeText(this,
                button.getText() + " is clicked",
                Toast.LENGTH_SHORT).show();
        if (behavior.getState() == STATE_EXPANDED) {
            behavior.setState(STATE_COLLAPSED);
        } else {
            behavior.setState(STATE_EXPANDED);
        }
    }
}