package ru.mobnius.cic.adaper;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import java.util.Comparator;
import java.util.List;

import ru.mobnius.cic.adaper.holder.route.OnRouteInfoClickListener;
import ru.mobnius.cic.adaper.holder.route.OnRouteItemClickListener;
import ru.mobnius.cic.adaper.holder.route.RouteHolder;
import ru.mobnius.cic.databinding.ItemRouteBinding;
import ru.mobnius.cic.ui.model.RouteItem;

public class RouteAdapter extends RecyclerView.Adapter<RouteHolder> {
    @NonNull
    private final OnRouteItemClickListener itemClickListener;
    @NonNull
    private final OnRouteInfoClickListener infoClickListener;

    private final int routeType;
    @NonNull
    private final SortedList<RouteItem> sortedList;
    @NonNull
    @SuppressWarnings("ComparatorCombinators")
    private final Comparator<RouteItem> comparator = (a, b) -> Integer.compare(a.order, b.order);

    public RouteAdapter(final @NonNull List<RouteItem> items, int typeRoute,
                        final @NonNull OnRouteItemClickListener itemClickListener, final @NonNull OnRouteInfoClickListener infoClickListener) {
        final SortedList.Callback<RouteItem> sortedCallback = new SortedList.Callback<RouteItem>() {

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
            public int compare(@NonNull RouteItem a, @NonNull RouteItem b) {
                return comparator.compare(a, b);
            }

            @Override
            public boolean areContentsTheSame(@NonNull RouteItem oldItem, @NonNull RouteItem newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(@NonNull RouteItem item1, @NonNull RouteItem item2) {
                return item1.id.equals(item2.id);
            }
        };
        sortedList = new SortedList<>(RouteItem.class, sortedCallback);
        sortedList.addAll(items);
        routeType = typeRoute;
        this.itemClickListener = itemClickListener;
        this.infoClickListener = infoClickListener;
    }

    @NonNull
    @Override
    public RouteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final ItemRouteBinding binding = ItemRouteBinding.inflate(inflater, parent, false);
        return new RouteHolder(routeType, binding, itemClickListener, infoClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteHolder holder, int position) {
        holder.bindRoute(sortedList.get(position));
    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }

}
