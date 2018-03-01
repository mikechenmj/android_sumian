package com.sumian.common.media;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.sumian.common.R;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by haibin
 * on 17/2/27.
 */
public class ImagePickerActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks,
        Contract.Presenter {

    private static final int RC_CAMERA_PERM = 0x03;
    private static final int RC_EXTERNAL_STORAGE = 0x04;

    private static SelectOptions mOption;
    private Contract.View mView;

    public static void show(Context context, SelectOptions options) {
        mOption=options;
        Intent intent = new Intent(context, ImagePickerActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_image);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && actionBar.isShowing())
            actionBar.hide();
        requestExternalStorage();
    }

    @AfterPermissionGranted(RC_CAMERA_PERM)
    @Override
    public void requestCamera() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (mView != null) {
                mView.onOpenCameraSuccess();
            }
        } else {
            EasyPermissions.requestPermissions(this, "没有权限,你需要去设置中开启相机权限.", RC_CAMERA_PERM, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    @AfterPermissionGranted(RC_EXTERNAL_STORAGE)
    @Override
    public void requestExternalStorage() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (mView == null) {
                handleView();
            }
        } else {
            EasyPermissions.requestPermissions(this, "没有权限,你需要去设置中开启读取手机存储权限.", RC_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }


    @Override
    public void setDataView(Contract.View view) {
        mView = view;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == RC_EXTERNAL_STORAGE) {
            removeView();
            showDialog("", "没有权限,你需要去设置中开启读取手机存储权限.", "去设置", "取消", (dialog, which) -> {
                startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                finish();
            }, (dialog, which) -> finish());
        } else {
            if (mView != null)
                mView.onCameraPermissionDenied();
            showDialog("", "没有权限,你需要去设置中开启相机权限.", "去设置", "取消", (dialog, which) -> startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS)), null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onDestroy() {
        //mOption = null;
        super.onDestroy();
    }

    private void removeView() {
        Contract.View view = mView;
        if (view == null)
            return;
        try {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove((Fragment) view)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleView() {
        try {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_content, ImagePickerFragment.newInstance(mOption))
                    .commitNowAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDialog(String title, String message, String positiveButton, String negativeButton,
                            DialogInterface.OnClickListener listener,
                            DialogInterface.OnClickListener negativeListener) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, listener)
                .setNegativeButton(negativeButton, negativeListener)
                .setCancelable(false)
                .create().show();
    }
}
