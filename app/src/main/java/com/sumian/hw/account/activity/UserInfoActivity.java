package com.sumian.hw.account.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;
import com.sumian.hw.account.contract.ModifyUserInfoContract;
import com.sumian.hw.account.contract.UserInfoContract;
import com.sumian.hw.account.presenter.UserInfoPresenter;
import com.sumian.hw.account.sheet.SelectBottomSheet;
import com.sumian.hw.account.sheet.SelectGenderBottomSheet;
import com.sumian.hw.base.BaseActivity;
import com.sumian.hw.common.helper.ToastHelper;
import com.sumian.hw.network.response.HwUserInfo;
import com.sumian.hw.widget.BottomSheetView;
import com.sumian.hw.widget.TitleBar;
import com.sumian.hw.widget.refresh.BlueRefreshView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jzz
 * on 2017/10/12.
 * desc:
 */

public class UserInfoActivity extends BaseActivity implements TitleBar.OnBackListener
        , SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, UserInfoContract.View {

    TitleBar mTitleBar;
    BlueRefreshView mBlueRefreshView;
    CircleImageView mIvAvatar;
    TextView mTvNickname;
    TextView mTvMobile;
    TextView mTvArea;
    TextView mTvGender;
    TextView mTvBirthday;
    TextView mTvHeight;
    TextView mTvWeight;
    TextView mTvCareer;

    private UserInfoContract.Presenter mPresenter;
    private HwUserInfo mUserInfo;

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
        mTitleBar = findViewById(R.id.title_bar);
        mBlueRefreshView = findViewById(R.id.refresh);
        mIvAvatar = findViewById(R.id.iv_avatar);
        mTvNickname = findViewById(R.id.tv_nickname);
        mTvMobile = findViewById(R.id.tv_mobile);
        mTvArea = findViewById(R.id.tv_area);
        mTvGender = findViewById(R.id.tv_gender);
        mTvBirthday = findViewById(R.id.tv_birthday);
        mTvHeight = findViewById(R.id.tv_height);
        mTvWeight = findViewById(R.id.tv_weight);
        mTvCareer = findViewById(R.id.tv_career);

        findViewById(R.id.lay_avatar).setOnClickListener(this);
        findViewById(R.id.lay_nickname).setOnClickListener(this);
        findViewById(R.id.lay_area).setOnClickListener(this);
        findViewById(R.id.lay_gender).setOnClickListener(this);
        findViewById(R.id.lay_birthday).setOnClickListener(this);
        findViewById(R.id.lay_height).setOnClickListener(this);
        findViewById(R.id.lay_weight).setOnClickListener(this);
        findViewById(R.id.lay_career).setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        String formKey = null;
        int id = v.getId();
        if (id == R.id.lay_avatar) {
            AvatarImageActivity.show(v.getContext(), mUserInfo.getAvatar());
        } else if (id == R.id.lay_nickname) {
            ModifyNicknameActivity.show(this, ModifyNicknameActivity.NICKNAME_TYPE);
        } else if (id == R.id.lay_gender) {
            commitBottomSheet(SelectGenderBottomSheet.newInstance(ModifyUserInfoContract.KEY_GENDER));
        } else if (id == R.id.lay_area) {
            formKey = ModifyUserInfoContract.KEY_AREA;
        } else if (id == R.id.lay_birthday) {
            formKey = ModifyUserInfoContract.KEY_BIRTHDAY;
        } else if (id == R.id.lay_height) {
            formKey = ModifyUserInfoContract.KEY_HEIGHT;
        } else if (id == R.id.lay_weight) {
            formKey = ModifyUserInfoContract.KEY_WEIGHT;
        } else if (id == R.id.lay_career) {
            ModifyNicknameActivity.show(this, ModifyNicknameActivity.CAREER_TYPE);
        }
        if (TextUtils.isEmpty(formKey)) {
            return;
        }
        commitBottomSheet(SelectBottomSheet.newInstance(formKey, mUserInfo));
    }

    @Override
    public void setPresenter(UserInfoContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onSyncCacheUserInfoSuccess(HwUserInfo userInfo) {
        runUiThread(() -> {
//            Glide.with(UserInfoActivity.this)
//                    .load(userInfo.getAvatar())
//                    .asBitmap()
//                    .placeholder(R.mipmap.ic_default_avatar) //设置占位图，在加载之前显示
//                    .error(R.mipmap.ic_default_avatar) //在图像加载失败时显示
//                    .into(mIvAvatar);

            setText(userInfo.getNickname(), mTvNickname);
            setText(userInfo.getMobile(), mTvMobile);
            setText(userInfo.getArea(), mTvArea);
            mTvGender.setText(mPresenter.formatGender(userInfo.getGender()));
            setText(userInfo.getBirthday(), mTvBirthday);
            String height = userInfo.getHeight();
            setText(height, mTvHeight);
            if (!TextUtils.isEmpty(height)) {
                mTvHeight.append(" cm");
            }

            String weight = userInfo.getWeight();
            setText(weight, mTvWeight);
            if (!TextUtils.isEmpty(weight)) {
                mTvWeight.append(" kg");
            }
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
