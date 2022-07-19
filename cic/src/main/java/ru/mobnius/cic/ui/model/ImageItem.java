package ru.mobnius.cic.ui.model;

import android.graphics.Bitmap;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import ru.mobnius.cic.MobniusApplication;
import ru.mobnius.simple_core.utils.StringUtil;


public class ImageItem {
    public static final String PLACEHOLDER_ID = "ru.mobnius.cic.ui.model.PLACEHOLDER_ID";
    public static final String ERROR_ID = "ru.mobnius.cic.ui.model.ERROR_ID";
    @NonNull
    public final String id;
    @NonNull
    public final String absFilePath;
    @NonNull
    public final String fileName;
    @NonNull
    public final Date date;
    @NonNull
    public final Bitmap bitmap;
    @Nullable
    public String resultId;
    @NonNull
    public ImageType imageType;
    @NonNull
    public final Location location;

    public final boolean loadedFromDb;


    public ImageItem(final @NonNull String id,
                     final @NonNull String fileName,
                     final @NonNull String absFilePath,
                     final @NonNull Date date,
                     final @NonNull Bitmap bitmap,
                     final @NonNull ImageType imageType,
                     final @NonNull Location location,
                     final boolean loadedFromDb) {
        this.id = id;
        this.fileName = fileName;
        this.absFilePath = absFilePath;
        this.date = date;
        this.bitmap = bitmap;
        this.imageType = imageType;
        this.location = location;
        this.loadedFromDb = loadedFromDb;
    }

    @NonNull
    public static ImageItem createPlaceholderItem() {
        final Bitmap.Config config = Bitmap.Config.ARGB_8888;
        final Bitmap emptyBitmap = Bitmap.createBitmap(1, 1, config);
        final Calendar calendar = Calendar.getInstance();
        calendar.set(2001, 0, 1);
        return new ImageItem(PLACEHOLDER_ID,
                StringUtil.EMPTY, StringUtil.EMPTY,
                calendar.getTime(),
                emptyBitmap,
                new ImageType(-1L, StringUtil.EMPTY, StringUtil.EMPTY, StringUtil.EMPTY, false),
                new Location(MobniusApplication.APP_NAME), false);
    }

    @NonNull
    public static ImageItem createErrorItem(final @NonNull Bitmap defaultErrorIcon) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(2001, 0, 1);
        return new ImageItem(ERROR_ID,
                StringUtil.EMPTY, StringUtil.EMPTY,
                calendar.getTime(),
                defaultErrorIcon,
                new ImageType(-1L, StringUtil.EMPTY, StringUtil.EMPTY, StringUtil.EMPTY, false),
                new Location(MobniusApplication.APP_NAME), false);
    }

    @NonNull
    public static ImageItem createTestItem(long imageType) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(2001, 0, 1);
        final Bitmap.Config config = Bitmap.Config.ARGB_8888;
        final Bitmap emptyBitmap = Bitmap.createBitmap(1, 1, config);
        return new ImageItem(UUID.randomUUID().toString(),
                StringUtil.EMPTY, StringUtil.EMPTY,
                calendar.getTime(),
                emptyBitmap,
                new ImageType(imageType, StringUtil.EMPTY, StringUtil.EMPTY, StringUtil.EMPTY, false),
                new Location(MobniusApplication.APP_NAME), false);
    }

    public boolean isPlaceholder() {
        return this.id.equals(PLACEHOLDER_ID);
    }

}
