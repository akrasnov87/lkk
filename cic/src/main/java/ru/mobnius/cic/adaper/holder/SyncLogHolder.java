package ru.mobnius.cic.adaper.holder;

import android.content.res.Resources;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.mobnius.cic.R;
import ru.mobnius.cic.databinding.ItemSyncLogBinding;
import ru.mobnius.cic.ui.model.SyncLogItem;
import ru.mobnius.simple_core.utils.DateUtil;

public class SyncLogHolder extends RecyclerView.ViewHolder {
    @NonNull
    private final ItemSyncLogBinding binding;

    public SyncLogHolder(@NonNull ItemSyncLogBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(final @NonNull SyncLogItem model) {
        final Resources resources = itemView.getContext().getResources();
        if (model.b_error) {
            binding.itemSyncMessage.setVisibility(View.VISIBLE);
            binding.itemSyncDate.setVisibility(View.VISIBLE);
            binding.itemSyncMessage.setTextColor(resources.getColor(R.color.colorAccent));
        } else {
            binding.itemSyncMessage.setVisibility(View.GONE);
            binding.itemSyncDate.setVisibility(View.GONE);
            binding.itemSyncMessage.setTextColor(resources.getColor(R.color.colorHint));
        }
        binding.itemSyncDate.setText(DateUtil.dateToTimeString(model.d_date));
        binding.itemSyncMessage.setText(model.c_message);
    }
}
