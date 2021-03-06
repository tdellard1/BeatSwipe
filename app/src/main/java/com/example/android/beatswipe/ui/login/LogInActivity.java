package com.example.android.beatswipe.ui.login;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.beatswipe.ui.main.MainActivity;
import com.example.android.beatswipe.R;
import com.example.android.beatswipe.databinding.ActivityLogInBinding;
import com.example.android.beatswipe.ui.signup.SignUpActivity;

public class LogInActivity extends AppCompatActivity implements com.example.android.beatswipe.ui.ui.login.LogInNavigator {

    public static Intent getLogInActivityIntent(Context context) {
        return new Intent(context, LogInActivity.class);
    }

    private LogInViewModel logInViewModel;
    private ActivityLogInBinding mActivityLogInBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        logInViewModel = ViewModelProviders.of(this).get(LogInViewModel.class);
        logInViewModel.setLogInNavigator(this);
        mActivityLogInBinding = DataBindingUtil.setContentView(this, R.layout.activity_log_in);
        mActivityLogInBinding.setLoginViewModel(logInViewModel);
        logInViewModel.getLoginErrorMessage().observe(this, s -> {
            if (s != null) {
                logInViewModel.handleError(s);
            }
        });
        logInViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                goToMainActivity();
            }
        });


    }

    @Override
    public void SignUpClicked() {
        startActivity(SignUpActivity.SignUpIntent(this));
        finish();
    }

    @Override
    public void goToMainActivity() {
        startActivity(MainActivity.getMainActivityIntent(this));
        finish();
    }


}
