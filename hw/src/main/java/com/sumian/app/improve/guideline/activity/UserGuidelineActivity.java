package com.sumian.app.improve.guideline.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapEncoder;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.sumian.app.R;
import com.sumian.app.account.activity.LoginRouterActivity;
import com.sumian.app.app.AppManager;
import com.sumian.app.app.App;
import com.sumian.app.base.BaseActivity;
import com.sumian.app.improve.guideline.bean.Guideline;
import com.sumian.app.improve.guideline.utils.GuidelineUtils;
import com.sumian.app.improve.main.HomeActivity;
import com.sumian.app.improve.widget.GuidelineIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sm
 * on 2018/3/22.
 * desc:
 */

public class UserGuidelineActivity extends BaseActivity {

    ViewPager mGuidelineViewpager;

    private PagerAdapter mAdapter;

    private List<Guideline> mGuidelines = new ArrayList<>(0);

    public static void show(Context context) {
        context.startActivity(new Intent(context, UserGuidelineActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_guide_line_activity;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mGuidelineViewpager = findViewById(R.id.guideline_viewpager);
        mGuidelineViewpager.setAdapter(mAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return mGuidelines.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                Guideline guideline = mGuidelines.get(position);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.initView(guideline);
                container.addView(viewHolder.getItemView());
                return container.getChildAt(position);
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                //super.destroyItem(container, position, object);
                //container.removeViewAt(position);
            }

            @Override
            public int getItemPosition(@NonNull Object object) {
                return PagerAdapter.POSITION_NONE;
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();

        Guideline guidelineOne = new Guideline();
        guidelineOne.h1Label = R.string.guideline_one_h1;
        guidelineOne.h2Label = R.string.guideline_one_h2;
        guidelineOne.iconId = R.mipmap.rotation_1;
        guidelineOne.indicatorPosition = 1;

        this.mGuidelines.add(guidelineOne);

        Guideline guidelineTwo = new Guideline();
        guidelineTwo.h1Label = R.string.guideline_two_h1;
        guidelineTwo.h2Label = R.string.guideline_two_h2;
        guidelineTwo.iconId = R.mipmap.rotation_2;
        guidelineTwo.indicatorPosition = 2;
        this.mGuidelines.add(guidelineTwo);

        Guideline guidelineThree = new Guideline();
        guidelineThree.h1Label = R.string.guideline_three_h1;
        guidelineThree.h2Label = R.string.guideline_three_h2;
        guidelineThree.iconId = R.mipmap.rotation_3;
        guidelineThree.indicatorPosition = 3;

        this.mGuidelines.add(guidelineThree);

        Guideline guidelineFour = new Guideline();
        guidelineFour.h1Label = R.string.guideline_four_h1;
        guidelineFour.h2Label = R.string.guideline_four_h2;
        guidelineFour.iconId = R.mipmap.rotation_4;
        guidelineFour.indicatorPosition = 4;

        this.mGuidelines.add(guidelineFour);

        this.mAdapter.notifyDataSetChanged();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0 全透明实现
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {//4.4 全透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    class ViewHolder implements View.OnClickListener {

        TextView mTvGuidelineOne;
        TextView mTvGuidelineTwo;
        ImageView mIvGuidelineIcon;
        Button mBtAction;
        GuidelineIndicator mGuidelineIndicator;

        private View itemView;

        @SuppressLint("InflateParams")
        public ViewHolder() {
            itemView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.hw_lay_guideline_container, null, false);
            ButterKnife.bind(this, itemView);

            mTvGuidelineOne = itemView.findViewById(R.id.tv_guideline_one);
            mTvGuidelineTwo = itemView.findViewById(R.id.tv_guideline_two);
            mIvGuidelineIcon = itemView.findViewById(R.id.iv_guideline_icon);
            mBtAction = itemView.findViewById(R.id.bt_action);
            mGuidelineIndicator = itemView.findViewById(R.id.guideline_indicator);
        }

        public void initView(Guideline guideline) {
            mTvGuidelineOne.setText(guideline.h1Label);
            mTvGuidelineTwo.setText(guideline.h2Label);
            //mIvGuidelineIcon.setImageResource(guideline.iconId);

            DisplayMetrics displayMetrics = itemView.getResources().getDisplayMetrics();
            int heightPixels = displayMetrics.heightPixels;
            int widthPixels = displayMetrics.widthPixels;

            Glide
                    .with(itemView.getContext())
                    .load(guideline.iconId)
                    .asBitmap()
                    .encoder(new BitmapEncoder(Bitmap.CompressFormat.PNG, 100))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .override(widthPixels, heightPixels)
                    .centerCrop()
                    //.into(mIvGuidelineIcon);
                    .into(new SimpleTarget<Bitmap>(mIvGuidelineIcon.getWidth(), mIvGuidelineIcon.getHeight()) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            mIvGuidelineIcon.setImageBitmap(resource);
                        }
                    });

            if (guideline.indicatorPosition == 4) {
                mBtAction.setOnClickListener(this);
                mBtAction.setVisibility(View.VISIBLE);
            } else {
                mBtAction.setVisibility(View.INVISIBLE);
            }
            mGuidelineIndicator.showIndicator(guideline.indicatorPosition);
        }

        public View getItemView() {
            return itemView;
        }

        @Override
        public void onClick(View v) {
            GuidelineUtils.putBoolean(GuidelineUtils.SP_KEY_NEED_SHOW_WELCOME_USER_GUIDE, false);
            boolean login = AppManager.getAccountModel().isLogin();
            if (login) {
                HomeActivity.show(App.getAppContext());
            } else {
                LoginRouterActivity.show(UserGuidelineActivity.this);
            }
            finish();
        }
    }

}
