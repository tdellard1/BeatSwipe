package com.example.android.beatswipe;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

@Database(entities = {Beat.class, User.class}, version = 5)
public abstract class BeatRoomDatabase extends RoomDatabase {

    public abstract BeatDao beatDao();
    public abstract UserDao userDao();

    private static BeatRoomDatabase INSTANCE;

    public static BeatRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BeatRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                           BeatRoomDatabase.class, "beat_database").fallbackToDestructiveMigration().addCallback(mRoomDatabaseCallback)
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

        private final BeatDao bDao;
        private final UserDao uDao;

        PopulateDbAsync(BeatRoomDatabase db) {
            bDao = db.beatDao();
            uDao = db.userDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            return null;
        }
    }

}
