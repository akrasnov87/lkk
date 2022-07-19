package ru.mobnius.cic.adaper.diffutil;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import ru.mobnius.cic.ui.model.PointItem;

public class PointDiffUtil extends DiffUtil.Callback {

    private final List<PointItem> oldItems;
    private final List<PointItem> newItems;

    public PointDiffUtil(final @NonNull List<PointItem> oldItems, final @NonNull List<PointItem> newItems) {
        this.oldItems = oldItems;
        this.newItems = newItems;
    }

    @Override
    public int getOldListSize() {
        return oldItems.size();
    }

    @Override
    public int getNewListSize() {
        return newItems.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        final PointItem oldItem = oldItems.get(oldItemPosition);
        final PointItem newItem = newItems.get(newItemPosition);
        return PointItem.getDiffUtill().areItemsTheSame(oldItem, newItem);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final PointItem oldItem = oldItems.get(oldItemPosition);
        final PointItem newItem = newItems.get(newItemPosition);
        return PointItem.getDiffUtill().areContentsTheSame(oldItem, newItem);
    }

}
