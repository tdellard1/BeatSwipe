package com.example.android.beatswipe.ui.signup;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.beatswipe.ui.login.LogInActivity;
import com.example.android.beatswipe.ui.main.MainActivity;
import com.example.android.beatswipe.R;
import com.example.android.beatswipe.databinding.SignUpActivityBinding;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

import static com.example.android.beatswipe.data.remote.FirebaseService.ErrorCode;
import static com.example.android.beatswipe.data.remote.FirebaseService.Error;
import static com.example.android.beatswipe.utils.ErrorConstants.CREATE_ACCOUNT_ERROR;

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
                signUpViewModel.loadAllBeats();
                signUpViewModel.loadAllUsers();
                goToMainActivity();
            }
        });
        signUpViewModel.getSignUpErrorMessage().observe(this, signUpViewModel::handleError);

    }

    @Override
    public void LogInClicked() {
        startActivity(LogInActivity.LogInIntent(this));
        finish();
    }

    @Override
    public void goToMainActivity() {
        startActivity(MainActivity.MainIntent(this));
        finish();
    }
}
