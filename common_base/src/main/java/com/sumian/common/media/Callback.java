package com.sumian.common.media;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sm
 * on 2018/1/28.
 * desc:
 */

public class Callback implements Parcelable {

    public Callback() {

    }

    protected Callback(Parcel in) {
    }

    public static final Creator<Callback> CREATOR = new Creator<Callback>() {
        @Override
        public Callback createFromParcel(Parcel in) {
            return new Callback(in);
        }

        @Override
        public Callback[] newArray(int size) {
            return new Callback[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }


    public void doSelected(String[] images) {

    }
}
