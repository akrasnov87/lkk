package ru.mobnius.cic.adaper.diffutil;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.mobnius.cic.ui.model.SyncLogItem;

public class SyncLogDiffUtilTest {

    @Test
    public void syncLogTest() {
        final SyncLogDiffUtil syncLogDiffUtil = new SyncLogDiffUtil();
        final SyncLogItem first = SyncLogItem.getTestInstanse();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final SyncLogItem second = SyncLogItem.getTestInstanse();
        assertFalse(syncLogDiffUtil.areItemsTheSame(first, second));
        assertTrue(syncLogDiffUtil.areContentsTheSame(first, second));
    }
}