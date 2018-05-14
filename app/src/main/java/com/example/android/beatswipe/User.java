package com.example.android.beatswipe;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;

import java.util.List;

@Entity(tableName = "user")
public class User {
    @PrimaryKey
    @NonNull
    private String userId;    // Uid
    private String name;      // Display Name
    //private List<Beat> beats; // List of producers Beats

    @Ignore
    public User() {}

    public User(@NonNull String userId) {
        this.userId = userId;
    }

    @Ignore
    public User(FirebaseUser user) {
        this.userId = user.getUid();
    }

    /*@Ignore
    public void setName(FirebaseUser user) {
        this.name = user.getDisplayName();
    }*/

    public String getUserId() {
        return userId;
    }

    public void setUserId(FirebaseUser user) {
        this.userId = user.getUid();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
