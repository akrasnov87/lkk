package ru.mobnius.simple_core.data.packager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;

import ru.mobnius.simple_core.BaseApp;
import ru.mobnius.simple_core.data.rpc.RPCItem;
import ru.mobnius.simple_core.data.rpc.RPCResult;
import ru.mobnius.simple_core.data.zip.ZipManager;
import ru.mobnius.simple_core.utils.StringUtil;

public class PackageUtil {

    @NonNull
    public static MetaSize readSize(@Nullable byte[] bytes) throws Exception {
        if (bytes != null && bytes.length >= 16) {
            final StringBuilder str = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                str.append(new String(new byte[]{bytes[i]}));
            }
            try {
                final String type = str.substring(0, 3);
                final int status = Integer.parseInt(str.substring(15));
                final String sizeStr = str.substring(3, 15).replace(StringUtil.DOT, StringUtil.EMPTY);
                try {
                    return new MetaSize(Integer.parseInt(sizeStr), status, type);
                } catch (Exception e) {
                    throw new Exception(BaseApp.META_INFO_READ_ERROR + sizeStr);
                }
            } catch (Exception e2) {
                throw new Exception(BaseApp.ERROR_READ_PACKAGE_STATUS + str);
            }
        } else {
            throw new Exception(BaseApp.PACKAGE_LENGTH_LESS_THEN_16);
        }
    }

    @NonNull
    public static MetaPackage readMeta(final @Nullable byte[] bytes, final boolean zip) throws Exception {
        if (bytes == null) {
            throw new Exception(BaseApp.PACKAGE_LENGTH_LESS_THEN_16);
        }
        return new Gson().fromJson(getString(Arrays.copyOfRange(bytes, 16, readSize(bytes).metaSize + 16), zip), MetaPackage.class);
    }

    @NonNull
    public static List<StringMapItem> readMap(final @Nullable byte[] bytes, final boolean zip) throws Exception {
        if (bytes == null) {
            throw new Exception(BaseApp.PACKAGE_LENGTH_LESS_THEN_16);
        }
        final MetaSize metaSize = readSize(bytes);
        final MetaPackage aPackage = readMeta(bytes, zip);
        final int start = metaSize.metaSize + 16;
        return Arrays.asList(new Gson().fromJson(getString(Arrays.copyOfRange(bytes, start, aPackage.stringSize + start), zip), StringMapItem[].class));
    }

    @NonNull
    public static RPCResult[] readMapItemResult(final @Nullable byte[] bytes, final int idx, final boolean zip) throws Exception {
        if (bytes == null) {
            throw new Exception(BaseApp.PACKAGE_LENGTH_LESS_THEN_16);
        }
        final List<StringMapItem> mapItems = readMap(bytes, zip);
        int start = readSize(bytes).metaSize + 16 + readMeta(bytes, zip).stringSize;
        for (int i = 0; i < idx; i++) {
            start += mapItems.get(i).length;
        }
        return RPCResult.createInstanceByGson(getString(Arrays.copyOfRange(bytes, start, mapItems.get(idx).length + start), zip));
    }

    @NonNull
    public static BinaryBlock readBinaryBlock(final @Nullable byte[] bytes, final boolean zip) throws Exception {
        if (bytes == null) {
            throw new Exception(BaseApp.PACKAGE_LENGTH_LESS_THEN_16);
        }
        final MetaSize metaSize = readSize(bytes);
        final MetaPackage aPackage = readMeta(bytes, zip);
        final int start = metaSize.metaSize + 16 + aPackage.stringSize + aPackage.bufferBlockToLength + aPackage.bufferBlockFromLength;
        final int end = aPackage.binarySize + start;
        final BinaryBlock binaryBlock = new BinaryBlock();
        if (start == end) {
            return binaryBlock;
        }
        final byte[] temp = Arrays.copyOfRange(bytes, start, end);
        int idx = 0;
        for (final MetaAttachment attachment : aPackage.attachments) {
            final byte[] t = new byte[attachment.size];
            System.arraycopy(temp, idx, t, 0, attachment.size);
            idx += attachment.size;
            binaryBlock.add(attachment.name, attachment.key, t);
        }
        return binaryBlock;
    }

    @NonNull
    public static String getString(final @NonNull byte[] temp, final boolean zip) {
        if (!zip) {
            return new String(temp);
        }
        try {
            byte[] zipBytes = ZipManager.decompress(temp);
            if (zipBytes != null) {
                return new String(zipBytes);
            }
            return new String(temp);
        } catch (IOException | DataFormatException e) {
            return new String(temp);
        }
    }
}
