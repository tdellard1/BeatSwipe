package com.example.android.beatswipe.data.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface BeatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Beat beat);

    @Query("DELETE FROM beat_table")
    void deleteAll();

    @Query("SELECT * FROM beat_table")
    LiveData<List<Beat>> getAllBeats();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Beat[] params);

    @Delete
    void deleteBeat(Beat beat);

    @Update
    void updateBeat(Beat beat);
}