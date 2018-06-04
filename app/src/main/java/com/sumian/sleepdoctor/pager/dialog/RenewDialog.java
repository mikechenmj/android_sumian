package com.sumian.sleepdoctor.pager.dialog;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.pager.activity.GroupDetailActivity;
import com.sumian.sleepdoctor.tab.bean.GroupDetail;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sm
 * on 2018/2/2.
 * desc:
 */

public class RenewDialog extends QMUIDialog implements View.OnClickListener {


    public RenewDialog(Context context) {
        this(context, R.style.QMUI_Dialog);
    }

    public RenewDialog(Context context, int styleRes) {
        super(context, styleRes);
    }


    public RenewDialog bindContentView(@LayoutRes int id) {
        setContentView(id);
        ButterKnife.bind(this);
        return this;
    }

    @OnClick({R.id.bt_cancel, R.id.bt_renewal})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_cancel:
                dismiss();
                break;
            case R.id.bt_renewal:
                GroupDetail<UserProfile, UserProfile> groupDetail = null;
                if (getOwnerActivity() != null) {
                    groupDetail = ((GroupDetailActivity) getOwnerActivity()).onGetGroupDetail();
                }
                //Bundle extras = new Bundle();
                //extras.putSerializable(ARGS_DOCTOR_SERVICE, groupDetail);
                // ShoppingCarActivity.show(v.getContext(), );
                dismiss();
                break;
            default:
                break;
        }

    }
}
