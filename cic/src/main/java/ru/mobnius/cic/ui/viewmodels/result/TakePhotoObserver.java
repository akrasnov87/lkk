package ru.mobnius.cic.ui.viewmodels.result;

import android.net.Uri;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public class TakePhotoObserver implements DefaultLifecycleObserver {
    public static final String TAKE_PHOTO_RESULT_KEY = "ru.mobnius.cic.ui.viewmodels.TAKE_PHOTO_RESULT_KEY";

    private final ActivityResultRegistry registry;
    private ActivityResultLauncher<Uri> takePhotoLauncher;
    private final ActivityResultCallback<Boolean> callback;

    public TakePhotoObserver(final @NonNull ActivityResultRegistry registry, final ActivityResultCallback<Boolean> callback) {
        this.registry = registry;
        this.callback = callback;
    }

    public void onCreate(@NonNull LifecycleOwner owner) {
        takePhotoLauncher = registry.register(TAKE_PHOTO_RESULT_KEY, owner, new ActivityResultContracts.TakePicture(), callback);
    }

    public void takePhoto(final @NonNull Uri uri) {
        takePhotoLauncher.launch(uri);
    }
}
