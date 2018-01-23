package com.sumian.sleepdoctor.pager.sheet;

import android.view.View;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.widget.BaseBottomSheetView;

import butterknife.OnClick;

/**
 * Created by jzz
 * on 2018/1/24.
 * desc:
 */

public class AvatarBottomSheet extends BaseBottomSheetView implements View.OnClickListener {

    private OnTakePhotoCallback mOnTakePhotoCallback;

    public static AvatarBottomSheet newInstance() {
        return new AvatarBottomSheet();
    }

    public AvatarBottomSheet addOnTakePhotoCallback(OnTakePhotoCallback onTakePhotoCallback) {
        mOnTakePhotoCallback = onTakePhotoCallback;
        return this;
    }

    @Override
    protected int getLayout() {
        return R.layout.lay_bottom_sheet_modify_avatar;
    }

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
        }
        dismiss();
    }


    public interface OnTakePhotoCallback {

        void onTakePhotoCallback();

        void onPicPictureCallback();
    }
}
