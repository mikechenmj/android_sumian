package com.sumian.common.media;

/**
 * Created by haibin
 * on 17/2/27.
 */
interface Contract {
    interface Presenter {
        void requestCamera();

        void requestExternalStorage();

        void setDataView(View view);
    }

    interface View {

        void onOpenCameraSuccess();

        void onCameraPermissionDenied();
    }
}
