package de.afarber.roundedqrcode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QrView qrView = findViewById(R.id.qrView);
        EditText editText = findViewById(R.id.editText);
        editText.setOnEditorActionListener((tv, actionId, event) -> {
            String str = tv.getText().toString().trim();
            if (!TextUtils.isEmpty(str)) {
                tv.setText(null);
                Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();
                // hide the soft keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(tv.getWindowToken(), 0);
            }
            return true;
        });
    }

}

