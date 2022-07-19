package ru.mobnius.cic.adaper.holder;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.mobnius.cic.R;
import ru.mobnius.cic.adaper.holder.route.RouteHolder;
import ru.mobnius.cic.databinding.ItemRouteTypeBinding;

public class RouteTypeHolder extends RecyclerView.ViewHolder {
    @NonNull
    private final ItemRouteTypeBinding binding;

    public RouteTypeHolder(final @NonNull ItemRouteTypeBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void setText(String txt) {
        binding.itemRouteTypeTitle.setText(txt);
    }

    public void setTextColor(int color) {
        binding.itemRouteTypeTitle.setTextColor(color);
    }

    public void setAdapter(RecyclerView.Adapter<RouteHolder> adapter) {
        binding.itemRouteTypeItems.setAdapter(adapter);
    }

    public void setVisible(final boolean visible) {
        if (visible) {
            itemView.setVisibility(View.VISIBLE);
            final RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            final int margin = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.indent_m);
            params.bottomMargin = margin;
            params.topMargin = margin;
            itemView.setLayoutParams(params);
        } else {
            itemView.setVisibility(View.GONE);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }
    }
}
