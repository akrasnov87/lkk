package ru.mobnius.cic.ui.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.huawei.hms.api.HuaweiApiAvailability;

import java.util.List;

import ru.mobnius.cic.MobniusApplication;
import ru.mobnius.cic.R;
import ru.mobnius.cic.data.permissions.PermissionsHandler;
import ru.mobnius.cic.ui.fragment.RationalLocationPermissionDialog;
import ru.mobnius.cic.ui.uiutils.ThemeUtil;
import ru.mobnius.cic.ui.viewmodels.MainViewModel;
import ru.mobnius.simple_core.data.configuration.PreferencesManager;
import ru.mobnius.simple_core.preferences.GeneralPreferences;
import ru.mobnius.simple_core.utils.AlertDialogUtil;
import ru.mobnius.simple_core.utils.DoubleUtil;
import ru.mobnius.simple_core.utils.LongUtil;
import ru.mobnius.simple_core.utils.StringUtil;

public class MainActivity extends AppCompatActivity {
    @NonNull
    private Location location = new Location(MobniusApplication.APP_NAME);
    @Nullable
    private MainViewModel viewModel;
    @Nullable
    private FusedLocationProviderClient client;
    @Nullable
    private LocationCallback locationCallback;

    @NonNull
    private final ActivityResultLauncher<String[]> getPermissisons = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
            permissions -> {
                if (permissions.containsKey(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    final Boolean locationGrantedOrNull = permissions.get(Manifest.permission.ACCESS_FINE_LOCATION);
                    if (locationGrantedOrNull == null) {
                        return;
                    }
                    if (!locationGrantedOrNull) {
                        showRationalLocationPermissionAlert();
                    } else {
                        handleLocationUpdates();
                    }
                }
            });

    public final PermissionsHandler permissionsHandler = new PermissionsHandler();

    @Override
    protected void onStart() {
        super.onStart();
        ThemeUtil.changeColor(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        if (client == null) {
            client = LocationServices.getFusedLocationProviderClient(this);
        }
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        final NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.auth_nav_host);
        if (navHostFragment == null) {
            return;
        }
        final NavController navController = navHostFragment.getNavController();
        final NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.nav_graph);
        if (PreferencesManager.getInstance() != null && PreferencesManager.getInstance().isPin() &&
                GeneralPreferences.getInstance() != null && GeneralPreferences.getInstance().isSingleUser()) {
            navGraph.setStartDestination(R.id.enter_pin_code_nav);
        } else {
            navGraph.setStartDestination(R.id.auth_nav);
        }
        navController.setGraph(navGraph);
        if (permissionsHandler.isLocationPermissionGranted(this)) {
            getPermissisons.launch(permissionsHandler.permissionsArray);
        } else {
            showLocationPermissionAlert();
        }

    }

    public void showLocationPermissionAlert() {
        final AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setPositiveButton(getString(R.string.confirm), (dialog, which) -> getPermissisons.launch(permissionsHandler.permissionsArray));
        final AlertDialog alert = adb.create();
        alert.setTitle(getString(R.string.attention));
        alert.setMessage(getString(R.string.location_updates_permission_necessary_message));
        alert.setCancelable(true);
        alert.show();
    }

    public void showRationalLocationPermissionAlert() {
        final RationalLocationPermissionDialog dialog = new RationalLocationPermissionDialog();
        dialog.show(getSupportFragmentManager(), RationalLocationPermissionDialog.RATIONAL_LOCATION_DIALOG_TAG);
    }

    public void handleLocationUpdates() {
        if (viewModel == null) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            viewModel.isCheckingLocationUpdates = false;
            AlertDialogUtil.alert(this,
                    getString(R.string.no_location_permission), getString(R.string.good), (dialog, which) -> {
                        try {
                            final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            startActivity(intent);
                        } catch (ActivityNotFoundException | SecurityException e) {
                            Toast.makeText(MainActivity.this, StringUtil.defaultEmptyString(e.getMessage()), Toast.LENGTH_SHORT).show();
                        }
                    }, null, null);
            return;
        }
        if (isLocationDisabled()) {
            viewModel.isCheckingLocationUpdates = false;
            return;
        }

        if (isLocationServicesNotAvailable()) {
            viewModel.isCheckingLocationUpdates = false;
            return;
        }
        if (viewModel.isCheckingLocationUpdates) {
            return;
        }
        if (client == null) {
            client = LocationServices.getFusedLocationProviderClient(this);
        }
        if (isEmptyLocation()) {
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (isEmptyLocation() && task.getResult() != null) {
                        location = task.getResult();
                    }
                }
            });

            client.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
                @NonNull
                @Override
                public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                    return this;
                }

                @Override
                public boolean isCancellationRequested() {
                    return false;
                }
            }).addOnCompleteListener(task -> {
                if (task.getResult() != null) {
                    location = task.getResult();
                }
            });
        }
        final LocationRequest locationRequest = LocationRequest.create().setInterval(LongUtil.ZERO).setFastestInterval(LongUtil.ZERO);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                final List<Location> locations = locationResult.getLocations();
                if (locations.size() > 0) {
                    location = locations.get(locations.size() - 1);
                } else {
                    if (locationResult.getLastLocation() != null) {
                        location = locationResult.getLastLocation();
                    }
                }
            }
        };

        client.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        viewModel.isCheckingLocationUpdates = true;
    }


    @NonNull
    public Location getLocation() {
        return location;
    }

    public boolean isLocationEnabled() {
        if (viewModel == null) {
            return false;
        }
        return viewModel.isCheckingLocationUpdates;
    }

    private boolean isLocationServicesNotAvailable() {
        final int gmsResult = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        final int hmsResult = HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(this);
        boolean hmsAvailable = (com.huawei.hms.api.ConnectionResult.SUCCESS == hmsResult);
        boolean gmsAvailable = (com.google.android.gms.common.ConnectionResult.SUCCESS == gmsResult);
        if (hmsAvailable || gmsAvailable) {
            return false;
        }
        AlertDialogUtil.alert(this, getString(R.string.location_services_not_available),
                getString(R.string.good), (dialog, which) -> {
                    try {
                        final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    } catch (ActivityNotFoundException | SecurityException e) {
                        Toast.makeText(MainActivity.this, StringUtil.defaultEmptyString(e.getMessage()), Toast.LENGTH_SHORT).show();
                    }
                }, null, null);
        return true;
    }

    private boolean isLocationDisabled() {
        if (!(getSystemService(Context.LOCATION_SERVICE) instanceof LocationManager)) {
            return true;
        }
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;
        boolean networkEnabled = false;

        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gpsEnabled || networkEnabled) {
            return false;
        }
        AlertDialogUtil.alert(this, getString(R.string.location_updates_not_available),
                getString(R.string.good), (dialog, which) -> {
                    try {
                        final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    } catch (ActivityNotFoundException | SecurityException e) {
                        Toast.makeText(MainActivity.this, StringUtil.defaultEmptyString(e.getMessage()), Toast.LENGTH_SHORT).show();
                    }
                }, null, null);
        return true;
    }

    private boolean isEmptyLocation() {
        boolean zeroLocation = location.getLatitude() == DoubleUtil.ZERO && location.getLongitude() == DoubleUtil.ZERO;
        return zeroLocation || StringUtil.equalsIgnoreCase(location.getProvider(), MobniusApplication.APP_NAME);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (client == null || locationCallback == null || viewModel == null) {
            return;
        }
        client.removeLocationUpdates(locationCallback);
        viewModel.isCheckingLocationUpdates = false;
    }
}
