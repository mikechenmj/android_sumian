package com.sumian.sleepdoctor.improve.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.widget.shapeImageView.progress.GlideApp;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/3 14:45
 *     desc   : 医生服务Item
 *     version: 1.0
 * </pre>
 */
public class DoctorServiceItemView extends FrameLayout {
    @BindView(R.id.v_price_bg)
    View mPriceBg;
    @BindView(R.id.tv_price)
    TextView mPriceTv;
    @BindView(R.id.iv)
    ImageView mImageView;
    @BindView(R.id.tv_title)
    TextView mTitleTv;
    @BindView(R.id.tv_desc)
    TextView mDescTv;

    public DoctorServiceItemView(Context context) {
        this(context, null);
    }

    public DoctorServiceItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.lay_doctor_service_item, this);
        ButterKnife.bind(this);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.DoctorServiceItemView, 0, 0);
        boolean showPrice = attributes.getBoolean(R.styleable.DoctorServiceItemView_dsiv_show_price, false);
        float price = attributes.getFloat(R.styleable.DoctorServiceItemView_dsiv_price, 0f);
        String title = attributes.getString(R.styleable.DoctorServiceItemView_dsiv_title);
        String desc = attributes.getString(R.styleable.DoctorServiceItemView_dsiv_desc);
        attributes.recycle();

        showPrice(showPrice);
        setPrice(price);
        setTitle(title);
        setDesc(desc);
    }

    public void showPrice(boolean showPrice) {
        int visibility = showPrice ? VISIBLE : GONE;
        mPriceTv.setVisibility(visibility);
        mPriceBg.setVisibility(visibility);
    }

    public void setPrice(float price) {
        String format = String.format(Locale.getDefault(), "%.0f元", price);
        mPriceTv.setText(format);
    }

    public void setTitle(String title) {
        mTitleTv.setText(title);
    }

    public void setDesc(String desc) {
        mDescTv.setText(desc);
    }

    public void loadImage(@DrawableRes int drawableId) {
        mImageView.setImageResource(drawableId);
    }

    @SuppressWarnings("unused")
    public void loadImage(String uri) {
        GlideApp.with(this)
                .load(uri)
                .into(mImageView);
    }
}
