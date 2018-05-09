package com.example.android.beatswipe;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class BeatViewModel extends AndroidViewModel {

    private BeatRepository mRepository;

    private LiveData<List<Beat>> mAllBeats;

    public BeatViewModel(Application application) {
        super(application);
        mRepository = new BeatRepository(application);
        mAllBeats = mRepository.getAllBeats();
    }

    LiveData<List<Beat>> getAllBeats() { return mAllBeats; }

    public void insert(Beat beat) { mRepository.insert(beat); }
}
