package ru.mobnius.cic.adaper.holder.image;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import ru.mobnius.cic.databinding.ItemImageBinding;
import ru.mobnius.cic.ui.model.ImageItem;
import ru.mobnius.simple_core.utils.DateUtil;
import ru.mobnius.simple_core.utils.LocationUtil;

public class ImageHolder extends RecyclerView.ViewHolder {
    @NonNull
    private final ItemImageBinding binding;
    @Nullable
    private ImageItem imageItem;

    public ImageHolder(final @NonNull ItemImageBinding binding,
                       final @NonNull OnImageItemClickListener listener,
                       final @NonNull OnImageChangeClickListener changeClickListener,
                       final @NonNull OnPlaceholderClickListener placeholderClickListener) {
        super(binding.getRoot());
        this.binding = binding;
        binding.itemImageMainLayout.setOnClickListener(v -> {
            if (imageItem == null) {
                return;
            }
            if (imageItem.isPlaceholder()) {
                placeholderClickListener.onPlaceHolderClicked();
                return;
            }
            changeClickListener.onImageChangeClick(imageItem);
        });

        binding.itemImageThumb.setOnClickListener(v -> {
            if (imageItem == null) {
                return;
            }
            listener.onImageClicked(imageItem);

        });
    }

    public void bindPhoto(final @NonNull ImageItem image) {
        this.imageItem = image;
        if (image.isPlaceholder()) {
            binding.itemImagePlaceholderLayout.setVisibility(View.VISIBLE);
            binding.itemImageContentLayout.setVisibility(View.GONE);
            return;
        }
        binding.itemImageThumb.setImageBitmap(image.bitmap);

        binding.itemImageType.setText(imageItem.imageType.typeName);
        final String userDate = DateUtil.convertDateToCustomString(imageItem.date, DateUtil.USER_TIME_FORMAT);

        binding.itemImageDate.setText(userDate);
        binding.itemImageLocation.setText(LocationUtil.toString(imageItem.location));

        binding.itemImageNotice.setText(imageItem.imageType.notice);
    }
}
