package com.sumian.sleepdoctor.pager.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jaeger.library.StatusBarUtil;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.chat.activity.MsgActivity;
import com.sumian.sleepdoctor.chat.widget.CustomPopWindow;
import com.sumian.sleepdoctor.pager.contract.GroupDetailContract;
import com.sumian.sleepdoctor.pager.presenter.GroupDetailPresenter;
import com.sumian.sleepdoctor.tab.bean.GroupDetail;
import com.sumian.sleepdoctor.widget.TitleBar;

import net.qiujuer.genius.ui.widget.Button;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sm
 * on 2018/1/22.
 * desc:
 */

public class ScanGroupResultActivity extends BaseActivity<GroupDetailPresenter> implements View.OnClickListener,
        GroupDetailContract.View, TitleBar.OnBackListener {

    private static final String TAG = ScanGroupResultActivity.class.getSimpleName();

    public static final String ARGS_GROUP_ID = "args_group_id";

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.lay_group_icon)
    CircleImageView mIvGroupIcon;

    @BindView(R.id.tv_desc)
    TextView mTvDesc;
    @BindView(R.id.tv_doctor_name)
    TextView mTvDoctorName;

    @BindView(R.id.tv_group_desc)
    TextView mTvGroupDesc;

    @BindView(R.id.tv_money)
    TextView mTvMoney;

    @BindView(R.id.iv_faq)
    ImageView mIvFaq;

    @BindView(R.id.bt_join)
    Button mBtJoin;
    @BindView(R.id.bt_re_scan)
    Button mBtReScan;


    private int mGroupId;

    private GroupDetail<UserProfile, UserProfile> mGroupDetail;

    @Override
    protected boolean initBundle(Bundle bundle) {
        this.mGroupId = bundle.getInt(ARGS_GROUP_ID, 0);
        return super.initBundle(bundle);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pager_join_one;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary), 0);
        this.mTitleBar.addOnBackListener(this);
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        GroupDetailPresenter.init(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.getGroupDetail(mGroupId);
    }

    @OnClick({R.id.iv_faq, R.id.bt_join, R.id.bt_re_scan})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_faq:

                @SuppressLint("InflateParams") View rootView = LayoutInflater.from(v.getContext()).inflate(R.layout.lay_pop_pay_faq, null, false);

                CustomPopWindow popWindow = new CustomPopWindow.PopupWindowBuilder(v.getContext())
                        .setView(rootView)//显示的布局，还可以通过设置一个View
                        //     .size(600,400) //设置显示的大小，不设置就默认包裹内容
                        .setFocusable(true)//是否获取焦点，默认为ture
                        .setOutsideTouchable(true)//是否PopupWindow 以外触摸dissmiss
                        .create()//创建PopupWindow
                        .showAsDropDown(mIvFaq, -3 * (mIvFaq.getWidth()), (int) (-4.4 * mIvFaq.getHeight()), Gravity.TOP | Gravity.CENTER);//显示PopupWindow

                v.postDelayed(popWindow::dismiss, 3000);
                rootView.setOnClickListener(v1 -> popWindow.dismiss());

                break;
            case R.id.bt_join:


                List<GroupDetail<UserProfile, UserProfile>> groupDetails = AppManager.getGroupViewModel().getGroupDetails();
                if (groupDetails == null || groupDetails.isEmpty()) {//扫码未加入过该群,进入支付
                    goPayCenter(mGroupDetail);
                } else {//读取本地群列表缓存,查看是否已加入过该群,进行鉴权判断

                    GroupDetail<UserProfile, UserProfile> tempGroupDetail = null;

                    for (GroupDetail<UserProfile, UserProfile> groupDetail : groupDetails) {
                        if (groupDetail.id == mGroupId) {
                            tempGroupDetail = groupDetail;
                            break;
                        }
                    }

                    if (tempGroupDetail == null) {//扫码未加入过该群,进入支付
                        goPayCenter(mGroupDetail);
                    } else {
                        if (tempGroupDetail.role == 0) {//该用户在该群中的身份,是否是患者
                            if (tempGroupDetail.day_last == 0) {//需要续费
                                goPayCenter(mGroupDetail);
                            } else {
                                goMsgCenter(v);
                            }

                        } else {
                            goMsgCenter(v);
                        }
                    }
                }

                break;
            case R.id.bt_re_scan:
                ScanQrCodeActivity.show(this, ScanQrCodeActivity.class);
                finish();
                break;
            default:
                break;
        }
    }

    private void goPayCenter(GroupDetail<UserProfile, UserProfile> groupDetail) {
        Bundle args = new Bundle();
        args.putSerializable(PayGroupActivity.ARGS_GROUP_DETAIL, groupDetail);
        PayGroupActivity.show(this, PayGroupActivity.class, args);
    }

    private void goMsgCenter(View v) {
        Bundle extras = new Bundle();
        extras.putSerializable(MsgActivity.ARGS_GROUP_DETAIL, mGroupDetail);
        MsgActivity.show(v.getContext(), MsgActivity.class, extras);
    }

    @Override
    public void bindPresenter(GroupDetailContract.Presenter presenter) {
        mPresenter = (GroupDetailPresenter) presenter;
    }

    @Override
    public void onGetGroupDetailSuccess(GroupDetail<UserProfile, UserProfile> groupDetail) {
        this.mGroupDetail = groupDetail;
        runOnUiThread(() -> {
            RequestOptions options = new RequestOptions();
            options.placeholder(R.mipmap.group_avatar).error(R.mipmap.group_avatar).getOptions();
            Glide.with(this).load(groupDetail.avatar).apply(options).into(mIvGroupIcon);

            mTvDesc.setText(groupDetail.name);
            UserProfile doctor = groupDetail.doctor;
            if (doctor == null) {
                mTvDoctorName.setVisibility(View.GONE);
            } else {
                mTvDoctorName.setText(String.format(Locale.getDefault(), "%s%s", getString(R.string.doctor), groupDetail.doctor.nickname));
            }
            mTvGroupDesc.setText(groupDetail.description);

            mTvMoney.setText(String.format(Locale.getDefault(), "%.2f", groupDetail.monthly_price / 100.00f));

        });
    }

    @Override
    public void onFailure(String error) {
        showToast(error);
    }

    @Override
    public void onBack(View v) {
        finish();
    }
}
