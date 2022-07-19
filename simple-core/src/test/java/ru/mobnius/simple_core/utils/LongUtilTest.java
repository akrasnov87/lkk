package ru.mobnius.simple_core.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LongUtilTest {

    @Test
    public void convertToLong() {
        assertEquals(LongUtil.getLongOrMinus("123"), 123);
    }
}