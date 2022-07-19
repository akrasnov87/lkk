package ru.mobnius.cic.ui.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.mobnius.cic.data.manager.DataManager;
import ru.mobnius.simple_core.utils.StringUtil;

@SuppressWarnings("StringOperationCanBeSimplified")
public class ImageType implements Parcelable {

    @NonNull
    public final String typeName;
    @NonNull
    public final String typeConst;
    @NonNull
    public String notice;
    @Nullable
    public String imageId;
    public final long typeId;
    public final boolean isDefault;

    public ImageType(final long typeId,
                     final @NonNull String typeName,
                     final @NonNull String typeConst,
                     final @NonNull String notice,
                     final boolean isDefault) {
        this.typeName = typeName;
        this.typeConst = typeConst;
        this.notice = notice;
        this.typeId = typeId;
        this.isDefault = isDefault;
    }

    public ImageType(final @NonNull ImageType anotherImageType) {
        if (StringUtil.isNotEmpty(anotherImageType.imageId)) {

            this.imageId = new String(anotherImageType.imageId);
        }
        this.typeName = new String(anotherImageType.typeName);
        this.typeConst = anotherImageType.typeConst;
        this.notice = new String(anotherImageType.notice);
        this.typeId = anotherImageType.typeId;
        this.isDefault = anotherImageType.isDefault;
    }

    protected ImageType(@NonNull Parcel in) {
        typeName = StringUtil.defaultEmptyString(in.readString());
        typeConst = StringUtil.defaultEmptyString(in.readString());
        notice = StringUtil.defaultEmptyString(in.readString());
        imageId = in.readString();
        typeId = in.readLong();
        isDefault = in.readByte() != 0;
    }


    public boolean isMeterPhoto() {
        return StringUtil.equalsIgnoreCase(DataManager.METER_PHOTO_CONST, typeConst);
    }

    public boolean isSealPhoto() {
        return StringUtil.equalsIgnoreCase(DataManager.SEAL_PHOTO_CONST, typeConst);
    }

    public boolean isDevicePhoto() {
        return StringUtil.equalsIgnoreCase(DataManager.DEVICE_PHOTO_CONST, typeConst);
    }

    public static final Creator<ImageType> CREATOR = new Creator<ImageType>() {
        @NonNull
        @Override
        public ImageType createFromParcel(@NonNull Parcel in) {
            return new ImageType(in);
        }

        @NonNull
        @Override
        public ImageType[] newArray(int size) {
            return new ImageType[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(typeName);
        parcel.writeString(typeConst);
        parcel.writeString(notice);
        parcel.writeString(imageId);
        parcel.writeLong(typeId);
        parcel.writeByte((byte) (isDefault ? 1 : 0));
    }

    public boolean isNotSame(final @NonNull ImageType imageType) {
        if (StringUtil.notEquals(this.typeName, imageType.typeName)) {
            return true;
        }
        if (StringUtil.notEquals(this.typeConst, imageType.typeConst)) {
            return true;
        }
        return this.typeId != imageType.typeId;
    }
}
