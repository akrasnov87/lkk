package ru.mobnius.cic.adaper;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import ru.mobnius.cic.adaper.holder.point.OnPointItemClickListener;
import ru.mobnius.cic.adaper.holder.route.OnRouteItemClickListener;
import ru.mobnius.cic.adaper.holder.search.SearchHeaderHolder;
import ru.mobnius.cic.adaper.holder.search.SearchResultHolder;
import ru.mobnius.cic.data.search.SearchResult;
import ru.mobnius.cic.databinding.ItemSearchHeaderBinding;
import ru.mobnius.cic.databinding.ItemSearchResultBinding;
import ru.mobnius.cic.ui.model.SearchHeader;
import ru.mobnius.simple_core.utils.StringUtil;

public class SearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @NonNull
    public final SortedList<SearchResult> sortedList;
    @NonNull
    public final OnPointItemClickListener pointItemClickListener;
    @NonNull
    public final OnRouteItemClickListener routeItemClickListener;
    @NonNull
    private final Comparator<SearchResult> comparator = (a, b) -> {
        if (a.getPriority() == b.getPriority()) {
            final Collator collator = Collator.getInstance(Locale.getDefault());
            collator.setStrength(Collator.PRIMARY);
            if (StringUtil.notEquals(a.getFirstLineText(), b.getFirstLineText())) {
                return collator.compare(a.getFirstLineText(), b.getFirstLineText());
            }
            if (StringUtil.notEquals(a.getSecondLineText(), b.getSecondLineText())) {
                return collator.compare(a.getSecondLineText(), b.getSecondLineText());
            }
            if (StringUtil.notEquals(a.getThirdLineText(), b.getThirdLineText())) {
                return collator.compare(a.getThirdLineText(), b.getThirdLineText());
            }
            if (StringUtil.notEquals(a.getFourthLineText(), b.getFourthLineText())) {
                return collator.compare(a.getFourthLineText(), b.getFourthLineText());
            }
            return collator.compare(a.getId(), b.getId());

        }
        return Integer.compare(a.getPriority(), b.getPriority());
    };
    @NonNull
    private final Map<String, SearchResult> uniqueMapping = new HashMap<>();

    public SearchResultAdapter(final @NonNull List<SearchResult> searchResults,
                               final @NonNull OnPointItemClickListener pointItemClickListener,
                               final @NonNull OnRouteItemClickListener routeItemClickListener) {
        this.pointItemClickListener = pointItemClickListener;
        this.routeItemClickListener = routeItemClickListener;
        final SortedList.Callback<SearchResult> callback = new SortedList.Callback<SearchResult>() {
            @Override
            public int compare(SearchResult o1, SearchResult o2) {
                return comparator.compare(o1, o2);
            }

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
            public boolean areContentsTheSame(SearchResult oldItem, SearchResult newItem) {
                if (StringUtil.notEquals(oldItem.getFirstLineText(), newItem.getFirstLineText())) {
                    return false;
                }
                if (StringUtil.notEquals(oldItem.getSecondLineText(), newItem.getSecondLineText())) {
                    return false;
                }
                if (StringUtil.notEquals(oldItem.getThirdLineText(), newItem.getThirdLineText())) {
                    return false;
                }
                if (StringUtil.notEquals(oldItem.getFourthLineText(), newItem.getFourthLineText())) {
                    return false;
                }
                return StringUtil.equals(oldItem.getId(), newItem.getId());
            }

            @Override
            public boolean areItemsTheSame(SearchResult item1, SearchResult item2) {
                return StringUtil.equals(item1.getId(), item2.getId());
            }
        };
        sortedList = new SortedList<>(SearchResult.class, callback);
        insertAll(searchResults);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == SearchResult.VIEW_TYPE_HEADER) {
            final ItemSearchHeaderBinding itemSearchHeaderBinding = ItemSearchHeaderBinding.inflate(inflater, parent, false);
            return new SearchHeaderHolder(itemSearchHeaderBinding);
        } else {
            final ItemSearchResultBinding itemSearchResultBinding = ItemSearchResultBinding.inflate(inflater, parent, false);
            return new SearchResultHolder(itemSearchResultBinding, pointItemClickListener, routeItemClickListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == SearchResult.VIEW_TYPE_HEADER) {
            final SearchHeader header = (SearchHeader) sortedList.get(position);
            final SearchHeaderHolder searchHeaderHolder = (SearchHeaderHolder) holder;
            searchHeaderHolder.bind(header);
        } else {
            final SearchResult result = sortedList.get(position);
            final SearchResultHolder searchResultHolder = (SearchResultHolder) holder;
            searchResultHolder.bind(result);
        }
    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return sortedList.get(position).getViewType();
    }

    public void insert(final @NonNull SearchResult item) {
        final String key = item.getId();
        final SearchResult existing = uniqueMapping.put(key, item);
        if (existing == null) {
            sortedList.add(item);
        } else {
            int pos = sortedList.indexOf(existing);
            if (pos != -1) {
                sortedList.updateItemAt(pos, item);
            }
        }
    }


    public void insertAll(final @NonNull List<SearchResult> items) {
        for (final SearchResult item : items) {
            insert(item);
        }
    }

    public void swapList(final @NonNull List<SearchResult> items) {
        final Set<String> newListKeys = new HashSet<>();
        for (final SearchResult item : items) {
            newListKeys.add(item.getId());
        }
        for (int i = sortedList.size() - 1; i >= 0; i--) {
            final SearchResult item = sortedList.get(i);
            final String key = item.getId();
            if (!newListKeys.contains(key)) {
                uniqueMapping.remove(key);
                sortedList.removeItemAt(i);
            }
        }
        insertAll(items);
    }
}
