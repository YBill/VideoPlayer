package com.bill.videoplayer.small_video;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bill.videoplayer.util.Utils;

/**
 * author ywb
 * date 2022/1/8
 * desc 小视频分割间距
 */
public class SmallVideoListDecoration extends RecyclerView.ItemDecoration {

    private int interval;

    public SmallVideoListDecoration() {
        this.interval = (int) Utils.dp2Px(1.5f);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildLayoutPosition(view);
        if (position > -1) {

            if (position % 2 == 0) {
                outRect.right = interval;
            } else {
                outRect.left = interval;
            }

            if (position > 1) {
                outRect.top = interval;
            }
            outRect.bottom = interval;

        }
    }

}