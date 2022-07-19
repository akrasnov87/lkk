package ru.mobnius.cic.concurent;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import ru.mobnius.cic.data.manager.DataManager;
import ru.mobnius.cic.data.search.AndPointFilter;
import ru.mobnius.cic.data.search.AndRouteFilter;
import ru.mobnius.cic.data.search.OrPointFilter;
import ru.mobnius.cic.data.search.OrRouteFilter;
import ru.mobnius.cic.data.search.SearchResult;

/**
 * Класс обрабаывающий выполнения глобального поиска
 * в дополнительном потоке
 */
public class SearchResultTask implements Callable<List<SearchResult>> {
    @NonNull
    private final String searchQuery;
    @NonNull
    private final OrPointFilter orPointFilter;
    @NonNull
    private final AndPointFilter andPointFilter;
    @NonNull
    private final OrRouteFilter orRouteFilter;
    @NonNull
    private final AndRouteFilter andRouteFilter;
    private final int mode;

    public SearchResultTask(final @NonNull String searchQuery,
                            final @NonNull OrPointFilter orPointFilter,
                            final @NonNull AndPointFilter andPointFilter,
                            final @NonNull OrRouteFilter orRouteFilter,
                            final @NonNull AndRouteFilter andRouteFilter,
                            final int mode) {
        this.searchQuery = searchQuery;
        this.orPointFilter = orPointFilter;
        this.andPointFilter = andPointFilter;
        this.orRouteFilter = orRouteFilter;
        this.andRouteFilter = andRouteFilter;
        this.mode = mode;
    }

    @NonNull
    @Override
    public List<SearchResult> call() throws Exception {
        if (DataManager.getInstance() == null) {
            return new ArrayList<>();
        }
        return DataManager.getInstance().getSearchResults(searchQuery, orPointFilter, andPointFilter, orRouteFilter, andRouteFilter, mode);
    }
}
