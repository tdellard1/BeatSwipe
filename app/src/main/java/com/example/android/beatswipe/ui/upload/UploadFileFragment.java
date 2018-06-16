package com.example.android.beatswipe.ui.upload;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.beatswipe.R;
import com.example.android.beatswipe.databinding.UploadFileFragmentBinding;
import com.example.android.beatswipe.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import static com.example.android.beatswipe.ui.main.MainActivity.IMAGE_FILE_CODE;

public class UploadFileFragment extends Fragment implements UploadViewModel.UploadHandler {

    private static final String AUDIO_FILE_URI = "FILE_URI";


    public static final String FIREBASE_AUDIO_URL = "firebaseaudiourl";
    public static final String FIREBASE_IMAGE_URL = "firebaseimageurl";

    private UploadFileFragmentBinding mUploadFragmentBinging;
    private UploadViewModel uploadViewModel;
    private Uri AudioFileUri;
    public Uri ImageFileUri;

    public UploadFileFragment() {}

    public static UploadFileFragment newInstance(@NonNull Uri AudioFileUri) {
        UploadFileFragment fragment = new UploadFileFragment();
        Bundle args = new Bundle();
        args.putParcelable(AUDIO_FILE_URI, AudioFileUri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDetach() {
        Activity activity = getActivity();
        if (activity != null) {
            activity.invalidateOptionsMenu();
        }
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            AudioFileUri = getArguments().getParcelable(AUDIO_FILE_URI);
        } catch (NullPointerException e) {
            goBack();
        }
        uploadViewModel = UploadViewModel.getInstance(this, AudioFileUri,
                Utils.getDisplayName(getActivity(), AudioFileUri), this);
        mUploadFragmentBinging = DataBindingUtil.inflate(inflater, R.layout.upload_file_fragment, container, false);
        mUploadFragmentBinging.setViewModel(uploadViewModel);
        View view = mUploadFragmentBinging.getRoot();
        uploadViewModel.getProgress().observe(this, aDouble -> {
            try {
                if (aDouble >= 0.0 && aDouble < 100.0 ) {
                    NumberFormat nf = new DecimalFormat("#");
                    String progress = nf.format(aDouble);
                    mUploadFragmentBinging.progressbar.setProgress(Integer.parseInt(progress));
                } else if (aDouble == 100.0) {
                    goBack();
                }
            } catch (NullPointerException e) {
                // Nothing To Show
                e.printStackTrace();
            }
        });
        selectPicture();
        return view;
    }

    private void selectPicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_FILE_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Picasso.get().load(data.getData()).into(mUploadFragmentBinging.imageView);
            uploadViewModel.setImageFileUri(data.getData());
            uploadViewModel.setImageFileName(Utils.getDisplayName(getActivity(), data.getData()));
        } else {
            goBack();
        }
    }

    /**
     * goBack() interface implemented method used to navigate back to previous activity/fragment by
     * removing this current fragment from the FragmentManager Stack.
     */
    @Override
    public void goBack() {
        //getActivity().getSupportFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
        getActivity().getSupportFragmentManager().popBackStack();
    }

    /**
     * mediaPlayer() used in order to play media file of file that is intended to be uploaded...
     * More fore verification purposes than anything.
     */
    @Override
    public void mediaPlayer() {
        MediaPlayer mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mPlayer.setDataSource(getActivity().getBaseContext(), AudioFileUri);
            mPlayer.prepare();
        } catch (IOException | NullPointerException npio) {
           npio.printStackTrace();
        }
        mPlayer.start();
    }
}
