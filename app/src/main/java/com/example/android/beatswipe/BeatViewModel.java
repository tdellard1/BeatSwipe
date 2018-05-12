package com.example.android.beatswipe;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.net.Uri;

import java.util.List;

import static android.support.v4.app.ActivityCompat.startActivityForResult;
import static com.example.android.beatswipe.MainActivity.READ_REQUEST_CODE;

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

    public void loadBeat() {
        //mRepository.beatUrlFromDatabaseToRoom();
    }

    public void selectBeat(Activity activity) {
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(activity,intent,READ_REQUEST_CODE,null);
    }

    public void uploadFile(Activity activity, Uri audioUri) {
        String displayName = Utils.getDisplayName(activity, audioUri);
        mRepository.uploadFile(displayName, audioUri);

    }
}
