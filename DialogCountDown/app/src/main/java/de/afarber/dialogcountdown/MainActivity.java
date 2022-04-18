package de.afarber.dialogcountdown;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.show_dialog_button);
        button.setOnClickListener(view -> {
            MyDialog dialog = new MyDialog();
            dialog.show(getSupportFragmentManager(), MyDialog.TAG);
        });
    }
}