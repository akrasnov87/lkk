package ru.mobnius.simple_core.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Location;

import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;
import androidx.annotation.NonNull;

import java.io.FileOutputStream;
import java.io.IOException;
import ru.mobnius.simple_core.BaseApp;

public class BitmapUtil {

    public final static String IMAGE_TYPE = ".jpeg";

    @SuppressWarnings("UnusedAssignment")
    public static void compressImage(final @NonNull String filePath,
                                     final @NonNull Location location) {

        Bitmap scaledBitmap;
        rotate(filePath);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);//Необходимо для заполнения options.outHeight и options.outWidth
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        final float maxHeight = 1600.0f;
        final float maxWidth = 960.0f;
        float imgRatio = (float) actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[16 * 1024];
        Bitmap signed;
        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
            signed = signBitmap(bmp, location);
        } catch (IllegalArgumentException | OutOfMemoryError e) {
            e.printStackTrace();
            return;
        }

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (IllegalArgumentException | OutOfMemoryError e) {
            e.printStackTrace();
            return;
        }
        final float ratioX = actualWidth / (float) options.outWidth;
        final float ratioY = actualHeight / (float) options.outHeight;
        final float middleX = actualWidth / 2.0f;
        final float middleY = actualHeight / 2.0f;

        final Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        final Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        final float leftPoint = middleX -  (signed.getWidth() >> 1);
        final float topPoint = middleY -  (signed.getHeight() >> 1);

        canvas.drawBitmap(signed, leftPoint, topPoint, new Paint(Paint.FILTER_BITMAP_FLAG));

        try (final FileOutputStream out = new FileOutputStream(filePath)) {
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void rotate(final @NonNull String filePath) {
        final Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        if (bitmap == null) {
            return;
        }
        ExifInterface ei;
        try {
            ei = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
        Bitmap rotatedBitmap = null;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = getImageRotated(bitmap, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = getImageRotated(bitmap, 180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = getImageRotated(bitmap, 270);
                break;
        }
        if (rotatedBitmap == null) {
            return;
        }
        try (final FileOutputStream out = new FileOutputStream(filePath)) {
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private static Bitmap getImageRotated(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                    matrix, true);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;

        }
    }

    private static int calculateInSampleSize(final @NonNull BitmapFactory.Options options,
                                            final int reqWidth,
                                            final int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = Math.min(heightRatio, widthRatio);
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    private static Bitmap signBitmap(final @NonNull Bitmap background,
                                    final @NonNull Location location) {
        final Bitmap rotatedBackground = background.copy(Bitmap.Config.ARGB_8888, true);
        final Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        final int fontSize = 40;
        paint.setTextSize(fontSize);
        final String dateString = DateUtil.getDateStringForUser();
        final Canvas canvas = new Canvas(rotatedBackground);
        canvas.drawText(dateString, 100, rotatedBackground.getHeight() - 40, paint);
        String latitude = String.valueOf(location.getLatitude());
        if (latitude.length() > 6) {
            latitude = latitude.substring(0, 6);
        }
        String longitude = String.valueOf(location.getLongitude());
        if (longitude.length() > 6) {
            longitude = longitude.substring(0, 6);
        }
        final String coordinates = String.format(BaseApp.COORDINATES, latitude, longitude);
        if (StringUtil.isNotEmpty(coordinates)) {
            canvas.drawText(coordinates, 100, rotatedBackground.getHeight() - 40 - fontSize, paint);
        }
        return rotatedBackground;
    }


}
