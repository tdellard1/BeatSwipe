package com.example.android.beatswipe.ui.upload;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
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

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;

public class UploadFileFragment extends Fragment implements UploadHandler {

    public static final String GENRE = "genre";
    public static final String NAME = "name";
    public static final String URL = "url";
    public static final String UID = "uid";

    private UploadFileFragmentBinding mUploadFragmentBinging;
    private Uri fileUri;

    public void setFileUri(Uri fileUri) { this.fileUri = fileUri; }

    public UploadFileFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        UploadViewModel uploadViewModel = ViewModelProviders.of(this).get(UploadViewModel.class);
        uploadViewModel.setFileUri(fileUri);
        uploadViewModel.setUriName(new ObservableField<>(Utils.getDisplayName(getActivity(), fileUri)));
        uploadViewModel.setUploadHandler(this);
        mUploadFragmentBinging = DataBindingUtil.inflate(inflater, R.layout.upload_file_fragment, container, false);
        mUploadFragmentBinging.setViewModel(uploadViewModel);
        View view = mUploadFragmentBinging.getRoot();
        uploadViewModel.getProgress().observe(this, new Observer<Double>() {
            @Override
            public void onChanged(@Nullable Double aDouble) {
                if (aDouble == null) {
                    return;
                } else {
                    if (aDouble >= 0.0 && aDouble < 100.0 ) {
                        NumberFormat nf = new DecimalFormat("#");
                        String progress = nf.format(aDouble);
                        mUploadFragmentBinging.progressbar.setProgress(Integer.parseInt(progress));
                    } else if (aDouble == 100.0) {
                        goBack();
                    }
                }
            }
        });
        return view;
    }


    /**
     * goBack() interface implemented method used to navigate back to previous activity/fragment by
     * removing this current fragment from the FragmentManager Stack.
     */
    @Override
    public void goBack() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
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
            mPlayer.setDataSource(getActivity().getBaseContext(), fileUri);
            mPlayer.prepare();
        } catch (IOException | NullPointerException npio) {
           npio.printStackTrace();
        }
        mPlayer.start();
    }

    @Override
    public String getUriName() {
        return Utils.getDisplayName(Objects.requireNonNull(getActivity()), fileUri);
    }
}
