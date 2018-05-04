package com.example.android.beatswipe.Model;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class BeatRepository {
    private BeatDao mbeatDao;
    private LiveData<List<Beat>> mAllBeats;

    BeatRepository(Application application) {
        BeatRoomDatabase db = BeatRoomDatabase.getDatabase(application);
        mbeatDao = db.beatDao();
        mAllBeats = mbeatDao.getTypeBeat();
    }

    LiveData<List<Beat>>getAllBeats(){
        return mAllBeats;
    }


}
