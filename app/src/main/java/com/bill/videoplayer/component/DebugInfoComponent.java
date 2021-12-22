package com.bill.videoplayer.component;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.bill.baseplayer.config.VideoPlayType;
import com.bill.baseplayer.config.VideoPlayerType;
import com.bill.baseplayer.controller.ControlWrapper;
import com.bill.baseplayer.controller.IControlComponent;
import com.bill.videoplayer.util.DebugUtils;

/**
 * author ywb
 * date 2021/12/1
 * desc 显示播放器信息组件，测试使用
 */
public class DebugInfoComponent extends AppCompatTextView implements IControlComponent {

    private ControlWrapper mControlWrapper;
    private int mCurrentPlayState;

    public DebugInfoComponent(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        setBackgroundColor(Color.parseColor("#66000000"));
        setTextSize(10);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        setLayoutParams(lp);
    }

    private String getDebugString(int playState) {
        return DebugUtils.getCurrentPlayer(mControlWrapper) + DebugUtils.getCurrentRenderer(mControlWrapper) + "\n"
                + "Video Size: [" + mControlWrapper.getVideoSize()[0] + "," + mControlWrapper.getVideoSize()[1] + "]\n"
                + DebugUtils.getAspectRatioType(mControlWrapper) + "\n"
                + "Play Speed：" + mControlWrapper.getSpeed() + "\n"
                + DebugUtils.getPlayState2str(playState);
    }

    public void refreshUI() {
        setText(getDebugString(mCurrentPlayState));
    }

    @Override
    public void onVisibilityChanged(boolean isVisible) {

    }

    @Override
    public void onPlayStateChanged(@VideoPlayType int playState) {
        this.mCurrentPlayState = playState;
        refreshUI();
    }

    @Override
    public void onPlayerStateChanged(@VideoPlayerType int playerState) {

    }

    @Override
    public void setProgress(long duration, long position) {

    }

    @Override
    public void setBufferingProgress(int percent) {

    }

    @Override
    public void onLockStateChanged(boolean isLocked) {

    }

    @Override
    public void onSingleTapConfirmed() {

    }

    @Override
    public void onDoubleTap() {

    }

    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
        mControlWrapper = controlWrapper;
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

    @Nullable
    @Override
    public String getKey() {
        return this.getClass().getSimpleName();
    }
}
