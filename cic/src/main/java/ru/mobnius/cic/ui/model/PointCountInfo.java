package ru.mobnius.cic.ui.model;

public class PointCountInfo {
    public final int totalCount;
    public final int doneCount;
    public final int syncCount;

    public PointCountInfo(final int totalCount, final int doneCount, final int syncCount) {
        this.totalCount = totalCount;
        this.doneCount = doneCount;
        this.syncCount = syncCount;
    }
}
