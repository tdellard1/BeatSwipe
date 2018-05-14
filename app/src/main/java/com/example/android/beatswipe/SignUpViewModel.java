package com.example.android.beatswipe;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import static android.support.v4.content.ContextCompat.startActivity;

public class SignUpViewModel extends AndroidViewModel {

    private BeatRepository mRepository;
    private LiveData<User> currentUser;

    public SignUpViewModel(@NonNull Application application) {
        super(application);
        mRepository = new BeatRepository(application);
        currentUser = mRepository.getCurrentUser();
    }

    LiveData<User> getCurrentUser() { return currentUser; }

    public void createAccount(String name, String email, String password) {
        mRepository.createAccount(name, email, password);
    }

    public void goToMainActivity(SignUpActivity signUpActivity) {
        Intent intent = new Intent(signUpActivity, MainActivity.class);
        signUpActivity.startActivity(intent);
        signUpActivity.finish();
    }

    public void LogInScreen(Activity activity) {
        Intent intent = new Intent(activity, LogInActivity.class);
        activity.startActivity(intent);
    }
}
