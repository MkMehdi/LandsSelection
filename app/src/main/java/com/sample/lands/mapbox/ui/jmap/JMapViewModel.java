package com.sample.lands.mapbox.ui.jmap;

import androidx.lifecycle.ViewModel;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.sample.lands.mapbox.db.AppDatabase;
import com.sample.lands.mapbox.db.model.Land;
import com.sample.lands.mapbox.db.model.Location;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class JMapViewModel extends ViewModel {

    public ArrayList<Location> locations = new ArrayList<>();
    public Land land = new Land();

    public void collectLandPoints(LatLng point){
        Location location = new Location();
        location.setLatitude(point.getLatitude());
        location.setLongitude(point.getLongitude());
        locations.add(location);
    }

    public void saveLand(){
        land.setPoints(locations);
        Executor myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute(() -> AppDatabase.getInstance().landDao().insertAll(land));
    }

    public void clear(){
        locations = new ArrayList<>();
        land = new Land();
    }
}