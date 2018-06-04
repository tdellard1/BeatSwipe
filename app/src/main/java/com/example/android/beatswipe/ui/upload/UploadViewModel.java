package com.example.android.beatswipe.ui.upload;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.databinding.ObservableField;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.android.beatswipe.data.local.User;
import com.example.android.beatswipe.utils.BeatRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

import static com.example.android.beatswipe.ui.upload.UploadFileFragment.GENRE;
import static com.example.android.beatswipe.ui.upload.UploadFileFragment.NAME;
import static com.example.android.beatswipe.ui.upload.UploadFileFragment.URL;

public class UploadViewModel extends AndroidViewModel{

    private Uri fileUri;
    private UploadHandler mHandler;
    private LiveData<Double> progress;
    private BeatRepository mRepository;
    public ObservableField<String> name = new ObservableField<>();
    public ObservableField<String> genre = new ObservableField<>();
    public ObservableField<String> uriName = new ObservableField<>();
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
     * Requires genre field to be supplied but not name field unless the default name contains
     * forbidden characters.
     *
     * Creates a Map object and puts all the necessary <Beat> model parameters minus userId that
     * supplies beat_owner(beat gets beat_owner parameter in firebase api class to avoid errors)
     *
     * ToDo - Escape or prevent characters: "." "#" "$" "[" or "]"
     */
    public void uploadFile() {
        Map<String, String> FileMap = new HashMap<>();
        if (genre.get() != null) {
            if (name.get() == null) {
                FileMap.put(NAME, mHandler.getUriName());
            } else {
                FileMap.put(NAME, name.get().trim());
            }
            FileMap.put(GENRE, genre.get().trim());
            FileMap.put(URL, fileUri.toString());
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
     * Gets a Uri and sets it to the fileUri field in order to be sent off in uploadFile() method
     * @param fileUri
     */
    public void setFileUri(Uri fileUri) { this.fileUri = fileUri; }

    public void setUriName(ObservableField<String> uriName) { this.uriName = uriName; }

    public void setUploadHandler(UploadHandler mHandler) { this.mHandler = mHandler; }
}
