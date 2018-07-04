package com.sumian.sleepdoctor.chat.sheet;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.widget.BaseBottomSheetView;

import java.util.List;

import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by jzz
 * on 2017/10/5
 * <p>
 * desc:
 */

public class SelectPictureBottomSheet extends BaseBottomSheetView implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    // private static final String TAG = PictureBottomSheet.class.getSimpleName();

    public static final int CAMERA_PERM = 0x01;

    private OnTakePhotoCallback mOnTakePhotoCallback;

    public static SelectPictureBottomSheet newInstance() {
        return new SelectPictureBottomSheet();
    }

    public SelectPictureBottomSheet addOnTakePhotoCallback(OnTakePhotoCallback onTakePhotoCallback) {
        mOnTakePhotoCallback = onTakePhotoCallback;
        return this;
    }

    @Override
    protected int getLayout() {
        return R.layout.lay_bottom_sheet_take_picture;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @OnClick({R.id.tv_take_photo, R.id.tv_pic_photo, R.id.tv_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_take_photo:
                requestCameraPermission();
                break;
            case R.id.tv_pic_photo:
                if (mOnTakePhotoCallback != null)
                    mOnTakePhotoCallback.onPicPictureCallback();
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }

    @SuppressWarnings("ConstantConditions")
    @AfterPermissionGranted(CAMERA_PERM)
    private void requestCameraPermission() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            if (mOnTakePhotoCallback != null)
                mOnTakePhotoCallback.onTakePhotoCallback();
            dismiss();
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(this, getResources().getString(R.string.str_request_camera_message), CAMERA_PERM, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        //Log.e(TAG, "onPermissionsGranted: -------------->" + perms.toString());
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
        // Do something after user returned from app settings screen, like showing a Toast.
        //}
    }

    public interface OnTakePhotoCallback {

        void onTakePhotoCallback();

        void onPicPictureCallback();
    }

}
