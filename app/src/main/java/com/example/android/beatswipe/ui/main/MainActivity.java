package com.example.android.beatswipe.ui.main;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.beatswipe.R;
import com.example.android.beatswipe.ui.login.LogInActivity;
import com.example.android.beatswipe.ui.profile.ProfileFragment;
import com.example.android.beatswipe.ui.swipe.SwipeActivity;
import com.example.android.beatswipe.ui.upload.UploadFileFragment;
import com.example.android.beatswipe.databinding.MainActivityBinding;
import com.example.android.beatswipe.utils.Utils;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

import static com.example.android.beatswipe.ui.profile.ProfileFragment.PROFILE_FRAGMENT_TAG;
import static com.example.android.beatswipe.utils.Utils.testRef;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, BeatViewModel.MainNavigator{

    public static final int AUDIO_FILE_CODE = 2;
    public static final int IMAGE_FILE_CODE = 1;
    private Menu menu;
    private MainActivityBinding mainActivityBinding;
    private BeatViewModel mBeatViewModel;
    private FirebaseUser mCurrentUser;

    public static Intent getMainActivityIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBeatViewModel = ViewModelProviders.of(this).get(BeatViewModel.class);
        mainActivityBinding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        mainActivityBinding.setBeatViewModel(mBeatViewModel);
        mBeatViewModel.setMainNavigator(this);
        init();
        RegisterObservers();
        /**
         * Can't get Spinner Data Binding Working
         */
        setUpSpinner();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mBeatViewModel.genre.set(parent.getItemAtPosition(position).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void GoToSwipeActivity() {
        startActivity(SwipeActivity.getSwipeIntent(this, mBeatViewModel.genre.get()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && requestCode == AUDIO_FILE_CODE) {
            UploadFragment(data.getData());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_upload:
                selectBeat();
                return(true);

            case R.id.menu_profile:
                ProfileFragment();
                return (true);

            case R.id.menu_logout:
                mBeatViewModel.signOutUser();
                return (true);
        }
        return super.onOptionsItemSelected(item);
    }

    private void UploadFragment(Uri AudioFileUri) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.profile_frag, UploadFileFragment.newInstance(AudioFileUri), PROFILE_FRAGMENT_TAG).addToBackStack(null).commitAllowingStateLoss();
        MenuItem item = menu.findItem(R.id.menu_upload);
        item.setVisible(false);
    }

    private void ProfileFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.profile_frag, ProfileFragment.newInstance(mCurrentUser.getUid(), null)).addToBackStack(null).commit();
        MenuItem item = menu.findItem(R.id.menu_profile);
        item.setVisible(false);
    }

    private void RegisterObservers() {
        mBeatViewModel.getCurrentUser().observe(this, user -> {
            if (user != null)
            {
                mCurrentUser = user;
            } else {
                startActivity(LogInActivity.getLogInActivityIntent(this));
                finish();
            }
        });
    }

    public void init() {
        if (!(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
        }
    }

    public void setUpSpinner() {
        Spinner spinner = findViewById(R.id.genre_spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.genre, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void selectBeat() {
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, AUDIO_FILE_CODE);
    }
}
