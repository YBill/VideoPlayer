package com.bill.player.controller.component;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.bill.baseplayer.config.VideoPlayType;
import com.bill.baseplayer.controller.IGestureComponent;
import com.bill.player.controller.R;
import com.bill.player.controller.util.ComponentUtils;

/**
 * author ywb
 * date 2021/12/6
 * desc 手势组件
 */
public class GestureComponent extends BaseComponent implements IGestureComponent {

    private View contentView;
    private AppCompatImageView gesTypeIconIv;
    private AppCompatTextView gesPercentTv;
    private ProgressBar gesPercentPb;

    public GestureComponent(@NonNull Context context) {
        super(context);
        initView();
        this.setVisibility(GONE);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_component_gesture;
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_component_gesture, this, true);
        contentView = this.findViewById(R.id.ll_component_ges);
        gesTypeIconIv = this.findViewById(R.id.iv_component_ges_icon);
        gesPercentTv = this.findViewById(R.id.tv_component_ges_msg);
        gesPercentPb = this.findViewById(R.id.pb_component_ges);
    }

    @Override
    public void onStartSlide() {
        if (mControlWrapper != null)
            mControlWrapper.hide();
        contentView.setVisibility(VISIBLE);
        contentView.setAlpha(1f);
    }

    @Override
    public void onStopSlide() {
        contentView.animate()
                .alpha(0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        contentView.setVisibility(GONE);
                    }
                })
                .start();
    }

    @Override
    public void onPositionChange(long slidePosition, long currentPosition, long duration) {
        gesPercentPb.setVisibility(GONE);
        if (slidePosition > currentPosition) {
            gesTypeIconIv.setImageResource(R.drawable.icon_component_forward);
        } else {
            gesTypeIconIv.setImageResource(R.drawable.icon_component_backward);
        }
        gesPercentTv.setText(String.format("%s/%s", ComponentUtils.stringForTime(slidePosition), ComponentUtils.stringForTime(duration)));
    }

    @Override
    public void onBrightnessChange(int percent) {
        gesPercentPb.setVisibility(VISIBLE);
        gesTypeIconIv.setImageResource(R.drawable.icon_component_brightness);
        gesPercentTv.setText(percent + "%");
        gesPercentPb.setProgress(percent);
    }

    @Override
    public void onVolumeChange(int percent) {
        gesPercentPb.setVisibility(VISIBLE);
        if (percent <= 0) {
            gesTypeIconIv.setImageResource(R.drawable.icon_component_volume_0);
        } else if (percent < 50) {
            gesTypeIconIv.setImageResource(R.drawable.icon_component_volume_low);
        } else {
            gesTypeIconIv.setImageResource(R.drawable.icon_component_volume_high);
        }
        gesPercentTv.setText(percent + "%");
        gesPercentPb.setProgress(percent);
    }

    @Override
    public void onPlayStateChanged(@VideoPlayType int playState) {
        if (playState == VideoPlayType.STATE_IDLE
                || playState == VideoPlayType.STATE_START_ABORT
                || playState == VideoPlayType.STATE_PREPARING
                || playState == VideoPlayType.STATE_PREPARED
                || playState == VideoPlayType.STATE_ERROR
                || playState == VideoPlayType.STATE_COMPLETED) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
        }
    }

}
