package com.example.android.beatswipe.data.local;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Entity(tableName = "beat_table", indices = {@Index("beat_owner")})
public class Beat {


    @ColumnInfo(name = "beat_name")
    public String name;
    @NonNull
    public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }


    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "beat_url")
    public String url;
    @NonNull
    public String getUrl() { return url; }
    public void setUrl(@NonNull String url) { this.url = url; }


    @Nullable
    @ColumnInfo(name = "beat_genre")
    public String genre;
    @Nullable
    public String getGenre() { return genre; }
    public void setGenre(@Nullable String genre) { this.genre = genre; }


    // uid of user who uploaded this beat
    @ColumnInfo(name = "beat_owner")
    private String owner;
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    @Ignore
    public Beat() {}

    public Beat(String name, String url,String owner, String genre) {
        this.name = name;
        this.url = url;
        this.owner = owner;
        this.genre = genre;
    }
}
