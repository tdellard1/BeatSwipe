package com.example.android.beatswipe;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "beat_table",foreignKeys = @ForeignKey(entity = User.class,
        parentColumns = "userId",
        childColumns = "user",
        onDelete = CASCADE))
public class Beat {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo
    private int id;
    private String user;


    @NonNull
    @ColumnInfo(name = "beat")
    private String mBeatUrl;

    @Ignore
    public Beat() {}

    public Beat(@NonNull String beatUrl) { this.mBeatUrl = beatUrl;}

    public String getBeatUrl(){return this.mBeatUrl;}

    public void setBeatUrl(@NonNull String mBeatUrl) {this.mBeatUrl = mBeatUrl;}

    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public String getUser() { return user; }

    public void setUser(String user) { this.user = user; }
}
