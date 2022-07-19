package ru.mobnius.cic.adaper;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.mobnius.cic.adaper.holder.image.ImageHolder;
import ru.mobnius.cic.adaper.holder.image.OnImageChangeClickListener;
import ru.mobnius.cic.adaper.holder.image.OnImageItemClickListener;
import ru.mobnius.cic.adaper.holder.image.OnPlaceholderClickListener;
import ru.mobnius.cic.databinding.ItemImageBinding;
import ru.mobnius.cic.ui.model.ImageItem;
import ru.mobnius.cic.ui.model.ImageType;

public class ImageAdapter extends RecyclerView.Adapter<ImageHolder> {
    @NonNull
    private final SortedList<ImageItem> sortedList;
    @NonNull
    private final OnImageItemClickListener itemClickListener;
    @NonNull
    private final OnImageChangeClickListener changeClickListener;
    @NonNull
    private final OnPlaceholderClickListener placeholderClickListener;
    @NonNull
    private final Map<String, ImageItem> uniqueMapping = new HashMap<>();
    @NonNull
    private final Comparator<ImageItem> comparator = (a, b) -> {
        if (b.isPlaceholder()) {
            return -1;
        }
        return Long.compare(b.date.getTime(), a.date.getTime());
    };

    public ImageAdapter(
            final @NonNull OnImageItemClickListener itemClickListener,
            final @NonNull OnImageChangeClickListener changeClickListener,
            final @NonNull OnPlaceholderClickListener placeholderClickListener) {
        this.itemClickListener = itemClickListener;
        this.changeClickListener = changeClickListener;
        this.placeholderClickListener = placeholderClickListener;
        final SortedList.Callback<ImageItem> callback = new SortedList.Callback<ImageItem>() {
            @Override
            public int compare(ImageItem o1, ImageItem o2) {
                return comparator.compare(o1, o2);
            }

            @Override
            public boolean areContentsTheSame(ImageItem oldItem, ImageItem newItem) {
                if (!oldItem.id.equals(newItem.id)) {
                    return false;
                }
                if (!oldItem.absFilePath.equals(newItem.absFilePath)) {
                    return false;
                }
                if (!oldItem.date.equals(newItem.date)) {
                    return false;
                }
                if (!oldItem.bitmap.equals(newItem.bitmap)) {
                    return false;
                }
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(ImageItem item1, ImageItem item2) {
                return item1.id.equals(item2.id);
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }
        };
        sortedList = new SortedList<>(ImageItem.class, callback);
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final ItemImageBinding binding = ItemImageBinding.inflate(inflater, parent, false);
        return new ImageHolder(binding, itemClickListener, changeClickListener, placeholderClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        holder.bindPhoto(sortedList.get(position));
    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }

    public void insert(final @NonNull ImageItem item) {
        final String key = item.id;
        final ImageItem existing = uniqueMapping.put(key, item);
        if (existing == null) {
            sortedList.add(item);
        } else {
            int pos = sortedList.indexOf(existing);
            if (pos != -1) {
                sortedList.updateItemAt(pos, item);
            }
        }
    }


    private void insertAll(final @NonNull List<ImageItem> items) {
        for (final ImageItem item : items) {
            insert(item);
        }
    }

    public void swapList(final @NonNull List<ImageItem> items) {
        final Set<String> newListKeys = new HashSet<>();
        for (final ImageItem item : items) {
            newListKeys.add(item.id);
        }
        for (int i = sortedList.size() - 1; i >= 0; i--) {
            final ImageItem item = sortedList.get(i);
            final String key = item.id;
            if (!newListKeys.contains(key)) {
                uniqueMapping.remove(key);
                sortedList.removeItemAt(i);
            }
        }
        insertAll(items);
    }

    public void updateImageItem(final @NonNull ImageType type) {
        final ImageItem item = uniqueMapping.get(type.imageId);
        if (item != null) {
            item.imageType = type;
            insert(item);
        }
    }

    public void removeImageItem(final @NonNull String itemId) {
        final ImageItem item = uniqueMapping.remove(itemId);
        if (item != null) {
            sortedList.remove(item);
        }
    }
}
