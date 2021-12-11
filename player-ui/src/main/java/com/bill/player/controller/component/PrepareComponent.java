package com.bill.player.controller.component;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.bill.baseplayer.config.VideoPlayType;
import com.bill.baseplayer.config.VideoViewManager;
import com.bill.player.controller.R;

/**
 * author ywb
 * date 2021/12/7
 * desc 准备组件
 */
public class PrepareComponent extends BaseComponent implements View.OnClickListener {

    private AppCompatImageView thumbIv;
    private View playBtn;
    private ProgressBar loadPb;
    private View netView;

    public PrepareComponent(@NonNull Context context) {
        super(context);
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_component_prepare;
    }

    private void initView() {
        thumbIv = this.findViewById(R.id.iv_component_pre_thumb);
        playBtn = this.findViewById(R.id.iv_component_pre_play);
        loadPb = this.findViewById(R.id.pb_component_pre_loading);
        netView = this.findViewById(R.id.fl_component_pre_net);
        playBtn.setOnClickListener(this);
        this.findViewById(R.id.tv_component_pre_continue_play).setOnClickListener(this);
    }

    /**
     * 封面图
     */
    public AppCompatImageView getThumbIv() {
        return thumbIv;
    }

    @Override
    public void onPlayStateChanged(@VideoPlayType int playState) {
        switch (playState) {
            case VideoPlayType.STATE_PREPARING:
                setVisibility(VISIBLE);
                playBtn.setVisibility(View.GONE);
                netView.setVisibility(GONE);
                loadPb.setVisibility(View.VISIBLE);
                break;
            case VideoPlayType.STATE_PLAYING:
            case VideoPlayType.STATE_PAUSED:
            case VideoPlayType.STATE_ERROR:
            case VideoPlayType.STATE_COMPLETED:
                setVisibility(GONE);
                break;
            case VideoPlayType.STATE_IDLE:
                setVisibility(VISIBLE);
                loadPb.setVisibility(View.GONE);
                netView.setVisibility(GONE);
                playBtn.setVisibility(View.VISIBLE);
                thumbIv.setVisibility(View.VISIBLE);
                break;
            case VideoPlayType.STATE_START_ABORT:
                setVisibility(VISIBLE);
                netView.setVisibility(VISIBLE);
                loadPb.setVisibility(View.GONE);
                playBtn.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_component_pre_play) {
            if (mControlWrapper != null)
                mControlWrapper.start();
        } else if (v.getId() == R.id.tv_component_pre_continue_play) {
            netView.setVisibility(GONE);
            VideoViewManager.getInstance().setPlayOnMobileNetwork(true);
            if (mControlWrapper != null)
                mControlWrapper.start();
        }
    }

}
