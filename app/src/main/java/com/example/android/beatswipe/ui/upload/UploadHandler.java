package com.example.android.beatswipe.ui.upload;

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

    /**
     * Class used to access context in order to get the Uri file's original name
     *
     * @return string of the uploaded file's name
     */
    String getUriName();
}
