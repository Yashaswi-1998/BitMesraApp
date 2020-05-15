package com.example.bitmesra;

import androidx.room.Dao;
import androidx.room.Insert;
@Dao
public interface TaskDao {

    @Insert
    void insert(Task task);

}
