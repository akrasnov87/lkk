package ru.mobnius.cic.data.map;

import android.graphics.Canvas;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.osmdroid.bonuspack.clustering.StaticCluster;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;


/**
 * Базовый класс для отрисовки кластеров на карте OSM
 */
public abstract class BaseMarkerClusterer extends Overlay {

    protected static final int FORCE_CLUSTERING = -1;
    @NonNull
    public final ArrayList<CicMarker> items = new ArrayList<>();
    @NonNull
    protected ArrayList<StaticCluster> clusters = new ArrayList<>();
    @Nullable
    protected String name;
    public int currentZoomLevel;
    protected int lastZoomLevel;

    @NonNull
    public abstract ArrayList<StaticCluster> clusterer(final @NonNull MapView mapView);

    @NonNull
    public abstract Marker buildClusterMarker(final @NonNull StaticCluster cluster,
                                              final @NonNull MapView mapView);

    public abstract void renderer(final @NonNull ArrayList<StaticCluster> clusters,
                                  final @NonNull Canvas canvas,
                                  final @NonNull MapView mapView);

    public BaseMarkerClusterer() {
        super();
        lastZoomLevel = FORCE_CLUSTERING;
    }

    @Override
    public void draw(final @NonNull Canvas canvas, final @NonNull MapView mapView, final boolean shadow) {
        if (shadow) {
            return;
        }
        int zoomLevel = (int) mapView.getZoomLevelDouble();
        if (zoomLevel != lastZoomLevel && !mapView.isAnimating()) {
            clusters = clusterer(mapView);
            renderer(clusters, canvas, mapView);
            lastZoomLevel = zoomLevel;
        }
        for (final StaticCluster cluster : clusters) {
            cluster.getMarker().draw(canvas, mapView.getProjection());
        }
    }

    @NonNull
    public Iterable<StaticCluster> reversedClusters() {
        return () -> {
            final ListIterator<StaticCluster> i = clusters.listIterator(clusters.size());
            return new Iterator<StaticCluster>() {
                @Override
                public boolean hasNext() {
                    return i.hasPrevious();
                }

                @Override
                public StaticCluster next() {
                    return i.previous();
                }

                @Override
                public void remove() {
                    i.remove();
                }
            };
        };
    }

    @Override
    public boolean onSingleTapConfirmed(final @NonNull MotionEvent event, final @NonNull MapView mapView) {
        for (final StaticCluster cluster : reversedClusters()) {
            if (cluster.getMarker().onSingleTapConfirmed(event, mapView))
                return true;
        }
        return false;
    }

    @Override
    public boolean onLongPress(final @NonNull MotionEvent event, final @NonNull MapView mapView) {
        for (final StaticCluster cluster : reversedClusters()) {
            if (cluster.getMarker().onLongPress(event, mapView))
                return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(final @NonNull MotionEvent event, final @NonNull MapView mapView) {
        for (final StaticCluster cluster : reversedClusters()) {
            if (cluster.getMarker().onTouchEvent(event, mapView))
                return true;
        }
        return false;
    }
}
