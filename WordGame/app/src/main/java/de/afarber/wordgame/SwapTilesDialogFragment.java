package de.afarber.wordgame;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

public class SwapTilesDialogFragment extends DialogFragment {

    public final static String TAG = SwapTilesDialogFragment.class.getName();

    private final static String ARG1 = "ARG1";
    private final static String ARG2 = "ARG2";

    private SwapTilesListener mListener;
    private String mLetters;
    private boolean[] mChecked;

    public interface SwapTilesListener {
        public void swapTiles(String letters);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof SwapTilesListener) {
            mListener = (SwapTilesListener) context;
        } else {
            throw new ClassCastException(context.toString() +
                    " must implement " + TAG + ".SwapTilesListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    private class MyViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public MyViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            CheckedTextView checkedText = (CheckedTextView) v;
            int i = getAdapterPosition();
            mChecked[i] = !mChecked[i];
            checkedText.setChecked(mChecked[i]);
        }
    }

    public static SwapTilesDialogFragment newInstance(String letters) {
        SwapTilesDialogFragment f = new SwapTilesDialogFragment();

        Bundle args = new Bundle();
        args.putString(ARG1, letters);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        // save mChecked array, when device is rotated
        outState.putBooleanArray(ARG2, mChecked);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mLetters = getArguments().getString(ARG1);

        if (savedInstanceState != null) {
            // try to restore mChecked array, saved during rotation
            mChecked = savedInstanceState.getBooleanArray(ARG2);
        }

        // restoring mChecked has failed or device was not rotated
        if (mChecked == null || mChecked.length != mLetters.length()) {
            mChecked = new boolean[mLetters.length()];
            for (int i = 0; i < mLetters.length(); i++) {
                char letter = mLetters.charAt(i);
                mChecked[i] = (letter != '*');
            }
        }

        RecyclerView rv = new RecyclerView(getContext());
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new RecyclerView.Adapter<MyViewHolder>() {

            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(
                        android.R.layout.simple_list_item_multiple_choice,
                        parent,
                        false);

                return new MyViewHolder(v);
            }

            @Override
            public void onBindViewHolder(MyViewHolder vh, int position) {
                CheckedTextView v = (CheckedTextView) vh.itemView;
                char letter = mLetters.charAt(position);
                v.setText(String.valueOf(letter));
                v.setChecked(mChecked[position]);
            }

            @Override
            public int getItemCount() {
                return mLetters.length();
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.swap_tiles_title)
                .setView(rv)
                .setPositiveButton(R.string.swap_tiles_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < mLetters.length(); i++)
                                    if (mChecked[i])
                                        sb.append(mLetters.charAt(i));

                                if (sb.length() > 0)
                                    mListener.swapTiles(sb.toString());
                            }
                        }
                )
                .setNegativeButton(R.string.swap_tiles_cancel, null)
                .create();
    }
}
