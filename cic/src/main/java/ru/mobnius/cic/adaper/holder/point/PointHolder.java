package ru.mobnius.cic.adaper.holder.point;

import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import ru.mobnius.cic.databinding.ItemPointBinding;
import ru.mobnius.cic.ui.model.PointItem;

public class PointHolder extends RecyclerView.ViewHolder {

    @NonNull
    private final ItemPointBinding binding;
    @Nullable
    private PointItem pointItem;

    public PointHolder(final @NonNull ItemPointBinding binding,
                       final @NonNull OnPointItemClickListener itemClickListener,
                       final @NonNull OnPointInfoClickListener infoClickListener) {
        super(binding.getRoot());
        this.binding = binding;
        this.binding.getRoot().setOnClickListener(v -> {
            if (pointItem == null) {
                return;
            }
            itemClickListener.onPointItemClick(pointItem, getLayoutPosition());
        });
        this.binding.itemPointInfo.setOnClickListener(v -> {
            if (pointItem == null) {
                return;
            }
            infoClickListener.onPointInfoClick(pointItem, getLayoutPosition());
        });

    }

    public void bindPoints(final @NonNull PointItem pointItem, final @NonNull Drawable drawable) {
        this.pointItem = pointItem;
        if (pointItem.reject) {
            binding.itemPointRejectStatus.setVisibility(View.VISIBLE);
        } else {
            binding.itemPointRejectStatus.setVisibility(View.GONE);
        }
        binding.itemPointOwnerName.setValue(pointItem.owner);
        binding.itemPointAccountNumber.setValue(pointItem.accountNumber);
        binding.itemPointDeviceNumber.setValue(pointItem.deviceNumber);
        binding.itemPointAddress.setValue(pointItem.address);
        binding.itemPointSyncStatus.setImageDrawable(drawable);

        binding.itemPointDoneStatus.setVisibility(pointItem.done ? View.VISIBLE : View.GONE);
    }
}
