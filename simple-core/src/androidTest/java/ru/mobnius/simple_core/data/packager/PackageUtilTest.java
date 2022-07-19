package ru.mobnius.simple_core.data.packager;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import ru.mobnius.simple_core.data.rpc.RPCItem;
import ru.mobnius.simple_core.data.zip.ZipManager;
import ru.mobnius.simple_core.utils.PackageCreateUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PackageUtilTest {

    @Test
    public void getString() throws IOException {
        String inputString = "HelloWorld!!!";
        byte[] output = ZipManager.compress(inputString).getCompress();
        String result = PackageUtil.getString(output, true);
        assertEquals(inputString, result);
        result = PackageUtil.getString(inputString.getBytes(), true);
        assertEquals(inputString, result);
    }

    @Test
    public void readMetaTest() throws Exception {
        String meta = "{\"attachments\":[],\"binarySize\":0,\"dataInfo\":\"full\",\"stringSize\":100,\"transaction\":true,\"version\":\"1.1\"}";
        int size = meta.length();
        MetaSize ms = new MetaSize(size, MetaSize.PROCESSED, "NML");
        byte[] bytes = (ms.toJsonString() + meta).getBytes();
        MetaPackage mp = PackageUtil.readMeta(bytes, false);

        assertTrue(mp.transaction);
        assertEquals(mp.attachments.length, 0);
        assertEquals(mp.binarySize, 0);
        assertEquals(mp.stringSize, 100);
        assertEquals(mp.version, "1.1");
        assertEquals(mp.dataInfo, "full");
    }



}
