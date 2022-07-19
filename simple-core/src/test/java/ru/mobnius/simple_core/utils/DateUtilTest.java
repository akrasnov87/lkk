package ru.mobnius.simple_core.utils;

import org.junit.Test;

import ru.mobnius.simple_core.data.Version;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DateUtilTest {

    @Test
    public void convertDateToUserString() {
        String userDateString = DateUtil.getNonNullDateTextMiddle(Version.BIRTH_DAY);
        assertEquals("21.06.2020 00:00:00", userDateString);

        userDateString = DateUtil.convertDateToCustomString(Version.BIRTH_DAY, DateUtil.USER_FORMAT);
        assertEquals("21.06.2020 00:00:00", userDateString);
    }


    @Test
    public void geenerateTid() {
        assertTrue(DateUtil.geenerateTid() > 0);
    }
}