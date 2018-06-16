package com.example.android.beatswipe.ui.login;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.PropertyChangeRegistry;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;

import com.example.android.beatswipe.BR;
import com.example.android.beatswipe.utils.BeatRepository;
import com.google.firebase.auth.FirebaseUser;

public class LogInViewModel extends AndroidViewModel implements Observable, TextWatcher {

    private BeatRepository mRepository;
    private LiveData<FirebaseUser> currentUser;
    private LiveData<String> loginErrorMessage;
    private ObservableField<String> loginError = new ObservableField<>();
    private ObservableField<String> email = new ObservableField<>();
    private ObservableField<String> password = new ObservableField<>();
    private PropertyChangeRegistry registry = new PropertyChangeRegistry();
    private ObservableBoolean taskOnGoing = new ObservableBoolean(false);
    private com.example.android.beatswipe.ui.ui.login.LogInNavigator logInNavigator;


    @Bindable
    public ObservableField<String> getLoginError() { return loginError; }

    public ObservableField<String> getEmail() { return email; }

    public ObservableField<String> getPassword() { return password; }
    @Bindable
    public ObservableBoolean getTaskOnGoing() { return taskOnGoing; }

    private com.example.android.beatswipe.ui.ui.login.LogInNavigator getLogInNavigator() {
        return logInNavigator;
    }

    public LiveData<String> getLoginErrorMessage() { return loginErrorMessage;  }

    LiveData<FirebaseUser> getCurrentUser() { return currentUser; }



    private void setLoginError(String message) {
        this.loginError.set(message);
        registry.notifyChange(this, BR.loginError);
    }

    public void setEmail(ObservableField<String> email) { this.email = email; }

    public void setPassword(ObservableField<String> password) { this.password = password;}


    private void setTaskOnGoing(boolean taskOnGoing) {
        this.taskOnGoing.set(taskOnGoing);
        registry.notifyChange(this, BR.taskOnGoing);
    }

    public void setLogInNavigator(com.example.android.beatswipe.ui.ui.login.LogInNavigator logInNavigator) { this.logInNavigator = logInNavigator; }

    public LogInViewModel(@NonNull Application application) {
        super(application);
        mRepository = new BeatRepository(application);
        currentUser = mRepository.getCurrentUser();
        loginErrorMessage = mRepository.getLoginErrorMessage();
    }

    public void handleError(String s) {
        setTaskOnGoing(false);
        setLoginError(s);
    }

    public void LogInUser() {
        try {
            if (email.get().trim().isEmpty() || password.get().trim().isEmpty()){
                setLoginError("Email And Password Fields Must Be Filled Out");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email.get().trim()).matches()){
                setLoginError("Not A Valid Email Format");
            } else {
                mRepository.LogInUser(email.get(), password.get());
                setTaskOnGoing(true);
            }
        } catch (NullPointerException e) {
            setLoginError("Email And Password Fields Must Be Filled Out");
        }
    }

    public void SignUp() {
        getLogInNavigator().SignUpClicked();
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        registry.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        registry.remove(callback);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        setLoginError("");
    }
}
