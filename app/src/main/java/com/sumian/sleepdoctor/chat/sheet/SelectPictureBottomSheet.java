package com.sumian.sleepdoctor.chat.sheet;

import android.view.View;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.widget.BottomSheetView;

import butterknife.OnClick;

/**
 * Created by jzz
 * on 2017/10/5
 * <p>
 * desc:
 */

public class SelectPictureBottomSheet extends BottomSheetView implements View.OnClickListener {

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
