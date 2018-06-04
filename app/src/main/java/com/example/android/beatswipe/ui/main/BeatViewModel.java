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
import com.example.android.beatswipe.ui.login.LogInActivity;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import static android.support.v4.app.ActivityCompat.startActivityForResult;
import static com.example.android.beatswipe.ui.main.MainActivity.READ_REQUEST_CODE;

public class BeatViewModel extends AndroidViewModel {

    private BeatRepository mRepository;
    private LiveData<List<Beat>> mAllBeats;
    private LiveData<List<User>> mAllUsers;
    private LiveData<FirebaseUser> currentUser;
    private LiveData<Double> progress;
    private MainNavigator mainNavigator;
    public ObservableField<String> genre = new ObservableField<>();

    public LiveData<Double> getProgress() {
        return progress;
    }

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
        progress = mRepository.getProgress();
        mAllUsers = mRepository.getAllUsers();
    }

    LiveData<List<User>> getAllUsers() { return mAllUsers; }
    LiveData<List<Beat>> getAllBeats() { return mAllBeats; }
    LiveData<FirebaseUser> getCurrentUser() { return currentUser; }

    public void delete() { mRepository.deleteBeats(); }

    public void selectBeat(Activity activity) {
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(activity,intent,READ_REQUEST_CODE,null);
    }

    public void signOutUser() {
        mRepository.signOutUser();
    }

    public void SignInPage(Activity activity) {
        Intent intent = new Intent(activity, LogInActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public void testQuery() {
        mainNavigator.GoToSwipeActivity();
    }
}
