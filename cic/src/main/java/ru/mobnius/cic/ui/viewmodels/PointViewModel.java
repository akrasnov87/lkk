package ru.mobnius.cic.ui.viewmodels;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.mobnius.cic.Names;
import ru.mobnius.cic.R;
import ru.mobnius.cic.concurent.LoadPointsTask;
import ru.mobnius.cic.concurent.MainTaskExecutor;
import ru.mobnius.cic.concurent.MainTaskExecutorCallback;
import ru.mobnius.cic.concurent.RefreshPointTask;
import ru.mobnius.cic.data.search.AndPointFilter;
import ru.mobnius.cic.data.search.BasePointFilter;
import ru.mobnius.cic.data.search.OrPointFilter;
import ru.mobnius.cic.data.search.PointCompanyFilter;
import ru.mobnius.cic.data.search.PointFilter;
import ru.mobnius.cic.data.search.PointFilterAddress;
import ru.mobnius.cic.data.search.PointFilterDeviceNumber;
import ru.mobnius.cic.data.search.PointFilterDone;
import ru.mobnius.cic.data.search.PointFilterOwnerName;
import ru.mobnius.cic.data.search.PointFilterSubscrNumber;
import ru.mobnius.cic.data.search.PointFilterUndone;
import ru.mobnius.cic.data.search.PointPersonFilter;
import ru.mobnius.cic.ui.model.PointItem;
import ru.mobnius.cic.ui.model.concurent.SavedResult;
import ru.mobnius.simple_core.utils.StringUtil;

public class PointViewModel extends ViewModel {
    public final int DEFAULT_SORT = 0;
    public final int ADDRESS_SORT = 1;
    public final int OWNER_NAME_SORT = 2;
    public final int DEVICE_NUMBER_SORT = 3;
    public final int SUBSCR_NUMBER_SORT = 4;

    @Nullable
    public String routeId;
    @Nullable
    public String routeTitle;
    @NonNull
    public String query = StringUtil.EMPTY;
    @Nullable
    public MutableLiveData<List<PointItem>> pointItemsLiveData;
    @Nullable
    public MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();
    @Nullable
    private MainTaskExecutor executor;

    private int searchAreaMode;

    @NonNull
    public List<PointItem> loadedItems = new ArrayList<>();
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
    public final OrPointFilter orFilter = new OrPointFilter(new ArrayList<>());
    @NonNull
    public final AndPointFilter andFilter = new AndPointFilter(new ArrayList<>());
    @NonNull
    private final List<BasePointFilter> allFilters = new ArrayList<>();
    private int sortType = 0;

    public PointViewModel() {
        allFilters.add(addressFilter);
        allFilters.add(ownerNameFilter);
        allFilters.add(subscrFilter);
        allFilters.add(deviceFilter);
        allFilters.add(doneFilter);
        allFilters.add(undoneFilter);
        allFilters.add(personTypeFilter);
        allFilters.add(companyTypeFilter);

        for (final BasePointFilter pointFilter : allFilters) {
            if (pointFilter.isAndFilter()) {
                andFilter.filterList.add(pointFilter);
            } else {
                orFilter.filterList.add(pointFilter);
            }
        }
    }

    public void setStatusFilter(final int status) {
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

    public void setTypeFilter(final int type) {
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

    public void setAreaFilter(final int type) {
        searchAreaMode = type;
        switch (type) {
            case PointFilter.AREA_ADDRESS:
                addressFilter.addFilter();
                deviceFilter.removeFilter();
                subscrFilter.removeFilter();
                ownerNameFilter.removeFilter();
                break;
            case PointFilter.AREA_DEVICE_NUMBER:
                addressFilter.removeFilter();
                deviceFilter.addFilter();
                subscrFilter.removeFilter();
                ownerNameFilter.removeFilter();
                break;
            case PointFilter.AREA_SUBSCR_NUMBER:
                addressFilter.removeFilter();
                deviceFilter.removeFilter();
                subscrFilter.addFilter();
                ownerNameFilter.removeFilter();
                break;
            case PointFilter.AREA_OWNER_NAME:
                addressFilter.removeFilter();
                deviceFilter.removeFilter();
                subscrFilter.removeFilter();
                ownerNameFilter.addFilter();
                break;
            default:
                addressFilter.addFilter();
                deviceFilter.addFilter();
                subscrFilter.addFilter();
                ownerNameFilter.addFilter();
                break;
        }
    }

    public int countFilter() {
        int count = 0;
        for (final BasePointFilter filter : orFilter.filterList) {
            if (filter.isNotAdded()) {
                count++;
                break;
            }
        }
        for (final BasePointFilter filter : andFilter.filterList) {
            if (filter.isAdded()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Установка нового состояния фильтра
     */
    public void setNewState() {
        for (final BasePointFilter filter : allFilters) {
            filter.setNewState();
        }
        refreshPointItems();
    }

    /**
     * Возврат к предыдущему состоянию фильтра если новый не был выбран
     */
    public void returnToState() {
        for (final BasePointFilter filter : allFilters) {
            filter.returnToState();
        }
    }

    public boolean isDifferent() {
        for (final BasePointFilter filter : allFilters) {
            if (filter.isDifferent()) {
                return true;
            }
        }
        return false;
    }

    public boolean isAllFields() {
        return searchAreaMode == PointFilter.AREA_ALL;
    }

    public boolean isSubscrNumberOnly() {
        return searchAreaMode == PointFilter.AREA_SUBSCR_NUMBER;
    }

    public boolean isDeviceNumberOnly() {
        return searchAreaMode == PointFilter.AREA_DEVICE_NUMBER;
    }

    public boolean isAddressOnly() {
        return searchAreaMode == PointFilter.AREA_ADDRESS;
    }

    public boolean isOwnerOnly() {
        return searchAreaMode == PointFilter.AREA_OWNER_NAME;
    }

    @NonNull
    public MutableLiveData<List<PointItem>> getPointItems() {
        if (pointItemsLiveData == null) {
            pointItemsLiveData = new MutableLiveData<>();
            setLoading();
            loadPoints(pointItemsLiveData);
        }
        return pointItemsLiveData;
    }

    @NonNull
    public MutableLiveData<List<PointItem>> refreshPointItems() {
        if (pointItemsLiveData == null) {
            pointItemsLiveData = new MutableLiveData<>();
        }
        setLoading();
        refresh(pointItemsLiveData);
        return pointItemsLiveData;
    }

    private void loadPoints(final @NonNull MutableLiveData<List<PointItem>> liveData) {
        if (executor != null) {
            executor = null;
        }
        executor = new MainTaskExecutor();
        if (StringUtil.isEmpty(routeId)) {
            return;
        }
        final LoadPointsTask loadPointsTask = new LoadPointsTask(routeId, query, orFilter, andFilter);
        executor.executeAsync(loadPointsTask, new MainTaskExecutorCallback<List<PointItem>>() {
            @Override
            public void onCallableComplete(@NonNull List<PointItem> result) {

                loadedItems = result;
                postNotLoading();
                liveData.postValue(loadedItems);
            }

            @Override
            public void onCallableError(@NonNull String error) {
                postNotLoading();
                liveData.postValue(loadedItems);
            }
        });
    }

    private void refresh(final @NonNull MutableLiveData<List<PointItem>> liveData) {
        if (loadedItems.size() == 0) {
            return;
        }
        if (executor != null) {
            executor = null;
        }
        executor = new MainTaskExecutor();
        if (StringUtil.isEmpty(routeId)) {
            return;
        }
        final RefreshPointTask refreshPointTask = new RefreshPointTask(query, new ArrayList<>(loadedItems), orFilter, andFilter, getComparator());
        executor.executeAsync(refreshPointTask, new MainTaskExecutorCallback<List<PointItem>>() {
            @Override
            public void onCallableComplete(@NonNull List<PointItem> result) {
                postNotLoading();
                liveData.postValue(result);
            }

            @Override
            public void onCallableError(@NonNull String error) {
                postNotLoading();
                liveData.postValue(loadedItems);
            }
        });
    }

    public void setNewSort(final long newSort) {
        sortType = (int) newSort;
    }

    public void setNewSearch(final String newSearch) {
        this.query = newSearch;
    }

    public int getSortType() {
        return sortType;
    }

    @NotNull
    public ArrayList<Map<String, Object>> getSortTypesData(Context context) {
        final ArrayList<Map<String, Object>> sortTypeMaps = new ArrayList<>();
        final Map<String, Object> def = new HashMap<>();
        def.put(Names.ID, DEFAULT_SORT);
        def.put(Names.NAME, context.getString(R.string.on_default));
        sortTypeMaps.add(def);
        final Map<String, Object> address = new HashMap<>();
        address.put(Names.ID, ADDRESS_SORT);
        address.put(Names.NAME, context.getString(R.string.address));
        sortTypeMaps.add(address);
        final Map<String, Object> owner = new HashMap<>();
        owner.put(Names.ID, OWNER_NAME_SORT);
        owner.put(Names.NAME, context.getString(R.string.owner_name_sort));
        sortTypeMaps.add(owner);
        final Map<String, Object> deviceNumber = new HashMap<>();
        deviceNumber.put(Names.ID, DEVICE_NUMBER_SORT);
        deviceNumber.put(Names.NAME, context.getString(R.string.device_number_sort));
        sortTypeMaps.add(deviceNumber);
        final Map<String, Object> subscrNumber = new HashMap<>();
        subscrNumber.put(Names.ID, SUBSCR_NUMBER_SORT);
        subscrNumber.put(Names.NAME, context.getString(R.string.subscr_number_sort));
        sortTypeMaps.add(subscrNumber);
        return sortTypeMaps;
    }

    @Nullable
    private Comparator<PointItem> getComparator() {
        switch (sortType) {
            case OWNER_NAME_SORT:
                return (o1, o2) -> {
                    final Collator collator = Collator.getInstance(Locale.getDefault());
                    collator.setStrength(Collator.PRIMARY);
                    return collator.compare(o1.owner, o2.owner);
                };
            case DEVICE_NUMBER_SORT:
                return (o1, o2) -> {
                    final Collator collator = Collator.getInstance(Locale.getDefault());
                    collator.setStrength(Collator.PRIMARY);
                    return collator.compare(o1.deviceNumber, o2.deviceNumber);
                };
            case SUBSCR_NUMBER_SORT:
                return (o1, o2) -> {
                    final Collator collator = Collator.getInstance(Locale.getDefault());
                    collator.setStrength(Collator.PRIMARY);
                    return collator.compare(o1.accountNumber, o2.accountNumber);
                };
            case ADDRESS_SORT:
                return (o1, o2) -> {
                    final Collator collator = Collator.getInstance(Locale.getDefault());
                    collator.setStrength(Collator.PRIMARY);
                    return collator.compare(o1.address, o2.address);
                };
            default:
                return null;
        }
    }

    private void setLoading() {
        if (loadingLiveData == null) {
            loadingLiveData = new MutableLiveData<>();
        }
        loadingLiveData.setValue(true);
    }

    private void postNotLoading() {
        if (loadingLiveData == null) {
            loadingLiveData = new MutableLiveData<>();
        }
        loadingLiveData.postValue(false);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor = null;
        routeId = null;
        routeTitle = null;
        pointItemsLiveData = null;
        loadingLiveData = null;
    }

    public void setPointDone(final @NonNull SavedResult savedResult) {
        for (int i = 0; i < loadedItems.size(); i++) {
            if (StringUtil.equalsIgnoreCase(loadedItems.get(i).id, savedResult.pointId)) {
                loadedItems.get(i).done = true;
                loadedItems.get(i).resultId = savedResult.resultId;
                break;
            }
        }
    }

    public void setPointUnDone(final @NonNull SavedResult savedResult) {
        for (int i = 0; i < loadedItems.size(); i++) {
            if (StringUtil.equalsIgnoreCase(loadedItems.get(i).id, savedResult.pointId)) {
                loadedItems.get(i).done = false;
                loadedItems.get(i).resultId = null;
                break;
            }
        }
    }
}
