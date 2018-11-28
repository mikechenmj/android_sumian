package com.sumian.sd.widget.dialog;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.sumian.sd.R;

/**
 * Created by jzz
 * on 2017/11/3.
 * <p>
 * desc:
 */

@SuppressWarnings("deprecation")
public class ActionLoadingDialog extends DialogFragment {


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
        return inflater.inflate(R.layout.lay_action_loading, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public ActionLoadingDialog show(FragmentManager fragmentManager) {
        show(fragmentManager, this.getClass().getSimpleName());
        return this;
    }
}
