package de.afarber.topplayers;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class TopViewModel extends AndroidViewModel {

    private LiveData<List<TopEntity>> mTops;

    public TopViewModel(Application app) {
        super(app);
        mTops = TopDatabase.getInstance(app).topDao().fetchTops();
    }

    public LiveData<List<TopEntity>> getTops() {
        return mTops;
    }
}
