package ru.mobnius.cic.data.map;

import androidx.annotation.NonNull;

import ru.mobnius.cic.ui.model.PointItem;

public interface PointClickListener {
    void onPointClick(@NonNull PointItem pointItem);
}
