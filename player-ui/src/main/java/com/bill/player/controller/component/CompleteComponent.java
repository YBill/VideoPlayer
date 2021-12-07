package com.bill.player.controller.component;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.bill.baseplayer.base.VideoView;
import com.bill.baseplayer.util.Utils;
import com.bill.player.controller.R;

/**
 * author ywb
 * date 2021/12/7
 * desc 完成组件
 */
public class CompleteComponent extends BaseComponent implements View.OnClickListener {

    public CompleteComponent(@NonNull Context context) {
        super(context);
        this.setVisibility(GONE);
        this.findViewById(R.id.iv_component_com_back).setOnClickListener(this);
        this.findViewById(R.id.ll_component_com_replay).setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_component_complete;
    }

    @Override
    public void onPlayStateChanged(int playState) {
        if (playState == VideoView.STATE_COMPLETED) {
            setVisibility(VISIBLE);
        } else {
            setVisibility(GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_component_com_back) {
            if (mControlWrapper != null && mControlWrapper.isFullScreen()) {
                Activity activity = Utils.scanForActivity(getContext());
                mControlWrapper.toggleFullScreen(activity);
            } else {
                Activity activity = Utils.scanForActivity(getContext());
                activity.finish();
            }
        } else if (v.getId() == R.id.ll_component_com_replay) {
            if (mControlWrapper != null)
                mControlWrapper.replay(true);
        }
    }
}
