package com.example.bitmesra;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Transaction;

import java.io.Serializable;
@Entity
public class Task implements Serializable{

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name="latitude")
    private String latitude;

    @ColumnInfo(name="longitude")
    private String longitude;

    @ColumnInfo(name="signal")
    private int signal;

 public Task()
 {

 }


    public void setLatitude(String latitude)
    {
        this.latitude=latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public int getSignal() {
        return signal;
    }

    public int getId() {
        return id;
    }
}
