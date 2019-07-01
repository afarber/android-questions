package de.afarber.vehicles;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MapsViewModel extends AndroidViewModel {
    private LiveData<List<Poi>> mVehicles;

    public MapsViewModel(Application app) {
        super(app);
        mVehicles = PoiDatabase.getDao(app).getVehicles();
    }

    public LiveData<List<Poi>> getVehicles() {
        return mVehicles;
    }
}
