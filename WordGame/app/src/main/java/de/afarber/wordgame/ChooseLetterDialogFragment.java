package de.afarber.wordgame;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ChooseLetterDialogFragment extends DialogFragment {

    public final static String TAG = ChooseLetterDialogFragment.class.getName();

    private final static Character[] LETTERS = new Character[]{
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
            'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    public interface ChooseLetterListener {
        public void chooseLetter(char c);
    }

    private ChooseLetterListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ChooseLetterListener) {
            mListener = (ChooseLetterListener) context;
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
            Character c = LETTERS[getAdapterPosition()];
            mListener.chooseLetter(c);
            dismiss();
        }
    }

    public static ChooseLetterDialogFragment newInstance() {
        return new ChooseLetterDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        RecyclerView rv = new RecyclerView(getContext());
        rv.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rv.setAdapter(new RecyclerView.Adapter<MyViewHolder>() {

            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(
                        android.R.layout.simple_list_item_1,
                        parent,
                        false);

                return new MyViewHolder(v);
            }

            @Override
            public void onBindViewHolder(MyViewHolder vh, int position) {
                TextView tv = (TextView) vh.itemView;
                tv.setText(String.valueOf(LETTERS[position]));
            }

            @Override
            public int getItemCount() {
                return LETTERS.length;
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.choose_letter_title)
                .setView(rv)
                .setNegativeButton(R.string.choose_letter_cancel, null)
                .create();
    }
}