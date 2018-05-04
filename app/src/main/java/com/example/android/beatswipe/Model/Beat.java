package com.example.android.beatswipe.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Beat {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @NonNull
    private String beatName;
    private String beatType;
    @NonNull
    private String storageUrl;
    private String imageUrl;
    private String description;

    public Beat(int id, String beatName, String beatType, String storageUrl, String imageUrl, String description) {
        this.id = id;
        this.beatName = beatName;
        this.beatType = beatType;
        this.storageUrl = storageUrl;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBeatName() {
        return beatName;
    }

    public void setBeatName(String beatName) {
        this.beatName = beatName;
    }

    public String getBeatType() {
        return beatType;
    }

    public void setBeatType(String beatType) {
        this.beatType = beatType;
    }

    public String getStorageUrl() {
        return storageUrl;
    }

    public void setStorageUrl(String storageUrl) {
        this.storageUrl = storageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
