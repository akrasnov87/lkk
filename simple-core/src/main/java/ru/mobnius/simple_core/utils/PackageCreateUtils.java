package ru.mobnius.simple_core.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.mobnius.simple_core.data.configuration.PreferencesManager;
import ru.mobnius.simple_core.data.packager.BinaryBlock;
import ru.mobnius.simple_core.data.packager.MetaPackage;
import ru.mobnius.simple_core.data.packager.MetaSize;
import ru.mobnius.simple_core.data.packager.StringMapItem;
import ru.mobnius.simple_core.data.rpc.RPCItem;
import ru.mobnius.simple_core.data.zip.ZipManager;

public class PackageCreateUtils {
    @Nullable
    private BinaryBlock binaryBlock;
    @Nullable
    private ArrayList<RPCItem> from;
    private final boolean isZip;
    @Nullable
    private ArrayList<RPCItem> to;

    public PackageCreateUtils(final boolean isZip) {
        this.isZip = isZip;
        from = new ArrayList<>();
        to = new ArrayList<>();
        binaryBlock = new BinaryBlock();
    }

    public void setBinaryBlock(final @NonNull BinaryBlock block) {
        binaryBlock = block;
    }

    @NonNull
    public PackageCreateUtils addTo(final @NonNull RPCItem to) {
        if (this.to == null) {
            this.to = new ArrayList<>();
        }
        this.to.add(to);
        return this;
    }

    public void addAllTo(final @NonNull RPCItem[] to) {
        if (this.to == null) {
            this.to = new ArrayList<>();
        }
        this.to.addAll(Arrays.asList(to));
    }

    @NonNull
    public PackageCreateUtils addFrom(final @NonNull RPCItem from) {
        if (this.from == null) {
            this.from = new ArrayList<>();
        }
        this.from.add(from);
        return this;
    }

    @NonNull
    public PackageCreateUtils addAllFrom(final @NonNull RPCItem[] from) {
        if (this.from == null) {
            this.from = new ArrayList<>();
        }
        this.from.addAll(Arrays.asList(from));
        return this;
    }

    @NonNull
    public PackageCreateUtils addFile(final @NonNull String name, final @NonNull String key, final @NonNull byte[] bytes) {
        if (binaryBlock == null) {
            binaryBlock = new BinaryBlock();
        }
        binaryBlock.add(name, key, bytes);
        return this;
    }

    @NonNull
    public byte[] generatePackage(final @NonNull String tid, final boolean transaction) throws IOException {
        int bufferBlockToLength = 0;
        int bufferBlockFromLength = 0;

        byte[] mBytes;
        byte[] fromBuffer;
        byte[] toBuffer;

        final Gson json = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
        final ArrayList<StringMapItem> stringMapArray = new ArrayList<>();
        if (to == null) {
            to = new ArrayList<>();
        }
        final List<byte[]> bufferBlockTo = new ArrayList<>(to.size());

        int idx = 0;
        for (final RPCItem rpcItem : to) {
            final String str = json.toJson(rpcItem);
            toBuffer = isZip ? ZipManager.compress(str).getCompress() : str.getBytes();

            bufferBlockTo.add(toBuffer);
            bufferBlockToLength += toBuffer.length;
            stringMapArray.add(new StringMapItem("to" + idx, toBuffer.length));
            idx++;
        }
        if (from == null) {
            from = new ArrayList<>();
        }
        final List<byte[]> bufferBlockFrom = new ArrayList<>(from.size());

        idx = 0;
        for (final RPCItem rpcItem : this.from) {
            final String str = json.toJson(rpcItem);
            fromBuffer = isZip ? ZipManager.compress(str).getCompress() : str.getBytes();
            bufferBlockFrom.add(fromBuffer);
            bufferBlockFromLength += fromBuffer.length;
            stringMapArray.add(new StringMapItem("from" + idx, fromBuffer.length));
            idx++;
        }

        final String stringMap = json.toJson(stringMapArray);
        final byte[] stringMapBytes = isZip ? ZipManager.compress(stringMap).getCompress() : stringMap.getBytes();

        if (binaryBlock == null) {
            binaryBlock = new BinaryBlock();
        }

        final byte[] binaryBlockBytes = binaryBlock.toBytes();

        final MetaPackage meta = new MetaPackage(tid,
                binaryBlock.getAttachments(),
                StringUtil.EMPTY,
                PreferencesManager.SYNC_PROTOCOL_v2,
                binaryBlockBytes.length,
                bufferBlockFromLength,
                bufferBlockToLength,
                stringMapBytes.length,
                transaction);
        mBytes = isZip ? ZipManager.compress(meta.toJsonString()).getCompress() : meta.toJsonString().getBytes();

        final MetaSize ms = new MetaSize(mBytes.length, 0, isZip ? ZipManager.getMode() : "NML");
        final byte[] metaBytes = ms.toJsonString().getBytes();
        final ByteBuffer buff = ByteBuffer.wrap(new byte[(metaBytes.length + mBytes.length + stringMapBytes.length + bufferBlockFromLength + bufferBlockToLength + binaryBlockBytes.length)]);
        buff.put(metaBytes);
        buff.put(mBytes);
        buff.put(stringMapBytes);

        for (final byte[] b : bufferBlockTo) {
            buff.put(b);
        }

        for (final byte[] bytes : bufferBlockFrom) {
            buff.put(bytes);
        }
        buff.put(binaryBlockBytes);
        return buff.array();
    }

    @NonNull
    public byte[] generatePackage(final @NonNull String tid) throws IOException {
        return generatePackage(tid, true);
    }

    public void destroy() {
        if (to != null) {
            to.clear();
            to = null;
        }
        if (from != null) {
            from.clear();
            from = null;
        }
        if (binaryBlock != null) {
            binaryBlock.clear();
            binaryBlock = null;
        }
    }
}
