package de.afarber.wordgame;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FindWordDialogFragment extends DialogFragment {

    public final static String TAG = FindWordDialogFragment.class.getName();

    public interface FindWordListener {
        public void findWord(String word);
    }

    private FindWordListener mListener;

    public static FindWordDialogFragment newInstance() {
        return new FindWordDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final EditText editText = new EditText(getContext());
        editText.setMaxLines(1);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v,
                                          int actionId,
                                          KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    String word = editText.getText().toString();
                    mListener.findWord(word);
                    dismiss();
                    return true;
                }
                return false;
            }
        });

        return new AlertDialog.Builder(getContext())
            .setIcon(R.mipmap.ic_launcher)
            .setTitle(R.string.find_word_title)
            .setView(editText, 16, 16, 16, 16)
            .setPositiveButton(R.string.find_word_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String word = editText.getText().toString();
                        mListener.findWord(word);
                    }
                }
            )
            .setNegativeButton(R.string.find_word_cancel, null)
            .create();
    }
}