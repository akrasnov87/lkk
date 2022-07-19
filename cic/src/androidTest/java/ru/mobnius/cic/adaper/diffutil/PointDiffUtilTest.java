package ru.mobnius.cic.adaper.diffutil;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ru.mobnius.cic.ui.model.PointItem;

public class PointDiffUtilTest {
    private final List<PointItem> oldItems = new ArrayList<>();
    private final List<PointItem> newItems = new ArrayList<>();

    @Before
    public void setUp() {
        oldItems.add(PointItem.getTestInstance());
        newItems.add(PointItem.getTestInstance());
        oldItems.add(PointItem.getRandomTestInstance());
        newItems.add(PointItem.getRandomTestInstance());
    }

    @Test
    public void testDiffUtil() {
        final PointDiffUtil pointDiffUtil = new PointDiffUtil(oldItems, newItems);
        assertTrue(pointDiffUtil.areItemsTheSame(0, 0));
        assertTrue(pointDiffUtil.areContentsTheSame(0, 0));
        assertFalse(pointDiffUtil.areItemsTheSame(1, 1));
        assertFalse(pointDiffUtil.areContentsTheSame(1, 1));
    }

}