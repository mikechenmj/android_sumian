package com.sumian.sleepdoctor.pager.decoration;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.chat.bean.PinYinUserProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sm
 * on 2018/2/23.
 * desc:
 */

public class MemberDecoration extends RecyclerView.ItemDecoration {

    private List<PinYinUserProfile> mItems;

    public MemberDecoration() {
        this.mItems = new ArrayList<>(0);
    }

    public void addAllItems(List<PinYinUserProfile> items) {
        mItems.addAll(items);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        int paddingLeft = parent.getPaddingLeft();
        int paddingRight = parent.getPaddingRight();
        int right = parent.getRight() - paddingRight;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        paint.setColor(parent.getResources().getColor(R.color.b1_color));
        Rect rect;
        for (int i = 0; i < childCount; i++) {
            View itemView = parent.getChildAt(i);

            int position = parent.getChildAdapterPosition(itemView);

            if (position <= -1) continue;

            if (position == 0 || isFirstPinYinMembers(position)) {
                int top = itemView.getTop() - 32;
                int bottom = itemView.getTop();

                rect = new Rect(paddingLeft, top, right, bottom);
                c.drawRect(rect, paint);
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        if (position == 0 || isFirstPinYinMembers(position)) {
            outRect.top = 32;
        } else {
            outRect.top = 2;
        }
    }

    private boolean isFirstPinYinMembers(int position) {
        if (position == 0) {
            return true;
        } else {
            PinYinUserProfile prePinYinUserProfile = mItems.get(position - 1);
            PinYinUserProfile currentPinYinUserProfile = mItems.get(position);

            // String preFirstChar = prePinYinUserProfile.firstChar;
            // String currentFirstChar = currentPinYinUserProfile.firstChar;

            return true;
                    //!preFirstChar.equals(currentFirstChar) ||
                   // prePinYinUserProfile.userProfile.role > currentPinYinUserProfile.userProfile.role;
        }
    }
}
