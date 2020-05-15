package com.example.bitmesra;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
@Entity
public class Task implements Serializable{

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name="latitude")
    private String latitude;

    @ColumnInfo(name="longitude")
    private String longitude;

    @ColumnInfo(name="signal")
    private int signal;

    Task() {
    }

    String getLatitude() {
        return latitude;
    }

    void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    String getLongitude() {
        return longitude;
    }

    void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    int getSignal() {
        return signal;
    }

    void setSignal(int signal) {
        this.signal = signal;
    }

    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }
}
