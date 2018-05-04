package com.example.android.beatswipe.Model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface BeatDao {

    // @Query("SELECT * FROM beat WHERE beatType LIKE :typeBeat")


    @Query("SELECT * FROM beat")
    LiveData<List<Beat>> getTypeBeat(/* String typeBeat */ );
}
