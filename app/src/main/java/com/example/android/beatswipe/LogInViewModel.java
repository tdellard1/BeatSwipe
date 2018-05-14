package com.example.android.beatswipe;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.support.annotation.NonNull;

public class LogInViewModel extends AndroidViewModel {

    private BeatRepository mRepository;
    private LiveData<User> currentUser;

    public LogInViewModel(@NonNull Application application) {
        super(application);
        mRepository = new BeatRepository(application);
        currentUser = mRepository.getCurrentUser();
    }

    LiveData<User> getCurrentUser() { return currentUser; }


    public void LogInUser(String email, String password) {
        mRepository.LogInUser(email, password);
    }

    public void goToMainActivity(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public void CreateAccountScreen(Activity activity) {
        Intent intent = new Intent(activity, SignUpActivity.class);
        activity.startActivity(intent);
    }
}
