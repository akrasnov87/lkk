package ru.mobnius.cic.ui.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.UUID;

import ru.mobnius.cic.data.search.SearchResult;
import ru.mobnius.simple_core.utils.StringUtil;

public class SearchHeader implements SearchResult {
    public final static int PROFILE_TYPE_POINT_HEADER = 0;
    public final static int PROFILE_TYPE_ROUTE_HEADER = 2;
    @NonNull
    private final String headerName;
    @NonNull
    private final String id;
    private final int profileType;

    public SearchHeader(final @NonNull String headerName, final int profileType) {
        this.headerName = headerName;
        id = UUID.randomUUID().toString();
        this.profileType = profileType;
    }

    @Override
    public int getViewType() {
        return SearchResult.VIEW_TYPE_HEADER;
    }

    @NonNull
    @Override
    public String getHeaderName() {
        return headerName;
    }

    @NonNull
    @Override
    public String getId() {
        return id;
    }

    @NonNull
    @Override
    public String getFirstLineText() {
        return StringUtil.EMPTY;
    }

    @NonNull
    @Override
    public String getSecondLineText() {
        return StringUtil.EMPTY;
    }

    @NonNull
    @Override
    public String getThirdLineText() {
        return StringUtil.EMPTY;
    }

    @NonNull
    @Override
    public String getFourthLineText() {
        return StringUtil.EMPTY;
    }

    @Override
    public int getPriority() {
        return profileType;
    }

    @Nullable
    @Override
    public PointItem getPointItem() {
        return null;
    }

    @Nullable
    @Override
    public RouteItem getRouteItem() {
        return null;
    }
}
