package ru.mobnius.simple_core.data.packager.v2;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.mobnius.simple_core.data.packager.FileBinary;
import ru.mobnius.simple_core.data.packager.MetaPackage;
import ru.mobnius.simple_core.data.packager.MetaSize;
import ru.mobnius.simple_core.data.packager.PackageUtil;
import ru.mobnius.simple_core.data.rpc.RPCItem;
import ru.mobnius.simple_core.data.zip.ZipManager;
import ru.mobnius.simple_core.utils.PackageCreateUtils;

@RunWith(AndroidJUnit4.class)
public class PackageUtilTest {
    @Test
    public void getString() throws IOException {
        Assert.assertEquals("HelloWorld!!!", PackageUtil.getString(ZipManager.compress("HelloWorld!!!").getCompress(), true));
        Assert.assertEquals("HelloWorld!!!", PackageUtil.getString("HelloWorld!!!".getBytes(), true));
    }

    @Test
    public void readMetaTest() throws Exception {
        MetaSize ms = new MetaSize("{\"attachments\":[{\"key\":\"name\",\"name\":\"name.jpg\",\"size\":100}],\"binarySize\":0,\"bufferBlockFromLength\":1,\"bufferBlockToLength\":1,\"dataInfo\":\"full\",\"id\":\"\",\"stringSize\":100,\"transaction\":true,\"version\":\"1.1\"}".length(), 3, "NML");
        MetaPackage mp = PackageUtil.readMeta((ms.toJsonString() + "{\"attachments\":[{\"key\":\"name\",\"name\":\"name.jpg\",\"size\":100}],\"binarySize\":0,\"bufferBlockFromLength\":1,\"bufferBlockToLength\":1,\"dataInfo\":\"full\",\"id\":\"\",\"stringSize\":100,\"transaction\":true,\"version\":\"1.1\"}").getBytes(), false);
        Assert.assertTrue(mp.transaction);
        Assert.assertEquals(mp.attachments.length, 1);
        Assert.assertEquals(mp.binarySize, 0);
        Assert.assertEquals(mp.stringSize, 100);
        Assert.assertEquals(mp.version, "1.1");
        Assert.assertEquals(mp.dataInfo, "full");
        Assert.assertEquals(mp.bufferBlockToLength, 1);
        Assert.assertEquals(mp.bufferBlockFromLength, 1);
    }

}
