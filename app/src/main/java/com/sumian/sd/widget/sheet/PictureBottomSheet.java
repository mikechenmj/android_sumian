package com.sumian.sd.widget.sheet;

import android.view.View;

import com.sumian.sd.R;
import com.sumian.sd.widget.base.BaseBottomSheetView;

/**
 * Created by jzz
 * on 2018/1/24.
 * desc:
 */

public class PictureBottomSheet extends BaseBottomSheetView implements View.OnClickListener {

    private OnTakePhotoCallback mOnTakePhotoCallback;

    public static PictureBottomSheet newInstance() {
        return new PictureBottomSheet();
    }

    public PictureBottomSheet addOnTakePhotoCallback(OnTakePhotoCallback onTakePhotoCallback) {
        mOnTakePhotoCallback = onTakePhotoCallback;
        return this;
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_take_photo:
                mOnTakePhotoCallback.onTakePhotoCallback();
                break;
            case R.id.tv_pic_photo:
                mOnTakePhotoCallback.onPicPictureCallback();
                break;
            case R.id.tv_cancel:
                break;
            default:
                break;
        }
        dismiss();
    }


    public interface OnTakePhotoCallback {

        void onTakePhotoCallback();

        void onPicPictureCallback();
    }
}
