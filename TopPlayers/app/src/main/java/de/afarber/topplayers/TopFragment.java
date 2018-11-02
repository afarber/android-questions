package de.afarber.topplayers;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.commons.utils.FastAdapterDiffUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class TopFragment extends Fragment {

    private final static String TAG = TopFragment.class.getSimpleName();
    private final static String DATA = "data";

    private final ItemAdapter<TopItem> mItemAdapter = new ItemAdapter<>();
    private final FastAdapter<TopItem> mFastAdapter = FastAdapter.with(mItemAdapter);

    private TopViewModel mViewModel;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;

    private final Callback mCallback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException ex) {
            Log.w(TAG, "onFailure", ex);
            hideProgressBar();
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            hideProgressBar();
            if (response.isSuccessful() && response.body() != null) {
                String jsonStr = response.body().string();
                Log.d(TAG, "onResponse jsonStr=" + jsonStr);
                try {
                    // parse the list of 30 top users and store them into SQLite
                    List<TopEntity> tops = new ArrayList<>();
                    JSONObject root = new JSONObject(jsonStr);
                    JSONArray data = root.getJSONArray(DATA);
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonObj = data.getJSONObject(i);
                        TopEntity top = new TopEntity(jsonObj);
                        tops.add(top);
                    }
                    if (!tops.isEmpty()) {
                        TopDatabase.getInstance(getActivity()).topDao().insertTops(tops);
                    }
                } catch (JSONException | NullPointerException ex) {
                    Log.w(TAG, "parsing top failed", ex);
                }
            }
        }
    };

    public static TopFragment newInstance() {
        return new TopFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        mViewModel = ViewModelProviders.of(this).get(TopViewModel.class);
        mViewModel.getTops().observe(this, tops -> {
            List<TopItem> oldList = mItemAdapter.getAdapterItems();
            List<TopItem> newList = new ArrayList<>();
            for (TopEntity top: tops) {
                TopItem item = new TopItem(top);
                newList.add(item);
            }

            // (1) THIS STRAIGHTFORWARD WAY WORKS, BUT FLICKERS ONCE
            // mItemAdapter.clear().add(newList);

            // (2) THIS RESULTS IN EMPTY RECYCLERVIEW FOR SOME REASON
            // DiffCallback diffCallback = new DiffCallback(oldList, newList);
            // DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
            // diffResult.dispatchUpdatesTo(mFastAdapter);

            // (3) THIS WORKS AND DOES NOT FLICKER, BUT I WONDER HOW TO USE DiffUtil INSTEAD
            DiffUtil.DiffResult diffResult = FastAdapterDiffUtil.calculateDiff(mItemAdapter, newList);
            FastAdapterDiffUtil.set(mItemAdapter, diffResult);
        });

        View v = inflater.inflate(R.layout.top_fragment, container, false);
        mProgressBar = v.findViewById(R.id.progressBar);
        mRecyclerView = v.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mFastAdapter);

        fetchJsonData();

        return v;
    }

    private void showProgressBar() {
        try {
            mProgressBar.post(() -> mProgressBar.setVisibility(View.VISIBLE));
        } catch (Exception ex) {
            Log.w(TAG, "show progress bar failed", ex);
        }
    }

    private void hideProgressBar() {
        try {
            mProgressBar.post(() -> mProgressBar.setVisibility(View.GONE));
        } catch (Exception ex) {
            Log.w(TAG, "show recycler view failed", ex);
        }
    }

    private void fetchJsonData() {
        showProgressBar();

        FormBody.Builder bb = new FormBody.Builder();
        Request request = new Request.Builder()
                .url("https://slova.de/words/top.php")
                .post(bb.build())
                .build();

        MainActivity.fetch(request, mCallback);
    }

    // {"data":[
    // {"uid":4264,"elo":2467,"avg_time":"01:08","avg_score":16.7,"given":"Andrej","photo":"https://avt-1.foto.mail.ru/mail/andrej.kudinov.97/_avatarbig?1363079237"},
    // {"uid":6042,"elo":2444,"avg_time":"00:40","avg_score":20.0,"given":"Josh","photo":"https://avt-26.foto.mail.ru/mail/ez19311931/_avatarbig?1526102608"},
    // {"uid":5176,"elo":2414,"avg_time":"00:32","avg_score":16.6,"given":"Sergej","photo":"https://avt-11.foto.mail.ru/mail/stangrit57/_avatarbig?1529002082"}
    // ]}
}