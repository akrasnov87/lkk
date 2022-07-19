package ru.mobnius.simple_core.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.mobnius.simple_core.data.packager.FileBinary;
import ru.mobnius.simple_core.data.packager.MetaPackage;
import ru.mobnius.simple_core.data.packager.MetaSize;
import ru.mobnius.simple_core.data.packager.PackageUtil;
import ru.mobnius.simple_core.data.packager.StringMapItem;
import ru.mobnius.simple_core.data.rpc.RPCItem;
import ru.mobnius.simple_core.data.rpc.RPCResult;

public class PackageReadUtils {
    private static final String TO = "to";
    private static final String FROM = "from";
    @Nullable
    private byte[] all;
    private final boolean isZip;

    public PackageReadUtils(final @NonNull byte[] bytes, final boolean isZip) {
        this.all = bytes;
        this.isZip = isZip;
    }

    public int getLength() {
        if (all == null) {
            return 0;
        }
        return all.length;
    }

    @NonNull
    public MetaSize getMetaSize() throws Exception {
        return PackageUtil.readSize(all);
    }

    @Nullable
    public MetaPackage getMeta() throws Exception {
        return PackageUtil.readMeta(all, isZip);
    }

    @NonNull
    public FileBinary[] getFiles() throws Exception {
        return PackageUtil.readBinaryBlock(all, isZip).getFiles();
    }

    @NonNull
    public RPCResult[] getToResult() throws Exception {
        final List<RPCResult> rpcItems = new ArrayList<>();
        final List<StringMapItem> mapItems = PackageUtil.readMap(all, isZip);
        int idx = 0;
        for (final StringMapItem mapItem : mapItems) {
            if (mapItem.name.startsWith(TO)) {
                rpcItems.addAll(Arrays.asList(PackageUtil.readMapItemResult(all, idx, isZip)));
            }
            idx++;
        }
        return rpcItems.toArray(new RPCResult[0]);
    }

    @NonNull
    public RPCResult[] getFromResult() throws Exception {
        final List<RPCResult> rpcItems = new ArrayList<>();
        final List<StringMapItem> mapItems = PackageUtil.readMap(all, isZip);
        int idx = 0;
        for (final StringMapItem mapItem : mapItems) {
            if (mapItem.name.startsWith(FROM)) {
                rpcItems.addAll(Arrays.asList(PackageUtil.readMapItemResult(all, idx, isZip)));
            }
            idx++;
        }
        return rpcItems.toArray(new RPCResult[0]);
    }

    public void destroy() {
        all = null;
    }
}
