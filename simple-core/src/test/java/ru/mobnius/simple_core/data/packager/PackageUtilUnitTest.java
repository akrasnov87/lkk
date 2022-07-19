package ru.mobnius.simple_core.data.packager;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PackageUtilUnitTest {
    @Test
    public void readSizeTest() throws Exception {
        String str = "NML950.........3HelloWorld!!!";
        MetaSize size = PackageUtil.readSize(str.getBytes());
        assertEquals(size.metaSize, 950);
        assertEquals(size.status, MetaSize.PROCESSED);
        str = "NML9501........9HelloWorld!!!";
        size = PackageUtil.readSize(str.getBytes());
        assertEquals(size.metaSize, 9501);
        assertEquals(size.status, MetaSize.PROCESSED_ERROR);

        str = "NML9501.....";
        try {
            PackageUtil.readSize(str.getBytes());
            Assert.fail();
        }catch (Exception ignored){
        }
    }

    @Test
    public void updateStatusTest() throws Exception {
    }
}
