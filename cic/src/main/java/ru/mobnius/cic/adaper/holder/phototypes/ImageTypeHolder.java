package ru.mobnius.cic.adaper.holder.phototypes;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.mobnius.cic.databinding.ItemPhotoTypeBinding;
import ru.mobnius.cic.ui.model.ImageType;

public class ImageTypeHolder extends RecyclerView.ViewHolder {

    private final ItemPhotoTypeBinding binding;
    private ImageType imageType;

    public ImageTypeHolder(final @NonNull ItemPhotoTypeBinding binding,
                           final @NonNull OnImageTypeSelectedListener photoTypeSelectedListener,
                           final @NonNull ImageTypeCheckedPositionCallback checkedPositionCallback) {
        super(binding.getRoot());
        this.binding = binding;
        binding.getRoot().setOnClickListener(v -> {
            if (imageType == null) {
                return;
            }
            photoTypeSelectedListener.onPhotoTypeSelected(imageType);
            binding.itemPhotoType.setSelected(true);
            checkedPositionCallback.onPositionChecked(getBindingAdapterPosition());
        });
    }

    public void bind(final @NonNull ImageType imageType, final boolean isChecked) {
        this.imageType = imageType;
        binding.itemPhotoType.setText(imageType.typeName);
        binding.itemPhotoType.setChecked(isChecked);
    }
}
