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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SwapTilesDialogFragment extends DialogFragment {

    public final static String TAG = "SwapTilesDialogFragment";

    private final static String ARG = "ARG";

    public interface SwapTilesListener {
        public void swapTiles(List<Character> selected);
    }

    private SwapTilesListener mListener;

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
            Character c = mLetters[getAdapterPosition()];
/*
            Toast.makeText(getContext(),
                    "You have clicked " + c,
                    Toast.LENGTH_SHORT).show();
*/
            checkedText.setChecked(!checkedText.isChecked());

            if (checkedText.isChecked()) {
                mSelected.add(c);
            } else {
                mSelected.remove(c);
            }
        }
    }

    private RecyclerView mRecyclerView;
    private char[] mLetters;
    private List<Character> mSelected;

    public static SwapTilesDialogFragment newInstance(char[] letters) {
        SwapTilesDialogFragment f = new SwapTilesDialogFragment();

        Bundle args = new Bundle();
        args.putCharArray(ARG, letters);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mLetters = getArguments().getCharArray(ARG);
        mSelected = new ArrayList<Character>();

        mRecyclerView = new RecyclerView(getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new RecyclerView.Adapter<MyViewHolder>() {

            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(
                        android.R.layout.simple_list_item_multiple_choice,
                        parent,
                        false);
                MyViewHolder vh = new MyViewHolder(v);
                return vh;
            }

            @Override
            public void onBindViewHolder(MyViewHolder vh, int position) {
                TextView tv = (TextView) vh.itemView;
                tv.setText(String.valueOf(mLetters[position]));
            }

            @Override
            public int getItemCount() {
                return mLetters.length;
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.swap_tiles_title)
                .setView(mRecyclerView)
                .setPositiveButton(R.string.swap_tiles_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mListener.swapTiles(mSelected);
                            }
                        }
                )
                .setNegativeButton(R.string.swap_tiles_cancel, null)
                .create();
    }
}