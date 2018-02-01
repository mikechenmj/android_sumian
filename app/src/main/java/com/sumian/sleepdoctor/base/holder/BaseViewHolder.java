package com.sumian.sleepdoctor.base.holder;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.sumian.sleepdoctor.R;

import java.util.Locale;

import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jzz
 * on 2018/1/21.
 * desc:
 */

public abstract class BaseViewHolder<Item> extends RecyclerView.ViewHolder implements View.OnClickListener {

    private static final String TAG = BaseViewHolder.class.getSimpleName();

    protected final RequestManager mLoader;

    protected Item mItem;

    protected OnReplayListener<Item> mOnReplayListener;

    public BaseViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mLoader = Glide.with(itemView.getContext());
    }

    public void initView(Item item) {
        this.mItem = item;
        itemView.setOnClickListener(this::onItemClick);
        itemView.setOnLongClickListener(v -> {
            onItemLongClick(v);
            return true;
        });
    }

    public void setOnReplayListener(OnReplayListener<Item> onReplayListener) {
        mOnReplayListener = onReplayListener;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: ---------->" + v.toString());
    }

    protected void load(String url, ImageView iv) {
        load(url, null, iv);
    }

    protected void load(String url, RequestOptions options, ImageView iv) {
        if (options == null) {
            mLoader.load(url).into(iv);
        } else {
            mLoader.load(url).apply(options).into(iv);
        }
    }

    protected String getText(@StringRes int strId) {
        return itemView.getContext().getString(strId);
    }

    protected void setText(TextView tv, String text) {
        tv.setText(text);
        tv.setVisibility(View.VISIBLE);
    }

    protected String formatText(String format, Object... args) {
        return String.format(Locale.getDefault(), format, args);
    }

    protected void gone(View v) {
        gone(true, v);
    }

    protected void gone(boolean isGone, View v) {
        v.setVisibility(isGone ? View.GONE : View.VISIBLE);
    }

    protected void visible(View v) {
        gone(false, v);
    }

    protected void invisible(View v) {
        v.setVisibility(View.INVISIBLE);
    }

    protected void onItemClick(View v) {
        Log.d(TAG, "onItemClick: -------->" + v.toString());
    }

    protected boolean onItemLongClick(View v) {
        Log.e(TAG, "onItemLongClick: ----------->" + v.toString());

        return false;
    }

    public interface OnReplayListener<Message> {

        void onReplyMsg(Message msg);
    }

    protected void formatRoleAvatar(int role, String url, CircleImageView cIv) {

        @DrawableRes int drawableId;
        if (role == 0) {
            drawableId = R.mipmap.info_avatar_patient;
        } else {
            drawableId = R.mipmap.info_avatar_doctor;
        }

        RequestOptions options = new RequestOptions();

        options.placeholder(drawableId).error(drawableId).getOptions();

        mLoader.load(url).apply(options).into(cIv);

    }

    protected void formatRoleLabel(int role, TextView tvRoleLabel) {
        String roleLabel = null;
        switch (role) {
            case 0://患者
                roleLabel = itemView.getResources().getString(R.string.patient);
                break;
            case 1://运营
                roleLabel = itemView.getResources().getString(R.string.dbo);
                break;
            case 2://助理
                roleLabel = itemView.getResources().getString(R.string.assistant);
                break;
            case 3://医生
                roleLabel = itemView.getResources().getString(R.string.doctor);
                break;
            default:
                break;
        }
        tvRoleLabel.setText(roleLabel);
        tvRoleLabel.setVisibility(TextUtils.isEmpty(roleLabel) ? View.INVISIBLE : View.VISIBLE);
    }

}
