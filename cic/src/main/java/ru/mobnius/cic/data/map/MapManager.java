package ru.mobnius.cic.data.map;

import static android.os.Looper.getMainLooper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.Iterator;
import java.util.List;

import ru.mobnius.cic.MobniusApplication;
import ru.mobnius.cic.R;
import ru.mobnius.cic.data.manager.DataManager;
import ru.mobnius.cic.ui.model.PointItem;
import ru.mobnius.cic.ui.model.RouteItem;
import ru.mobnius.simple_core.data.GlobalSettings;
import ru.mobnius.simple_core.utils.StringUtil;


/**
 * Класс для работы с картами OSM,
 * не самое лучшее решение, желательно в будущем переделать
 */
public class MapManager implements LocationListener {
    private static final String PRVATE_SERVER_TILE_SOURCE_NAME = "private";
    private static final String IMAGE_FILENAME_ENDING = ".png";

    @NonNull
    private final CicMarkerClusterer markerClusterer;
    @Nullable
    private MapView mapView;
    @Nullable
    private LocationManager locationManager;
    @Nullable
    private GeoPoint manualGeoPoint;
    @Nullable
    private CicMarker previousManualMarker;
    @NonNull
    private final PointClickListener pointClickListener;

    public MapManager(final @NonNull MapView mapView, final @NonNull PointClickListener pointClickListener) {
        this.mapView = mapView;
        this.pointClickListener = pointClickListener;
        final Resources resources = mapView.getContext().getResources();
        final DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        final int inspectorColor = resources.getColor(R.color.map_cluster_inspector);
        final int inspectorLastPointColor = resources.getColor(R.color.map_cluster_inspector_last_point);
        final int doneColor = resources.getColor(R.color.map_cluster_done);
        final int undoneColor = resources.getColor(R.color.map_cluster_undone);
        final float textSize = resources.getDisplayMetrics().density;
        markerClusterer = new CicMarkerClusterer(displayMetrics,
                inspectorColor,
                inspectorLastPointColor,
                doneColor,
                undoneColor,
                textSize);
        markerClusterer.radiusInPixels = 200;
        if (mapView.getContext().getSystemService(Context.LOCATION_SERVICE) instanceof LocationManager) {
            locationManager = (LocationManager) mapView.getContext().getSystemService(Context.LOCATION_SERVICE);
        }
        this.mapView.addMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                double level = event.getZoomLevel();
                markerClusterer.currentZoomLevel = (int) level;
                InfoWindow.closeAllInfoWindowsOn(MapManager.this.mapView);
                return true;
            }
        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
        final List<CicMarker> markers = markerClusterer.items;
        final Iterator<CicMarker> i = markers.iterator();
        while (i.hasNext()) {
            final CicMarker marker = i.next();
            if (marker.isInspectorPoint) {
                i.remove();
            }
        }
        setUserPoint(location, MobniusApplication.CURRENT_LOCATION);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
    }


    public void addOnlineOverlays() {
        if (mapView == null) {
            return;
        }
        final XYTileSource tileSourceBase = new XYTileSource(PRVATE_SERVER_TILE_SOURCE_NAME,
                10,
                18,
                256,
                IMAGE_FILENAME_ENDING,
                new String[]{GlobalSettings.getMapUrl()});
        final MapTileProviderBasic provider = new MapTileProviderBasic(mapView.getContext(), tileSourceBase);
        provider.setOfflineFirst(true);
        final TilesOverlay tilesOverlay = new TilesOverlay(provider, mapView.getContext());
        tilesOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);
        mapView.getOverlays().add(tilesOverlay);
        mapView.invalidate();
    }

    public void setGeoPoint() {
        if (mapView == null) {
            return;
        }
        final Context context = mapView.getContext();
        mapView.getController().setZoom(10d);
        final Handler handler = new Handler(getMainLooper());
        final Thread thread = new Thread(() -> {
            if (DataManager.getInstance() == null) {
                handler.post(() -> Toast.makeText(context, MobniusApplication.ERROR_ENG + DataManager.class.getName(), Toast.LENGTH_SHORT).show());
                return;
            }
            final List<RouteItem> routeItems = DataManager.getInstance().getAllRouteItems();
            if (routeItems.size() == 0) {
                handler.post(() -> Toast.makeText(context, MobniusApplication.NO_AVAILABLE_POINTS, Toast.LENGTH_SHORT).show());
                return;
            }
            handler.post(() -> Toast.makeText(context, MobniusApplication.BUILDING_POINTS_IN_PROCESS, Toast.LENGTH_SHORT).show());
            for (final RouteItem item : routeItems) {
                final List<PointItem> pointItems = DataManager.getInstance().getRoutePointItems(item.id);
                for (final PointItem pointItem : pointItems) {
                    handler.post(() -> {
                        if (pointItem.longitude != 0D && pointItem.latitude != 0D) {
                            try {
                                final GeoPoint point = new GeoPoint(pointItem.latitude, pointItem.longitude);
                                final Drawable drawable = ContextCompat.getDrawable(context, pointItem.done ? R.drawable.ic_done_point_location_24 : R.drawable.ic_undone_point_location_24);
                                final CicMarker marker = new CicMarker(mapView, pointItem.done, false, false);
                                marker.setPosition(point);
                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                marker.setIcon(drawable);
                                marker.setTitle(pointItem.owner);
                                marker.setInfoWindow(new CicInfoWindow(R.layout.cic_info_window_layout,
                                        mapView,
                                        pointItem,
                                        StringUtil.defaultEmptyString(pointItem.accountNumber),
                                        StringUtil.defaultEmptyString(pointItem.owner),
                                        StringUtil.defaultEmptyString(pointItem.deviceTypeName),
                                        StringUtil.defaultEmptyString(pointItem.deviceNumber),
                                        StringUtil.defaultEmptyString(pointItem.address),
                                        pointClickListener));
                                markerClusterer.items.add(marker);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
            handler.post(() -> {
                mapView.getOverlays().add(markerClusterer);
                mapView.invalidate();
            });
        });
        thread.start();
    }

    public void setUserPoint(final @NonNull Location location, final @NonNull String title) {
        if (mapView == null) {
            return;
        }
        final GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        mapView.getController().animateTo(startPoint);
        try {
            final CicMarker userMarker = new CicMarker(mapView, false, false, true);
            userMarker.setPosition(startPoint);
            userMarker.setTitle(title);
            userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            userMarker.setIcon(ContextCompat.getDrawable(mapView.getContext(), R.drawable.ic_user_location_24));
            markerClusterer.items.add(userMarker);
            mapView.getOverlays().add(markerClusterer);
            mapView.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLocation() {
        if (mapView == null) {
            return;
        }
        final Context context = mapView.getContext();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, context.getString(R.string.no_location_permission), Toast.LENGTH_SHORT).show();
            return;
        }
        Location location = null;
        if (locationManager != null) {
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) &&
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
        if (location != null) {
            setUserPoint(location, context.getString(R.string.last_known_location));
        } else {
            Toast.makeText(context, context.getString(R.string.can_not_get_last_location), Toast.LENGTH_SHORT).show();
        }
        boolean canRequestLocation = false;
        if (locationManager != null) {
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
                canRequestLocation = true;
            }
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
                canRequestLocation = true;
            }
        }
        if (!canRequestLocation) {
            Toast.makeText(context, context.getString(R.string.location_updates_disabled), Toast.LENGTH_SHORT).show();
        }
    }

    public void buildOnlinePoints() {
        setLocation();
        setGeoPoint();
    }

    public void setManualGeoPoint(final @NonNull GeoPoint geoPoint) {
        if (mapView == null) {
            return;
        }
        manualGeoPoint = geoPoint;
        mapView.getController().animateTo(geoPoint);
        try {
            final CicMarker userMarker = new CicMarker(mapView, false, false, true);
            userMarker.setPosition(geoPoint);
            userMarker.setTitle(MobniusApplication.LOCATION_SET_MANUALLY);
            userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            userMarker.setIcon(ContextCompat.getDrawable(mapView.getContext(), R.drawable.ic_undone_point_location_24));
            if (previousManualMarker != null) {
                mapView.getOverlays().remove(previousManualMarker);
            }
            previousManualMarker = userMarker;
            mapView.getOverlays().add(userMarker);
            mapView.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public GeoPoint getManualGeoPoint() {
        if (manualGeoPoint != null) {
            return manualGeoPoint;
        }
        final Location location = getLastKnownLocation();
        if (location == null) {
            return null;
        }
        return new GeoPoint(location.getLatitude(), location.getLongitude());
    }

    @Nullable
    private Location getLastKnownLocation() {
        if (mapView == null) {
            return null;
        }
        final Context context = mapView.getContext();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context,
                    context.getString(R.string.no_location_permission), Toast.LENGTH_SHORT).show();
            return null;
        }
        Location location = null;
        if (locationManager == null) {
            return null;
        }

        if (locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        return location;
    }

    public void destroy() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
        mapView = null;
    }
}
