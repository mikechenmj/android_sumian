package com.sumian.app.account.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.sumian.app.R;
import com.sumian.app.account.contract.ModifyUserInfoContract;
import com.sumian.app.account.contract.UserInfoContract;
import com.sumian.app.account.presenter.UserInfoPresenter;
import com.sumian.app.account.sheet.SelectBottomSheet;
import com.sumian.app.account.sheet.SelectGenderBottomSheet;
import com.sumian.app.app.App;
import com.sumian.app.base.BaseActivity;
import com.sumian.app.common.helper.ToastHelper;
import com.sumian.app.network.response.UserInfo;
import com.sumian.app.widget.BottomSheetView;
import com.sumian.app.widget.TitleBar;
import com.sumian.app.widget.refresh.BlueRefreshView;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jzz
 * on 2017/10/12.
 * desc:
 */

public class UserInfoActivity extends BaseActivity implements TitleBar.OnBackListener
    , SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, UserInfoContract.View {

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.refresh)
    BlueRefreshView mBlueRefreshView;

    @BindView(R.id.iv_avatar)
    CircleImageView mIvAvatar;
    @BindView(R.id.tv_nickname)
    TextView mTvNickname;
    @BindView(R.id.tv_mobile)
    TextView mTvMobile;
    @BindView(R.id.tv_area)
    TextView mTvArea;
    @BindView(R.id.tv_gender)
    TextView mTvGender;
    @BindView(R.id.tv_birthday)
    TextView mTvBirthday;
    @BindView(R.id.tv_height)
    TextView mTvHeight;
    @BindView(R.id.tv_weight)
    TextView mTvWeight;
    @BindView(R.id.tv_career)
    TextView mTvCareer;

    private UserInfoContract.Presenter mPresenter;
    private UserInfo mUserInfo;

    public static void show(Context context) {
        context.startActivity(new Intent(context, UserInfoActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_user_info;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        UserInfoPresenter.init(this);
        mTitleBar.addOnBackListener(this);
        mBlueRefreshView.setOnRefreshListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        this.mPresenter.doLoadCacheUserInfo();
    }

    @Override
    public void onBack(View v) {
        finish();
    }

    @OnClick({R.id.lay_avatar, R.id.lay_nickname, R.id.lay_area, R.id.lay_gender, R.id.lay_birthday,
        R.id.lay_height, R.id.lay_weight, R.id.lay_career})
    public void onClick(View v) {
        String formKey = null;
        switch (v.getId()) {
            case R.id.lay_avatar:
                AvatarImageActivity.show(v.getContext(), mUserInfo.getAvatar());
                break;
            case R.id.lay_nickname:
                ModifyNicknameActivity.show(this, ModifyNicknameActivity.NICKNAME_TYPE);
                break;
            case R.id.lay_gender:
                commitBottomSheet(SelectGenderBottomSheet.newInstance(ModifyUserInfoContract.KEY_GENDER));
                break;
            case R.id.lay_area:
                formKey = ModifyUserInfoContract.KEY_AREA;
                break;
            case R.id.lay_birthday:
                formKey = ModifyUserInfoContract.KEY_BIRTHDAY;
                break;
            case R.id.lay_height:
                formKey = ModifyUserInfoContract.KEY_HEIGHT;
                break;
            case R.id.lay_weight:
                formKey = ModifyUserInfoContract.KEY_WEIGHT;
                break;
            case R.id.lay_career:
                ModifyNicknameActivity.show(this, ModifyNicknameActivity.CAREER_TYPE);
                break;
            default:
                break;
        }
        if (TextUtils.isEmpty(formKey)) return;
        commitBottomSheet(SelectBottomSheet.newInstance(formKey, mUserInfo));
    }

    @Override
    public void setPresenter(UserInfoContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onSyncCacheUserInfoSuccess(UserInfo userInfo) {
        runUiThread(() -> {
            App.getRequestManager()
                .load(userInfo.getAvatar())
                .asBitmap()
                .placeholder(R.mipmap.ic_default_avatar) //设置占位图，在加载之前显示
                .error(R.mipmap.ic_default_avatar) //在图像加载失败时显示
                .into(mIvAvatar);

            setText(userInfo.getNickname(), mTvNickname);
            setText(userInfo.getMobile(), mTvMobile);
            setText(userInfo.getArea(), mTvArea);
            mTvGender.setText(mPresenter.formatGender(userInfo.getGender()));
            setText(userInfo.getBirthday(), mTvBirthday);
            String height = userInfo.getHeight();
            setText(height, mTvHeight);
            if (!TextUtils.isEmpty(height))
                mTvHeight.append(" cm");

            String weight = userInfo.getWeight();
            setText(weight, mTvWeight);
            if (!TextUtils.isEmpty(weight))
                mTvWeight.append(" kg");
            mTvCareer.setText(!TextUtils.isEmpty(userInfo.getCareer()) ? userInfo.getCareer() : getResources().getString(R.string.user_none_default_hint));
            this.mUserInfo = userInfo;
        });
    }

    @Override
    public void onSyncCacheUserInfoFailed(String error) {
        runUiThread(() -> ToastHelper.show(error));
    }

    @Override
    public void onRefresh() {
        this.mPresenter.doRefreshUserInfo();
    }

    @Override
    public void onStartSyncUserInfo() {
        this.onBegin();
    }

    @Override
    public void onCompletedUserInfo() {
        onFinish();
    }

    @Override
    protected void onRelease() {
        mPresenter.release();
        super.onRelease();
    }

    private void setText(String text, TextView textView) {
        textView.setText(TextUtils.isEmpty(text) || "".equals(text) ? getString(R.string.user_none_default_hint) : text);
    }

    @Override
    public void onFailure(String error) {
        runUiThread(() -> ToastHelper.show(error));
    }

    @Override
    public void onBegin() {
        runUiThread(() -> mBlueRefreshView.setRefreshing(true));
    }

    @Override
    public void onFinish() {
        runUiThread(() -> mBlueRefreshView.setRefreshing(false));
    }

    private void commitBottomSheet(BottomSheetView bottomSheetView) {
        getSupportFragmentManager()
            .beginTransaction()
            .add(bottomSheetView, bottomSheetView.getClass().getSimpleName())
            .commit();
    }
}
