package ru.mobnius.cic.ui.viewmodels;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import ru.mobnius.cic.concurent.MainTaskExecutor;
import ru.mobnius.cic.concurent.MainTaskExecutorCallback;
import ru.mobnius.cic.concurent.SearchResultTask;
import ru.mobnius.cic.data.search.AndPointFilter;
import ru.mobnius.cic.data.search.AndRouteFilter;
import ru.mobnius.cic.data.search.BasePointFilter;
import ru.mobnius.cic.data.search.BaseRouteFilter;
import ru.mobnius.cic.data.search.OrPointFilter;
import ru.mobnius.cic.data.search.OrRouteFilter;
import ru.mobnius.cic.data.search.PointCompanyFilter;
import ru.mobnius.cic.data.search.PointFilter;
import ru.mobnius.cic.data.search.PointFilterAddress;
import ru.mobnius.cic.data.search.PointFilterDeviceNumber;
import ru.mobnius.cic.data.search.PointFilterDone;
import ru.mobnius.cic.data.search.PointFilterOwnerName;
import ru.mobnius.cic.data.search.PointFilterSubscrNumber;
import ru.mobnius.cic.data.search.PointFilterUndone;
import ru.mobnius.cic.data.search.PointPersonFilter;
import ru.mobnius.cic.data.search.RouteNoticeFilter;
import ru.mobnius.cic.data.search.RouteNumberFilter;
import ru.mobnius.cic.data.search.SearchResult;
import ru.mobnius.simple_core.utils.StringUtil;

public class SearchViewModel extends ViewModel {

    @Nullable
    private MutableLiveData<List<SearchResult>> searchResultLiveData;
    @NonNull
    public String searchQuery = StringUtil.EMPTY;
    @NonNull
    public final MainTaskExecutor executor = new MainTaskExecutor();

    @NonNull
    public List<SearchResult> currentItems = new ArrayList<>();

    public final int mode = SearchResult.ALL_MODE;
    @NonNull
    public final BasePointFilter addressFilter = new PointFilterAddress(true);
    @NonNull
    public final BasePointFilter ownerNameFilter = new PointFilterOwnerName(true);
    @NonNull
    public final BasePointFilter subscrFilter = new PointFilterSubscrNumber(true);
    @NonNull
    public final BasePointFilter deviceFilter = new PointFilterDeviceNumber(true);
    @NonNull
    public final BasePointFilter doneFilter = new PointFilterDone(false);
    @NonNull
    public final BasePointFilter undoneFilter = new PointFilterUndone(false);
    @NonNull
    public final BasePointFilter personTypeFilter = new PointPersonFilter(false);
    @NonNull
    public final BasePointFilter companyTypeFilter = new PointCompanyFilter(false);
    @NonNull
    public final OrPointFilter orPointFilter = new OrPointFilter(new ArrayList<>());
    @NonNull
    public final AndPointFilter andPointFilter = new AndPointFilter(new ArrayList<>());
    @NonNull
    private final List<BasePointFilter> allPointFilters = new ArrayList<>();
    @NonNull
    public final BaseRouteFilter numberFilter = new RouteNumberFilter(true);
    @NonNull
    public final BaseRouteFilter noticeFilter = new RouteNoticeFilter(true);
    @NonNull
    public final OrRouteFilter orRouteFilter = new OrRouteFilter(new ArrayList<>());
    @NonNull
    public final AndRouteFilter andRouteFilter = new AndRouteFilter(new ArrayList<>());
    @NonNull
    private final List<BaseRouteFilter> allRouteFilters = new ArrayList<>();

    public SearchViewModel() {
        allPointFilters.add(addressFilter);
        allPointFilters.add(subscrFilter);
        allPointFilters.add(deviceFilter);
        allPointFilters.add(ownerNameFilter);
        allPointFilters.add(doneFilter);
        allPointFilters.add(undoneFilter);
        allPointFilters.add(personTypeFilter);
        allPointFilters.add(companyTypeFilter);

        for (final BasePointFilter pointFilter : allPointFilters) {
            if (pointFilter.isAndFilter()) {
                andPointFilter.filterList.add(pointFilter);
            } else {
                orPointFilter.filterList.add(pointFilter);
            }
        }

        allRouteFilters.add(numberFilter);
        allRouteFilters.add(noticeFilter);
        for (final BaseRouteFilter routeFilter : allRouteFilters) {
            if (routeFilter.isAndFilter()) {
                andRouteFilter.filterList.add(routeFilter);
            } else {
                orRouteFilter.filterList.add(routeFilter);
            }
        }
    }

    public void setPointStatusFilter(final int status) {
        switch (status) {
            case PointFilter.STATUS_DONE:
                undoneFilter.removeFilter();
                doneFilter.addFilter();
                break;
            case PointFilter.STATUS_UNDONE:
                undoneFilter.addFilter();
                doneFilter.removeFilter();
                break;
            default:
                undoneFilter.removeFilter();
                doneFilter.removeFilter();
                break;
        }
    }

    public void setPointTypeFilter(final int type) {
        switch (type) {
            case PointFilter.TYPE_PERSON:
                companyTypeFilter.removeFilter();
                personTypeFilter.addFilter();
                break;
            case PointFilter.TYPE_COMPANY:
                companyTypeFilter.addFilter();
                personTypeFilter.removeFilter();
                break;
            default:
                companyTypeFilter.removeFilter();
                personTypeFilter.removeFilter();
                break;
        }
    }

    public void setNewState() {
        for (final BasePointFilter filter : allPointFilters) {
            filter.setNewState();
        }

        for (final BaseRouteFilter filter : allRouteFilters) {
            filter.setNewState();
        }
    }


    public void returnToState() {
        for (final BasePointFilter filter : allPointFilters) {
            filter.returnToState();
        }
        for (final BaseRouteFilter filter : allRouteFilters) {
            filter.returnToState();
        }
    }

    public boolean isDifferent() {
        for (final BasePointFilter filter : allPointFilters) {
            if (filter.isDifferent()) {
                return true;
            }
        }
        for (final BaseRouteFilter filter : allRouteFilters) {
            if (filter.isDifferent()) {
                return true;
            }
        }
        return false;
    }

    @NonNull
    public MutableLiveData<List<SearchResult>> getSearchResult() {
        if (searchResultLiveData == null) {
            searchResultLiveData = new MutableLiveData<>();

        }search();
        return searchResultLiveData;
    }

    private void search() {
        if (StringUtil.isEmpty(searchQuery)) {
           return;
        }
        executor.isRunning.set(false);
        final SearchResultTask searchResultTask = new SearchResultTask(searchQuery, orPointFilter, andPointFilter, orRouteFilter, andRouteFilter, mode);
        executor.executeAsync(searchResultTask, new MainTaskExecutorCallback<List<SearchResult>>() {
            @Override
            public void onCallableComplete(@NonNull List<SearchResult> result) {
                if (searchResultLiveData == null) {
                    return;
                }
                currentItems = result;
                searchResultLiveData.postValue(result);
            }

            @Override
            public void onCallableError(@NonNull String error) {
                if (searchResultLiveData == null) {
                    return;
                }
                searchResultLiveData.postValue(new ArrayList<>());
            }
        });
    }


}
