package de.afarber.dialogcountdown;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

public class MyDialog extends AppCompatDialogFragment {
    public final static String TAG = MyDialog.class.getName();
    private final static long DEFAULT_TIMEOUT = 30L;

    private final StringBuilder mExpireStr = new StringBuilder(8);
    private final Handler mHandler = new Handler();
    private final Runnable mRunnable = new Runnable() {
        // runs on main thread and decreases countdown by 1 second
        @Override
        public void run() {
            
            // run again in 1 second
            mHandler.postDelayed(this, 1000L);
        }
    };

    public static MyDialog newInstance(long timeout) {
        MyDialog dialog = new MyDialog();
        Bundle args = new Bundle();
        args.putLong("timeout", timeout);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        long timeout = getArguments() == null ? DEFAULT_TIMEOUT :
                getArguments().getLong("timeout", DEFAULT_TIMEOUT);

        return new AlertDialog.Builder(requireActivity())
            .setTitle(R.string.dialog_title)
            .setPositiveButton(R.string.dialog_ok_button, (dialogInterface, i) -> Log.d(TAG, "Ok clicked"))
            .setNegativeButton(R.string.dialog_cancel_button, null)
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
