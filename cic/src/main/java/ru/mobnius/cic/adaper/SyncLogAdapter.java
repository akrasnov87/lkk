package ru.mobnius.cic.adaper;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;

import ru.mobnius.cic.adaper.diffutil.SyncLogDiffUtil;
import ru.mobnius.cic.adaper.holder.SyncLogHolder;
import ru.mobnius.cic.databinding.ItemSyncLogBinding;
import ru.mobnius.cic.ui.model.SyncLogItem;

public class SyncLogAdapter extends ListAdapter<SyncLogItem, SyncLogHolder> {

    public SyncLogAdapter() {
        super(new SyncLogDiffUtil());
    }

    @NonNull
    @Override
    public SyncLogHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ItemSyncLogBinding binding = ItemSyncLogBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SyncLogHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SyncLogHolder holder, int position) {
        holder.bind(getItem(position));
    }

}