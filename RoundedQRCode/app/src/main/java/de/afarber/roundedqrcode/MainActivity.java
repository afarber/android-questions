package de.afarber.roundedqrcode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private QrView mQrView;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mQrView = findViewById(R.id.qrView);
        mEditText = findViewById(R.id.editText);
        mEditText.setOnEditorActionListener((tv, actionId, event) -> {
            String str = tv.getText().toString();
            tv.setText(null);
            if (!TextUtils.isEmpty(str)) {
                Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(tv.getWindowToken(), 0);
            }
            return true;
        });
    }

}

