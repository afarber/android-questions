package de.afarber.bottomsheet1;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomSheetDialogFragment dialog = ModalBottomSheet.newInstance();

        Button mButton1 = findViewById(R.id.button1);
        mButton1.setOnClickListener(view -> dialog.show(getSupportFragmentManager(), ModalBottomSheet.TAG));
    }
}