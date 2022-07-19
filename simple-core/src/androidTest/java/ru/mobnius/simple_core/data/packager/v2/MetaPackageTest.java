package ru.mobnius.simple_core.data.packager.v2;

import org.junit.Assert;
import org.junit.Test;

import ru.mobnius.simple_core.data.packager.MetaAttachment;
import ru.mobnius.simple_core.data.packager.MetaPackage;

public class MetaPackageTest {
    @Test
    public void toJsonString() {
        MetaAttachment[] metaAttachments = {new MetaAttachment(100, "name.jpg", "id")};
        MetaPackage meta = new MetaPackage("",
                metaAttachments,
                "full",
                "1.1",
                0,
                1,
                1,
                100,
                true);
        Assert.assertEquals(meta.toJsonString(), "{\"attachments\":[{\"key\":\"name\",\"name\":\"name.jpg\",\"size\":100}],\"binarySize\":0,\"bufferBlockFromLength\":1,\"bufferBlockToLength\":1,\"dataInfo\":\"full\",\"id\":\"\",\"stringSize\":100,\"transaction\":true,\"version\":\"1.1\"}");
    }
}