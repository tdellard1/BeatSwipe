package com.example.android.beatswipe;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "beat_table")
public class Beat {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo
    private int id;


    @NonNull
    @ColumnInfo(name = "beat")
    private String mBeatUrl;

    public Beat(@NonNull String beatUrl) { this.mBeatUrl = beatUrl;}

    public String getBeatUrl(){return this.mBeatUrl;}

    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }
}
