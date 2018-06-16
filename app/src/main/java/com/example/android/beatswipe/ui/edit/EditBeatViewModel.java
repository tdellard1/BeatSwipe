package com.example.android.beatswipe.ui.edit;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.ObservableField;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.example.android.beatswipe.data.local.Beat;
import com.example.android.beatswipe.utils.BeatRepository;
import com.example.android.beatswipe.utils.Utils;

import java.util.HashMap;
import java.util.Map;

import static com.example.android.beatswipe.ui.upload.UploadViewModel.AUDIO_FILE_NAME;
import static com.example.android.beatswipe.ui.upload.UploadViewModel.AUDIO_FILE_URL;
import static com.example.android.beatswipe.ui.upload.UploadViewModel.GENRE;
import static com.example.android.beatswipe.ui.upload.UploadViewModel.IMAGE_FILE_NAME;
import static com.example.android.beatswipe.ui.upload.UploadViewModel.IMAGE_FILE_URL;
import static com.example.android.beatswipe.ui.upload.UploadViewModel.PURCHASE_LINK;

public class EditBeatViewModel extends AndroidViewModel {

    private EditBeatHandler handler;
    private Beat originalBeat;
    private String originalName;
    private Uri ImageFileUri;
    private String ImageFileName;
    private LiveData<Double> progress;
    private BeatRepository mRepository;
    public ObservableField<String> name = new ObservableField<>();
    public ObservableField<String> link = new ObservableField<>();
    public ObservableField<String> genre = new ObservableField<>();

    public static EditBeatViewModel getInstance(Fragment frag, Beat beat, EditBeatHandler handler) {
        EditBeatViewModel viewModel = ViewModelProviders.of(frag).get(EditBeatViewModel.class);
        viewModel.setOriginalProperties(beat);
        viewModel.setHandler(handler);
        return viewModel;
    }

    private void setOriginalProperties(@NonNull Beat beat) {
        this.originalBeat = beat;
        this.originalName = beat.getName();
        this.name.set(beat.getName());
        this.genre.set(beat.getGenre());
        this.link.set(beat.getLink());
    }

    public LiveData<Double> getProgress() { return progress; }

    public void setImageFileName(String imageFileName) {
        ImageFileName = imageFileName;
    }

    public void setImageFileUri(Uri imageFileUri) {
        ImageFileUri = imageFileUri;
    }

    private void setHandler(EditBeatHandler mHandler) {
        this.handler = mHandler;
    }

    public EditBeatViewModel(@NonNull Application application) {
        super(application);
        mRepository = new BeatRepository(application);
        progress = mRepository.getProgress();
    }

    public void finished() {
        if (name.get().isEmpty() || genre.get().isEmpty() || link.get().isEmpty()) {
            handler.deleteBeat();
        } else {
            Map<String, String> FileMap = new HashMap<>();
            FileMap.put(AUDIO_FILE_NAME, Utils.toString(name.get()));
            FileMap.put(AUDIO_FILE_URL, originalBeat.getAudioUrl());
            FileMap.put(IMAGE_FILE_NAME, Utils.toString(ImageFileName));
            FileMap.put(IMAGE_FILE_URL, ImageFileUri.toString());
            FileMap.put(GENRE, Utils.toString(genre.get().trim()));
            FileMap.put(PURCHASE_LINK, link.get().trim());
            mRepository.updateBeat(originalBeat, FileMap);
        }
    }

    public void deleteBeats() { mRepository.deleteUserBeatFromRemoteDatabase(originalName);}

    public void selectImage() {

        handler.selectImage();
        /*Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        handler.startSelectImage(intent);*/
    }

    public interface EditBeatHandler {
        void goBack();
        void deleteBeat();
        void selectImage();
    }
}
