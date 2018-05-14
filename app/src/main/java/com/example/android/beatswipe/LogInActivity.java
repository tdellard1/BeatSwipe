package com.example.android.beatswipe;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LogInActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button logInButton, createAccountButton;
    private LogInViewModel logInViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        emailEditText = findViewById(R.id.et_email_login);
        passwordEditText = findViewById(R.id.et_password_login);
        logInButton = findViewById(R.id.btn_login);
        createAccountButton = findViewById(R.id.btn_create_account_from_login);

        logInViewModel = ViewModelProviders.of(this).get(LogInViewModel.class);

        logInViewModel.getCurrentUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    logInViewModel.goToMainActivity(LogInActivity.this);
                }
            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInViewModel.LogInUser(emailEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInViewModel.CreateAccountScreen(LogInActivity.this);
            }
        });
    }
}
