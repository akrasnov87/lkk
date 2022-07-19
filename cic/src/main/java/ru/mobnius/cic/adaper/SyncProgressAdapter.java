package ru.mobnius.cic.adaper;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;

import ru.mobnius.cic.adaper.diffutil.SyncProgressDiffUtil;
import ru.mobnius.cic.adaper.holder.SyncProgressHolder;
import ru.mobnius.cic.databinding.ItemSyncProgressLineBinding;
import ru.mobnius.cic.ui.model.SyncProgressItem;

public class SyncProgressAdapter extends ListAdapter<SyncProgressItem, SyncProgressHolder> {

    public SyncProgressAdapter() {
        super(new SyncProgressDiffUtil());
    }

    @NonNull
    @Override
    public SyncProgressHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ItemSyncProgressLineBinding binding = ItemSyncProgressLineBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SyncProgressHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SyncProgressHolder holder, int position) {
        holder.bind(getItem(position));
    }
}
