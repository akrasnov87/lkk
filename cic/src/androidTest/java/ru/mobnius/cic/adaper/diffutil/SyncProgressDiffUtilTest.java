package ru.mobnius.cic.adaper.diffutil;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.mobnius.cic.ui.model.SyncProgressItem;

public class SyncProgressDiffUtilTest {

    @Test
    public void testSyncProgressDiffUtil() {
        final SyncProgressDiffUtil syncProgressDiffUtil = new SyncProgressDiffUtil();
        final SyncProgressItem first = SyncProgressItem.getTestInstanse();
        final SyncProgressItem second = SyncProgressItem.getTestInstanse();
        assertFalse(syncProgressDiffUtil.areItemsTheSame(first, second));
        assertTrue(syncProgressDiffUtil.areContentsTheSame(first, second));
    }
}