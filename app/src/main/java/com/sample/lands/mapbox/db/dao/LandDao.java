package com.sample.lands.mapbox.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.sample.lands.mapbox.db.model.Land;

import java.util.List;

@Dao
public interface LandDao {
    @Query("SELECT * FROM land")
    List<Land> getAll();

    @Insert
    void insertAll(Land... lands);

    @Query("DELETE FROM land")
    void deleteAll();
}
