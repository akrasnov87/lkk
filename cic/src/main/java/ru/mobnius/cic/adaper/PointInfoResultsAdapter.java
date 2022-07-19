package ru.mobnius.cic.adaper;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.mobnius.cic.adaper.holder.pointinfo.OnPointInfoResultCancelListener;
import ru.mobnius.cic.adaper.holder.pointinfo.PointInfoResultsHolder;
import ru.mobnius.cic.databinding.ItemPointInfoResultsBinding;
import ru.mobnius.cic.ui.model.ResultItem;

public class PointInfoResultsAdapter extends RecyclerView.Adapter<PointInfoResultsHolder> {
    @NonNull
    private final OnPointInfoResultCancelListener resultCancelListener;
    @NonNull
    private final List<ResultItem> resultItems;


    public PointInfoResultsAdapter(final @NonNull OnPointInfoResultCancelListener resultCancelListener) {
        this.resultCancelListener = resultCancelListener;
        resultItems = new ArrayList<>();
    }

    @NonNull
    @Override
    public PointInfoResultsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final ItemPointInfoResultsBinding binding = ItemPointInfoResultsBinding.inflate(inflater, parent, false);
        return new PointInfoResultsHolder(binding, resultCancelListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PointInfoResultsHolder holder, int position) {
        holder.bind(resultItems.get(position));
    }

    @Override
    public int getItemCount() {
        return resultItems.size();
    }

    public void setResults(final @NonNull List<ResultItem> resultItems) {
        final int size = this.resultItems.size();
        this.resultItems.clear();
        notifyItemRangeRemoved(0, size);
        this.resultItems.addAll(resultItems);
        notifyItemRangeInserted(0, resultItems.size());
    }
}
