package com.sumian.sd.widget.fold;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.sd.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sm
 * on 2018/5/30 13:54
 * desc:
 **/
public class FoldLayout extends LinearLayout implements View.OnClickListener {

    private static final String TAG = FoldLayout.class.getSimpleName();

    @BindView(R.id.tv_summary)
    TextView tvSummary;
    @BindView(R.id.tv_show_more)
    TextView tvShowMore;

    public FoldLayout(Context context) {
        this(context, null);
    }

    public FoldLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FoldLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ButterKnife.bind(inflate(context, R.layout.lay_fold_text_container, this));

    }

    @Override
    public void onClick(View v) {
        tvSummary.setMaxLines(Integer.MAX_VALUE);
        tvShowMore.setLines(Integer.MAX_VALUE);
        tvShowMore.setVisibility(GONE);
    }

    public void setText(String text) {
        tvSummary.setVisibility(GONE);
        tvSummary.setText(text);
        tvSummary.setVisibility(VISIBLE);
        if (tvSummary.getLineCount() > 4) {
            tvSummary.setEllipsize(TextUtils.TruncateAt.END);
            tvSummary.setMaxLines(4);

            tvShowMore.setTag(true);
            tvShowMore.setOnClickListener(this);
            tvShowMore.setVisibility(VISIBLE);
        } else {
            if (tvShowMore.getTag() == null) {
                tvShowMore.setVisibility(GONE);
            }
        }
    }
}
