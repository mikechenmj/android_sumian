package com.sumian.common.media.bean;

import android.os.Build;

import java.io.Serializable;

/**
 * Created by huanghaibin_dev
 * on 2016/7/11.
 */

public class Image implements Serializable {
    private int id;
    private String rawPath;
    private String contentPath;
    private String thumbPath;
    private boolean isSelect;
    private String folderName;
    private String name;
    private long date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRawPath() {
        return rawPath;
    }

    public String getContentPath() {
        return contentPath;
    }

    public String getImagePath() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            if (contentPath.isEmpty()) {
                return rawPath;
            }
            return contentPath;
        }
        return rawPath;
    }

    public void setRawPath(String rawPath) {
        this.rawPath = rawPath;
    }

    public void setContentPath(String path) {
        this.contentPath = path;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Image) {
            return this.rawPath.equals(((Image) o).getRawPath());
        }
        return false;
    }
}
