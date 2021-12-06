package com.bill.baseplayer.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * author ywb
 * date 2021/12/1
 * desc 组件基类
 */
public class BaseComponent extends FrameLayout implements IGestureComponent {

    protected ControlWrapper mControlWrapper;

    public BaseComponent(@NonNull Context context) {
        super(context);
    }

    public BaseComponent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseComponent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onVisibilityChanged(boolean isVisible) {

    }

    @Override
    public void onPlayStateChanged(int playState) {

    }

    @Override
    public void onPlayerStateChanged(int playerState) {

    }

    @Override
    public void setProgress(long duration, long position) {

    }

    @Override
    public void onLockStateChanged(boolean isLocked) {

    }

    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
        this.mControlWrapper = controlWrapper;
    }

    @Nullable
    @Override
    public View getView() {
        return this;
    }

    @Override
    public boolean isDissociate() {
        return false;
    }

    @Override
    public void onStartSlide() {

    }

    @Override
    public void onStopSlide() {

    }

    @Override
    public void onPositionChange(long slidePosition, long currentPosition, long duration) {

    }

    @Override
    public void onBrightnessChange(int percent) {

    }

    @Override
    public void onVolumeChange(int percent) {

    }

    @Override
    public void onSingleTapConfirmed() {

    }

    @Override
    public void onDoubleTap() {

    }
}
