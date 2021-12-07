package com.bill.player.controller.component;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.bill.baseplayer.base.VideoView;
import com.bill.player.controller.R;

/**
 * author ywb
 * date 2021/12/7
 * desc 准备组件
 */
public class PrepareComponent extends BaseComponent {

    private ProgressBar loadPb;
    private View netView;

    public PrepareComponent(@NonNull Context context) {
        super(context);
        loadPb = this.findViewById(R.id.pb_component_pre_loading);
        netView = this.findViewById(R.id.fl_component_pre_net);
        this.findViewById(R.id.tv_component_pre_continue_play).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                netView.setVisibility(GONE);
//                VideoViewManager.getInstance().setPlayOnMobileNetwork(true);
                mControlWrapper.start();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_component_prepare;
    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case VideoView.STATE_PREPARING:
                setVisibility(VISIBLE);
                netView.setVisibility(GONE);
                loadPb.setVisibility(View.VISIBLE);
                break;
            case VideoView.STATE_PLAYING:
            case VideoView.STATE_PAUSED:
            case VideoView.STATE_ERROR:
            case VideoView.STATE_COMPLETED:
                setVisibility(GONE);
                break;
            case VideoView.STATE_IDLE:
                setVisibility(VISIBLE);
                bringToFront();
                loadPb.setVisibility(View.GONE);
                netView.setVisibility(GONE);
                break;
            case VideoView.STATE_START_ABORT:
                setVisibility(VISIBLE);
                netView.setVisibility(VISIBLE);
                break;
        }
    }
}
