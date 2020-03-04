package com.sumian.common.media;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.sumian.common.R;
import com.sumian.common.base.BaseFragment;
import com.sumian.common.helper.FileProviderHelper;
import com.sumian.common.image.ImageLoader;
import com.sumian.common.image.ImagesScopeStorageHelper;
import com.sumian.common.media.adapter.ImageAdapter;
import com.sumian.common.media.adapter.ImageFolderAdapter;
import com.sumian.common.media.base.BaseRecyclerAdapter;
import com.sumian.common.media.bean.Image;
import com.sumian.common.media.bean.ImageFolder;
import com.sumian.common.media.config.ImageLoaderListener;
import com.sumian.common.media.config.SelectOptions;
import com.sumian.common.media.contract.SelectImageContract;
import com.sumian.common.widget.empty.EmptyLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

/**
 * 图片选择库实现界面
 * Created by huanghaibin_dev
 * on 2016/7/13.
 * <p>
 * Changed by qiujuer
 * on 2016/09/01
 */
@SuppressWarnings("ALL")
public class SelectFragment extends BaseFragment implements SelectImageContract.View, View.OnClickListener,
        ImageLoaderListener, BaseRecyclerAdapter.OnItemClickListener, ImagesScopeStorageHelper.ImageChangeListener {

    private RecyclerView mContentView;
    private Button mSelectFolderView;
    private ImageView mSelectFolderIcon;
    private View mToolbar;
    private Button mDoneView;
    private Button mPreviewView;

    EmptyLayout mErrorLayout;

    private ImageFolderPopupWindow mFolderPopupWindow;
    private ImageFolderAdapter mImageFolderAdapter;
    private ImageAdapter mImageAdapter;

    private List<Image> mSelectedImage;

    private String mCamImageName;
    private LoaderListener mCursorLoader = new LoaderListener();

    private SelectImageContract.Operator mOperator;

    private static SelectOptions mOption;

    public static SelectFragment newInstance(SelectOptions options) {
        mOption = options;
        return new SelectFragment();
    }

    @Override
    public void onAttach(Context context) {
        this.mOperator = (SelectImageContract.Operator) context;
        this.mOperator.setDataView(this);
        super.onAttach(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_select_image;
    }

    @Override
    protected void initWidget() {
        if (mOption == null) {
            getActivity().finish();
            return;
        }
        findView(R.id.icon_back).setOnClickListener(this);

        mContentView = findView(R.id.rv_image);
        mSelectFolderView = findView(R.id.btn_title_select);
        mSelectFolderView.setOnClickListener(this);
        mSelectFolderIcon = findView(R.id.iv_title_select);
        mToolbar = findView(R.id.toolbar);
        mDoneView = findView(R.id.btn_done);
        mDoneView.setOnClickListener(this);
        mPreviewView = findView(R.id.btn_preview);
        mPreviewView.setOnClickListener(this);

        mErrorLayout = findView(R.id.error_layout);

        mContentView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        mContentView.addItemDecoration(new SpaceGridItemDecoration((int) Util.dipToPx(getResources(), 1)));
        mImageAdapter = new ImageAdapter(getContext(), this);
        mImageAdapter.setSingleSelect(mOption.getSelectCount() <= 1);
        findView(R.id.lay_button).setVisibility(mOption.getSelectCount() == 1 ? View.GONE : View.VISIBLE);
        mImageFolderAdapter = new ImageFolderAdapter(getActivity());
        mImageFolderAdapter.setLoader(this);
        mContentView.setAdapter(mImageAdapter);
        mContentView.setItemAnimator(null);
        mImageAdapter.setOnItemClickListener(this);
        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
//                getLoaderManager().initLoader(0, null, mCursorLoader);
                HashMap<Integer, Image> imagesMap = ImagesScopeStorageHelper.INSTANCE.getImages().get();
                if (imagesMap == null || imagesMap.size() <= 0) {
                    ImagesScopeStorageHelper.INSTANCE.loadMedia();
                    return;
                }
                loaderMedia(imagesMap);
            }
        });
    }

    @Override
    protected void initData() {
        if (mOption == null) {
            getActivity().finish();
            return;
        }
        mSelectedImage = new ArrayList<>();

        if (mOption.getSelectCount() > 1 && mOption.getSelectedImages() != null) {
            List<String> images = mOption.getSelectedImages();
            for (String s : images) {
                // checkShare file exists
                if (s != null && new File(s).exists()) {
                    Image image = new Image();
                    image.setSelect(true);
                    image.setRawPath(s);
                    mSelectedImage.add(image);
                }
            }
        }
//        getLoaderManager().initLoader(0, null, mCursorLoader);
        ImagesScopeStorageHelper.INSTANCE.registerImageChangeListener(this);
        HashMap<Integer, Image> imagesMap = ImagesScopeStorageHelper.INSTANCE.getImages().get();
        if (imagesMap == null || imagesMap.size() <= 0) {
            ImagesScopeStorageHelper.INSTANCE.loadMedia();
            return;
        }
        loaderMedia(imagesMap);
    }

    @Override
    protected void onRelease() {
        super.onRelease();
        ImagesScopeStorageHelper.INSTANCE.unregisterImageChangeListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.icon_back) {
            mOperator.onBack();
        } else if (v.getId() == R.id.btn_preview) {
            if (mSelectedImage.size() > 0) {
//                ImageGalleryActivity.show(getActivity(), Util.toArray(mSelectedImage), 0, false);
            }
        } else if (v.getId() == R.id.btn_title_select) {
            showPopupFolderList();
        } else if (v.getId() == R.id.btn_done) {
            onSelectComplete();
        }
    }


    @Override
    public void onItemClick(int position, long itemId) {
        if (mOption.isHasCam()) {
            if (position != 0) {
                handleSelectChange(position);
            } else {
                if (mSelectedImage.size() < mOption.getSelectCount()) {
                    mOperator.requestCamera();
                } else {
                    Toast.makeText(getActivity(), "最多只能选择 " + mOption.getSelectCount() + " 张图片", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            handleSelectChange(position);
        }
    }

    private void handleSelectSizeChange(int size) {
        if (size > 0) {
            mPreviewView.setEnabled(true);
            mDoneView.setEnabled(true);
            mDoneView.setText(String.format("%s(%s)", getText(R.string.image_select_opt_done), size));
        } else {
            mPreviewView.setEnabled(false);
            mDoneView.setEnabled(false);
            mDoneView.setText(getText(R.string.image_select_opt_done));
        }
    }

    private void handleSelectChange(int position) {
        Image image = mImageAdapter.getItem(position);
        if (image == null)
            return;
        //如果是多选模式
        final int selectCount = mOption.getSelectCount();
        if (selectCount > 1) {
            if (image.isSelect()) {
                image.setSelect(false);
                mSelectedImage.remove(image);
                mImageAdapter.updateItem(position);
            } else {
                if (mSelectedImage.size() == selectCount) {
                    Toast.makeText(getActivity(), "最多只能选择 " + selectCount + " 张照片", Toast.LENGTH_SHORT).show();
                } else {
                    image.setSelect(true);
                    mSelectedImage.add(image);
                    mImageAdapter.updateItem(position);
                }
            }
            handleSelectSizeChange(mSelectedImage.size());
        } else {
            mSelectedImage.add(image);
            handleResult();
        }
    }

    private void handleResult() {
        if (mSelectedImage.size() != 0) {
            if (mOption.isCrop()) {
                List<String> selectedImage = mOption.getSelectedImages();
                selectedImage.clear();
                selectedImage.add(mSelectedImage.get(0).getImagePath());
                mSelectedImage.clear();
//                CropActivity.show(this, mOption);
            } else {
                mOption.getCallback().doSelected(Util.toArray(mSelectedImage));
                getActivity().finish();
            }
        }
    }

    /**
     * 完成选择
     */
    public void onSelectComplete() {
        handleResult();
    }

    /**
     * 申请相机权限成功
     */
    @Override
    public void onOpenCameraSuccess() {
        toOpenCamera();
    }


    @Override
    public void onCameraPermissionDenied() {

    }

    /**
     * 创建弹出的相册
     */
    private void showPopupFolderList() {
//        if (mFolderPopupWindow == null) {
//            ImageFolderPopupWindow popupWindow = new ImageFolderPopupWindow(getActivity(), new ImageFolderPopupWindow.Callback() {
//                @Override
//                public void onSelect(ImageFolderPopupWindow popupWindow, ImageFolder model) {
//                    addImagesToAdapter(model.getImages());
//                    mContentView.scrollToPosition(0);
//                    popupWindow.dismiss();
//                    mSelectFolderView.setText(model.getName());
//                }
//
//                @Override
//                public void onDismiss() {
//                    mSelectFolderIcon.setImageResource(R.mipmap.ic_arrow_bottom);
//                }
//
//                @Override
//                public void onShow() {
//                    mSelectFolderIcon.setImageResource(R.mipmap.ic_arrow_top);
//                }
//            });
//            popupWindow.setAdapter(mImageFolderAdapter);
//            mFolderPopupWindow = popupWindow;
//        }
//        mFolderPopupWindow.showAsDropDown(mToolbar);
    }

    /**
     * 打开相机
     */
    private void toOpenCamera() {
        // 判断是否挂载了SD卡
        mCamImageName = null;
        String savePath = "";
        if (Util.hasSDCard()) {
            savePath = Util.getCameraPath();
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
        }

        // 没有挂载SD卡，无法保存文件
        if (TextUtils.isEmpty(savePath)) {
            Toast.makeText(getActivity(), "无法保存照片，请检查SD卡是否挂载", Toast.LENGTH_LONG).show();
            return;
        }

        mCamImageName = Util.getSaveImageFullName();
        File out = new File(savePath, mCamImageName);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = FileProviderHelper.getUriForFile(getContext(), out);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent,
                0x03);
    }

    /**
     * 拍照完成通知系统添加照片
     *
     * @param requestCode requestCode
     * @param resultCode  resultCode
     * @param data        data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            switch (requestCode) {
                case 0x03:
                    if (mCamImageName == null) return;
                    Uri localUri = Uri.fromFile(new File(Util.getCameraPath() + mCamImageName));
                    Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
                    getActivity().sendBroadcast(localIntent);
                    break;
                case 0x04:
                    if (data == null) return;
                    mOption.getCallback().doSelected(new String[]{data.getStringExtra("crop_path")});
                    getActivity().finish();
                    break;
            }
        }
    }

    @Override
    public void displayImage(final ImageView iv, final String path) {
        ImageLoader.loadImage(path, iv, R.mipmap.ic_split_graph);
    }

    private void loaderMedia(HashMap<Integer, Image> imagesMap) {
        final ArrayList<Image> images = new ArrayList<>();
        final List<ImageFolder> imageFolders = new ArrayList<>();

        final ImageFolder defaultFolder = new ImageFolder();
        defaultFolder.setName("全部照片");
        defaultFolder.setPath("");
        imageFolders.add(defaultFolder);

        int count = imagesMap.size();
        if (count > 0) {
            for (Image image : imagesMap.values()) {
                images.add(image);
                //如果是新拍的照片
                if (mCamImageName != null && mCamImageName.equals(image.getName())) {
                    image.setSelect(true);
                    mSelectedImage.add(image);
                }
                //如果是被选中的图片
                if (mSelectedImage.size() > 0) {
                    for (Image i : mSelectedImage) {
                        if (i.getRawPath().equals(image.getRawPath())) {
                            image.setSelect(true);
                        }
                    }
                }
                File imageFile = new File(image.getRawPath());
                File folderFile = imageFile.getParentFile();
                ImageFolder folder = new ImageFolder();
                folder.setName(folderFile.getName());
                folder.setPath(folderFile.getAbsolutePath());
                if (!imageFolders.contains(folder)) {
                    folder.getImages().add(image);
                    folder.setAlbumPath(image.getImagePath());//默认相册封面
                    imageFolders.add(folder);
                } else {
                    // 更新
                    ImageFolder f = imageFolders.get(imageFolders.indexOf(folder));
                    f.getImages().add(image);
                }
            }
        }
        addImagesToAdapter(images);
        defaultFolder.getImages().addAll(images);
        int imageIndex = -1;
        if (mOption.isHasCam()) {
            defaultFolder.setAlbumPath(images.size() > 1 ? images.get(1).getImagePath() : null);
        } else {
            defaultFolder.setAlbumPath(images.size() > 0 ? images.get(0).getImagePath() : null);
        }
        mImageFolderAdapter.resetItem(imageFolders);

        //删除掉不存在的，在于用户选择了相片，又去相册删除
        if (mSelectedImage.size() > 0) {
            List<Image> rs = new ArrayList<>();
            for (Image i : mSelectedImage) {
                File f = new File(i.getRawPath());
                if (!f.exists()) {
                    rs.add(i);
                }
            }
            mSelectedImage.removeAll(rs);
        }

        // If add new mCamera picture, and we only need one picture, we result it.
        if (mOption.getSelectCount() == 1 && mCamImageName != null) {
            handleResult();
        }

        handleSelectSizeChange(mSelectedImage.size());
        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);

    }

    @Override
    public void onChange(@NotNull HashMap<Integer, Image> map) {
        loaderMedia(map);
    }

    private class LoaderListener implements LoaderManager.LoaderCallbacks<Cursor> {
        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.MINI_THUMB_MAGIC,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (id == 0) {
                //数据库光标加载器
                return new CursorLoader(getContext(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        null, null, IMAGE_PROJECTION[2] + " DESC");
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
            if (data != null) {
                final ArrayList<Image> images = new ArrayList<>();
                final List<ImageFolder> imageFolders = new ArrayList<>();

                final ImageFolder defaultFolder = new ImageFolder();
                defaultFolder.setName("全部照片");
                defaultFolder.setPath("");
                imageFolders.add(defaultFolder);

                int count = data.getCount();
                if (count > 0) {
                    data.moveToFirst();
                    do {
                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                        int id = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
                        String thumbPath = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
                        String bucket = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[5]));

                        Image image = new Image();
                        image.setRawPath(path);
                        String contentPath = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + "/" + id;
                        image.setContentPath(contentPath);
                        image.setName(name);
                        image.setDate(dateTime);
                        image.setId(id);
                        image.setThumbPath(thumbPath);
                        image.setFolderName(bucket);

                        images.add(image);

                        //如果是新拍的照片
                        if (mCamImageName != null && mCamImageName.equals(image.getName())) {
                            image.setSelect(true);
                            mSelectedImage.add(image);
                        }

                        //如果是被选中的图片
                        if (mSelectedImage.size() > 0) {
                            for (Image i : mSelectedImage) {
                                if (i.getRawPath().equals(image.getRawPath())) {
                                    image.setSelect(true);
                                }
                            }
                        }

                        File imageFile = new File(path);
                        File folderFile = imageFile.getParentFile();
                        ImageFolder folder = new ImageFolder();
                        folder.setName(folderFile.getName());
                        folder.setPath(folderFile.getAbsolutePath());
                        if (!imageFolders.contains(folder)) {
                            folder.getImages().add(image);
                            folder.setAlbumPath(image.getImagePath());//默认相册封面
                            imageFolders.add(folder);
                        } else {
                            // 更新
                            ImageFolder f = imageFolders.get(imageFolders.indexOf(folder));
                            f.getImages().add(image);
                        }


                    } while (data.moveToNext());
                }
                addImagesToAdapter(images);
                defaultFolder.getImages().addAll(images);
                int imageIndex = -1;
                if (mOption.isHasCam()) {
                    defaultFolder.setAlbumPath(images.size() > 1 ? images.get(1).getImagePath() : null);
                } else {
                    defaultFolder.setAlbumPath(images.size() > 0 ? images.get(0).getImagePath() : null);
                }
                mImageFolderAdapter.resetItem(imageFolders);

                //删除掉不存在的，在于用户选择了相片，又去相册删除
                if (mSelectedImage.size() > 0) {
                    List<Image> rs = new ArrayList<>();
                    for (Image i : mSelectedImage) {
                        File f = new File(i.getRawPath());
                        if (!f.exists()) {
                            rs.add(i);
                        }
                    }
                    mSelectedImage.removeAll(rs);
                }

                // If add new mCamera picture, and we only need one picture, we result it.
                if (mOption.getSelectCount() == 1 && mCamImageName != null) {
                    handleResult();
                }

                handleSelectSizeChange(mSelectedImage.size());
                mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    private void addImagesToAdapter(ArrayList<Image> images) {
        mImageAdapter.clear();
        if (mOption.isHasCam()) {
            Image cam = new Image();
            mImageAdapter.addItem(cam);
        }
        mImageAdapter.addAll(images);
    }

    @Override
    public void onDestroy() {
        mOption = null;
        super.onDestroy();
    }
}
