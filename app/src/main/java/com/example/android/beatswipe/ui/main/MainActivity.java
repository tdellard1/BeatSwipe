package com.example.android.beatswipe.ui.main;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.beatswipe.R;
import com.example.android.beatswipe.ui.profile.ProfileFragment;
import com.example.android.beatswipe.ui.swipe.SwipeActivity;
import com.example.android.beatswipe.ui.upload.UploadFileFragment;
import com.example.android.beatswipe.databinding.MainActivityBinding;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, MainNavigator{

    public static Intent MainIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    private BeatViewModel beatViewModel;
    public static final int READ_REQUEST_CODE = 42;
    private FirebaseUser currentUser;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beatViewModel = ViewModelProviders.of(this).get(BeatViewModel.class);
        MainActivityBinding mainActivityBinding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        mainActivityBinding.setBeatViewModel(beatViewModel);
        beatViewModel.setMainNavigator(this);
        init();
        RegisterObservers();
        /**
         * Can't get Spinner Data Binding Working
         */
        setUpSpinner();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && requestCode == READ_REQUEST_CODE) {
            loadUploadFragment(data.getData());
        } else {
            Toast.makeText(MainActivity.this, "File Not Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUploadFragment(Uri fileUri) {
        UploadFileFragment uploadFileFragment = new UploadFileFragment();
        uploadFileFragment.setFileUri(fileUri);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.profile_frag, uploadFileFragment, UploadFileFragment.class.getSimpleName()).addToBackStack(null).commitAllowingStateLoss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_upload:
                beatViewModel.selectBeat(this);
                return(true);

            case R.id.menu_profile:
                loadUserFragment();
                return (true);

            case R.id.menu_logout:
                beatViewModel.signOutUser();
                return (true);
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadUserFragment() {
        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setUid(currentUser.getUid());
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.profile_frag, profileFragment).addToBackStack(null).commit();
    }



    private void RegisterObservers() {
        beatViewModel.getCurrentUser().observe(this, user -> {
            if (user != null)
            {
                currentUser = user;
            } else {
                beatViewModel.SignInPage(MainActivity.this);
            }
        });
        beatViewModel.getAllBeats().observe(this, beats -> {
            if (beats != null && beats.size() > 0) {}
        });
        beatViewModel.getAllUsers().observe(this, users -> Toast.makeText(MainActivity.this, "Users: " + Integer.toString(users.size()), Toast.LENGTH_SHORT).show());
    }

    public void init() {
        if (!(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
        }
    }

    public void setUpSpinner() {
        spinner = findViewById(R.id.genre_spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.genre, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        beatViewModel.genre.set(parent.getItemAtPosition(position).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void GoToSwipeActivity() {
        Intent intent = new Intent(this, SwipeActivity.class);
        intent.putExtra("GENRE", beatViewModel.genre.get());
        startActivity(intent);
    }
}
