package de.afarber.myviewpager;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.afarber.myviewpager.MainActivity.GID;

public class ChatFragment extends ListFragment {
    private final static List<String> LIST = new ArrayList<>(Arrays.asList("chat 1", "chat 2", "chat 3"));

    public static ChatFragment newInstance(int gid) {
        ChatFragment f = new ChatFragment();

        Bundle args = new Bundle();
        args.putInt(GID, gid);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        View tv = v.findViewById(R.id.text);
        int gid = getArguments().getInt(GID);
        ((TextView)tv).setText("Chat in the game " + gid);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, LIST));
    }
}
