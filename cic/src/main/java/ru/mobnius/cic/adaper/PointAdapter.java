package ru.mobnius.cic.adaper;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.mobnius.cic.R;
import ru.mobnius.cic.adaper.diffutil.PointDiffUtil;
import ru.mobnius.cic.adaper.holder.point.OnPointInfoClickListener;
import ru.mobnius.cic.adaper.holder.point.OnPointItemClickListener;
import ru.mobnius.cic.adaper.holder.point.PointHolder;
import ru.mobnius.cic.databinding.ItemPointBinding;
import ru.mobnius.cic.ui.model.PointItem;
import ru.mobnius.cic.ui.model.concurent.SavedResult;
import ru.mobnius.simple_core.utils.StringUtil;

public class PointAdapter extends RecyclerView.Adapter<PointHolder> {

    @NonNull
    private final OnPointItemClickListener itemClickListener;
    @NonNull
    private final OnPointInfoClickListener infoClickListener;
    @NonNull
    private final Drawable syncDoneIcon;
    @NonNull
    private final Drawable syncUndoneIcon;
    @NonNull
    private final Drawable syncUnavailableIcon;
    @NonNull
    private final List<PointItem> pointItems;

    public PointAdapter(
            final @NonNull Resources resources,
            final @NonNull OnPointItemClickListener itemClickListener,
            final @NonNull OnPointInfoClickListener infoClickListener) {
        pointItems = new ArrayList<>();
        this.itemClickListener = itemClickListener;
        this.infoClickListener = infoClickListener;
        syncDoneIcon = getNonNullDrawable(resources, R.drawable.ic_sync_done_green_24dp);
        syncUndoneIcon = getNonNullDrawable(resources, R.drawable.ic_sync_problem_red_24dp);
        syncUnavailableIcon = getNonNullDrawable(resources, R.drawable.ic_sync_disabled_gray_24dp);
    }

    @NonNull
    @Override
    public PointHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final ItemPointBinding itemPointBinding = ItemPointBinding.inflate(inflater, parent, false);
        return new PointHolder(itemPointBinding, itemClickListener, infoClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PointHolder holder, int position) {
        final PointItem item = pointItems.get(position);
        final Drawable drawable;
        if (item.done) {
            if (item.sync) {
                drawable = syncDoneIcon;
            } else {
                drawable = syncUndoneIcon;
            }
        } else {
            drawable = syncUnavailableIcon;
        }
        holder.bindPoints(item, drawable);
    }

    @Override
    public int getItemCount() {
        return pointItems.size();
    }

    public void refresh(final @NonNull List<PointItem> newPoints) {
        int size = pointItems.size();
        pointItems.clear();
        notifyItemRangeRemoved(0, size);
        pointItems.addAll(newPoints);
        notifyItemRangeInserted(0, pointItems.size());
    }

    public void setNewPointItems(final @NonNull List<PointItem> newPointItems){
        final PointDiffUtil diffUtil = new PointDiffUtil(pointItems, newPointItems);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtil);
        pointItems.clear();
        pointItems.addAll(newPointItems);
        diffResult.dispatchUpdatesTo(this);

    }


    public void updateItemDone(final @NonNull SavedResult savedResult) {
        for (int i = 0; i < pointItems.size(); i++) {
            if (StringUtil.equalsIgnoreCase(pointItems.get(i).id, savedResult.pointId)) {
                final PointItem pointItem = pointItems.get(i);
                pointItem.done = true;
                pointItem.resultId = savedResult.resultId;
                pointItem.reject = false;
                pointItem.sync = false;
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void updateItemUnDone(final @NonNull SavedResult savedResult) {
        for (int i = 0; i < pointItems.size(); i++) {
            if (StringUtil.equalsIgnoreCase(pointItems.get(i).id, savedResult.pointId)) {
                pointItems.get(i).done = false;
                notifyItemChanged(i);
                break;
            }
        }
    }

    @NonNull
    private Drawable getNonNullDrawable(final @NonNull Resources resources, final @DrawableRes int res) {
        final Drawable drawable = ResourcesCompat.getDrawable(resources, res, null);
        if (drawable == null) {
            Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
            transparentDrawable.setBounds(24, 24, 24, 24);
            return transparentDrawable;
        }
        return drawable;
    }

}
