package ru.mobnius.cic.adaper.holder.search;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import ru.mobnius.cic.adaper.holder.point.OnPointItemClickListener;
import ru.mobnius.cic.adaper.holder.route.OnRouteItemClickListener;
import ru.mobnius.cic.databinding.ItemSearchResultBinding;
import ru.mobnius.cic.data.search.SearchResult;
import ru.mobnius.simple_core.utils.StringUtil;

public class SearchResultHolder extends RecyclerView.ViewHolder {
    @NonNull
    private final ItemSearchResultBinding binding;
    @Nullable
    private SearchResult searchResult;

    public SearchResultHolder(final @NonNull ItemSearchResultBinding binding,
                              final @NonNull OnPointItemClickListener pointItemClickListener,
                              final @NonNull OnRouteItemClickListener routeItemClickListener) {
        super(binding.getRoot());
        this.binding = binding;
        this.binding.getRoot().setOnClickListener(view -> {
            if (searchResult == null) {
                return;
            }
            if (searchResult.getPointItem() != null) {
                pointItemClickListener.onPointItemClick(searchResult.getPointItem(), getLayoutPosition());
                return;
            }
            if (searchResult.getRouteItem() != null) {
                routeItemClickListener.onRouteItemClick(searchResult.getRouteItem());
            }
        });
    }

    public void bind(final @NonNull SearchResult result) {
        this.searchResult = result;
        binding.itemSearchResultFirst.setText(result.getFirstLineText());
        if (StringUtil.isEmpty(result.getSecondLineText())) {
            binding.itemSearchResultSecond.setVisibility(View.GONE);
        } else {
            binding.itemSearchResultSecond.setText(result.getSecondLineText());
            binding.itemSearchResultSecond.setVisibility(View.VISIBLE);
        }
        if (StringUtil.isEmpty(result.getThirdLineText())) {
            binding.itemSearchResultThird.setVisibility(View.GONE);
        } else {
            binding.itemSearchResultThird.setText(result.getThirdLineText());
            binding.itemSearchResultThird.setVisibility(View.VISIBLE);
        }
        if (StringUtil.isEmpty(result.getFourthLineText())) {
            binding.itemSearchResultFourth.setVisibility(View.GONE);
        } else {
            binding.itemSearchResultFourth.setText(result.getFourthLineText());
            binding.itemSearchResultFourth.setVisibility(View.VISIBLE);
        }
    }
}
