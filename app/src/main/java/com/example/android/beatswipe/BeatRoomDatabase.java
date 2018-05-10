package com.example.android.beatswipe;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

@Database(entities = {Beat.class}, version = 2)
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
            /*mDao.deleteAll();
            Beat beat = new Beat("https://firebasestorage.googleapis.com/v0/b/beatswipe-10a20.appspot.com/o/April%2013th.mp3?alt=media&token=d5837af3-9b2e-446a-9372-a5b193bde9aa");
            mDao.insert(beat);*/
            return null;
        }
    }

}
