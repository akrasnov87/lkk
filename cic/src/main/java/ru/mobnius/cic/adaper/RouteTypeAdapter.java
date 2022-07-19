package ru.mobnius.cic.adaper;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ru.mobnius.cic.R;
import ru.mobnius.cic.adaper.holder.route.OnRouteInfoClickListener;
import ru.mobnius.cic.adaper.holder.route.OnRouteItemClickListener;
import ru.mobnius.cic.adaper.holder.RouteTypeHolder;
import ru.mobnius.cic.databinding.ItemRouteTypeBinding;
import ru.mobnius.cic.ui.model.concurent.LoadRoutesResult;
import ru.mobnius.cic.ui.model.routestatus.RouteStatus;

public class RouteTypeAdapter extends RecyclerView.Adapter<RouteTypeHolder> {
    @NonNull
    private final RecyclerView.RecycledViewPool viewPool;
    @NonNull
    private final OnRouteItemClickListener itemClickListener;
    @NonNull
    private final OnRouteInfoClickListener infoClickListener;
    @NonNull
    private final SortedList<RouteStatus> sortedList;
    @NonNull
    @SuppressWarnings("ComparatorCombinators")
    private final Comparator<RouteStatus> comparator = (rst1, rst2) -> Integer.compare(rst1.statusType(), rst2.statusType());

    public RouteTypeAdapter(final @NonNull LoadRoutesResult result,
                            final @NonNull OnRouteItemClickListener itemClickListener, final @NonNull OnRouteInfoClickListener infoClickListener) {
        this.itemClickListener = itemClickListener;
        this.infoClickListener = infoClickListener;
        final List<RouteStatus> statusTypes = new ArrayList<>();
        statusTypes.add(result.currentRoutes);
        statusTypes.add(result.futureRoutes);
        statusTypes.add(result.doneRoutes);
        statusTypes.add(result.overdueRoutes);
        final SortedList.Callback<RouteStatus> sortedCallback = new SortedList.Callback<RouteStatus>() {

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public int compare(@NonNull RouteStatus a, @NonNull RouteStatus b) {
                return comparator.compare(a, b);
            }

            @Override
            public boolean areContentsTheSame(@NonNull RouteStatus oldItem, @NonNull RouteStatus newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(@NonNull RouteStatus item1, @NonNull RouteStatus item2) {
                return item1.statusType() == item2.statusType();
            }
        };
        sortedList = new SortedList<>(RouteStatus.class, sortedCallback);
        sortedList.addAll(statusTypes);
        viewPool = new RecyclerView.RecycledViewPool();
    }

    @NonNull
    @Override
    public RouteTypeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final ItemRouteTypeBinding binding = ItemRouteTypeBinding.inflate(inflater, parent, false);
        binding.itemRouteTypeItems.setLayoutManager(new LinearLayoutManager(parent.getContext()));
        binding.itemRouteTypeItems.setRecycledViewPool(viewPool);
        return new RouteTypeHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteTypeHolder holder, int position) {
        final RouteStatus routeStatus = sortedList.get(position);
        holder.setVisible(routeStatus.isNotEmpty());
        holder.setText(routeStatus.getName());
        final Resources resources = holder.itemView.getResources();
        if (routeStatus.isHintColor()) {
            holder.setTextColor(resources.getColor(R.color.colorHintDark));
        } else {
            holder.setTextColor(resources.getColor(R.color.colorSecondaryAccent));
        }
        final RouteAdapter adapter = new RouteAdapter(routeStatus.getRouteItems(),
                routeStatus.statusType(), itemClickListener, infoClickListener);
        holder.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }

    public void refresh(final @NonNull LoadRoutesResult result) {
        sortedList.clear();
        final List<RouteStatus> statusTypes = new ArrayList<>();
        statusTypes.add(result.currentRoutes);
        statusTypes.add(result.futureRoutes);
        statusTypes.add(result.doneRoutes);
        statusTypes.add(result.overdueRoutes);
        sortedList.addAll(statusTypes);
    }

}
