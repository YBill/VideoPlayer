package com.bill.baseplayer.controller;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bill.baseplayer.config.AspectRatioType;
import com.bill.baseplayer.player.DataSource;

/**
 * author ywb
 * date 2021/11/24
 * desc  此类的目的是为了方便ControlComponent中既能调用VideoView的api又能调用BaseVideoController的api
 * 并对部分api做了封装，方便使用
 */
public class ControlWrapper implements PlayerControl, IVideoController {

    private final PlayerControl mPlayerControl;
    private final IVideoController mController;

    public ControlWrapper(@NonNull PlayerControl playerControl, @NonNull IVideoController controller) {
        mPlayerControl = playerControl;
        mController = controller;
    }

    @Override
    public void start() {
        mPlayerControl.start();
    }

    @Override
    public void pause() {
        mPlayerControl.pause();
    }

    @Override
    public void release() {
        mPlayerControl.release();
    }

    @Override
    public void replay(boolean resetPosition) {
        mPlayerControl.replay(resetPosition);
    }

    @Override
    public long getDuration() {
        return mPlayerControl.getDuration();
    }

    @Override
    public long getCurrentPosition() {
        return mPlayerControl.getCurrentPosition();
    }

    @Override
    public void seekTo(long pos) {
        mPlayerControl.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mPlayerControl.isPlaying();
    }

    @Override
    public int getBufferedPercentage() {
        return mPlayerControl.getBufferedPercentage();
    }

    @Override
    public void enterFullScreen() {
        mPlayerControl.enterFullScreen();
    }

    @Override
    public void exitFullScreen() {
        mPlayerControl.exitFullScreen();
    }

    @Override
    public boolean isFullScreen() {
        return mPlayerControl.isFullScreen();
    }

    @Override
    public void setMute(boolean isMute) {
        mPlayerControl.setMute(isMute);
    }

    @Override
    public boolean isMute() {
        return mPlayerControl.isMute();
    }

    @Override
    public void setScreenScaleType(@AspectRatioType int screenScaleType) {
        mPlayerControl.setScreenScaleType(screenScaleType);
    }

    @Override
    public void setSpeed(float speed) {
        mPlayerControl.setSpeed(speed);
    }

    @Override
    public float getSpeed() {
        return mPlayerControl.getSpeed();
    }

    @Override
    public long getTcpSpeed() {
        return mPlayerControl.getTcpSpeed();
    }

    @Override
    public void setMirrorRotation(boolean enable) {
        mPlayerControl.setMirrorRotation(enable);
    }

    @Override
    public Bitmap doScreenShot() {
        return mPlayerControl.doScreenShot();
    }

    @Override
    public int[] getVideoSize() {
        return mPlayerControl.getVideoSize();
    }

    @Override
    public void setRotation(float rotation) {
        mPlayerControl.setRotation(rotation);
    }

    @Override
    public void enterTinyScreen() {
        mPlayerControl.enterTinyScreen();
    }

    @Override
    public void exitTinyScreen() {
        mPlayerControl.exitTinyScreen();
    }

    @Override
    public boolean isTinyScreen() {
        return mPlayerControl.isTinyScreen();
    }

    @Override
    public void setTinyScreenView(ViewGroup tinyScreenContainerView) {
        mPlayerControl.setTinyScreenView(tinyScreenContainerView);
    }

    @Override
    public DataSource getDataSource() {
        return mPlayerControl.getDataSource();
    }

    @Override
    public void autoHideCountdown() {
        mController.autoHideCountdown();
    }

    @Override
    public void cancelHideCountdown() {
        mController.cancelHideCountdown();
    }

    @Override
    public boolean isShowing() {
        return mController.isShowing();
    }

    @Override
    public void setLocked(boolean locked) {
        mController.setLocked(locked);
    }

    @Override
    public boolean isLocked() {
        return mController.isLocked();
    }

    @Override
    public void startProgress() {
        mController.startProgress();
    }

    @Override
    public void stopProgress() {
        mController.stopProgress();
    }

    @Override
    public void hide() {
        mController.hide();
    }

    @Override
    public void show() {
        mController.show();
    }

    /**
     * 播放和暂停
     */
    public void togglePlay() {
        if (isPlaying()) {
            pause();
        } else {
            start();
        }
    }

    /**
     * 横竖屏切换，会旋转屏幕
     */
    public void toggleFullScreen(Activity activity) {
        if (activity == null || activity.isFinishing())
            return;
        if (isFullScreen()) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            exitFullScreen();
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            enterFullScreen();
        }
    }

    /**
     * 横竖屏切换，不会旋转屏幕
     */
    public void toggleFullScreen() {
        if (isFullScreen()) {
            exitFullScreen();
        } else {
            enterFullScreen();
        }
    }

    /**
     * 横竖屏切换，根据适配宽高决定是否旋转屏幕
     */
    public void toggleFullScreenByVideoSize(Activity activity) {
        if (activity == null || activity.isFinishing())
            return;
        int[] size = getVideoSize();
        int width = size[0];
        int height = size[1];
        if (isFullScreen()) {
            exitFullScreen();
            if (width > height) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } else {
            enterFullScreen();
            if (width > height) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
    }

    /**
     * 切换锁定状态
     */
    public void toggleLockState() {
        setLocked(!isLocked());
    }


    /**
     * 切换显示/隐藏状态
     */
    public void toggleShowState() {
        if (isShowing()) {
            hide();
        } else {
            show();
        }
    }

}
