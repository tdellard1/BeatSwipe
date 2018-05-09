package com.example.android.beatswipe;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface BeatDao {

    @Insert
    void insert(Beat beat);

    @Query("DELETE FROM beat_table")
    void deleteAll();

    @Query("SELECT * from beat_table")
    LiveData<List<Beat>> getAllBeats();
}