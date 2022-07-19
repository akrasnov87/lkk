package ru.mobnius.cic.adaper;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.mobnius.cic.adaper.holder.phototypes.OnImageTypeSelectedListener;
import ru.mobnius.cic.adaper.holder.phototypes.ImageTypeCheckedPositionCallback;
import ru.mobnius.cic.adaper.holder.phototypes.ImageTypeHolder;
import ru.mobnius.cic.databinding.ItemPhotoTypeBinding;
import ru.mobnius.cic.ui.model.ImageType;

public class ImageTypeAdapter extends RecyclerView.Adapter<ImageTypeHolder> {
    private final List<ImageType> imageTypes;
    private final OnImageTypeSelectedListener photoTypeSelectedListener;
    private final ImageTypeCheckedPositionCallback checkedPositionCallback;
    private int lastCheckedPosition;

    public ImageTypeAdapter(final @NonNull List<ImageType> imageTypes,
                            final @NonNull OnImageTypeSelectedListener photoTypeSelectedListener,
                            final int selection) {
        this.imageTypes = imageTypes;
        this.photoTypeSelectedListener = photoTypeSelectedListener;
        this.lastCheckedPosition = selection;
        checkedPositionCallback = checkedPosition -> {
            int copyOfLastCheckedPosition = lastCheckedPosition;
            lastCheckedPosition = checkedPosition;
            notifyItemChanged(copyOfLastCheckedPosition);
            notifyItemChanged(lastCheckedPosition);
        };
    }

    @NonNull
    @Override
    public ImageTypeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final ItemPhotoTypeBinding itemPhotoTypeBinding = ItemPhotoTypeBinding.inflate(inflater, parent, false);
        return new ImageTypeHolder(itemPhotoTypeBinding, photoTypeSelectedListener, checkedPositionCallback);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageTypeHolder holder, int position) {
        holder.bind(imageTypes.get(position), lastCheckedPosition == position);
    }

    @Override
    public int getItemCount() {
        return imageTypes.size();
    }

}
