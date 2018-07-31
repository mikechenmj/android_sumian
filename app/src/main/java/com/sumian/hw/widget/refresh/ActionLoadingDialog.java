package com.sumian.hw.widget.refresh;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.sumian.sleepdoctor.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by jzz
 * on 2017/11/3.
 * <p>
 * desc:
 */

public class ActionLoadingDialog extends DialogFragment {

    private Unbinder mUnbinder;
    private boolean mIsShowing; // 判断当前dialog是否正在显示，如果正在显示，则不重复调用show()

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_DialogWhenLarge_NoActionBar);
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = inflater.inflate(R.layout.hw_lay_action_loading, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        super.onDestroyView();
    }

    /**
     * 注意：这里不能用isAttach()代替mIsShowing，因为调用完super.show()之后，迅速调用isAttach()拿到的数据是还是false
     * @param fragmentManager fragmentManager
     * @return ActionLoadingDialog.this
     */
    public ActionLoadingDialog show(FragmentManager fragmentManager) {
        if (mIsShowing) {
            return this;
        }
        show(fragmentManager, this.getClass().getSimpleName());
        mIsShowing = true;
        return this;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mIsShowing = false;
    }
}
