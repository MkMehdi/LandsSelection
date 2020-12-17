package com.sample.lands.mapbox.db;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sample.lands.mapbox.db.model.Location;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListConverter {
    @TypeConverter
    public String fromValuesToList(ArrayList<Location> value) {
        if (value== null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Location>>() {}.getType();
        return gson.toJson(value, type);
    }

    @TypeConverter
    public ArrayList<Location> toOptionValuesList(String value) {
        if (value== null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Location>>() {
        }.getType();
        return gson.fromJson(value, type);
    }
}
