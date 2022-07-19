package ru.mobnius.cic.data.map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.osmdroid.bonuspack.clustering.StaticCluster;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Класс для обрабатывающий отображение кластеров на карте OSM
 */
public class CicMarkerClusterer extends BaseMarkerClusterer {
    @NonNull
    protected final Paint textPaint;
    @NonNull
    private final DisplayMetrics displayMetrics;
    @Nullable
    private ArrayList<CicMarker> clonedMarkers;

    public int radiusInPixels = 100;
    protected final int maxClusteringZoomLevel = 17;

    protected double radiusInMeters;
    private final int inspectorColor;
    private final int inspectorLastPointColor;
    private final int doneColor;
    private final int undoneColor;


    public final float anchorU = Marker.ANCHOR_CENTER;
    public final float anchorV = Marker.ANCHOR_CENTER;

    public final float textAnchorU = Marker.ANCHOR_CENTER;
    public final float textAnchorV = Marker.ANCHOR_CENTER;

    public CicMarkerClusterer(final @NonNull DisplayMetrics displayMetrics,
                              final int inspectorColor,
                              final int inspectorLastPointColor,
                              final int doneColor,
                              final int undoneColor,
                              final float textSize) {
        super();
        this.displayMetrics = displayMetrics;
        this.inspectorColor = inspectorColor;
        this.inspectorLastPointColor = inspectorLastPointColor;
        this.doneColor = doneColor;
        this.undoneColor = undoneColor;
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(15 * textSize);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
    }

    @NonNull
    @Override
    public ArrayList<StaticCluster> clusterer(@NonNull MapView mapView) {
        final ArrayList<StaticCluster> clusters = new ArrayList<>();
        final Rect screenRect = mapView.getIntrinsicScreenRect(null);
        final BoundingBox boundingBox = mapView.getBoundingBox();
        convertRadiusToMeters(screenRect, boundingBox);
        try {
            clonedMarkers = new ArrayList<>(items);
            while (!clonedMarkers.isEmpty()) {
                final CicMarker m = clonedMarkers.get(0);
                final StaticCluster cluster = createCluster(m);
                clusters.add(cluster);
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return clusters;
    }

    @NonNull
    private StaticCluster createCluster(final @NonNull CicMarker m) {
        final GeoPoint clusterPosition = m.getPosition();
        final StaticCluster cluster = new StaticCluster(clusterPosition);
        cluster.add(m);
        if (clonedMarkers == null){
            return cluster;
        }
        clonedMarkers.remove(m);
        if (currentZoomLevel > maxClusteringZoomLevel) {
            return cluster;
        }
        final Iterator<CicMarker> iterator = clonedMarkers.iterator();
        while (iterator.hasNext()) {
            final Marker neighbour = iterator.next();
            final double distance = clusterPosition.distanceToAsDouble(neighbour.getPosition());
            if (distance <= radiusInMeters) {
                cluster.add(neighbour);
                iterator.remove();
            }
        }
        return cluster;
    }

    @NonNull
    @Override
    public Marker buildClusterMarker(@NonNull StaticCluster cluster, @NonNull MapView mapView) {
        final Marker m = new Marker(mapView);
        final List<CicMarker> markers = new ArrayList<>(cluster.getSize());
        for (int i = 0; i < cluster.getSize(); i++) {
            if (cluster.getItem(i) instanceof CicMarker) {
                markers.add((CicMarker) cluster.getItem(i));
            }
        }
        if (markers.size() > 0) {
            boolean isInspectorLastPoint = false;
            boolean isInspector = false;
            int done = 0;
            int undone = 0;
            final Paint paint = new Paint();

            for (final CicMarker marker : markers) {
                if (marker.isInspectorPoint) {
                    isInspector = true;
                    paint.setColor(inspectorColor);
                    break;
                }
            }
            if (!isInspector) {
                for (final CicMarker marker : markers) {
                    if (marker.isInspectorPoint) {
                        isInspectorLastPoint = true;
                        paint.setColor(inspectorLastPointColor);
                        break;
                    }
                }
            }
            if (!isInspector && !isInspectorLastPoint) {
                for (final CicMarker marker : markers) {
                    if (marker.isDonePoint) {
                        done += 1;
                    } else {
                        undone += 1;
                    }
                }
                if (done >= undone) {
                    paint.setColor(doneColor);
                } else {
                    paint.setColor(undoneColor);
                }
            }
            m.setPosition(cluster.getPosition());
            m.setInfoWindow(null);
            m.setAnchor(anchorU, anchorV);
            final Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            final float dip = 24f;
            final float px = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dip,
                    displayMetrics
            );
            final Bitmap mutableBitmap = Bitmap.createBitmap((int) px, (int) px, conf);
            final Canvas iconCanvas = new Canvas(mutableBitmap);
            paint.setStyle(Paint.Style.FILL);
            iconCanvas.drawCircle(px / 2, px / 2, px / 2, paint);
            final String text = String.valueOf(cluster.getSize());
            final int textHeight = (int) (textPaint.descent() + textPaint.ascent());
            iconCanvas.drawText(text,
                    textAnchorU * mutableBitmap.getWidth(),
                    textAnchorV * mutableBitmap.getHeight() - (float) textHeight / 2,
                    textPaint);
            m.setIcon(new BitmapDrawable(mapView.getContext().getResources(), mutableBitmap));
        }
        return m;
    }

    @Override
    public void renderer(@NonNull ArrayList<StaticCluster> clusters, @NonNull Canvas canvas, @NonNull MapView mapView) {
        for (final StaticCluster cluster : clusters) {
            if (cluster.getSize() == 1) {
                cluster.setMarker(cluster.getItem(0));
            } else {
                final Marker m = buildClusterMarker(cluster, mapView);
                cluster.setMarker(m);
            }
        }
    }

    private void convertRadiusToMeters(final @NonNull Rect screenRect,
                                       final @NonNull BoundingBox boundingBox) {
        final int screenWidth = screenRect.right - screenRect.left;
        final int screenHeight = screenRect.bottom - screenRect.top;

        final double diagonalInMeters = boundingBox.getDiagonalLengthInMeters();
        final double diagonalInPixels = Math.sqrt(screenWidth * screenWidth + screenHeight * screenHeight);
        final double metersInPixel = diagonalInMeters / diagonalInPixels;

        radiusInMeters = radiusInPixels * metersInPixel;
    }
}
