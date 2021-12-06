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

import com.bill.baseplayer.base.VideoView;
import com.bill.baseplayer.controller.BaseComponent;
import com.bill.player.controller.R;
import com.bill.player.controller.util.ComponentUtils;

/**
 * author ywb
 * date 2021/12/6
 * desc
 */
public class GestureComponent extends BaseComponent {

    private View contentView;
    private AppCompatImageView mIcon;
    private AppCompatTextView mTextPercent;
    private ProgressBar mProgressPercent;

    public GestureComponent(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        initView();
        this.setVisibility(GONE);
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_component_gesture, this, true);
        contentView = this.findViewById(R.id.ll_component_ges);
        mIcon = this.findViewById(R.id.iv_component_ges_icon);
        mTextPercent = this.findViewById(R.id.tv_component_ges_msg);
        mProgressPercent = this.findViewById(R.id.pb_component_ges);
    }

    @Override
    public void onStartSlide() {
        super.onStartSlide();
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
        mProgressPercent.setVisibility(GONE);
        if (slidePosition > currentPosition) {
            mIcon.setImageResource(R.drawable.icon_component_forward);
        } else {
            mIcon.setImageResource(R.drawable.icon_component_backward);
        }
        mTextPercent.setText(String.format("%s/%s", ComponentUtils.stringForTime(slidePosition), ComponentUtils.stringForTime(duration)));
    }

    @Override
    public void onBrightnessChange(int percent) {
        mProgressPercent.setVisibility(VISIBLE);
        mIcon.setImageResource(R.drawable.icon_component_brightness);
        mTextPercent.setText(percent + "%");
        mProgressPercent.setProgress(percent);
    }

    @Override
    public void onVolumeChange(int percent) {
        mProgressPercent.setVisibility(VISIBLE);
        if (percent <= 0) {
            mIcon.setImageResource(R.drawable.icon_component_volume_0);
        } else if (percent < 50) {
            mIcon.setImageResource(R.drawable.icon_component_volume_low);
        } else {
            mIcon.setImageResource(R.drawable.icon_component_volume_high);
        }
        mTextPercent.setText(percent + "%");
        mProgressPercent.setProgress(percent);
    }

    @Override
    public void onPlayStateChanged(int playState) {
        if (playState == VideoView.STATE_IDLE
                || playState == VideoView.STATE_START_ABORT
                || playState == VideoView.STATE_PREPARING
                || playState == VideoView.STATE_PREPARED
                || playState == VideoView.STATE_ERROR
                || playState == VideoView.STATE_PLAYBACK_COMPLETED) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
        }
    }

}
