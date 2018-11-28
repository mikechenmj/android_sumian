package com.sumian.sd.widget.sheet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;

import com.sumian.common.helper.FileProviderHelper;
import com.sumian.common.media.SelectImageActivity;
import com.sumian.common.media.Util;
import com.sumian.common.media.config.SelectOptions;
import com.sumian.sd.R;
import com.sumian.sd.widget.base.BaseBottomSheetView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author jzz
 * on 2018/1/24.
 * desc:
 */

@SuppressLint("ValidFragment")
public class SelectPictureBottomSheet extends BaseBottomSheetView implements View.OnClickListener {

    private static final String TAG = SelectPictureBottomSheet.class.getSimpleName();

    private static final int PIC_REQUEST_CODE_CAMERA = 1;

    private File mPhotoFile;
    private int mSelectCount;

    private String[] mSelectImages;

    private OnPhotoSelectListener mOnPhotoSelectListener;

    private SelectPictureBottomSheet(OnPhotoSelectListener onPhotoSelectListener, int selectCount, String[] selectImages) {
        mOnPhotoSelectListener = onPhotoSelectListener;
        mSelectCount = selectCount;
        mSelectImages = selectImages;
    }

    public static void show(FragmentManager fragmentManager, OnPhotoSelectListener onPhotoSelectListener, int selectCount, String[] selectedImages) {
        SelectPictureBottomSheet selectPictureBottomSheet = new SelectPictureBottomSheet(onPhotoSelectListener, selectCount, selectedImages);
        fragmentManager
                .beginTransaction()
                .add(selectPictureBottomSheet, SelectPictureBottomSheet.class.getSimpleName())
                .commitNowAllowingStateLoss();
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        rootView.findViewById(R.id.tv_take_photo).setOnClickListener(this);
        rootView.findViewById(R.id.tv_pic_photo).setOnClickListener(this);
        rootView.findViewById(R.id.tv_cancel).setOnClickListener(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.lay_bottom_sheet_take_picture;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_take_photo:
                takePhoto();
                break;
            case R.id.tv_pic_photo:
                pickPhoto();
                break;
            case R.id.tv_cancel:
                dismissAllowingStateLoss();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(PIC_REQUEST_CODE_CAMERA)
    private void takePhoto() {
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(Objects.requireNonNull(getActivity()), permissions)) {
            mPhotoFile = createImageFile();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri uri = FileProviderHelper.getUriForFile(mContext, mPhotoFile);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, PIC_REQUEST_CODE_CAMERA);
        } else {
            EasyPermissions.requestPermissions(this, getResources().getString(R.string.str_request_camera_message), PIC_REQUEST_CODE_CAMERA, permissions);
        }
    }

    private void pickPhoto() {
        SelectImageActivity.show(Objects.requireNonNull(getActivity()),
                new SelectOptions
                        .Builder()
                        .setHasCam(false)
                        .setSelectCount(mSelectCount)
                        .setSelectedImages(new String[]{})
                        .setCallback(this::returnSelectPicturePathAndDismiss).build());
    }

    private void returnSelectPicturePathAndDismiss(String... image) {
        if (mOnPhotoSelectListener != null) {
            if (!mOnPhotoSelectListener.onPhotoSelect(image)) {
                dismissAllowingStateLoss();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PIC_REQUEST_CODE_CAMERA) {
            returnSelectPicturePathAndDismiss(mPhotoFile.getAbsolutePath());
        }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = new File(Util.getCameraPath());
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public interface OnPhotoSelectListener {
        boolean onPhotoSelect(String... filePath);
    }
}
