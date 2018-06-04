package com.example.android.beatswipe.ui.login;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import com.example.android.beatswipe.ui.main.MainActivity;
import com.example.android.beatswipe.R;
import com.example.android.beatswipe.data.local.User;
import com.example.android.beatswipe.databinding.ActivityLogInBinding;
import com.example.android.beatswipe.ui.signup.SignUpActivity;
import com.example.android.beatswipe.utils.ErrorConstants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

public class LogInActivity extends AppCompatActivity implements com.example.android.beatswipe.ui.ui.login.LogInNavigator {

    public static Intent LogInIntent(Context context) {
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
                logInViewModel.loadAddBeats();
                logInViewModel.loadAllUsers();
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
        startActivity(MainActivity.MainIntent(this));
        finish();
    }


}
