package ru.mobnius.cic.ui.model.concurent;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import ru.mobnius.simple_core.utils.StringUtil;

public class SavedResult implements Parcelable {
    @NonNull
    public final String pointId;
    @NonNull
    public final String resultId;
    public final boolean isSuccess;
    @NonNull
    public final String message;


    public SavedResult(final @NonNull String pointId,
                       final @NonNull String resultId,
                       final boolean isSuccess,
                       final @NonNull String message) {
        this.pointId = pointId;
        this.resultId = resultId;
        this.isSuccess = isSuccess;
        this.message = message;
    }



    public static final Creator<SavedResult> CREATOR = new Creator<SavedResult>() {
        @Override
        public SavedResult createFromParcel(Parcel in) {
            return new SavedResult(in);
        }

        @Override
        public SavedResult[] newArray(int size) {
            return new SavedResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(pointId);
        parcel.writeString(resultId);
        parcel.writeByte((byte) (isSuccess ? 1 : 0));
        parcel.writeString(message);
    }
    protected SavedResult(Parcel in) {
        pointId = StringUtil.defaultEmptyString(in.readString());
        resultId = StringUtil.defaultEmptyString(in.readString());
        isSuccess = in.readByte() != 0;
        message = StringUtil.defaultEmptyString(in.readString());
    }
}
