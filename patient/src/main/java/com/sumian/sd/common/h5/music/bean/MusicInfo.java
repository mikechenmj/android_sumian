package com.sumian.sd.common.h5.music.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class MusicInfo implements Parcelable {
    public String path;
    public int status;
    public int duration;
    public int currentPosition;

    public static final int STATUS_START = 0;
    public static final int STATUS_RESUME = 1;
    public static final int STATUS_PAUSE = 2;
    public static final int STATUS_STOP = 3;

    public MusicInfo(String path, int status, int duration, int currentPosition) {
        this.path = path;
        this.status = status;
        this.duration = duration;
        this.currentPosition = currentPosition;
    }

    protected MusicInfo(Parcel in) {
        path = in.readString();
        status = in.readInt();
        duration = in.readInt();
        currentPosition = in.readInt();
    }

    public static final Creator<MusicInfo> CREATOR = new Creator<MusicInfo>() {
        @Override
        public MusicInfo createFromParcel(Parcel in) {
            return new MusicInfo(in);
        }

        @Override
        public MusicInfo[] newArray(int size) {
            return new MusicInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeInt(status);
        dest.writeInt(duration);
        dest.writeInt(currentPosition);
    }
}
