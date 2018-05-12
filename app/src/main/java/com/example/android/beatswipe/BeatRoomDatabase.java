package com.example.android.beatswipe;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

@Database(entities = {Beat.class}, version = 4)
public abstract class BeatRoomDatabase extends RoomDatabase {

    public abstract BeatDao beatDao();

    private static BeatRoomDatabase INSTANCE;

    public static BeatRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BeatRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                           BeatRoomDatabase.class, "word_database").fallbackToDestructiveMigration().addCallback(mRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback mRoomDatabaseCallback = new RoomDatabase.Callback(){

        @Override
        public void onOpen (@NonNull SupportSQLiteDatabase db){
            super.onOpen(db);
            new PopulateDbAsync(INSTANCE).execute();
        }
    };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final BeatDao mDao;

        PopulateDbAsync(BeatRoomDatabase db) {
            mDao = db.beatDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            return null;
        }
    }

}
