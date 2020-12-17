package com.sample.lands.mapbox.ui.lands;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sample.lands.mapbox.db.AppDatabase;
import com.sample.lands.mapbox.db.model.Land;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LandListViewModel extends ViewModel {

    public LiveData<List<Land>> getLands(){
        MutableLiveData<List<Land>> lands = new MutableLiveData();
        Executor myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute(() -> lands.postValue(AppDatabase.getInstance().landDao().getAll()));
        return lands;
    }
}