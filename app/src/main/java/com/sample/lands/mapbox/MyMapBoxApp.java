package com.sample.lands.mapbox;

import android.app.Application;

import com.mapbox.mapboxsdk.Mapbox;
import com.sample.lands.R;

public class MyMapBoxApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Mapbox.getInstance(getApplicationContext(), getString(R.string.mapbox_access_token));

    }
}
