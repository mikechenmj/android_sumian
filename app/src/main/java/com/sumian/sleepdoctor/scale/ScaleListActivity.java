package com.sumian.sleepdoctor.scale;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.network.response.ErrorResponse;
import com.sumian.sleepdoctor.network.response.PaginationResponse;

public class ScaleListActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scale_list;
    }

    @Override
    protected void initData() {
        super.initData();
        AppManager.getHttpService()
                .getScaleList(1, 15, "all")
                .enqueue(new BaseResponseCallback<PaginationResponse<ScaleWrapper>>() {
                    @Override
                    protected void onSuccess(PaginationResponse<ScaleWrapper> response) {

                    }

                    @Override
                    protected void onFailure(ErrorResponse errorResponse) {

                    }
                });
    }
}
