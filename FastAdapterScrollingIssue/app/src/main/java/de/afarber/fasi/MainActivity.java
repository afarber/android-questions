package de.afarber.fasi;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.commons.utils.FastAdapterDiffUtil;
import com.mikepenz.fastadapter.listeners.ItemFilterListener;
import com.mikepenz.fastadapter.utils.ComparableItemListImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static de.afarber.fasi.MyItem.LOST;
import static de.afarber.fasi.MyItem.WON;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "fasi";

    private final DateAscComparator mAscComparator = new DateAscComparator();
    private final DateDescComparator mDescComparator = new DateDescComparator();
    private final ComparableItemListImpl<MyItem> mItemListImpl = new ComparableItemListImpl<>(mDescComparator);
    private final ItemAdapter<MyItem> mItemAdapter = new ItemAdapter<>(mItemListImpl);
    private final FastAdapter<MyItem> mFastAdapter = FastAdapter.with(mItemAdapter);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mFastAdapter);

        mFastAdapter.withSelectable(true);
        mFastAdapter.withOnClickListener((v, adapter, item, position) -> {
            item.details = !item.details;
            // pass item.details as payload to bindView to prevent flickering
            adapter.getFastAdapter().notifyAdapterItemChanged(position, item.details);
            return true;
        });

        mItemAdapter.getItemFilter().withFilterPredicate((item, constraint) -> {
            String str = constraint.toString().toUpperCase();
            return item.given2.toUpperCase().startsWith(str);
        });

        mItemAdapter.getItemFilter().withItemFilterListener(new ItemFilterListener<MyItem>() {
            @Override
            public void itemsFiltered(@Nullable CharSequence constraint, @Nullable List<MyItem> results) {
                Toast.makeText(MainActivity.this,
                        getString(R.string.filter_games_result, mItemAdapter.getAdapterItemCount()),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReset() {
            }
        });

        List<MyItem> newList = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            MyItem item = new MyItem();
            item.gid = i;
            item.stamp = i;
            item.state1 = (i % 2 == 0 ? WON : LOST);
            item.score1 = (int) (Math.random() * 50);
            item.score2 = (int) (Math.random() * 50);
            item.finished = new Date().toString().substring(0, 10);
            item.elo1 = 1500;
            item.elo2 = 1500;
            newList.add(item);
        }

        DiffUtil.DiffResult diffResult = FastAdapterDiffUtil.calculateDiff(mItemAdapter, newList);
        FastAdapterDiffUtil.set(mItemAdapter, diffResult);
    }

    private boolean isAsc() {
        return mItemListImpl.getComparator() == mAscComparator;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected asc=" + isAsc());

        switch (item.getItemId()) {
            case R.id.item_sort_asc:
                mItemListImpl.withComparator(mAscComparator);
                invalidateOptionsMenu();
                return true;
            case R.id.item_sort_desc:
                mItemListImpl.withComparator(mDescComparator);
                 invalidateOptionsMenu();
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu asc=" + isAsc());

        MenuItem ascSort = menu.findItem(R.id.item_sort_asc);
        MenuItem descSort = menu.findItem(R.id.item_sort_desc);
        if (ascSort != null && descSort != null) {
            ascSort.setVisible(!isAsc());
            descSort.setVisible(isAsc());
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu asc=" + isAsc());
        getMenuInflater().inflate(R.menu.my_menu, menu);

        View view = menu.findItem(R.id.search).getActionView();
        if (view instanceof SearchView) {
            SearchView searchView = (SearchView) view;
            searchView.setQueryHint(getString(R.string.insert_player));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    mItemAdapter.filter(s);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    mItemAdapter.filter(s);
                    return true;
                }
            });
        }

        return true;
    }

    private class DateAscComparator implements Comparator<MyItem>, Serializable {
        @Override
        public int compare(MyItem item1, MyItem item2) {
            return Long.compare(item1.stamp, item2.stamp);
        }
    }

    private class DateDescComparator implements Comparator<MyItem>, Serializable {
        @Override
        public int compare(MyItem item1, MyItem item2) {
            return Long.compare(item2.stamp, item1.stamp);
        }
    }
}
