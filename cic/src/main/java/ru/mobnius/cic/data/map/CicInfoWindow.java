package ru.mobnius.cic.data.map;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import ru.mobnius.cic.R;
import ru.mobnius.cic.ui.component.LabelValueView;
import ru.mobnius.cic.ui.model.PointItem;

/**
 * Класс для обрабатывающий отображение описания точек на карте OSM
 */
public class CicInfoWindow extends MarkerInfoWindow {
    @NonNull
    private final PointItem pointItem;

    private final String subscrNumber;
    private final String owner;
    private final String deviceType;
    private final String deviceNumber;
    private final String address;
    private final PointClickListener pointClickListener;

    public CicInfoWindow(final int layoutResId,
                         final @NonNull MapView mapView,
                         final @NonNull PointItem pointItem,
                         final @NonNull String subscrNumber,
                         final @NonNull String owner,
                         final @NonNull String deviceType,
                         final @NonNull String deviceNumber,
                         final @NonNull String address,
                         final @NonNull PointClickListener pointClickListener) {
        super(layoutResId, mapView);
        this.pointItem = pointItem;
        this.subscrNumber = subscrNumber;
        this.owner = owner;
        this.deviceType = deviceType;
        this.deviceNumber = deviceNumber;
        this.address = address;
        this.pointClickListener = pointClickListener;
    }

    @Override
    public void onOpen(final @NonNull Object item) {
        final LabelValueView lvvSubscrNumber = mView.findViewById(R.id.info_window_subscr_number);
        lvvSubscrNumber.setValue(subscrNumber);
        final LabelValueView lvvOwner = mView.findViewById(R.id.info_window_owner);
        lvvOwner.setValueWithLineLength(owner, 30);
        final LabelValueView lvvDeviceType = mView.findViewById(R.id.info_window_device_type);
        lvvDeviceType.setValue(deviceType);
        final LabelValueView lvvDeviceNumber = mView.findViewById(R.id.info_window_device_number);
        lvvDeviceNumber.setValue(deviceNumber);
        final LabelValueView lvvAddress = mView.findViewById(R.id.info_window_address);
        lvvAddress.setValueWithLineLength(address, 30);
        final AppCompatImageView ivPoint = mView.findViewById(R.id.info_window_open_point);
        ivPoint.setOnClickListener(v -> pointClickListener.onPointClick(pointItem));

    }

}