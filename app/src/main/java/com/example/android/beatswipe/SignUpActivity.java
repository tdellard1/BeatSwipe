package com.example.android.beatswipe;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private TextView errorText;
    private EditText emailEditText, nameEditText, passwordEditText;
    private Button createAccountButton, logInButton;
    private SignUpViewModel signUpViewModel;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        signUpViewModel = ViewModelProviders.of(this).get(SignUpViewModel.class);

        signUpViewModel.getCurrentUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    signUpViewModel.goToMainActivity(SignUpActivity.this);
                }
            }
        });

        errorText = findViewById(R.id.tv_error);
        emailEditText = findViewById(R.id.et_email);
        nameEditText = findViewById(R.id.et_name);
        passwordEditText = findViewById(R.id.et_password);
        createAccountButton = findViewById(R.id.btn_create_account);
        logInButton = findViewById(R.id.btn_login_from_create_account);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpViewModel.createAccount(nameEditText.getText().toString(),emailEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpViewModel.LogInScreen(SignUpActivity.this);
            }
        });
    }
}
