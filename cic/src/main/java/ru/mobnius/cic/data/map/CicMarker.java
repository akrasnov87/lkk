package ru.mobnius.cic.data.map;

import android.view.MotionEvent;

import androidx.annotation.NonNull;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

/**
 * Класс для обрабатывающий отображение точек на карте OSM
 */
public class CicMarker extends Marker {
    public final boolean isDonePoint;
    public final boolean isInspectorLastPoint;
    public final boolean isInspectorPoint;


    public CicMarker(final @NonNull MapView mapView,
                     boolean isDonePoint,
                     boolean isInspectorLastPoint,
                     boolean isInspectorPoint) {
        super(mapView);
        this.isDonePoint = isDonePoint;
        this.isInspectorLastPoint = isInspectorLastPoint;
        this.isInspectorPoint = isInspectorPoint;

    }

    @Override
    public boolean onLongPress(final @NonNull MotionEvent event, final @NonNull MapView mapView) {
        boolean touched = hitTest(event, mapView);
        if (touched) {
            if (mDraggable) {
                mIsDragged = true;
                closeInfoWindow();
                if (mOnMarkerDragListener != null) {
                    mOnMarkerDragListener.onMarkerDragStart(this);
                }
                moveToEventPosition(event, mapView);
            }
        }
        return touched;
    }

}
