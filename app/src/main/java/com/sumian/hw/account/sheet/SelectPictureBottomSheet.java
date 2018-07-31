package com.sumian.hw.account.sheet;

import android.view.View;

import com.sumian.sleepdoctor.R;
import com.sumian.hw.widget.BottomSheetView;

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
        return R.layout.hw_lay_bottom_sheet_modify_avatar;
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
        int id = view.getId();
        if (id == R.id.tv_take_photo) {
            mOnTakePhotoCallback.onTakePhotoCallback();
        } else if (id == R.id.tv_pic_photo) {
            mOnTakePhotoCallback.onPicPictureCallback();
        }
        dismiss();
    }


    public interface OnTakePhotoCallback {

        void onTakePhotoCallback();

        void onPicPictureCallback();
    }

}
