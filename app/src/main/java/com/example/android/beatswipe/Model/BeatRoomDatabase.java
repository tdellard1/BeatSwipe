package com.example.android.beatswipe.Model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Beat.class}, version = 1)
public abstract class BeatRoomDatabase extends RoomDatabase {
    public abstract BeatDao beatDao();

    private static BeatRoomDatabase INSTANCE;

    public static BeatRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BeatRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BeatRoomDatabase.class, "word_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
