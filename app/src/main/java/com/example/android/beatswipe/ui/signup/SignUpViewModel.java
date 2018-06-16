package com.example.android.beatswipe.ui.signup;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.PropertyChangeRegistry;
import android.support.annotation.NonNull;
import android.util.Patterns;

import com.example.android.beatswipe.BR;
import com.example.android.beatswipe.utils.BeatRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class SignUpViewModel extends AndroidViewModel implements Observable{

    public static final String USERNAME = "name";
    public static final String USEREMAIL = "email";
    public static final String USERPASSWORD = "password";

    
    private BeatRepository mRepository;
    private PropertyChangeRegistry registry = new PropertyChangeRegistry();
    private ObservableField<String> signUpError = new ObservableField<>();
    private com.example.android.beatswipe.ui.signup.SignUpNavigator signUpNavigator;
    private ObservableField<String> name = new ObservableField<>();
    private ObservableField<String> email = new ObservableField<>();
    private ObservableField<String> password = new ObservableField<>();
    private ObservableBoolean taskOnGoing = new ObservableBoolean(false);
    private LiveData<String> signUpErrorMessage;


    @Bindable
    public ObservableBoolean getTaskOnGoing() { return taskOnGoing; }

    private void setTaskOnGoing(boolean taskOnGoing) {
        this.taskOnGoing.set(taskOnGoing);
        registry.notifyChange(this, BR.taskOnGoing);
    }


    @Bindable
    public ObservableField<String> getSignUpError() {
        return signUpError;
    }

    private void setSignUpError(String error) {
        this.signUpError.set(error);
        registry.notifyChange(this, BR.signUpError);
    }

    private LiveData<FirebaseUser> currentUser;

    public LiveData<String> getSignUpErrorMessage() { return signUpErrorMessage;  }

    private com.example.android.beatswipe.ui.signup.SignUpNavigator getSignUpNavigator() {
        return signUpNavigator;
    }

    public void setSignUpNavigator(com.example.android.beatswipe.ui.signup.SignUpNavigator signUpNavigator) {
        this.signUpNavigator = signUpNavigator;
    }


    public ObservableField<String> getName() {
        return name;
    }

    public ObservableField<String> getEmail() {
        return email;
    }

    public ObservableField<String> getPassword() {
        return password;
    }

    public void setName(ObservableField<String> name) {
        this.name = name;
    }

    public void setEmail(ObservableField<String> email) {
        this.email = email;
    }

    public void setPassword(ObservableField<String> password) {
        this.password = password;
    }

    public SignUpViewModel(@NonNull Application application) {
        super(application);
        mRepository = new BeatRepository(application);
        currentUser = mRepository.getCurrentUser();
        signUpErrorMessage = mRepository.getSignUpErrorMessage();
    }

    LiveData<FirebaseUser> getCurrentUser() { return currentUser; }

    public void createAccount() {
        try {
            if (name.get().trim().isEmpty() || email.get().trim().isEmpty() || password.get().trim().isEmpty()) {
                setSignUpError("Text Field Empty");
            } else if(!Patterns.EMAIL_ADDRESS.matcher(email.get().trim()).matches()) {
                setSignUpError("Incorrect Email Format");
            } else {
                Map<String, String> UserMap = new HashMap<>();
                UserMap.put(USERNAME, name.get());
                UserMap.put(USEREMAIL, email.get());
                UserMap.put(USERPASSWORD, password.get());
                mRepository.createAccount(UserMap);
                setTaskOnGoing(true);
            }
        } catch (NullPointerException e) {
            setSignUpError("Please Fill Out All Fields");
        }
    }

    public void login() {
        getSignUpNavigator().LogInClicked();
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        registry.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        registry.remove(callback);
    }

    public void handleError(String s) {
        setTaskOnGoing(false);
        setSignUpError(s);
    }
}
