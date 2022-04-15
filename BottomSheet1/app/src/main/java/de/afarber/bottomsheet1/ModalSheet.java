package de.afarber.bottomsheet1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ModalSheet extends BottomSheetDialogFragment {
    public static final String TAG = ModalSheet.class.getName();

    public static ModalSheet newInstance() {
        return new ModalSheet();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_dialog, container, false);

        v.findViewById(R.id.button10).setOnClickListener(this::closeSheet);
        v.findViewById(R.id.button20).setOnClickListener(this::closeSheet);
        v.findViewById(R.id.button30).setOnClickListener(this::closeSheet);
        v.findViewById(R.id.button40).setOnClickListener(this::closeSheet);
        v.findViewById(R.id.button50).setOnClickListener(this::closeSheet);

        return v;
    }

    private void closeSheet(View view) {
        Button button = (Button) view;
        Toast.makeText(requireContext(),
                button.getText() + " is clicked",
                Toast.LENGTH_SHORT).show();
        dismiss();
    }
}
