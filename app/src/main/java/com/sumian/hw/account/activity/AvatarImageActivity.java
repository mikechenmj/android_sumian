package com.sumian.hw.account.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.sumian.common.image.ImageLoader;
import com.sumian.hw.account.contract.AvatarContract;
import com.sumian.hw.account.presenter.AvatarPresenter;
import com.sumian.hw.account.sheet.SelectPictureBottomSheet;
import com.sumian.hw.base.HwBaseActivity;
import com.sumian.hw.widget.TitleBar;
import com.sumian.sd.R;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * 图片预览Activity
 */
public class AvatarImageActivity extends HwBaseActivity implements EasyPermissions.PermissionCallbacks,
        TitleBar.OnBackClickListener, TitleBar.OnMoreListener, SelectPictureBottomSheet.OnTakePhotoCallback, AvatarContract.View {
    public static final String KEY_IMAGE = "images";
    public static final String KEY_POSITION = "position";
    public static final String KEY_NEED_SAVE = "save";

    TitleBar mTitleBar;
    ImageView mImagePager;

    private String[] mImageSources;
    private int mCurPosition;

    private AvatarContract.Presenter mPresenter;

    public static void show(Context context, String images) {
        show(context, images, true);
    }

    public static void show(Context context, String images, boolean needSaveLocal) {
        show(context, new String[]{images}, 0, needSaveLocal);
    }

    public static void show(Context context, String images, boolean needSaveLocal, boolean needCookie) {
        if (images == null)
            return;
        show(context, new String[]{images}, 0, needSaveLocal, needCookie);
    }

    public static void show(Context context, String[] images, int position) {
        show(context, images, position, true);
    }

    public static void show(Context context, String[] images, int position, boolean needSaveLocal) {
        show(context, images, position, needSaveLocal, false);
    }

    public static void show(Context context, String[] images, int position, boolean needSaveLocal, boolean needCookie) {
        if (images == null || images.length == 0) {
            return;
        }
        Intent intent = new Intent(context, AvatarImageActivity.class);
        intent.putExtra(KEY_IMAGE, images);
        intent.putExtra(KEY_POSITION, position);
        intent.putExtra(KEY_NEED_SAVE, needSaveLocal);
        context.startActivity(intent);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        mImageSources = bundle.getStringArray(KEY_IMAGE);
        mCurPosition = bundle.getInt(KEY_POSITION, 0);
        return true;
    }

    @Override
    protected void initWindow() {
        super.initWindow();
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_avatar;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTitleBar = findViewById(R.id.title_bar);
        mImagePager = findViewById(R.id.vp_image);
        setTitle("");
        mTitleBar.setOnBackClickListener(this)
                .showMoreIcon()
                .addOnMoreListener(this);

        String imageSource = mImageSources[0];
        ImageLoader.loadImage(imageSource, mImagePager, R.mipmap.ic_default_avatar, R.mipmap.ic_default_avatar);

    }

    @Override
    protected void initData() {
        super.initData();
        AvatarPresenter.init(this);
        int len = mImageSources.length;
        if (mCurPosition < 0 || mCurPosition >= len) {
            mCurPosition = 0;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.resultCodeDelegate(requestCode, resultCode, data);

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, R.string.gallery_save_file_not_have_external_storage_permission, Toast.LENGTH_SHORT).show();
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @AfterPermissionGranted(AvatarPresenter.PIC_REQUEST_CODE_CAMERA)
    private void openCamera() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            mPresenter.sendPic(this, AvatarPresenter.PIC_REQUEST_CODE_CAMERA);
        } else {
            EasyPermissions.requestPermissions(this, "没有权限,你需要去设置中开启相机权限.", AvatarPresenter.PIC_REQUEST_CODE_CAMERA, perms);
        }
    }

    @Override
    public void onBackClick(View v) {
        finish();
    }

    @Override
    public void onMore(View v) {

        getSupportFragmentManager()
                .beginTransaction()
                .add(SelectPictureBottomSheet.newInstance().addOnTakePhotoCallback(this), SelectPictureBottomSheet.class.getSimpleName())
                .commit();

    }

    @Override
    public void onTakePhotoCallback() {
        openCamera();
    }


    @Override
    public void onPicPictureCallback() {
        mPresenter.sendPic(this, AvatarPresenter.PIC_REQUEST_CODE_LOCAL);
    }

    @Override
    public void setPresenter(AvatarContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onFailure(String error) {

    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void imageIsExit() {

    }

    @Override
    public void uploadSuccess(String url) {
        ImageLoader.loadImage(url, mImagePager, R.mipmap.ic_default_avatar, R.mipmap.ic_default_avatar);
    }

    @Override
    public void loadLocalImageSuccess(String url) {
        uploadSuccess(url);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}
