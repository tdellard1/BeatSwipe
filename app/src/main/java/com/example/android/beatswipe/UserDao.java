package com.example.android.beatswipe;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
/*
 *
 * COME UP WITH A WAY TO HAVE ONLY ONE USER THERE AT A TIME
 *
 */



@Dao
public interface UserDao {

    @Update
    void getAllUsers(User user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Delete
    void removeUser(User user);

    @Query("DELETE FROM user")
    void deleteAll();

    @Query("SELECT * FROM user")
    LiveData<User> getCurrentUser();
}
