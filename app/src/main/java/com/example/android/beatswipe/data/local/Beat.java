package com.example.android.beatswipe.data.local;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

@Entity(tableName = "beat_table", indices = {@Index("beat_owner")})
public class Beat {

    @Ignore
    public static final String GENRE = "genre";
    @Ignore
    public static final String FILE_URL = "fileurl";
    @Ignore
    public static final String PURCHASE_LINK = "link";
    @Ignore
    public static final String AUDIO_FILE_NAME = "audiofilename";
    @Ignore
    public static final String AUDIO_FILE_URL = "audiofileurl";
    @Ignore
    public static final String IMAGE_FILE_NAME = "imagefilename";
    @Ignore
    public static final String IMAGE_FILE_URL = "imagefileurl";
    @Ignore
    public static final String UID = "uid";









    @ColumnInfo(name = "beat_name")
    public String name;
    @NonNull
    public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }


    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "beat_audio_url")
    public String audioUrl;
    @NonNull
    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(@NonNull String audio_url) { this.audioUrl = audio_url; }

    @ColumnInfo(name = "beat_image_url")
    public String imageUrl;
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(@NonNull String image_url) { this.imageUrl = image_url; }


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

    @ColumnInfo(name = "purchase_link")
    private String link;
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    @Ignore
    public Beat() {}

    public Beat(String name, String audioUrl,String owner, String genre, String link,@Nullable String imageUrl) {
        this.name = name;
        this.audioUrl = audioUrl;
        this.owner = owner;
        this.genre = genre;
        this.link = link;
        this.imageUrl = imageUrl;
    }

    @Ignore
    public static Map<String, String> toMap(Beat beat) {
        HashMap<String, String> result = new HashMap<>();
        if (beat.getOwner() != null) result.put(UID, beat.getOwner());
        result.put(AUDIO_FILE_NAME, beat.getName());
        result.put(AUDIO_FILE_URL, beat.getAudioUrl());
        if (beat.getGenre() != null) result.put(GENRE, beat.getGenre());
        if (beat.getLink() != null) result.put(PURCHASE_LINK, beat.getLink());
        if (beat.getImageUrl() != null) result.put(IMAGE_FILE_URL, beat.getImageUrl());
        return result;
    }

    @Ignore
    public static Beat toBeat(Map beatMap) {
        Beat beat = new Beat();
        if (beatMap.get(UID) != null) beat.setOwner((String) beatMap.get(UID));
        if (beatMap.get(AUDIO_FILE_NAME) != null) beat.setName((String) beatMap.get(AUDIO_FILE_NAME));
        if (beatMap.get(AUDIO_FILE_URL) != null) beat.setAudioUrl((String) beatMap.get(AUDIO_FILE_URL));
        if (beatMap.get(GENRE) != null) beat.setGenre((String) beatMap.get(GENRE));
        if (beatMap.get(PURCHASE_LINK) != null) beat.setLink((String) beatMap.get(PURCHASE_LINK));
        if (beatMap.get(IMAGE_FILE_URL) != null) beat.setImageUrl((String) beatMap.get(IMAGE_FILE_URL));
        return beat;
    }
}
