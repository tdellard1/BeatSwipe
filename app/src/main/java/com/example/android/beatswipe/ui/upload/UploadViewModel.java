package com.example.android.beatswipe.ui.upload;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.ObservableField;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.example.android.beatswipe.utils.BeatRepository;
import com.example.android.beatswipe.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class UploadViewModel extends AndroidViewModel{

    public static final String GENRE = "genre";
    public static final String FILE_URL = "fileurl";
    public static final String PURCHASE_LINK = "link";
    public static final String AUDIO_FILE_NAME = "audiofilename";
    public static final String AUDIO_FILE_URL = "audiofileurl";
    public static final String IMAGE_FILE_NAME = "imagefilename";
    public static final String IMAGE_FILE_URL = "imagefileurl";



    public static UploadViewModel getInstance(Fragment fragment, Uri AudioFileUri, String AudioName, UploadHandler handler) {
        UploadViewModel viewModel = ViewModelProviders.of(fragment).get(UploadViewModel.class);
        viewModel.AudioFileUri = AudioFileUri;
        viewModel.mHandler = handler;
        viewModel.AudioFileName = Utils.toString(AudioName);
        viewModel.AudioDisplayName.set(Utils.toString(AudioName));
        return viewModel;
    }

    public void setImageFileUri(Uri imageFileUri) {
        ImageFileUri = imageFileUri;
    }

    public void setImageFileName(String imageFileName) {
        ImageFileName = imageFileName;
    }

    private Uri AudioFileUri;
    private Uri ImageFileUri;
    private String AudioFileName;
    private String ImageFileName;
    private UploadHandler mHandler;
    private LiveData<Double> progress;
    private BeatRepository mRepository;
    public ObservableField<String> name = new ObservableField<>();
    public ObservableField<String> link = new ObservableField<>();
    public ObservableField<String> genre = new ObservableField<>();
    public ObservableField<String> AudioDisplayName = new ObservableField<>();
    public ObservableField<String> required = new ObservableField<>();

    public UploadViewModel(@NonNull Application application) {
        super(application);
        mRepository = new BeatRepository(application);
        progress = mRepository.getProgress();
    }

    /**
     * Plays current audio that is intended to be uploaded.
     */
    public void playAudio() { mHandler.mediaPlayer(); }

    /**
     * Called when user has finished filling out all information about their intended upload.
     *
     * Creates a Map object and puts all the necessary <Beat> model parameters minus userId that
     * supplies beat_owner(beat gets beat_owner parameter in firebase api class to avoid errors)
     */
    public void uploadFile() {
        Map<String, String> FileMap = new HashMap<>();
        if (genre.get() != null) {
            String suppliedAudioName = (name.get() == null) ? AudioFileName : name.get();
            FileMap.put(AUDIO_FILE_NAME, Utils.toString(suppliedAudioName));
            FileMap.put(AUDIO_FILE_URL, AudioFileUri.toString());
            FileMap.put(IMAGE_FILE_NAME, Utils.toString(ImageFileName));
            FileMap.put(IMAGE_FILE_URL, ImageFileUri.toString());
            FileMap.put(GENRE, Utils.toString(genre.get().trim()));
            FileMap.put(PURCHASE_LINK, link.get().trim());
            mRepository.uploadFile(FileMap);
            } else {
            required.set("Required");
        }
    }

    /**
     * Used to get and transfer upload progress to Upload Fragment to set progress bar
     * @return
     */
    public LiveData<Double> getProgress() { return progress; }

    /**
     * UploadHandler interface is used to get a reference to context and/or fragment from he
     * data bound view model in order to perform tasks that require one or the other.
     */

    public interface UploadHandler {
        /**
         * goBack() method is used to get the fragment manager from the
         * original activity to remove the upload fragment from the stack
         */
        void goBack();

        /**
         * mediaPlayer() method used from ViewModel to get start mediaPlayer
         * (media player requires context because it is playing from uri and not url)
         */
        void mediaPlayer();
    }
}
