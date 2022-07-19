package ru.mobnius.cic.adaper.holder.search;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.mobnius.cic.databinding.ItemSearchHeaderBinding;
import ru.mobnius.cic.ui.model.SearchHeader;

public class SearchHeaderHolder extends RecyclerView.ViewHolder {
    @NonNull
    private final ItemSearchHeaderBinding binding;

    public SearchHeaderHolder(@NonNull ItemSearchHeaderBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(final @NonNull SearchHeader searchHeader) {
        binding.itemSearchHeaderText.setText(searchHeader.getHeaderName());
    }
}
