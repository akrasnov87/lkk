package ru.mobnius.cic.adaper.holder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.mobnius.cic.R;
import ru.mobnius.cic.databinding.ItemSyncProgressLineBinding;
import ru.mobnius.cic.ui.model.SyncProgressItem;

public class SyncProgressHolder extends RecyclerView.ViewHolder {
    @NonNull
    public final ItemSyncProgressLineBinding binding;

    public SyncProgressHolder(final @NonNull ItemSyncProgressLineBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(final @NonNull SyncProgressItem item) {
        final String transferDiscription = itemView.getContext().getString(R.string.transfer_discription, item.name, item.transferData);
        binding.itemSyncDescription.setText(transferDiscription);
        binding.itemSyncStatus.setText(item.status);
        binding.itemSyncProgressIndicator.setProgress(item.uploadProgress);
    }
}
