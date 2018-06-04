package com.example.android.beatswipe.data.local;

import android.arch.persistence.room.ColumnInfo;
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
    @ColumnInfo(name = "user_id")
    private String userId;

    @NonNull
    @ColumnInfo(name = "user_name")
    private String name;

    @NonNull
    @ColumnInfo(name = "user_email")
    private String email;
    //private List<Beat> beats; // List of producers Beats

    @Ignore
    public User() {}

    public User(@NonNull String userId) {
        this.userId = userId;
    }

    @Ignore
    public User(FirebaseUser user) {
        this.userId = user.getUid();
        this.email = user.getEmail();
        if (user.getDisplayName() != null) {
            this.name = user.getDisplayName();
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String uid) {
        this.userId = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
