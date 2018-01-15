package com.sumian.common.media;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.sumian.common.R;
import com.sumian.common.utils.StreamUtil;

import java.io.FileOutputStream;


/**
 * Created by haibin
 * on 2016/12/2.
 */

public class CropActivity extends AppCompatActivity implements View.OnClickListener {
    private CropLayout mCropLayout;
    private static SelectOptions mOption;
    private View mCrop, mCancel;

    public static void show(Fragment fragment, SelectOptions options) {
        Intent intent = new Intent(fragment.getActivity(), CropActivity.class);
        mOption = options;
        fragment.startActivityForResult(intent, 0x04);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Translate);
        setContentView(R.layout.activity_crop);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && actionBar.isShowing())
            actionBar.hide();
        initWidget();
        initData();
    }


    private void initWidget() {
        setTitle("");
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        mCropLayout = (CropLayout) findViewById(R.id.cropLayout);

        mCrop = findViewById(R.id.tv_crop);
        mCancel = findViewById(R.id.tv_cancel);
        mCrop.setOnClickListener(this);
        mCancel.setOnClickListener(this);
    }

    private void initData() {

        String url = mOption.getSelectedImages().get(0);
        Glide.with(this).load(url)
            .fitCenter()
            .into(mCropLayout.getImageView());

        mCropLayout.setCropWidth(mOption.getCropWidth());
        mCropLayout.setCropHeight(mOption.getCropHeight());
        mCropLayout.start();
    }


    @Override
    public void onClick(View view) {
        if (view == mCrop) {
            Bitmap bitmap = null;
            FileOutputStream os = null;
            try {
                bitmap = mCropLayout.cropBitmap();
                String path = getFilesDir() + "/crop.jpg";
                os = new FileOutputStream(path);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.flush();
                os.close();

                Intent intent = new Intent();
                intent.putExtra("crop_path", path);
                setResult(RESULT_OK, intent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bitmap != null) bitmap.recycle();
                StreamUtil.close(os);
            }
        } else if (view == mCancel) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        mOption = null;
        super.onDestroy();
    }
}
