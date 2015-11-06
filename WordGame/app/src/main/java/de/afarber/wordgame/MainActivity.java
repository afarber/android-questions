package de.afarber.wordgame;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final static String[] LETTERS = new String[]{
            "*", "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q",
            "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void chooseLetter(View v) {
        ChooseLetterDialogFragment f = ChooseLetterDialogFragment.newInstance(42);
        f.show(getSupportFragmentManager(), ChooseLetterDialogFragment.TAG);
    }

    public void swapTiles(View v) {

    }

    public void doPositiveClick() {
        Toast.makeText(this,
                "doPositiveClick",
                Toast.LENGTH_SHORT).show();
    }

    public void doNegativeClick() {
        Toast.makeText(this,
                "doNegativeClick",
                Toast.LENGTH_SHORT).show();
    }

    public static class ChooseLetterDialogFragment extends DialogFragment {
        public final static String TAG = "ChooseLetterDialogFragment";
        private final static String ARG = "ARG";

        private class MyViewHolder
                extends RecyclerView.ViewHolder
                implements View.OnClickListener {

            public MyViewHolder(View v) {
                super(v);
                v.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),
                        "You have clicked " + ((TextView) v).getText(),
                        Toast.LENGTH_SHORT).show();
            }
        }

        private RecyclerView mRecyclerView;

        public static ChooseLetterDialogFragment newInstance(int num) {
            ChooseLetterDialogFragment f = new ChooseLetterDialogFragment();

            Bundle args = new Bundle();
            args.putInt(ARG, num);
            f.setArguments(args);

            return f;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int num = getArguments().getInt(ARG);

            mRecyclerView = new RecyclerView(getContext());
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mRecyclerView.setAdapter(new RecyclerView.Adapter<MyViewHolder>() {

                @Override
                public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View v = LayoutInflater.from(parent.getContext()).inflate(
                            android.R.layout.simple_list_item_1,
                            parent,
                            false);
                    MyViewHolder vh = new MyViewHolder(v);
                    return vh;
                }

                @Override
                public void onBindViewHolder(MyViewHolder vh, int position) {
                    TextView tv = (TextView) vh.itemView;
                    tv.setText(LETTERS[position]);
                }

                @Override
                public int getItemCount() {
                    return LETTERS.length;
                }
            });

            return new AlertDialog.Builder(getActivity())
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(R.string.choose_letter_title)
                    .setView(mRecyclerView)
                    .setPositiveButton(R.string.choose_letter_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    ((MainActivity)getActivity()).doPositiveClick();
                                }
                            }
                    )
                    .setNegativeButton(R.string.choose_letter_cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    ((MainActivity)getActivity()).doNegativeClick();
                                }
                            }
                    )
                    .create();
        }
    }
}
