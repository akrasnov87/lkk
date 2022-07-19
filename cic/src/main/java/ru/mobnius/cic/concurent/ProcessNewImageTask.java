package ru.mobnius.cic.concurent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Callable;

import ru.mobnius.cic.ui.model.ImageItem;
import ru.mobnius.cic.ui.model.ImageType;
import ru.mobnius.simple_core.utils.BitmapUtil;

/**
 * Класс первичного обработки новой фотографии
 * в дополнительном потоке
 */
public class ProcessNewImageTask implements Callable<ImageItem> {
    @NonNull
    private final String fileName;
    @NonNull
    private final String absFilePath;
    @NonNull
    private final ImageType imageType;
    @NonNull
    private final Location location;

    public ProcessNewImageTask(final @NonNull String fileName,
                               final @NonNull String absFilePath,
                               final @NonNull ImageType imageType,
                               final @NonNull Location location) {
        this.fileName = fileName;
        this.absFilePath = absFilePath;
        this.imageType = imageType;
        this.location = location;
    }

    @Nullable
    @Override
    public ImageItem call() throws Exception {
        BitmapUtil.compressImage(absFilePath, location);
        final Bitmap bitmap = BitmapFactory.decodeFile(absFilePath);
        if (bitmap == null){
            return null;
        }
        return new ImageItem(UUID.randomUUID().toString(), fileName, absFilePath, new Date(), bitmap, imageType, location, false);
    }
}
