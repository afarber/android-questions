package de.afarber.roundedqrcode;

import static de.afarber.roundedqrcode.AndroidUtilities.createQR;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private Bitmap qrCode;
    private int imageSize;

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
                Toast.makeText(this, str, Toast.LENGTH_LONG).show();
                // create QR code bitmap
                HashMap<EncodeHintType, Object> hints = new HashMap<>();
                hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
                hints.put(EncodeHintType.MARGIN, 0);
                hints.put(EncodeHintType.QR_VERSION, 4);
                qrView.setImageBitmap(qrCode = createQR(str, 768, 768, hints, qrCode));
                imageSize = qrCode == null ? 0 : qrCode.getWidth();
                // hide the soft keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(tv.getWindowToken(), 0);
            }
            return true;
        });
    }
}

