package com.sumian.sddoctor.widget.sheet;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import com.sumian.common.media.SelectImageActivity;
import com.sumian.common.media.config.SelectOptions;
import com.sumian.sddoctor.R;
import com.sumian.sddoctor.util.FileProviderUtil;

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

public class SelectPictureBottomSheet extends AbstractBottomSheetView implements View.OnClickListener {

    private static final int PIC_REQUEST_CODE_CAMERA = 1;

    private File mPhotoFile;
    private OnPhotoSelectListener mOnPhotoSelectListener;
    private boolean mPendingDismiss;

    public static void show(FragmentManager fragmentManager, OnPhotoSelectListener onPhotoSelectListener) {
        SelectPictureBottomSheet selectPictureBottomSheet = new SelectPictureBottomSheet();
        selectPictureBottomSheet.setOnPhotoSelectListener(onPhotoSelectListener);
        fragmentManager
                .beginTransaction()
                .add(selectPictureBottomSheet, SelectPictureBottomSheet.class.getSimpleName())
                .commit();
    }

    private void setOnPhotoSelectListener(OnPhotoSelectListener onPhotoSelectListener) {
        mOnPhotoSelectListener = onPhotoSelectListener;
    }

    @Override
    protected int getLayout() {
        return R.layout.lay_bottom_sheet_take_picture;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        rootView.findViewById(R.id.tv_take_photo).setOnClickListener(this);
        rootView.findViewById(R.id.tv_pic_photo).setOnClickListener(this);
        rootView.findViewById(R.id.tv_cancel).setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPendingDismiss) {
            dismiss();
        }
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
            Uri uri = FileProviderUtil.getCompatUriForFile(mContext, mPhotoFile);
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
                        .setSelectCount(1)
                        .setSelectedImages(new String[]{})
                        .setCallback(images -> {
                            if (images.length == 1) {
                                String image = images[0];
                                returnSelectPicturePathAndDismiss(image);
                            }
                        }).build());
    }

    private void returnSelectPicturePathAndDismiss(String image) {
        if (mOnPhotoSelectListener != null) {
            mOnPhotoSelectListener.onPhotoSelect(image);
        }
        mPendingDismiss = true;
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
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public interface OnPhotoSelectListener {
        void onPhotoSelect(String filePath);
    }
}
