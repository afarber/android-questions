package de.afarber.dialogcountdown;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

public class MyDialog extends AppCompatDialogFragment {
    public final static String TAG = MyDialog.class.getName();
    private final static long TIMEOUT = 20L;

    private long mCountDown = TIMEOUT;
    private final Handler mHandler = new Handler();
    // runs on main thread and decreases countdown by 1 second
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            AlertDialog dialog = (AlertDialog) requireDialog();
            Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            negativeButton.setText(getString(R.string.dialog_cancel_button, mCountDown));
            if (--mCountDown <= 0) {
                dismiss();
            } else {
                mHandler.postDelayed(this, 1000L);
            }
        }
    };

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireActivity())
            .setTitle(R.string.dialog_title)
            .setPositiveButton(R.string.dialog_ok_button, (dialogInterface, i) -> Log.d(TAG, "Ok clicked"))
            .setNegativeButton(getString(R.string.dialog_cancel_button, mCountDown), null)
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();
        mRunnable.run();
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mRunnable);
    }
}
