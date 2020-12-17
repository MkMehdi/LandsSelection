package com.sample.lands.mapbox.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.sample.lands.mapbox.db.dao.LandDao;
import com.sample.lands.mapbox.db.model.Land;
import com.sample.lands.mapbox.db.model.Location;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

@Database(entities = {Location.class,Land.class}, version = 1,exportSchema = false)
@TypeConverters({ListConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract LandDao landDao();

    private static AppDatabase appDatabase;

    public static AppDatabase getInstance() {
        if (null == appDatabase) {
            appDatabase = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "database-name").build();
        }
        return appDatabase;
    }

    public void cleanUp(){
        appDatabase = null;
    }

}