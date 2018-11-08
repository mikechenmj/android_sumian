package com.sumian.hw.upgrade.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sumian.hw.widget.BaseDialogFragment;
import com.sumian.sd.R;

import java.util.Locale;

/**
 * Created by jzz
 * on 2017/11/3.
 * <p>
 * desc:
 */

public class VersionDialog extends BaseDialogFragment {

    TextView mTvFirmwareTitle;
    TextView mTvProgress;
    ProgressBar mProgressBar;

    private String mDialogTitle;

    public static VersionDialog newInstance(String title) {
        VersionDialog versionDialog = new VersionDialog();
        Bundle args = new Bundle();
        args.putString("dialogTitle", title);
        versionDialog.setArguments(args);
        return versionDialog;
    }

    @Override
    protected void initBundle(Bundle arguments) {
        super.initBundle(arguments);
        this.mDialogTitle = arguments.getString("dialogTitle");
    }

    @Override
    protected int getLayout() {
        setCancelable(false);
        return R.layout.hw_lay_dialog_version;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mTvFirmwareTitle = rootView.findViewById(R.id.tv_version_title);
        mTvProgress = rootView.findViewById(R.id.tv_progress);
        mProgressBar = rootView.findViewById(R.id.progressBar);
    }

    @Override
    protected void initData() {
        super.initData();
        this.mTvFirmwareTitle.setText(mDialogTitle);
    }

    public void updateProgress(int progress) {
        runUiThread(() -> {
            this.mTvProgress.setText(String.format(Locale.getDefault(), "%d%s", progress, "%"));
            this.mProgressBar.setProgress(progress);
        });
    }

    public void cancel() {
        dismiss();
    }
}
