package de.afarber.bottomsheet1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class MainActivity extends AppCompatActivity {
    private BottomSheetDialog mBottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);

        Button button10 = mBottomSheetDialog.findViewById(R.id.button10);
        Button button20 = mBottomSheetDialog.findViewById(R.id.button20);
        Button button30 = mBottomSheetDialog.findViewById(R.id.button30);

        button10.setOnClickListener(view -> closeBottomSheetDialog(view));
        button20.setOnClickListener(view -> closeBottomSheetDialog(view));
        button30.setOnClickListener(view -> closeBottomSheetDialog(view));

        Button mButton1 = findViewById(R.id.button1);
        mButton1.setOnClickListener(view -> mBottomSheetDialog.show());
    }

    private void closeBottomSheetDialog(View view) {
        Button button = (Button) view;
        Toast.makeText(getApplicationContext(),
                button.getText() + " is clicked",
                Toast.LENGTH_SHORT).show();
        mBottomSheetDialog.dismiss();
    }
}