package com.example.android.beatswipe;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

public class BeatRepository {

    private BeatDao mBeatDao;
    private LiveData<List<Beat>> mAllBeats;

    BeatRepository(Application application) {
        BeatRoomDatabase db = BeatRoomDatabase.getDatabase(application);
        mBeatDao = db.beatDao();
        mAllBeats = mBeatDao.getAllBeats();
    }

    LiveData<List<Beat>> getAllBeats() { return mAllBeats; }

    public void insert(Beat beat) { new insertAsyncTask(mBeatDao).execute(beat); }

    private static class insertAsyncTask extends AsyncTask<Beat, Void, Void> {

        private BeatDao mAsyncTaskDao;

        public insertAsyncTask(BeatDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Beat... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }

    }

    public void beatUrlFromDatabaseToRoom() {
        new DbToRoomUrlTask(mBeatDao).execute();
        /*FirebaseDatabaseService fds = new FirebaseDatabaseService();
        String Url = "https://firebasestorage.googleapis.com/v0/b/beatswipe-10a20.appspot.com/o/April%2013th.mp3?alt=media&token=d5837af3-9b2e-446a-9372-a5b193bde9aa";
        Beat beat = new Beat(Url);
        mBeatDao.insert(beat);*/
    }

    private static class DbToRoomUrlTask extends AsyncTask<Void, Void, Void> {

        BeatDao mBeatDao;

        public DbToRoomUrlTask(BeatDao beatDao) {
            this.mBeatDao = beatDao;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            FirebaseDatabaseService fds = new FirebaseDatabaseService();
            //String Url = "https://firebasestorage.googleapis.com/v0/b/beatswipe-10a20.appspot.com/o/April%2013th.mp3?alt=media&token=d5837af3-9b2e-446a-9372-a5b193bde9aa";
            String Url = fds.getBeatUrl();
            Log.d("Link Retrieve", "?" + Url + "?");
            Beat beat = new Beat(Url);
            mBeatDao.insert(beat);
            return null;
        }
    }

}
