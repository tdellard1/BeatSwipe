package com.example.android.beatswipe;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.List;

import static android.support.v4.app.ActivityCompat.startActivityForResult;
import static com.example.android.beatswipe.MainActivity.READ_REQUEST_CODE;

public class BeatViewModel extends AndroidViewModel {

    private BeatRepository mRepository;

    private LiveData<List<Beat>> mAllBeats;
    private LiveData<User> currentUser;

    public BeatViewModel(@NonNull Application application) {
        super(application);
        mRepository = new BeatRepository(application);
        mAllBeats = mRepository.getAllBeats();
        currentUser = mRepository.getCurrentUser();
    }

    LiveData<List<Beat>> getAllBeats() { return mAllBeats; }

    LiveData<User> getCurrentUser() { return currentUser; }

    public void insert(Beat beat) { mRepository.insert(beat); }

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

    public void signOutUser() {
        mRepository.signOutUser();
    }

    public void SignInPage(Activity activity) {
        Intent intent = new Intent(activity, LogInActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }
}
