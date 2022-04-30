package de.afarber.roundedqrcode;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
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
                qrView.setImageBitmap(qrCode = createQR(this, str, qrCode));
                // hide the soft keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(tv.getWindowToken(), 0);
            }
            return true;
        });
    }

    public Bitmap createQR(Context context, String key, Bitmap oldBitmap) {
        try {
            HashMap<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 0);
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(key, BarcodeFormat.QR_CODE, 768, 768, hints);
            int w = bitMatrix.getWidth();
            int h = bitMatrix.getHeight();
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                int offset = y * w;
                for (int x = 0; x < w; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ? BLACK : WHITE;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
            //Bitmap bitmap = writer.encode(key, BarcodeFormat.QR_CODE, 768, 768, hints, oldBitmap);
            //imageSize = writer.getImageSize();
            return bitmap;
        } catch (Exception e) {
            Log.w("QR", "createQR failed", e);
        }
        return null;
    }
}

