package com.sumian.common.media;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by haibin
 * on 17/2/27.
 */
public class SelectOptions implements Parcelable{
    private boolean isCrop;
    private int mCropWidth, mCropHeight;
    private Callback mCallback;
    private boolean hasCam;
    private int mSelectCount;
    private List<String> mSelectedImages;
    private String mSavePath;

    private SelectOptions() {

    }

    protected SelectOptions(Parcel in) {
        isCrop = in.readByte() != 0;
        mCropWidth = in.readInt();
        mCropHeight = in.readInt();
        mCallback = in.readParcelable(Callback.class.getClassLoader());
        hasCam = in.readByte() != 0;
        mSelectCount = in.readInt();
        mSelectedImages = in.createStringArrayList();
        mSavePath = in.readString();
    }

    public static final Creator<SelectOptions> CREATOR = new Creator<SelectOptions>() {
        @Override
        public SelectOptions createFromParcel(Parcel in) {
            return new SelectOptions(in);
        }

        @Override
        public SelectOptions[] newArray(int size) {
            return new SelectOptions[size];
        }
    };

    public boolean isCrop() {
        return isCrop;
    }

    public int getCropWidth() {
        return mCropWidth;
    }

    public int getCropHeight() {
        return mCropHeight;
    }

    public Callback getCallback() {
        return mCallback;
    }

    public boolean isHasCam() {
        return hasCam;
    }

    public int getSelectCount() {
        return mSelectCount;
    }

    public List<String> getSelectedImages() {
        return mSelectedImages;
    }

    public String getSavePath() {
        return mSavePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isCrop ? 1 : 0));
        dest.writeInt(mCropWidth);
        dest.writeInt(mCropHeight);
        dest.writeParcelable(mCallback, flags);
        dest.writeByte((byte) (hasCam ? 1 : 0));
        dest.writeInt(mSelectCount);
        dest.writeStringList(mSelectedImages);
        dest.writeString(mSavePath);
    }

    public static class Builder {
        private boolean isCrop;
        private int cropWidth, cropHeight;
        private Callback callback;
        private boolean hasCam;
        private int selectCount;
        private List<String> selectedImages;
        private String savePath;

        public Builder() {
            selectCount = 1;
            hasCam = true;
            selectedImages = new ArrayList<>();
        }

        public Builder setCrop(int cropWidth, int cropHeight) {
            if (cropWidth <= 0 || cropHeight <= 0)
                throw new IllegalArgumentException("cropWidth or cropHeight mast be greater than 0 ");
            this.isCrop = true;
            this.cropWidth = cropWidth;
            this.cropHeight = cropHeight;
            return this;
        }

        public Builder setCallback(Callback callback) {
            this.callback = callback;
            return this;
        }

        public Builder setHasCam(boolean hasCam) {
            this.hasCam = hasCam;
            return this;
        }

        public Builder setSelectCount(int selectCount) {
            this.selectCount = selectCount <= 0 ? 1 : selectCount;
            return this;
        }

        public Builder setSelectedImages(List<String> selectedImages) {
            if (selectedImages == null || selectedImages.size() == 0) return this;
            this.selectedImages.addAll(selectedImages);
            return this;
        }

        public Builder setSelectedImages(String[] selectedImages) {
            if (selectedImages == null || selectedImages.length == 0) return this;
            if (this.selectedImages == null) this.selectedImages = new ArrayList<>();
            this.selectedImages.addAll(Arrays.asList(selectedImages));
            return this;
        }

        public Builder setSavaPath(String path) {
            this.savePath = path;
            return this;
        }

        public SelectOptions build() {
            SelectOptions options = new SelectOptions();
            options.hasCam = hasCam;
            options.isCrop = isCrop;
            options.mCropHeight = cropHeight;
            options.mCropWidth = cropWidth;
            options.mCallback = callback;
            options.mSelectCount = selectCount;
            options.mSelectedImages = selectedImages;
            options.mSavePath = savePath;
            if (isCrop) options.mSelectCount = 1;
            return options;
        }
    }

}
