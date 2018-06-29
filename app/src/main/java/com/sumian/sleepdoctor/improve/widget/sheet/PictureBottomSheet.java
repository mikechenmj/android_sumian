package com.sumian.sleepdoctor.improve.widget.sheet;

import android.view.View;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.widget.BaseBottomSheetView;

import butterknife.OnClick;

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
    @OnClick({R.id.tv_take_photo, R.id.tv_pic_photo, R.id.tv_cancel})
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
