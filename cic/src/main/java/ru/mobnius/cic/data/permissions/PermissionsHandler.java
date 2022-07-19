package ru.mobnius.cic.data.permissions;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import ru.mobnius.cic.MobniusApplication;

/**
 * Класс помощник для работы с разрешениями приложения
 */
public class PermissionsHandler {
    @NotNull
    public final HashMap<String, String> permissionsNames;
    @NotNull
    public final String[] permissionsArray;

    public PermissionsHandler() {
        permissionsNames = new HashMap<>();

        permissionsNames.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, MobniusApplication.STORAGE);
        permissionsNames.put(Manifest.permission.ACCESS_FINE_LOCATION, MobniusApplication.LOCATION);
        permissionsNames.put(Manifest.permission.ACCESS_COARSE_LOCATION, MobniusApplication.LOCATION);
        permissionsNames.put(Manifest.permission.READ_PHONE_STATE, MobniusApplication.CONTACTS);
        permissionsNames.put(Manifest.permission.CAMERA, MobniusApplication.CAMERA);
        permissionsNames.put(Manifest.permission.RECORD_AUDIO, MobniusApplication.RECORD_AUDIO);

        permissionsArray = new String[5];
        permissionsArray[0] = (Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionsArray[1] = (Manifest.permission.ACCESS_FINE_LOCATION);
        permissionsArray[2] = (Manifest.permission.READ_PHONE_STATE);
        permissionsArray[3] = (Manifest.permission.CAMERA);
        permissionsArray[4] = (Manifest.permission.RECORD_AUDIO);
    }

    public boolean isFileAndCameraPermissionGranted(final @NotNull FragmentActivity fragmentActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (fragmentActivity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || fragmentActivity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {

                return false;
            }
        }
        return ActivityCompat.checkSelfPermission(fragmentActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(fragmentActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isLocationPermissionGranted(final @NotNull FragmentActivity fragmentActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (fragmentActivity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) || fragmentActivity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                return false;
            }

        }
        return ActivityCompat.checkSelfPermission(fragmentActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(fragmentActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

}
