package com.example.android.beatswipe.ui.individualbeat;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.example.android.beatswipe.data.local.Beat;
import com.example.android.beatswipe.utils.BeatRepository;

import java.util.Objects;

public class EditBeatViewModel extends AndroidViewModel {

    private EditBeatHandler handler;
    private Beat originalBeat;
    private String originalName;
    private String originalGenre;
    private BeatRepository mRepository;
    public ObservableField<String> name = new ObservableField<>();
    public ObservableField<String> genre = new ObservableField<>();

    public void setOriginalProperties(Beat beat) {
        this.originalBeat = beat;
        this.originalName = beat.getName();
        this.originalGenre = beat.getGenre();
        this.name.set(beat.getName());
        this.genre.set(beat.getGenre());

    }

    public void setHandler(EditBeatHandler mHandler) {
        this.handler = mHandler;
    }

    public EditBeatViewModel(@NonNull Application application) {
        super(application);
        mRepository = new BeatRepository(application);
    }

    public void finished() {
        /*if ((originalName.equals(name.get()) && originalGenre.equals(genre.get())) || (name.get().isEmpty() && genre.get().isEmpty())) {
            handler.goBack();
        } else{*/
            originalBeat.setGenre(genre.get());
            originalBeat.setName(Objects.requireNonNull(name.get()));
            mRepository.updateBeat(originalBeat);
            handler.goBack();
        //}
    }
}
