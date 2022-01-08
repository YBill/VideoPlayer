package com.bill.videoplayer.small_video;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.bill.baseplayer.config.VideoPlayType;
import com.bill.player.controller.component.BaseComponent;
import com.bill.videoplayer.R;

/**
 * author ywb
 * date 2022/1/8
 * desc 小视频详情页组件
 */
public class SmallVideoComponent extends BaseComponent {

    private AppCompatImageView coverIv;
    private AppCompatImageView playBtn;

    public SmallVideoComponent(@NonNull Context context) {
        super(context);
        initView();
    }

    public SmallVideoComponent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        coverIv = this.findViewById(R.id.iv_c_sv_cover);
        playBtn = this.findViewById(R.id.iv_c_sv_play);

        this.setOnClickListener(v -> {
            if (mControlWrapper != null)
                mControlWrapper.togglePlay();
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_small_video_component;
    }

    public AppCompatImageView getCoverIv() {
        return coverIv;
    }

    @Override
    public void onSingleTapConfirmed() {

    }

    @Override
    public void onPlayStateChanged(@VideoPlayType int playState) {
        switch (playState) {
            case VideoPlayType.STATE_IDLE:
                coverIv.setVisibility(VISIBLE);
                break;
            case VideoPlayType.STATE_PLAYING:
                coverIv.setVisibility(GONE);
                playBtn.setVisibility(GONE);
                break;
            case VideoPlayType.STATE_PAUSED:
                coverIv.setVisibility(GONE);
                playBtn.setVisibility(VISIBLE);
                break;
            case VideoPlayType.STATE_PREPARED:
                break;
        }

    }

    @Override
    public boolean isDissociate() {
        // 注意这里要设置为游离的，即View的层级交给Adapter处理，这里只是用来处理状态的
        return true;
    }
}
