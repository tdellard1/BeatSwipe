package com.example.android.beatswipe.ui.main;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.example.android.beatswipe.data.local.Beat;
import com.example.android.beatswipe.data.local.User;
import com.example.android.beatswipe.utils.BeatRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import static android.support.v4.app.ActivityCompat.startActivityForResult;
import static com.example.android.beatswipe.ui.main.MainActivity.AUDIO_FILE_CODE;

public class BeatViewModel extends AndroidViewModel {

    private BeatRepository mRepository;
    private MainNavigator mainNavigator;
    private LiveData<List<Beat>> mAllBeats;
    private LiveData<List<User>> mAllUsers;
    private LiveData<FirebaseUser> currentUser;

    public ObservableField<String> genre = new ObservableField<>();
    public ObservableField<String> getGenre() {
        return genre;
    }

    public void setMainNavigator(MainNavigator mainNavigator) {
        this.mainNavigator = mainNavigator;
    }

    public void setGenre(ObservableField<String> genre) {
        this.genre = genre;
    }

    public BeatViewModel(@NonNull Application application) {
        super(application);
        mRepository = new BeatRepository(application);
        mAllBeats = mRepository.getAllBeats();
        currentUser = mRepository.getCurrentUser();
        mAllUsers = mRepository.getAllUsers();
    }

    LiveData<List<User>> getAllUsers() { return mAllUsers; }
    LiveData<List<Beat>> getAllBeats() { return mAllBeats; }
    LiveData<FirebaseUser> getCurrentUser() { return currentUser; }

    public void selectBeat(Activity activity) {
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(activity,intent, AUDIO_FILE_CODE,null);
    }

    public void signOutUser() {
        mRepository.signOutUser();
    }

    public void SwipeActivity() {
        mainNavigator.GoToSwipeActivity();
    }

    interface MainNavigator {
        void GoToSwipeActivity();
    }
}
