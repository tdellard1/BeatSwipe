package com.example.android.beatswipe.ui.signup;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.beatswipe.ui.login.LogInActivity;
import com.example.android.beatswipe.ui.main.MainActivity;
import com.example.android.beatswipe.R;
import com.example.android.beatswipe.databinding.SignUpActivityBinding;

public class SignUpActivity extends AppCompatActivity implements com.example.android.beatswipe.ui.signup.SignUpNavigator {

    public static Intent SignUpIntent(Context context) {
        return new Intent(context, SignUpActivity.class);
    }
    private SignUpViewModel signUpViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpViewModel = ViewModelProviders.of(this).get(SignUpViewModel.class);
        signUpViewModel.setSignUpNavigator(this);
        SignUpActivityBinding mSignUpActivityBinding = DataBindingUtil.setContentView(this, R.layout.sign_up_activity);
        mSignUpActivityBinding.setSignupViewModel(signUpViewModel);
        signUpViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                goToMainActivity();
            }
        });
        signUpViewModel.getSignUpErrorMessage().observe(this, signUpViewModel::handleError);

    }

    @Override
    public void LogInClicked() {
        startActivity(LogInActivity.getLogInActivityIntent(this));
        finish();
    }

    @Override
    public void goToMainActivity() {
        startActivity(MainActivity.getMainActivityIntent(this));
        finish();
    }
}
