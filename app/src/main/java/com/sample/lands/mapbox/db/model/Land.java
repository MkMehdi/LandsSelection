package com.sample.lands.mapbox.db.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;


@Entity
public class Land implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private ArrayList<Location> points;

    public Land() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Location> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Location> points) {
        this.points = points;
    }
}
